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
 * <p>A Connection handles the actual sockety things to do with
 * sending and receiving stuff.
 */
public class Connection implements Runnable {
	public static final byte[] END_OF_LINE = { (byte)'\r', (byte)'\n' };
	private String disconnectMessage = "Connection reset by peer";
	private ConnectedTarget owner;
	private java.net.Socket socket;
	private java.io.BufferedOutputStream out;
	private boolean disconnect = false;




public void run() {
	try {
		String line;
		java.io.BufferedReader in =
			new java.io.BufferedReader(
				new java.io.InputStreamReader(socket.getInputStream()));

		while (((line = in.readLine()) != null) && !disconnect) {
			System.out.println("Rec: " + line);
			getOwner().processCommand(line);
		}
		System.out.println("User disconnected. user = "+getOwner ().getName ());
		getOwner().doDisconnect(disconnectMessage);
	} catch (java.io.IOException ioe) {
		System.out.println("IOException in user "+getOwner ().getName ()+": " + ioe);
		getOwner().doDisconnect(ioe);
	} finally {
		try {
			socket.close();
		} catch (java.io.IOException ioe) {
		}
	}
}

/**
 * Shouldn't be called except from within ConnectedTarget.
 */
protected void send(String message) {
	try {
      if (!disconnect)
         {
   		out.write(message.getBytes(), 0, message.length());
   		out.write(END_OF_LINE, 0, END_OF_LINE.length);
   		out.flush();
         }
	} catch (java.io.IOException ioe) {
		disconnect = true;
	}
}





public void disconnect(String disconnectMessage) {
	this.disconnectMessage = (disconnectMessage == null) ? "" : disconnectMessage;
	disconnect = true;
}

	public Connection(ConnectedTarget owner, java.net.Socket socket) {
		this.socket = socket;
		this.owner = owner;

		try {
			this.out = new java.io.BufferedOutputStream(
				socket.getOutputStream());
		} catch (java.io.IOException ioe) {
			System.out.println("Fark");
		}
	}

	public ConnectedTarget getOwner() {
		return owner;
	}

public boolean isDisconnected() {
	return disconnect;
}

public void quietKill() {
	try {
		disconnect = true;
		socket.close();
	} catch (Exception e) {}
}

	public void setOwner(ConnectedTarget owner) {
		this.owner = owner;
	}
}
