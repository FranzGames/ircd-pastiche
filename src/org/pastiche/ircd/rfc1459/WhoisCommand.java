package org.pastiche.ircd.rfc1459;

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
 
public class WhoisCommand extends org.pastiche.ircd.Command {
private void doWhois(RegisteredUser user) {
	ReplyHandler.getInstance().whoisUser(getSource(), user.getNick(), user.getUsername(), user.getHostname(), user.getRealName());
	String[] visibleChannels = getChannels(user);

	if (visibleChannels.length > 0) {
		ReplyHandler.getInstance().whoisChannels(getSource(), user.getNick(), visibleChannels);
	}

	ReplyHandler.getInstance().whoisServer(getSource(), user.getNick(), user.getServer().getName(), user.getServer().getInfo());
	ReplyHandler.getInstance().whoisIdle(getSource(), user.getNick(), user.getSecondsIdle());
	ReplyHandler.getInstance().endOfWhois(getSource());
}
private String[] getChannels(RegisteredUser user) {
	Channel[] channels = user.getChannels();
	java.util.List channelStrings = new java.util.ArrayList(channels.length);

	for (int i = 0; i < channels.length; i++) {
		if (channels[i].isVisible() || channels[i].isOnChannel(getSource()))
			channelStrings.add(channels[i].getNickListModifier(user) + channels[i].getName());
	}

	String[] visibleChannels = new String[channelStrings.size()];
	channelStrings.toArray(visibleChannels);
	return visibleChannels;
}
private boolean isMask(String s) {
	return (s.indexOf("*") != -1) ||
	(s.indexOf("?") != -1);
}
public void process() {
	java.util.Iterator i = getArguments().iterator();
	
	while (i.hasNext()) {
		String nickMask = (String)i.next();
		if (isMask(nickMask)) {
			java.util.Iterator nicks = getSource().getServer().getUsersMatching(new RegisteredNickMask(nickMask));
			if (!nicks.hasNext()) {
				ErrorHandler.getInstance().noSuchNick(getSource(), nickMask);
			} else {
				while (nicks.hasNext()) {
					doWhois((RegisteredUser)nicks.next());
				}
			}
		} else {
			RegisteredUser found = (RegisteredUser) 
				getSource().getServer().getTarget(nickMask);
			if (found == null) {
				ErrorHandler.getInstance().noSuchNick(getSource(), nickMask);
			} else {
				doWhois(found);
			}
		}
	}
}
public boolean requiresProcess() {
	return true;
}
}
