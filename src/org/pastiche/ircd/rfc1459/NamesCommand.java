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
 
public class NamesCommand extends org.pastiche.ircd.Command {
public void process() {
	String[] targets = calculateTargets(0);
	StringBuffer editedTargetList = new StringBuffer(getArgument(0).length());

	for (int i = 0; i < targets.length; i++) {
		Channel channel = (Channel) getSource().getServer().getChannel(targets[i]);

		if (channel == null) {
			continue;
		}
		
		if (editedTargetList.length() == 0) {
			editedTargetList.append(channel.getName());
		} else {
			editedTargetList.append("," + channel.getName());
		}
		
		ReplyHandler.getInstance().names(getSource(), channel, channel.getMembers());
	}
	ReplyHandler.getInstance().namesEnd(getSource(), editedTargetList.toString());	
}
public boolean requiresProcess() {
	return true;
}
}
