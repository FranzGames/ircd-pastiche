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
 
public class TopicCommand extends org.pastiche.ircd.Command {
	private boolean requiresProcess = true;
private boolean canSetTopic(Channel channel) {
	return !channel.isTopicRestricted() || channel.isOp(getSource());
}
public void preProcess() {
	if (getArgumentCount() < 1) {
		ErrorHandler.getInstance().needMoreParams(getSource(), getName());
		requiresProcess = false;
	}
}
public void process() {
	Channel channel = (Channel) getSource().getServer().getChannel(getArgument(0));

	if (channel == null || !channel.isOnChannel(getSource())) {
		ErrorHandler.getInstance().notInChannel(getSource(), getArgument(0));
		return;
	}

	if (getArgumentCount() == 1) {
		ReplyHandler.getInstance().topic(getSource(), channel);
	} else {
		if (!canSetTopic(channel)) {
			ErrorHandler.getInstance().chanopPrivsNeeded(getSource(), channel);
			return;
		}
		
		channel.setTopic(getSource(), getArgument(1));
	}
}
public boolean requiresProcess() {
	return requiresProcess;
}
}
