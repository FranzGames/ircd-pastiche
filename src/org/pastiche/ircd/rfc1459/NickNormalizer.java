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
 
/**
 * <p>This class breaks rfc1459, in favour of rfc2812. rfc1459's rules on allowed nickname
 * characters, and character equivalence are internally inconsistent, and not implemented on
 * any network or client anywhere, afaik.
 */
public class NickNormalizer implements org.pastiche.ircd.NameNormalizer {
	public static final int MAX_NICK_LENGTH = 9;
	public static final char[] SPECIAL_CHARS = {'-', '[', ']', '\\', '`', '^', '{', '}', '~', '|', '_'};
	public static final char[] UPPER_CASE_SPECIAL_CHARS = {'[', ']', '\\', '~'};
	public static final char[] LOWER_CASE_SPECIAL_CHARS = {'{', '}', '|', '^'};
/**
 * isValidName method comment.
 */
public boolean isValidName(String name) {
	if (name.length() > MAX_NICK_LENGTH)
		return false;

	if (name.charAt(0) == '-' ||
		Character.isDigit(name.charAt(0)))
		return false;
		
	for (int i = 0; i < name.length(); i++) {
		if (!isValidNickChar(name.charAt(i)))
			return false;
	}

	return true;
}
private boolean isValidNickChar(char character) {
	if (Character.isLetterOrDigit(character))
		return true;

	boolean isSpecialChar = false;
	
	for (int i = 0; i < SPECIAL_CHARS.length; i++) {
		if (character == SPECIAL_CHARS[i])
			isSpecialChar = true;
	}
			
	return isSpecialChar;
}
public char normalise(char c) {
	if (Character.isDigit(c))
		return c;

	if (Character.isLetter(c))
		return Character.toLowerCase(c);

	for (int i = 0; i < UPPER_CASE_SPECIAL_CHARS.length; i++) {
		if (c == UPPER_CASE_SPECIAL_CHARS[i]) {
			return LOWER_CASE_SPECIAL_CHARS[i];
		}
	}

	return c;
}
/**
 * normalise method comment.
 */
public String normalise(String name) {
	StringBuffer buf = new StringBuffer(name.length());

	for (int i = 0; i < name.length(); i++) {
		buf.append(normalise(name.charAt(i)));
	}

	return buf.toString();
}
}
