package org.pastiche.ircd.rfc1459;

import java.util.List;
import org.pastiche.ircd.Target;
import org.pastiche.ircd.Server;
/*
 *   Pastiche IRCd - Java Internet Relay Chat
 *   Copyright (C) 2001 Charles Miller
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

/**
 * <p>A <i>Method Object</i> to handle the intricacies of the mode command in
 * one convenient location.
 *
 * <p>The error handling in the stock ircd is 100% schizophrenic.
 * <ul><li>If you do MODE #channel +l without an argument, you get an error.
 * <li>If you do MODE #channel +o without an argument, you get no error.
 * <li>If You do MODE #channel +l with a non-numeric argument, you get no error.</ul>
 * <p>I'm sure it gets worse. I'm not sure whether it's a good idea to continue
 * to try to be bug-for-bug compatible with something so totally insane. 
 */
public class ChannelModeHandler {
	private org.pastiche.ircd.Target source = null;
	private Channel channel = null;
	private byte[] modeChars = null;
	private java.util.Stack args = null;
	private boolean addMode = true;
	private StringBuffer visibleModeString = new StringBuffer("+");
	private java.util.List visibleArgs = new java.util.ArrayList();
	private boolean hasDoneBanList = false;
public ChannelModeHandler(Target source, Channel channel, String modeString, List args) {
	this.source = source;
	this.channel = channel;
	this.modeChars = modeString.getBytes();
	this.args = new java.util.Stack();
	
	java.util.Iterator i = args.iterator();
	while (i.hasNext()) {
		this.args.push(i.next());
	}
}
private String completeBanMask(String mask) {
	int atPosition = mask.lastIndexOf('@');
	int bangPosition = mask.indexOf('!');
		
	String nickPart = "";
	String userPart = "";
	String hostPart = "";

	if ((bangPosition == -1) && (atPosition == -1))
		nickPart = mask;
		
	if (bangPosition != -1)
		nickPart = mask.substring(0, bangPosition);
		
	if (atPosition > bangPosition) {
		userPart = mask.substring(bangPosition + 1, atPosition);
		hostPart = mask.substring(atPosition + 1);
	}

	return maskIfEmpty(nickPart) + "!" + maskIfEmpty(userPart) + "@" + maskIfEmpty(hostPart);
}
public static String createModeString(Channel channel) {
	return "+" +
		(channel.isRestrictExternalMessages() ? "n" : "") +
		(channel.isTopicRestricted() ? "t" : "") +
		(channel.isModerated() ? "m" : "") +
		(channel.isInviteOnly() ? "i" : "") + 
		(channel.isMemberLimited() ? "l" : "") +
		(channel.isPrivate() ? "p" : "") +
		(channel.isSecret() ? "s" : "") +
		(channel.isLocked() ? "k" : "") + 
		(channel.isMemberLimited() ? " " + channel.getMemberLimit() : "") +
		(channel.isLocked() ? " " + channel.getKey() : "");
}
private void doBan() {
	if (args.size() == 0 && !hasDoneBanList) {
		doBanList();
		return;
	}

	String mask = (String) args.pop();

	if (!isWellFormedBanMask(mask))
		mask = completeBanMask(mask);

	if (addMode && channel.isBanned(mask))
		return;

	if (!addMode && !channel.hasBan(mask))
		return;

	if (!addMode) {
		channel.removeBan(mask);
	} else {
		channel.addBan(source, mask);
	}

	visibleModeString.append('b');
	visibleArgs.add(mask);
}
private void doBanList() {
	java.util.Iterator i = channel.getBans().iterator();
	int banCount = 0;
	
	while (i.hasNext()) {
		Ban ban = (Ban)i.next();
		ReplyHandler.getInstance().banList(source, channel, ban.getMask().toString());
	}

	ReplyHandler.getInstance().endOfBanList(source, channel);
	hasDoneBanList = true;
}
private void doInviteOnly() {
	if (channel.isInviteOnly() == addMode)
		return;
		
	channel.setInviteOnly(addMode);	
	visibleModeString.append('i');
}
private void doLimit() {
	if (!addMode) {
		if (channel.isMemberLimited()) {
			channel.setMemberLimited(false);
			visibleModeString.append('l');
		}
		return;
	}
	
	if (args.size() == 0) {
		ErrorHandler.getInstance().needMoreParams(source, "MODE +l");
		return;
	}
	

	try {
		channel.setMemberLimit(Integer.parseInt((String)args.pop()));
		channel.setMemberLimited(true);
	} catch (NumberFormatException nfe) {
		// Drop on floor - bug for bug compatibility with ircd.
		return;
	}
	
	visibleModeString.append('l');
	visibleArgs.add(Integer.toString(channel.getMemberLimit()));	
}
private void doLock() {
	if (!addMode) {
		if (channel.isLocked()) {
			channel.unlock();
			visibleModeString.append('k');
		}
		return;
	}
	
	if (args.size() == 0) {
		ErrorHandler.getInstance().needMoreParams(source, "MODE +k");
		return;
	}
	
	channel.lock((String)args.pop());
	visibleModeString.append('k');
	visibleArgs.add(channel.getKey());	
}
private void doModerate() {
	if (channel.isModerated() == addMode)
		return;
		
	channel.setModerated(addMode);
	visibleModeString.append('m');
}
private void doOp() {
	if (args.size() == 0)
		return;

	String nickName = (String)args.pop();
	org.pastiche.ircd.Target target = channel.getServer().getUser(nickName);

	if (target == null) {
		ErrorHandler.getInstance().noSuchNick(source, nickName);
		return;
	}

	if (!channel.isOnChannel(target))
		return;


	if (addMode) {
		if (channel.isOp(target))
			return;
		channel.op(target);
	} else {
		if (!channel.isOp(target))
			return;
		channel.deop(target);
	}
	
	visibleModeString.append('o');
	visibleArgs.add(nickName);	
}
private void doPrivate() {
	if (channel.isPrivate() == addMode)
		return;

	if (addMode) {
		if (channel.isSecret()) {
			setAddMode(false);
			doSecret();
			setAddMode(true);
		}
		channel.setVisibility(ChannelVisibility.PRIVATE);
	} else {
		channel.setVisibility(ChannelVisibility.VISIBLE);
	}
	
	visibleModeString.append('p');
}
private void doRestrictExternalMessages() {
	if (channel.isRestrictExternalMessages() == addMode)
		return;
		
	channel.setRestrictExternalMessages(addMode);
	visibleModeString.append('n');
}
private void doSecret() {
	if (channel.isSecret() == addMode)
		return;

	if (addMode) {
		if (channel.isPrivate()) {
			setAddMode(false);
			doPrivate();
			setAddMode(true);
		}
		channel.setVisibility(ChannelVisibility.SECRET);
	} else {
		channel.setVisibility(ChannelVisibility.VISIBLE);
	}
	
	visibleModeString.append('s');
}
private void doTopicRestricted() {
	if (channel.isTopicRestricted() == addMode)
		return;
		
	channel.setTopicRestricted(addMode);	
	visibleModeString.append('t');
}
private void doVoice() {
	if (args.size() == 0)
		return;

	String nickName = (String)args.pop();
	org.pastiche.ircd.Target target = channel.getServer().getUser(nickName);

	if (target == null) {
		ErrorHandler.getInstance().noSuchNick(source, nickName);
		return;
	}

	if (!channel.isOnChannel(target))
		return;

	if (channel.isVoiced(target) == addMode)
		return;

	if (addMode) {
		channel.voice(target);
	} else {
		channel.deVoice(target);
	}
	
	visibleModeString.append('v');
	visibleArgs.add(nickName);	
}
public String getVisibleModeCommand() {
	StringBuffer command = new StringBuffer(visibleModeString.toString());
	if (command.charAt(command.length() - 1) == '+' ||
			command.charAt(command.length() - 1) == '-')
		command.deleteCharAt(command.length() - 1);

	java.util.Iterator i = visibleArgs.iterator();

	while (i.hasNext()) {
		command.append(" ");
		command.append(i.next());
	}

	return command.toString();
}
private boolean isWellFormedBanMask (String mask) {
	int atPosition = mask.lastIndexOf('@');
	int bangPosition = mask.indexOf('!');

	return (bangPosition > 0) && 
		(atPosition > (bangPosition + 1)) && 
		(atPosition + 1 != mask.length());
}
private String maskIfEmpty(String part) {
	return part.equals("") ? "*" : part;
}
public void process() {
	for (int i = 0; i < modeChars.length; i++) {
		switch (modeChars[i]) {
			case '+':
				setAddMode(true);
				break;
			case '-':
				setAddMode(false);
				break;
			case 'o':
				doOp();
				break;
			case 'p':
				doPrivate();
				break;
			case 's':
				doSecret();
				break;
			case 'i':
				doInviteOnly();
				break;
			case 't':
				doTopicRestricted();
				break;
			case 'n':
				doRestrictExternalMessages();
				break;
			case 'm':
				doModerate();
				break;
			case 'l':
				doLimit();
				break;
			case 'b':
				doBan();
				break;
			case 'v':
				doVoice();
				break;
			case 'k':
				doLock();
				break;
			default:
				ErrorHandler.getInstance().unknownMode(source, (char)modeChars[i]);
				break;
		}
	}
}
private void setAddMode(boolean addMode) {
	if (addMode == this.addMode)
		return;

	this.addMode = addMode;

	char addModeChar = (addMode) ? '+' : '-';
	
	if (visibleModeString.charAt(visibleModeString.length() - 1) == '+' ||
		visibleModeString.charAt(visibleModeString.length() - 1) == '-') {

		visibleModeString.setCharAt(visibleModeString.length() - 1, addModeChar);
	} else {
		visibleModeString.append(addModeChar);
	}
}
}
