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

public abstract class Channel implements Target {
	private java.util.Set members = new java.util.HashSet();
	private String name = null;
	private Server server = null;
public Channel(Server server, String name) throws BadChannelNameException {
	if (!IrcdConfiguration.getInstance().getChannelNormalizer().isValidName(name))
		throw new BadChannelNameException();

	this.name = name;
	this.server = server;
	server.addChannel(name, this);
}
public void add(Target user) throws AlreadyOnChannelException {
	if (members.contains(user))
		throw new AlreadyOnChannelException();
		
	members.add(user);
}
public String getLongName() {
	return name;
}
public Target[] getMembers() {
	Target[] members = new Target[this.members.size()];
	this.members.toArray(members);
	return members;
}
public String getName() {
	return name;
}
public Server getServer() {
	return server;
}
public java.util.Set getVisibleLocalTargets() {
	return members;
}
public boolean isOnChannel(Target target) {
	return members.contains(target);
}
public void remove() {
	if (!members.isEmpty())
		throw new RuntimeException("Tried to remove a channel that isn't empty!");
		
	server.removeChannel(this);
}
public void remove(Target user) throws NotOnChannelException {
	if (!members.contains(user))
		throw new NotOnChannelException();
		
	members.remove(user);
	
	if (members.isEmpty()) {
		remove();
	}
}
public void send(Target source, String command) {
	java.util.Iterator i = members.iterator();

	while (i.hasNext()) {
		Target target = (Target) i.next();

		if (target != source) {
			target.send(source, command);
		}
	}
}
}