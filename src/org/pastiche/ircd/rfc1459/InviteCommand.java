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

public class InviteCommand extends org.pastiche.ircd.Command {
	private boolean requiresProcess = true;
public void preProcess() {
	if (getArgumentCount() < 2) {
		ErrorHandler.getInstance().needMoreParams(getSource(), getName());
		requiresProcess = false;
	}
}
public void process() {
	org.pastiche.ircd.Target invitee = getSource().getServer().getUser(getArgument(0));

	if (invitee == null) {
		ErrorHandler.getInstance().noSuchNick(getSource(), getArgument(0));
		return;
	}

	Channel channel = (Channel) getSource().getServer().getChannel(getArgument(1));

	if (channel != null) {
		if (channel.isInviteOnly() && !channel.isOp(getSource())) {
			ErrorHandler.getInstance().chanopPrivsNeeded(getSource(), channel);
			return;
		}
		if (channel.isOnChannel(invitee)) {
			ErrorHandler.getInstance().userOnChannel(getSource(), getArgument(0), getArgument(1));
			return;
		}

		channel.addInvitation(invitee);
		invitee.send(getSource(), "INVITE " + invitee.getName() + " :" + channel.getName());
	}
}
public boolean requiresProcess() {
	return requiresProcess;
}
}
