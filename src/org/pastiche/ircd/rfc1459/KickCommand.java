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

public class KickCommand extends org.pastiche.ircd.Command {
	private boolean requiresProcess = true;
public void notifyChannelOfKick(Channel channel, org.pastiche.ircd.Target victim) {
	org.pastiche.ircd.Target[] members = channel.getMembers();
	String kickReason = getArgument(2);

	if (kickReason == null)
		kickReason = getSource().getName();

	for (int i = 0; i < members.length; i++) {
		members[i].send(getSource(), "KICK " + channel.getName() + " " + victim.getName() + " :" + kickReason);
	}
}
public void preProcess() {
	if (getArgumentCount() < 2) {
		ErrorHandler.getInstance().needMoreParams(getSource(), getName());
		requiresProcess = false;
	}
}
public void process() {
	String[] channels = this.calculateTargets(0);
	String[] nicks = this.calculateTargets(1);

	for (int i = 0; i < channels.length; i++) {
		Channel channel = (Channel) getSource().getServer().getChannel(channels[i]);

		if (channel == null) {
			ErrorHandler.getInstance().noSuchChannel(getSource(), channels[i]);
			continue;
		}

		if (!channel.isOnChannel(getSource())) {
			ErrorHandler.getInstance().notInChannel(getSource(), channels[i]);
			continue;
		}
		
		if (!channel.isOp(getSource())) {
			ErrorHandler.getInstance().chanopPrivsNeeded(getSource(), channel);
			continue;
		}

		for (int j = 0; j < nicks.length; j++) {
			org.pastiche.ircd.Target victim = getSource().getServer().getUser(nicks[j]);
			if ((victim != null) && channel.isOnChannel(victim)) {
				notifyChannelOfKick(channel, victim);
				try {
					channel.remove(victim);
				} catch (org.pastiche.ircd.NotOnChannelException noce) {
				}
			}
			// drop non-existant, not on channel nicks on floor.
		}	
	}
}
public boolean requiresProcess() {
	return requiresProcess;
}
}
