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
/**
 * <p>The Ison command.
 */
public class IsonCommand extends Command {
@Override
public void process() {
   if (getArgumentCount() == 0) {
      ErrorHandler.getInstance().needMoreParams(getSource(), "ISON");
      return;
   }
   
	java.util.Iterator i = getArguments().iterator();
	StringBuffer reply = new StringBuffer(getArguments().size() * NickNormalizer.MAX_NICK_LENGTH);
	while (i.hasNext()) {
		org.pastiche.ircd.Target found = 
			getSource().getServer().getTarget((String)i.next());

		if (found != null) {
			reply.append(found.getName());
			reply.append(" ");
		}
	}

	ReplyHandler.getInstance().ison(getSource(), reply.toString());
}
@Override
public boolean requiresProcess() {
	return true;
}
}
