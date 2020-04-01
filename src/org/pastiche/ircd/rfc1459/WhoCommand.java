package org.pastiche.ircd.rfc1459;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.pastiche.ircd.Command;
import org.pastiche.ircd.Target;

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
public class WhoCommand extends org.pastiche.ircd.Command {

   @Override
   public void process() {
      String query = ((getArgumentCount() == 0) ? null : getArguments().get(0));
      Pattern pattern = null;
      boolean onlyOper = false;

      if (getArguments().size() > 1) {
         onlyOper = getArguments().get(1).equals("o");
      }

      // If the query is 0 then it is a wild card
      if (query != null && query.equals("0")) {
         query = null;
      }

      // If the query does not begin with a pound sign (i.e. "#"), 
      // then it is a generic query on user information.
      if (query != null && !query.startsWith("#")) {
         pattern = Command.createPattern(query);

         //Null out so that all rooms are selected.
         query = null;
      }

      List<Channel> channels = new ArrayList<Channel>();

      if (query == null) {
         Iterator iter = getSource().getServer().getChannels();

         while (iter.hasNext()) {
            channels.add((Channel) iter.next());
         }
      } else {
         Channel channel = (Channel) getSource().getServer().getChannel(query);

         if (channel != null) {
            channels.add(channel);
         }
      }

      for (Channel channel : channels) {
         Target[] targets = channel.getMembers();

         for (int i = 0; i < targets.length; i++) {
            if (targets[i] instanceof RegisteredUser) {
               RegisteredUser user = (RegisteredUser) targets[i];
               String op = channel.getNickListModifier(user);

               // If filtering for Operator only and not an operator, then skip
               if (onlyOper && !channel.isOp(user)) {
                  continue;
               }

               if (pattern != null) {
                  boolean found = false;
                  List<String> searchInfo = new ArrayList<String>();

                  searchInfo.add(user.getUsername());
                  searchInfo.add(user.getNick());
                  searchInfo.add(user.getRealName());
                  searchInfo.add(user.getHostname());

                  for (String info : searchInfo) {
                     Matcher matcher = pattern.matcher(info);
                     
                     if (matcher.find()) {
                        found = true;
                        break;
                     }
                  }
                  
                  // If there is no match, then skip
                  if (!found) {
                     continue;
                  }
               }

               ReplyHandler.getInstance().who(getSource(), channel, user.getServer(),
                  user.getNick(), user.getUsername(), user.getHostname(),
                  user.getRealName(), "*" + op);
            }
         }
      }

      ReplyHandler.getInstance().endOfWho(getSource(), query);
   }

   public boolean requiresProcess() {
      return true;
   }
}
