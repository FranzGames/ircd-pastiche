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
import org.pastiche.ircd.IrcMessage;
import org.pastiche.ircd.Target;

/**
 * Abstract superclass for NOTICE and PRIVMSG, since they behave identically
 * anyway.
 */
public abstract class MessageCommand extends org.pastiche.ircd.Command {

   private boolean requiresProcess = true;

   protected abstract String getCommandName();

   public void preProcess() {
      if (getArgumentCount() < 2) {
         ErrorHandler.getInstance().noTextToSend(getSource());
         requiresProcess = false;
      }
   }

   public void process() {
      String[] targets = calculateTargets(0);

      for (int i = 0; i < targets.length; i++) {
         Target target = getSource().getServer().getTarget(targets[i]);
         boolean isChannel = (target instanceof Channel);

         if (target != null) {
            if (!target.canSend(getSource())) {
               if (isChannel) {
                  ErrorHandler.getInstance().cannotSendToChan(getSource(), (Channel) target);
               } // else drop on floor
               return;
            }

            if (!isChannel) {
               if (((RegisteredUser) target).isAway()) {
                  ReplyHandler.getInstance().away(getSource(), targets[i], ((RegisteredUser) target).getAwayMsg());
               } else {
                  TargetIrcMessage msg = new TargetIrcMessage (new IrcMessage (getCommandName(), getArgument(1)), targets[i]);
                  
                  target.send(getSource(), msg);
               }
            } else {
               TargetIrcMessage msg = new TargetIrcMessage (new IrcMessage (getCommandName(), getArgument(1)), targets[i]);
               
               target.send(getSource(), msg);
            }
         } else {
            ErrorHandler.getInstance().noSuchNick(getSource(), targets[i]);
         }
      }
   }

   public boolean requiresProcess() {
      return requiresProcess;
   }
}
