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
 *
 */
import org.pastiche.ircd.Target;
import org.pastiche.ircd.Mask;

public class NickUserHostMask extends Mask {
	private String nickPart = null;
	private String userPart = null;
	private String hostPart = null;
	private String normalisedMask = null;
public NickUserHostMask(String mask) {
	int bangLocation = mask.indexOf('!');
	int atLocation = mask.indexOf('@');
	
	nickPart = collapseMaskPart(org.pastiche.ircd.IrcdConfiguration.getInstance().getUserNormalizer().normalise(mask.substring(0, bangLocation)));
	userPart = collapseMaskPart(mask.substring(bangLocation + 1, atLocation).toLowerCase());
	hostPart = collapseMaskPart(mask.substring(atLocation + 1).toLowerCase());
	this.normalisedMask = nickPart + "!" + userPart + "@" + hostPart;
}
public boolean equals(Object o) {
	if (this == o)
		return true;

	if ((o instanceof NickUserHostMask)) {
		return (((NickUserHostMask)o).normalisedMask.equals(normalisedMask));
	}
	
	return false;
}
public String getHostPart() {
	return hostPart;
}
public String getNickPart() {
	return nickPart;
}
public String getUserPart() {
	return userPart;
}
public int hashCode() {
	return normalisedMask.hashCode();
}
public boolean match(Mask mask) {
	if (mask.getClass() != this.getClass())
		return false;

	return wildcardMatch(normalisedMask, ((NickUserHostMask)mask).normalisedMask);
}
public boolean match(Target target) {
	if (!(target instanceof RegisteredUser))
		return false;

	return wildcardMatch(nickPart, org.pastiche.ircd.IrcdConfiguration.getInstance().getUserNormalizer().normalise(((RegisteredUser)target).getNick())) &&
		wildcardMatch(userPart, ((RegisteredUser)target).getUsername().toLowerCase()) && 
		wildcardMatch(hostPart, ((RegisteredUser)target).getHostname().toLowerCase());
}
public String toString() {
	return normalisedMask;
}
}
