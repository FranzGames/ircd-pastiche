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
public class KillCommand extends org.pastiche.ircd.Command
   {
	private boolean requiresProcess = true;

   protected String getCommandName()
      {
      return "KILL";
      }

   public void preProcess()
      {
      if (getArgumentCount() < 1)
         {
         ErrorHandler.getInstance().needMoreParams(getSource(), getCommandName ());
         requiresProcess = false;
         }

      if (!IrcdConfiguration.getInstance().isOperator (getSource().getName ()))
         {
         ErrorHandler.getInstance().noPrivileges(getSource());
         requiresProcess = false;
         }
      }

   public void process()
      {
      Target target = getSource().getServer().getTarget(getArgument(0));

      if (target != null)
         {
         ((ConnectedTarget) target).processCommand("QUIT");
         ((ConnectedTarget) target).getConnection ().quietKill ();
         }
      else
      	ErrorHandler.getInstance().noSuchNick(getSource(), getArgument(0));

      // This is for the case where there is no connection (i.e. the QUIT
      // command does not work)
      if (getArgumentCount() == 2 && getArgument(1).equalsIgnoreCase("remove") && target != null)
         getSource().getServer().removeUser(target);
      }

   public boolean requiresProcess()
      {
   	return requiresProcess;
      }
   }
