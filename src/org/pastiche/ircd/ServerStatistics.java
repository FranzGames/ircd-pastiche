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
public class ServerStatistics {
	private int visibleUsers = 0;
	private int invisibleUsers = 0;
	private int channels = 0;
	private int servers = 0;
	private int ircops = 0;
	private int unknownConnections = 0;
	private int localUsers = 0;
	private int localServers = 0;
/**
 * ServerStatistics constructor comment.
 */
public ServerStatistics(int visibleUsers, int invisibleUsers, int servers, int channels, 
	int ircops, int unknownConnections, int localUsers, int localServers) {

	this.visibleUsers = visibleUsers;
	this.invisibleUsers = invisibleUsers;
	this.servers = servers;
	this.channels = channels;
	this.ircops = ircops;
	this.unknownConnections = unknownConnections;
	this.localUsers = localUsers;
	this.localServers = localServers;
}
/**
 * Insert the method's description here.
 * Creation date: (7/01/2001 2:51:37 AM)
 * @return int
 */
public int getChannels() {
	return channels;
}
/**
 * Insert the method's description here.
 * Creation date: (7/01/2001 2:51:37 AM)
 * @return int
 */
public int getInvisibleUsers() {
	return invisibleUsers;
}
/**
 * Insert the method's description here.
 * Creation date: (7/01/2001 2:51:37 AM)
 * @return int
 */
public int getIrcops() {
	return ircops;
}
/**
 * Insert the method's description here.
 * Creation date: (7/01/2001 2:51:37 AM)
 * @return int
 */
public int getLocalServers() {
	return localServers;
}
/**
 * Insert the method's description here.
 * Creation date: (7/01/2001 2:51:37 AM)
 * @return int
 */
public int getLocalUsers() {
	return localUsers;
}
/**
 * Insert the method's description here.
 * Creation date: (7/01/2001 2:51:37 AM)
 * @return int
 */
public int getServers() {
	return servers;
}
/**
 * Insert the method's description here.
 * Creation date: (7/01/2001 2:51:37 AM)
 * @return int
 */
public int getUnknownConnections() {
	return unknownConnections;
}
/**
 * Insert the method's description here.
 * Creation date: (7/01/2001 2:51:37 AM)
 * @return int
 */
public int getVisibleUsers() {
	return visibleUsers;
}
}
