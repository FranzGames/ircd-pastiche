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
 
public class ModeCommand extends org.pastiche.ircd.Command {
	private boolean requiresProcess = true;
private void doChannelMode(Channel channel) {
	if (!channel.isOnChannel(getSource())) {
		ErrorHandler.getInstance().notInChannel(getSource(), channel.getName());
		return;
	}

	if (getArgumentCount() == 1) {
		ReplyHandler.getInstance().channelModeIs(getSource(), channel);
		return;
	}

	if (!channel.isOp(getSource())) {
		ErrorHandler.getInstance().chanopPrivsNeeded(getSource(), channel);
		return;
	}

	java.util.List argumentList = new java.util.ArrayList();

	if (getArgumentCount() > 2) {
		argumentList = getArguments().subList(2, getArgumentCount());
	}
		
	ChannelModeHandler handler = new ChannelModeHandler(getSource(), channel, getArgument(1),
		argumentList);

	handler.process();

	org.pastiche.ircd.Target[] channelMembers = channel.getMembers();

	String visibleModeCommand = handler.getVisibleModeCommand();

	if (visibleModeCommand.length() > 0) {
		for (int i = 0; i < channelMembers.length; i++) {
			channelMembers[i].send(getSource(), "MODE " + channel.getName() + " " + handler.getVisibleModeCommand());
		}
	}
		
	return;
}
private void doUserMode(RegisteredUser user) {
	if (getArgumentCount() == 1) {
		ReplyHandler.getInstance().uModeIs((RegisteredUser)getSource());
		return;
	}	
	// USER MODES HANDLED HERE
	// UserModeHandler handler = new UserModeHandler(getSource(), (String)getArguments().get(1));		
	// handler.process();
	// getSource().send(getSource(), "MODE " + handler.getVisibleModeCommand());
	getSource().send(getSource(), "MODE +");	
}
public void preProcess() {
	if (getArgumentCount() < 1) {
		ErrorHandler.getInstance().needMoreParams(getSource(), getName());
		requiresProcess = false;
	}
}
public void process() {
	org.pastiche.ircd.Target target = getSource().getServer().getTarget(getArgument(0));

	if (target == null) {
		ErrorHandler.getInstance().noSuchNick(getSource(), getArgument(0));
		return;
	}

	if (target instanceof Channel) {
		doChannelMode((Channel)target);
	} else {
		if (target != getSource()) {
			ErrorHandler.getInstance().usersDontMatch(target);
			return;
		}
		
		doUserMode((RegisteredUser)target);
	}
}
public boolean requiresProcess() {
	return requiresProcess;
}
}
