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

public class JoinCommand extends org.pastiche.ircd.Command {
	private boolean requiresProcess = true;
private void join(Channel channel) {
	try {
		channel.add(getSource());
		((RegisteredUser) getSource()).addChannel(channel);
		((RegisteredUser) getSource()).processCommand("TOPIC " + channel.getName());
		((RegisteredUser) getSource()).processCommand("NAMES " + channel.getName());
	} catch (org.pastiche.ircd.AlreadyOnChannelException aoce) {
		// drop on the floor.
	}
}
public void preProcess() {
	if (getArgumentCount() < 1) {
		ErrorHandler.getInstance().needMoreParams(getSource(), getName());
		requiresProcess = false;
	}
}
public void process() {
	String[] channelNames = calculateTargets(0);

	for (int i = 0; i < channelNames.length; i++) {
		tryToJoin((Channel)Channel.findOrCreateChannel(getSource().getServer(), channelNames[i]));
	}
}
public boolean requiresProcess() {
	return requiresProcess;
}
private void tryToJoin(Channel channel) {
	if (channel.isInviteOnly() && !channel.isInvited(getSource())) {
		ErrorHandler.getInstance().inviteOnlyChan(getSource(), channel);
		return;
	}

	if (channel.isMemberLimited() && (channel.getMembers().length >= channel.getMemberLimit())) {
		ErrorHandler.getInstance().channelIsFull(getSource(), channel);
		return;
	}

	if (channel.isLocked() && !((getArgumentCount() > 1) && (channel.getKey().equals(getArgument(1))))) {
		ErrorHandler.getInstance().badChannelKey(getSource(), channel);
		return;
	}

	if (channel.isBanned((RegisteredUser)getSource())) {
		ErrorHandler.getInstance().bannedFromChan(getSource(), channel.getName());
		return;
	}

	join(channel);
}
}
