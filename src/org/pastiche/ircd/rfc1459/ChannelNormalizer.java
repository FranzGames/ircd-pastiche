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
 
public class ChannelNormalizer implements org.pastiche.ircd.NameNormalizer {
	public static final char[] VALID_CHANNEL_START_CHARS = {'#', '&'};
	public static final char[] BAD_CHANNEL_CHARS = {' ', '\t', '\n', '\r', ','};
public String fixChannelName(String name) {
	if (name.length() == 0)
		return "#how_the_hell_did_you_do_that?";
	
	StringBuffer newName = new StringBuffer(name.length());
	int offset = 1;

	if (java.util.Arrays.binarySearch(VALID_CHANNEL_START_CHARS, name.charAt(0)) == 0) {
		newName.append(VALID_CHANNEL_START_CHARS[0]);
		offset = 0;
	}
	
	for (int i = offset; i < name.length(); i++) {
		if (java.util.Arrays.binarySearch(BAD_CHANNEL_CHARS, name.charAt(i)) == 0)
			newName.append(name.charAt(i));
	}

	return newName.toString();
}
/**
 * isValidName method comment.
 */
public boolean isValidName(String name) {
	if (name.length() == 0)
		return false;

	boolean validFirstChar = false;

	for (int i = 0; i < VALID_CHANNEL_START_CHARS.length; i++) {
		if (name.charAt(0) == VALID_CHANNEL_START_CHARS[i]) {
			validFirstChar = true;
		}
	}

	if (!validFirstChar)
		return false;
		
	for (int i = 0; i < BAD_CHANNEL_CHARS.length; i++) {
		if (name.indexOf(BAD_CHANNEL_CHARS[i]) != -1) {
			return false;
		}
	}

	return true;
}
public char normalise(char character) {
	return Character.toLowerCase(character);
}
public String normalise(String name) {
	return name.toLowerCase();
}
}
