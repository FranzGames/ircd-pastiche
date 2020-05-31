package org.pastiche.ircd.rfc1459;

import java.util.ArrayList;
import java.util.List;

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
public class NamesCommand extends org.pastiche.ircd.Command {

   public void process() {
      String[] targets = calculateTargets(0);

      if (targets == null || targets.length == 0) {
         java.util.Vector vec = new java.util.Vector();

         java.util.Iterator iter = getSource().getServer().getChannels();

         if (iter != null) {
            while (iter.hasNext()) {
               String name = ((Channel) iter.next()).getName();
               vec.addElement(name);
            }

            targets = new String[vec.size()];

            vec.copyInto(targets);
         }
      }

      List<String> channels = new ArrayList<String>();

      for (int i = 0; i < targets.length; i++) {
         Channel channel = (Channel) getSource().getServer().getChannel(targets[i]);

         if (channel == null) {
            continue;
         }

         channels.add(channel.getName());

         ReplyHandler.getInstance().names(getSource(), channel, channel.getMembers());
      }
      
      String[] names = new String[channels.size()];
      
      names = channels.toArray(names);
      ReplyHandler.getInstance().namesEnd(getSource(), names);
   }

   public boolean requiresProcess() {
      return true;
   }
}
