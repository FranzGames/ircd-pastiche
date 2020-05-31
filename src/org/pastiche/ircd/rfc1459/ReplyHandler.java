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
import org.pastiche.ircd.Server;

/**
 * NumericHandler for the RPL_ numerics.
 */
public class ReplyHandler extends NumericHandler {

   private static ReplyHandler instance = null;

   /**
    * ReplyHandler constructor comment.
    */
   public ReplyHandler() {
      super();
   }

   public void banList(Target target, Channel channel, String banMask) {
      IrcMessage msg = new IrcMessage(367);

      msg.addParameter(channel.getName());
      msg.addParameter(banMask);

      sendTo(target, msg);

   }

   public void channelModeIs(Target target, Channel channel) {
      IrcMessage msg = new IrcMessage(324);

      msg.addParameter(channel.getName());
      msg.addParameter(ChannelModeHandler.createModeString(channel));

      sendTo(target, msg);
   }

   public void created(Target target) {
      sendTo(target, new IrcMessage(3, "This server was created " + org.pastiche.ircd.Version.COMPILED));
   }

   public void endOfBanList(Target target, Channel channel) {
      IrcMessage msg = new IrcMessage(368, "End of channel ban list");

      msg.addParameter(channel.getName());

      sendTo(target, msg);
   }

   public void endOfList(Target target) {
      sendTo(target, new IrcMessage(323, "End of /LIST"));
   }

   public void endOfMotd(Target target) {
      sendTo(target, new IrcMessage(376, "End of /MOTD command."));
   }

   public void endOfWhois(Target target) {
      sendTo(target, new IrcMessage(318, "End of /WHOIS list"));
   }

   public void endOfWho(Target target, String user) {
      IrcMessage msg = new IrcMessage(315, "End of /WHO list");

      msg.addParameter(user);

      sendTo(target, msg);
   }

   public static ReplyHandler getInstance() {
      if (instance == null) {
         instance = new ReplyHandler();
      }

      return instance;
   }

   public void ison(Target target, String[] onlineNicks) {
      IrcMessage msg = new IrcMessage(303);

      for (String name : onlineNicks) {
         msg.addPostParameter(name);
      }

      sendTo(target, msg);
   }

   public void info(Target target, String info) {
      sendTo(target, new IrcMessage(370, info));
   }

   public void endOfInfo(Target target) {
      sendTo(target, new IrcMessage(374, "End of /INFO list"));
   }

   public void links(Target source, String mask, Server server, String hopCount) {
      IrcMessage msg = new IrcMessage(364, "" + hopCount + " " + server.getInfo());

      msg.addParameter(mask);
      msg.addParameter(server.getName());

      sendTo(source, msg);
   }

   public void endOfLinks(Target target, String mask) {
      sendTo(target, new IrcMessage(365, "End of /LINKS list"));
   }

   public void list(Target target, String channelName, int memberCount, String topic) {
      IrcMessage msg = new IrcMessage(322, topic == null ? "" : topic);

      msg.addParameter(channelName);
      msg.addParameter("" + memberCount);

      sendTo(target, msg);
   }

   public void luserChannels(Target target, int channels) {
      IrcMessage msg = new IrcMessage(364, "channels formed");

      msg.addParameter("\002" + channels + "\002");

      sendTo(target, msg);
   }

   public void luserClient(Target target, int visibleUsers, int invisibleUsers, int servers) {
      sendTo(target, new IrcMessage(251, "There are \002" + visibleUsers + "\002 users and \002" + invisibleUsers
              + "\002 invisible on \002" + servers + "\002 servers"));
   }

   public void luserMe(Target target, int localUsers, int localServers) {
      sendTo(target, new IrcMessage(255, "I have \002" + localUsers + "\002 clients and \002" + localServers + "\002 servers"));
   }

   public void luserOp(Target target, int ircOps) {
      IrcMessage msg = new IrcMessage(252, "operator(s) online");

      msg.addParameter("\002" + ircOps + "\002");

      sendTo(target, msg);
   }

   public void luserUnknown(Target target, int unknownConnections) {
      IrcMessage msg = new IrcMessage(253, "unknown connection(s)");

      msg.addParameter("\002" + unknownConnections + "\002");

      sendTo(target, msg);
   }

   public void away(Target target, String nick, String message) {
      IrcMessage msg = new IrcMessage(301, message);

      msg.addParameter(nick);

      sendTo(target, msg);
   }

   public void unAway(Target target) {
      sendTo(target, new IrcMessage(305, "You are no longer marked as being away"));
   }

   public void nowAway(Target target) {
      sendTo(target, new IrcMessage(306, "You have been marked as being away"));
   }

   public void motd(Target target, String line) {
      sendTo(target, new IrcMessage(372, "- " + line));
   }

   public void motdStart(Server server, Target target) {
      sendTo(target, new IrcMessage(375, "- " + server.getName() + " Message of the day - "));
   }

   public void myInfo(Server server, Target target) {
      IrcMessage msg = new IrcMessage(4);

      msg.addParameter(server.getName());
      msg.addParameter(CommandSetVersion.getFullVersion());
      msg.addParameter("o");
      msg.addParameter("o");

      sendTo(target, msg);
   }

   public void names(Target target, Channel channel, Target[] names) {
      for (int i = 0; i < names.length; i++) {
         IrcMessage msg = new IrcMessage(353, channel.getNickListModifier(names[i]) + names[i].getName());

         msg.addParameter(channel.getName());

         sendTo(target, msg);
      }
   }

   public void namesEnd(Target target, String[] channelNames) {
      IrcMessage msg = new IrcMessage(366, "End of /NAMES list.");

      for (String name : channelNames) {
         msg.addParameter(name);
      }

      msg.setParameterSeparator(",");

      sendTo(target, msg);
   }

   public void time(Target source, Server server, String time) {
      IrcMessage msg = new IrcMessage(391, time);

      msg.addParameter(server.getName());

      sendTo(source, msg);
   }

   public void topic(Target target, Channel channel) {
      IrcMessage msg = new IrcMessage(channel.getTopic() == null ? 331 : 332,
              channel.getTopic() == null ? "No topic is set" : channel.getTopic());

      msg.addParameter(channel.getName());

      sendTo(target, msg);
   }

   public void uModeIs(RegisteredUser user) {
      IrcMessage msg = new IrcMessage(221);

      msg.addParameter("+");

      sendTo(user, msg);
   }

   public void userhost(Target target, String[] userhosts) {
      IrcMessage msg = new IrcMessage(302);

      for (String hostEntry : userhosts) {
         msg.addPostParameter(hostEntry);
      }

      sendTo(target, msg);
   }

   public void welcome(Server server, RegisteredUser user) {
      sendTo(user, new IrcMessage(1, "Welcome to the " + server.getNetworkName()
              + " IRC network " + user.getNickUserAtHost()));
   }

   public void whoisChannels(Target target, String nick, String[] channels) {
      for (int i = 0; i < channels.length; i++) {
         IrcMessage msg = new IrcMessage(319, nick);

         msg.addParameter(channels[i]);

         sendTo(target, msg);
      }
   }

   public void whoisIdle(Target source, String nick, int idleSeconds) {
      IrcMessage msg = new IrcMessage(317, "seconds idle");

      msg.addParameter(nick);
      msg.addParameter("" + idleSeconds);

      sendTo(source, msg);
   }

   public void whoisServer(Target source, String nick, String server, String serverInfo) {
      IrcMessage msg = new IrcMessage(312, serverInfo);

      msg.addParameter(nick);
      msg.addParameter(server);

      sendTo(source, msg);
   }

   public void whoisUser(Target source, String nick, String user, String host, String realName) {
      IrcMessage msg = new IrcMessage(311, realName);

      msg.addParameter(nick);
      msg.addParameter(user);
      msg.addParameter(host);
      msg.addParameter("*");

      sendTo(source, msg);
   }

   public void who(Target source, Channel channel, Server server, String nick, String user, String host, String realName, String op) {
      IrcMessage msg = new IrcMessage(352, "0 " + realName);

      msg.addParameter(channel.getName());
      msg.addParameter(user);
      msg.addParameter(host);
      msg.addParameter(server.getName());
      msg.addParameter(nick);
      msg.addParameter(op);

      sendTo(source, msg);
   }

   public void version(Target source, String version, Server server, String comments) {
      IrcMessage msg = new IrcMessage(351, comments);

      msg.addParameter(version);
      msg.addParameter(server.getName());

      sendTo(source, msg);
   }

   public void yourHost(Server server, Target target) {
      sendTo(target, new IrcMessage(2, "Your host is " + server.getName()
              + "[@" + server.getAddress() + "], running version "
              + org.pastiche.ircd.Version.getFullVersion() + "["
              + CommandSetVersion.getFullVersion() + "]"));
   }
}
