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
 * After an UnregisteredClient has supplied a USER and NICK command,
 * it is passed to the constructor of the ConnectedUser.
 */
public class RegisteredUser extends org.pastiche.ircd.ConnectedTarget {
	public static final String COMMAND_FACTORY_ID = "connectedUser";
	private String nick = null;
	private String username = null;
	private String hostname = null;
	private String realName = null;
	private java.util.Set myChannels = new java.util.HashSet();
	private String disconnectReason = null;
	private long lastUnidle = 0;

	private org.pastiche.ircd.Command disconnect = new org.pastiche.ircd.Command() {
		public void process() {
			java.util.Iterator i = getVisibleLocalTargets().iterator();

			while (i.hasNext()) {
				((Target)i.next()).send(RegisteredUser.this, "QUIT :" + disconnectReason);
			}

			i = myChannels.iterator();

			while (i.hasNext()) {
				try {
					((Channel)i.next()).remove(RegisteredUser.this);
				} catch (org.pastiche.ircd.NotOnChannelException noce) {
				}
			}
			
			getServer().removeUser(RegisteredUser.this);			
		}
	};
public RegisteredUser(UnregisteredClient initialConnection) {
	super(initialConnection.getServer(), initialConnection.getConnection());
	initialConnection.remove();
	
	System.out.println("User connected: " + initialConnection.getNick());

	super.getConnection().setOwner(this);	
	this.nick = initialConnection.getNick();
	this.username = initialConnection.getUsername();
	this.hostname = initialConnection.getHostname();
	this.realName = initialConnection.getIrcname();
	this.lastUnidle = System.currentTimeMillis();
	super.setConnection(initialConnection.getConnection());
}
public void addChannel(Channel channel) {
	myChannels.add(channel);
}
/** Does this belong elsewhere? */
public static RegisteredUser convertToRegisteredUser(UnregisteredClient user) {
	RegisteredUser registeredUser = new RegisteredUser(user);
	registeredUser.getServer().replaceUser(registeredUser);

	ReplyHandler.getInstance().welcome(registeredUser.getServer(), registeredUser);
	ReplyHandler.getInstance().yourHost(registeredUser.getServer(), registeredUser);
	ReplyHandler.getInstance().created(registeredUser);
	ReplyHandler.getInstance().myInfo(registeredUser.getServer(), registeredUser);
	registeredUser.processCommand("LUSERS");
	registeredUser.processCommand("MOTD");
	return registeredUser;
}
protected void doDisconnect(Exception error) {
	doDisconnect(error.getMessage());
}
protected void doDisconnect(String reason) {
	if (!getConnection().isDisconnected()) {
		getConnection().quietKill();
	}

	this.disconnectReason = reason;
	org.pastiche.ircd.CommandQueue.getInstance().add(disconnect);
}
public Channel[] getChannels() {
	Channel[] channels = new Channel[myChannels.size()];
	myChannels.toArray(channels);
	return channels;
}
public org.pastiche.ircd.CommandFactory getCommandFactory() {
	return org.pastiche.ircd.CommandFactory.getCommandFactory(COMMAND_FACTORY_ID);
}
public String getHostname() {
	return hostname;
}
public java.lang.Object getIdentifier() {
	return getNick();
}
public java.lang.String getLongName() {
	return getNick() + "!" + getUsername() + "@" + getHostname();
}
public int getMaximumMessageSize(Target source) {
	return UnregisteredClient.MAX_MESSAGE_SIZE - source.getLongName().length() - 2;
}
public java.lang.String getName() {
	return getNick().toString();
}
public String getNick() {
	return nick;
}
public String getNickUserAtHost() {
	return getNick() + "!" + getUsername() + "@" + getHostname();
}
public String getRealName() {
	return realName;
}
public String getUsername() {
	return username;
}
/**
 * getVisibleLocalTargets method comment.
 */
public java.util.Set getVisibleLocalTargets() {
	java.util.Set targets = new java.util.HashSet();
	java.util.Iterator i = myChannels.iterator();

	while (i.hasNext()) {
		targets.add(i.next());
	}

	return targets;
}
/**
 * isDead method comment.
 */
public boolean isDead() {
	return false;
}
/**
 * isIdle method comment.
 */
public boolean isIdle() {
	return false;
}
public void part(org.pastiche.ircd.rfc1459.Channel channel, String notifyCommand) 
	throws org.pastiche.ircd.NotOnChannelException {
		
	channel.remove(this, notifyCommand);
	myChannels.remove(channel);
}
public void remove() {	
}
public void setNick(String newNick) throws org.pastiche.ircd.CollisionException {
	getServer().replaceUser(newNick, this);

	java.util.Iterator i = getVisibleLocalTargets().iterator();

	while (i.hasNext()) {
		((org.pastiche.ircd.Target)i.next()).send(this, "NICK " + newNick);
	}
	
	send(this, "NICK " + newNick);
	this.nick = newNick;
}
}
