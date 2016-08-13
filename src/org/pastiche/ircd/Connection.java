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

import java.util.Date;

/**
 * <p>A Connection handles the actual sockety things to do with
 * sending and receiving stuff.
 */
public class Connection implements Runnable {
	public static final byte[] END_OF_LINE = { (byte)'\r', (byte)'\n' };
	private String disconnectMessage = "Connection reset by peer";
	private ConnectedTarget owner;
	private java.net.Socket socket;
	private SendQueuer sendQueuer;
	private boolean disconnect = false;




public void run() {
	try {
      sendQueuer.setName (Thread.currentThread ().getName ()+" Send Queue");

		String line;
		java.io.BufferedReader in =
			new java.io.BufferedReader(
				new java.io.InputStreamReader(socket.getInputStream()));

		while (((line = in.readLine()) != null) && !disconnect) {
			// System.out.println("Rec: " + line);
         try
            {
			   getOwner().processCommand(line);
            }
         catch (Throwable t)
            {
            System.out.println ("Problem in thread "+Thread.currentThread ().getName ()+" user = "+getOwner ().getName ());
            t.printStackTrace ();
            }
		}
      System.out.println ("Disconnect in thread "+Thread.currentThread ().getName ());
		System.out.println("User disconnected. Time = "+(new Date())+" user = "+getOwner ().getName ());
		getOwner().doDisconnect(disconnectMessage);
	} catch (java.io.IOException ioe) {
      System.err.println ("Exception in thread "+Thread.currentThread ().getName ()+" Time = "+(new Date()));
		System.out.println("IOException in user "+getOwner ().getName ()+": " + ioe);
		getOwner().doDisconnect(ioe);
   } catch (Throwable thr) {
      System.err.println ("Exception in thread "+Thread.currentThread ().getName ()+" Time = "+(new Date()));
		System.out.println("Generic Exception in user "+getOwner ().getName ()+": " + thr);
		getOwner().doDisconnect(thr.toString());
	} finally {
		try {
         // Stop the Queuer thread.

         if (sendQueuer != null)
            sendQueuer.disconnect ();

			socket.close();
		} catch (java.io.IOException ioe) {
		}
	}
}

/**
 * Shouldn't be called except from within ConnectedTarget.
 */
protected void send(String message)
   {
   if (!disconnect)
      sendQueuer.addMessageToQueue (message);
   }

protected void sendPriority(String message)
   {
   if (!disconnect)
      sendQueuer.addPriorityMessageToQueue (message);
   }

public void disconnect(String disconnectMessage) {
	this.disconnectMessage = (disconnectMessage == null) ? "" : disconnectMessage;
	disconnect = true;
}

	public Connection(ConnectedTarget owner, java.net.Socket socket) {
		this.socket = socket;
		this.owner = owner;

      try
         {
         socket.setSoTimeout (IrcdConfiguration.getInstance().getDeadClientTimeout() * 1000);
         }
		catch (java.net.SocketException se) {
         se.printStackTrace ();
		}

		try {
         // Add thread for sending so that one slow connection will not
         // slow the rest of the connections.
         sendQueuer = new SendQueuer (new java.io.BufferedOutputStream(
				socket.getOutputStream()));
         sendQueuer.start ();
		} catch (java.io.IOException ioe) {
         ioe.printStackTrace ();
			System.out.println("This is bad");
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
