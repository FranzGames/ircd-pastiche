package org.pastiche.ircd.debug;

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
 * <p>An interface that contains a single constant to turn debugging
 * on or off. When you change this, you MUST recompile all the
 * classes that depend on it, or Bad Things Will Happen. Turning this
 * on will likely guarantee that the code won't work outside Charles'
 * PC.
 */
public interface Debug {
	public static final boolean DEBUG = true;
}