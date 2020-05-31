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
 
public class PartCommand extends org.pastiche.ircd.Command {
	private boolean requiresProcess = true;
public void preProcess() {
	if (getArgumentCount() < 1) {
		ErrorHandler.getInstance().needMoreParams(getSource(), getName());
		requiresProcess = false;
	}
}
public void process() {
	String[] channelNames = calculateTargets(0);

	for (int i = 0; i < channelNames.length; i++) {
		try {
			Channel channel = (Channel)	getSource().getServer().getChannel(channelNames[i]);

			if (channel == null) {
				ErrorHandler.getInstance().noSuchChannel(getSource(), channelNames[i]);
				continue;
			}
			
			((RegisteredUser)getSource()).part(channel, channel.getName());
		} catch (org.pastiche.ircd.NotOnChannelException aoce) {
			ErrorHandler.getInstance().notInChannel(getSource(), channelNames[i]);
		}
	}
}
public boolean requiresProcess() {
	return requiresProcess;
}
}
