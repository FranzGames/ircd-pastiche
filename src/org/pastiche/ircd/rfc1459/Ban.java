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

import org.pastiche.ircd.Mask;

public class Ban {
	private Mask mask = null;
	private String setBy = null;
public Ban(Mask mask, String setBy) {
	this.mask = mask;
	this.setBy = setBy;
}
public boolean equals(Object o) {
	if (this == o)
		return true;

	if ((o instanceof Ban) && (this.mask.equals(((Ban)o).mask)))
		return true;

	return false;
}
public Mask getMask() {
	return mask;
}
public String getSetBy() {
	return setBy;
}
public int hashCode() {
	return mask.hashCode();
}
public boolean matches(Mask mask) {
	return mask.match(mask);
}
public boolean matches(RegisteredUser user) {
	return mask.match(user);
}
}
