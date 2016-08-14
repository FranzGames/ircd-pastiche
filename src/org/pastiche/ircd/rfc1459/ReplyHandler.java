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
	sendTo(target, "367", channel.getName() + " " + banMask);
}
public void channelModeIs (Target target, Channel channel) {
	sendTo(target, "324", channel.getName() + " " + ChannelModeHandler.createModeString(channel));
}
public void created (Target target) {
	sendTo(target, "003", ":This server was created " + org.pastiche.ircd.Version.COMPILED);
}
public void endOfBanList(Target target, Channel channel) {
	sendTo(target, "368", channel.getName() + " :End of channel ban list");
}
public void endOfList (Target target) {
	sendTo(target, "323", "End of /LIST");
}
public void endOfMotd(Target target) {
	sendTo(target, "376", ":End of /MOTD command.");
}
public void endOfWhois(Target target) {
	sendTo(target, "318", ":End of /WHOIS list");
}
public void endOfWho(Target target, String user) {
	sendTo(target, "315", user+" :End of /WHO list");
}

public static ReplyHandler getInstance() {
	if (instance == null) {
		instance = new ReplyHandler();
	}

	return instance;
}
public void ison(Target target, String onlineNicks) {
	sendTo(target, "303", ":" + onlineNicks);
}
public void list (Target target, String channelName, int memberCount, String topic) {
	StringBuffer reply = new StringBuffer(channelName + " " + memberCount + " :");

	if (topic != null) {
		reply.append(topic);
	}

	sendTo(target, "322", reply.toString());
}
public void luserChannels (Target target, int channels) {
	sendTo(target, "254", "\002"+channels +"\002 :channels formed");
}
public void luserClient (Target target, int visibleUsers, int invisibleUsers, int servers) {
	sendTo(target, "251", ":There are \002" + visibleUsers + "\002 users and \002" + invisibleUsers +
		"\002 invisible on \002" + servers + "\002 servers");
}
public void luserMe (Target target, int localUsers, int localServers) {
	sendTo(target, "255",  ":I have \002" + localUsers + "\002 clients and \002" + localServers + "\002 servers");
}
public void luserOp (Target target, int ircOps) {
	sendTo(target, "252", "\002"+ircOps + "\002 :operator(s) online");
}
public void luserUnknown (Target target, int unknownConnections) {
	sendTo(target, "253", "\002"+unknownConnections + "\002 :unknown connection(s)");
}

public void away(Target target, String nick, String msg) {
	sendTo(target, "301", nick+" :"+msg);
}

public void unAway(Target target) {
	sendTo(target, "305", ": You are no longer marked as being away");
}

public void nowAway(Target target) {
	sendTo(target, "306", ": You have been marked as being away");
}

public void motd(Target target, String line) {
	sendTo(target, "372", ":- " + line);
}
public void motdStart(Server server, Target target) {
	sendTo(target, "375", ":- " + server.getName() + " Message of the day - ");
}
public void myInfo (Server server, Target target) {
	sendTo(target, "004", server.getName() + " " +
		CommandSetVersion.getFullVersion() + " o o");
}
public void names (Target target, Channel channel, Target[] names) {
	StringBuffer reply = new StringBuffer(UnregisteredClient.MAX_MESSAGE_SIZE);

	for (int i = 0; i < names.length; i++) {
		if ((reply.length() + names[i].getName().length() + 3) >
			target.getMaximumMessageSize(target.getServer())) {

			sendTo(target, "353", "= " + channel.getName() + " :" + reply);
			reply = new StringBuffer(UnregisteredClient.MAX_MESSAGE_SIZE);
		}

		reply.append(channel.getNickListModifier(names[i]) + names[i].getName() + " ");
	}

	sendTo(target, "353", "= " + channel.getName() + " :" + reply);
}
public void namesEnd(Target target, String channelNames) {
	sendTo(target, "366", channelNames + " :End of /NAMES list.");
}
public void topic(Target target, Channel channel) {
	if (channel.getTopic() == null) {
		sendTo(target, "331", channel.getName() + " :No topic is set");
	} else {
		sendTo(target, "332", channel.getName() + " :" + channel.getTopic());
	}
}
public void uModeIs (RegisteredUser user) {
	sendTo(user, "221", "+");
}
public void userhost(Target target, String userhosts) {
	sendTo(target, "302", ":" + userhosts);
}
public void welcome (Server server, RegisteredUser user) {
	sendTo(user, "001", ":Welcome to the " + server.getNetworkName() +
		" IRC network " + user.getNickUserAtHost());
}
public void whoisChannels (Target target, String nick, String[] channels) {
	StringBuffer reply = new StringBuffer(UnregisteredClient.MAX_MESSAGE_SIZE);

	for (int i = 0; i < channels.length; i++) {
		if ((reply.length() + channels[i].length() + 2) >
			target.getMaximumMessageSize(target.getServer())) {

			sendTo(target, "319", nick + " :" + reply);
			reply = new StringBuffer(UnregisteredClient.MAX_MESSAGE_SIZE);
		}

		reply.append(channels[i] + " ");
	}

	sendTo(target, "319", nick + " :" + reply);
}
public void whoisIdle(Target source, String nick, int idleSeconds) {
	sendTo(source, "317", nick + " " + idleSeconds + " :seconds idle");
}
public void whoisServer(Target source, String nick, String server, String serverInfo) {
	sendTo(source, "312", nick + " " + server + " :" + serverInfo);
}
public void whoisUser(Target source, String nick, String user, String host, String realName) {
	sendTo(source, "311", nick + " " + user + " " + host + " * :" + realName);
}
public void who(Target source, Channel channel, Server server, String nick, String user, String host, String realName, String op) {
	sendTo(source, "352", channel.getName ()+" "+ user + " " + host + " "+server.getName()+" "+nick+" "+op+" :0 " + realName);
}

public void version(Target source, String version, Server server, String comments) {
	sendTo(source, "351", version + " "+server.getName()+" : " + comments);
}

public void yourHost (Server server, Target target) {
	sendTo(target, "002", ":Your host is " + server.getName() +
		"[@" + server.getAddress() + "], running version " +
		org.pastiche.ircd.Version.getFullVersion() + "[" +
		CommandSetVersion.getFullVersion() + "]");
}
}
