package org.pastiche.ircd.rfc1459;

/*
 *   Pastiche IRCd - Java Internet Relay Chat
 *   Copyright (C) 2003 Paul Franz
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

import org.pastiche.ircd.*;

/**
 * KILL command for server admins
 */
public class AwayCommand extends org.pastiche.ircd.Command
   {
	private boolean requiresProcess = true;

   protected String getCommandName()
      {
      return "AWAY";
      }

   public void process()
      {
      if (getArgumentCount() == 0)
         {
         ((RegisteredUser)getSource()).setAway (null);
         ReplyHandler.getInstance().unAway(getSource());
         }
      else
         {
         String msg = getArgument(0);

         ((RegisteredUser)getSource()).setAway (""+msg);

         ReplyHandler.getInstance().nowAway(getSource());
         }
      }

   public boolean requiresProcess()
      {
   	return requiresProcess;
      }
   }
