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

import org.pastiche.ircd.Command;
import org.pastiche.ircd.CommandFactory;
import org.pastiche.ircd.CommandQueue;

public class UnregisteredUserCommand extends Command {
/**
 * preProcess method comment.
 */
public void preProcess() {
	UnregisteredClient source = (UnregisteredClient)getSource();

	if (source.getUsername() == null) {
		source.setUsername(getArgument(0));
	}

	source.setIrcname(getArgument(3));
   getSource().send (getSource ().getServer (), "NOTICE "+source.getUsername()+" :*** Enter your login and password now.");
}
/**
 * process method comment.
 */
public void process() {
	UnregisteredClient source = (UnregisteredClient)getSource();
	source.setIrcname(getArgument(3));
	if (source.getNick() != null) {
		RegisteredUser.convertToRegisteredUser(source);
	}
}
/**
 * requiresProcess method comment.
 */
public boolean requiresProcess() {
	return true;
}
}
