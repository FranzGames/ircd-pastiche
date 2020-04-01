package org.pastiche.ircd.rfc1459;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.pastiche.ircd.Command;
import org.pastiche.ircd.Ircd;

/*
 *   Pastiche IRCd - Java Internet Relay Chat
 *   Copyright (C) 2016 Paul Franz
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
public class InfoCommand extends org.pastiche.ircd.Command {

   @Override
   public void process() {
      String serverNameQuery = (getArgumentCount() == 0 ? null : getArguments().get(0));
      
      if (serverNameQuery != null) {
         Pattern pattern = Command.createPattern(serverNameQuery);
         Matcher matcher = pattern.matcher(getSource().getServer().getName());

         if (!matcher.find()) {
            ErrorHandler.getInstance().noSuchServer(getSource(), getSource().getServer().getName());
            return;
         }
      }

	BufferedReader info = null;
	
	try {
		java.io.InputStream infoin = InfoCommand.class.getResourceAsStream("/info.txt");

		if (infoin == null)
			throw new java.io.FileNotFoundException("INFO file not found");

		info = new BufferedReader(new InputStreamReader(
			infoin));

		String line = null;

		while ((line = info.readLine()) != null) {
			ReplyHandler.getInstance().info(getSource(), line);
		}
	} catch (java.io.IOException ioe) {
	} finally {
		try { if (info != null) info.close(); } catch (Exception e) {}
	}      
      ReplyHandler.getInstance().endOfInfo(getSource());
   }

   public boolean requiresProcess() {
      return true;
   }
}
