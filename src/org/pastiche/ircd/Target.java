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

/**
 * <p>A Target is any destination that is the source of, or can
 * be the destination of commands. This includes channels, local and remote
 * users, and directly and indirectly connected servers.
 */
public interface Target {

	/* For pinger:
	public boolean isIdle();
	public boolean isDead();
	*/
public boolean canSend(Target source);

public String getLongName();

public int getMaximumMessageSize(Target source);

public String getName();

public Server getServer();

public java.util.Set getVisibleLocalTargets();

public void remove();

	public void send(Target source, String command);
}