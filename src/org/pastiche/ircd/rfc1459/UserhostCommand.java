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
 
/**
 * <p>The USERHOST command. Since nothing is changed during a
 * userhost, this all takes place in preProcess().
 */
public class UserhostCommand extends org.pastiche.ircd.Command {
@Override
public void process() {
   if (getArgumentCount() == 0) {
      ErrorHandler.getInstance().needMoreParams(getSource(), "USERHOST");
      return;
   }
   
	// FIXME: needs IRCop and AWAY/HERE notification.
	java.util.Iterator i = getArguments().iterator();
	StringBuffer reply = new StringBuffer(getArguments().size() * NickNormalizer.MAX_NICK_LENGTH);
	while (i.hasNext()) {
		RegisteredUser found = (RegisteredUser) 
			getSource().getServer().getTarget((String)i.next());

		if (found != null) {
			reply.append(found.getNick());
			reply.append("=+");
			reply.append(found.getUsername());
			reply.append("@");
			reply.append(found.getHostname());
		}
	}

	ReplyHandler.getInstance().userhost(getSource(), reply.toString());
}
@Override
public boolean requiresProcess() {
	return true;
}
}
