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
 * <p>This class is responsible for starting up the ircd. It is
 * just a main method - it initialises the IrcdConfiguration
 * object. Then it starts up any Listener threads defined in
 * the configuration, and the server is started.
 */
public class Ircd {
public static int port = 6667;
public static String server = "localhost";

public static void main(String[] args) {
	System.out.print("Loading configuration...");

	try {
		IrcdConfiguration.getInstance().checkConfiguration();
	} catch (ConfigurationException ce) {
		System.out.println("failed");
		System.out.println(ce.getMessage());
		System.exit(127);
	}

	System.out.println("done.");
	System.out.println("Starting listeners:");

	for (int i = 0; i < IrcdConfiguration.getInstance().getListeners().length; i++) {
		Listener l = IrcdConfiguration.getInstance().getListeners()[i];
		System.out.print(l + "...");
		new Thread(IrcdConfiguration.getInstance().getListeners()[i], "Connection Listener").start();
		System.out.println("done.");
	}
}
}
