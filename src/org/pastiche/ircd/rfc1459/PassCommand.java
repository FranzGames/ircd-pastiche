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

public class PassCommand extends org.pastiche.ircd.Command {
public void process() {
   if (getArgumentCount() < 1)
      {
		ErrorHandler.getInstance().needMoreParams(getSource(), "PASS");
      return;
      }

	String passwd = getArgument(0);
   if (getSource () instanceof UnregisteredClient)
      {
	   UnregisteredClient source = (UnregisteredClient) getSource();

      source.setPassword (passwd);
      }
   else
		ErrorHandler.getInstance().alreadyRegistered(getSource());
}
public boolean requiresProcess() {
	return true;
}
}
