package org.pastiche.ircd;

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

import java.util.Date;
import java.util.Map;
import java.util.Iterator;

/**
 * <p>Represents the state of the current server. It might be
 * necessary to split off a separate Network object that represents the network
 * as a whole, but right now it's all in here.
 */
public class Server implements Target {
	private String networkName = "FooBar";
	private String serverInfo = "This is hard-coded";
	private Map connectedTargets = new java.util.HashMap();
	private Map networkUsers = new java.util.HashMap();
	private Map channels = new java.util.HashMap();
   private boolean firstConnection = true;

/**
 * Server constructor comment.
 */
public Server() {
	super();
}

public synchronized void newConnectedTarget (Target target)
   {
   if (firstConnection)
      {
      firstConnection = false;

      // Send notifications twice before timing out
      Scheduler.addTask (new DeadClientTask (this,
         IrcdConfiguration.getInstance().getDeadClientTimeout() / 2),
         (IrcdConfiguration.getInstance().getDeadClientTimeout() / 4) * 1000,
         (IrcdConfiguration.getInstance().getDeadClientTimeout() / 2) * 1000);
      }
   }

public void addChannel(String identifier, Channel channel) {
	identifier = IrcdConfiguration.getInstance().getChannelNormalizer().normalise(identifier);

	if (channels.containsKey(identifier))
		throw new RuntimeException("Tried to add a channel that already exists");

	channels.put(identifier, channel);
}
public void addUser(String identifier, Target user) throws CollisionException {
	identifier = IrcdConfiguration.getInstance().getUserNormalizer().normalise(identifier);

System.out.println ("addUser ("+(new Date())+") : id = "+identifier);

	if (networkUsers.containsKey(identifier))
		throw new CollisionException();

	networkUsers.put(identifier, user);

	if (user instanceof ConnectedTarget)
		connectedTargets.put(identifier, user);
}
public boolean canSend(Target source) {
   if (source == this)
      System.out.println ("Can you send to yourself");

   System.out.println ("canSend: this.getName () = "+getName ()+" source.name = "+getName ());

	return true;
}
private void forceReplaceUser(String normalizedIdentifier, Target user) throws CollisionException {
	String oldIdentifier = IrcdConfiguration.getInstance().getUserNormalizer().normalise(user.getName());

	networkUsers.remove(oldIdentifier);
	networkUsers.put(normalizedIdentifier, user);

	if (user instanceof ConnectedTarget) {
		connectedTargets.remove(oldIdentifier);
		connectedTargets.put(normalizedIdentifier, user);
	}
}

public void pingNecessaryUsers (int idle)
   {
   Iterator iter = connectedTargets.values ().iterator ();

   while (iter.hasNext ())
      {
      ConnectedTarget targ = (ConnectedTarget) iter.next ();

      if (targ.getSecondsIdle() >= idle)
         targ.ping ();
      }
   }

public String getAddress() {
	try {
		return java.net.InetAddress.getLocalHost().getHostAddress();
	} catch (java.net.UnknownHostException uhe) {
		return "0.0.0.0";
	}
}
public Channel getChannel(String identifier) {
	return (Channel)channels.get(
			IrcdConfiguration.getInstance().getChannelNormalizer().normalise(identifier));
}
public java.util.Iterator getChannels() {
	return channels.values().iterator();
}
public String getInfo() {
	return serverInfo;
}
public java.lang.String getLongName() {
	return getName();
}
public int getMaximumMessageSize(Target source) {
	throw new RuntimeException("No need to check this yet.");
}
public String getName() {
	return IrcdConfiguration.getInstance().getServerName();
}
public String getNetworkName() {
	return networkName;
}
/** This method should never be called :) */
public Server getServer() {
	return this;
}
public ServerStatistics getServerStatistics() {
	return new ServerStatistics(networkUsers.size(), 0, 1, 0, 0, 0, networkUsers.size(), 0);
}
/**
 * Note: This method assumes that a valid username, servername and channel name
 * can not overlap. This is probably a bad thing.
 */
public Target getTarget(String identifier) {
	if (IrcdConfiguration.getInstance().getUserNormalizer().isValidName(identifier)) {
		return (Target)networkUsers.get(
			IrcdConfiguration.getInstance().getUserNormalizer().normalise(identifier));
	}

	if (IrcdConfiguration.getInstance().getChannelNormalizer().isValidName(identifier)) {
		return (Target)channels.get(
			IrcdConfiguration.getInstance().getChannelNormalizer().normalise(identifier));
	}

	return null;
}
/**
 * Note: This method assumes that a valid username, servername and channel name
 * can not overlap. This is probably a bad thing.
 */
public Target getUser(String identifier) {
	return (Target)networkUsers.get(
			IrcdConfiguration.getInstance().getUserNormalizer().normalise(identifier));
}
public java.util.Iterator getUsersMatching(Mask mask) {
	java.util.List returnList = new java.util.ArrayList();
	java.util.Iterator users = networkUsers.values().iterator();

	while (users.hasNext()) {
		Target target = (Target)users.next();
		if (mask.match(target)) {
			returnList.add(target);
		}
	}

	return returnList.iterator();
}
/**
 * getVisibleLocalTargets method comment.
 */
public java.util.Set getVisibleLocalTargets() {
	return connectedTargets.entrySet();
}
/**
 * remove method comment.
 */
public void remove() {}
public void removeChannel(Channel channel) {
	channels.remove(IrcdConfiguration.getInstance().getChannelNormalizer().normalise(channel.getName()));
}
public void removeUser(Target user) {
	String identifier = IrcdConfiguration.getInstance().getUserNormalizer().normalise(user.getName());

System.out.println ("removeUser ("+(new Date())+") : id = "+identifier);

	if (networkUsers.remove(identifier) == null)
      System.err.println (user.getName()+" not in user list.");

	if (user instanceof ConnectedTarget)
      {
		if (connectedTargets.remove(identifier) == null)
         System.err.println (user.getName()+" not in connected list.");
      }
}
public void replaceUser(String identifier, Target user) throws CollisionException {
	identifier = IrcdConfiguration.getInstance().getUserNormalizer().normalise(identifier);

	if (identifier.equals(IrcdConfiguration.getInstance().getUserNormalizer().normalise(user.getName())))
		return;

	if (networkUsers.containsKey(identifier))
		throw new CollisionException();

	forceReplaceUser(identifier, user);
}
public void replaceUser(Target user) {
	try {
		forceReplaceUser(IrcdConfiguration.getInstance().getUserNormalizer().normalise(user.getName()), user);
	} catch (CollisionException ce) {}
}
public void send(Target source, java.lang.String command) {
	throw new RuntimeException("Tried to send a message to myself!");
}
}
