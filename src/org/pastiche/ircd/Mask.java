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
 *
 */
 
public abstract class Mask {
protected String collapseMaskPart(String mask) {
	StringBuffer collapsedMask = new StringBuffer(mask.length());
	org.pastiche.ircd.NameNormalizer normaliser = org.pastiche.ircd.IrcdConfiguration.getInstance().getUserNormalizer();
	boolean inStarRun = false;
	
	for (int i = 0; i < mask.length(); i++) {
		char c = mask.charAt(i);
		if (c == '*') {
			if (!inStarRun) collapsedMask.append('*');
			inStarRun = true;
			continue;
		}

		if (c == '?') {
			collapsedMask.append('?');
			continue;
		}

		collapsedMask.append(normaliser.normalise(mask.charAt(i)));
		inStarRun = false;
	}
	
	return collapsedMask.toString();
}
public boolean equals(Object o) {
	if (this == o)
		return true;

	if (this.getClass() == o.getClass())
		return true;
		
	return false;
}
public abstract boolean match(Mask mask);
public abstract boolean match(org.pastiche.ircd.Target target);
/** 
 * <p><b>FIXME</b> Really needs reworking, written during brain-death
 * <p>Written with the match() function from the old DALnet ircd in front
 * of me, but JoelKatz (David Schwartz) &lt;djls@gate.net&gt; and Barubary
 * are not responsible for the mess I made of their idea.
 */
protected boolean wildcardMatch(String mask, String target) {
	if (mask.equals("*")) return true;

	int targetOffset = 0;
	int maskOffset = 0;
	boolean wild = false;
	boolean escape = false;
	
	while ((maskOffset < mask.length()) && (targetOffset < target.length())) {
		if (!escape && (mask.charAt(maskOffset) == '?')) {
			targetOffset++;
			maskOffset++;
			escape = false;
			continue;
		}

		if (!escape && (mask.charAt(maskOffset) == '*')) {
			maskOffset++;
			wild = true;
			escape = false;
			continue;
		}

		escape = false;
		
		if (mask.charAt(maskOffset) == '\\') {
			escape = true;
		}
		
		if (mask.charAt(maskOffset) != target.charAt(targetOffset)) {
			if (wild) {
				targetOffset++;
				continue;
			}
			
			return false;
		}

		if (wild) {
			if (wildcardMatch(mask.substring(maskOffset), target.substring(targetOffset))) {
				return true;
			}

			targetOffset++;
			continue;
		}

		targetOffset++;
		maskOffset++;		
	}

	if (maskOffset == mask.length() && (wild || targetOffset == target.length())) {
		return true;
	}
	
	return false;
}
}
