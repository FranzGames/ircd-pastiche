package org.pastiche.ircd;

import java.net.ServerSocket;
import java.net.Socket;

/**
 * <p>A Listener is responsible for holding open a ServerSocket,
 * and redirecting any incoming connections to the right objects.
 * There will be one Listener thread for each port that the server
 * listens on.
 */
public class Listener implements Runnable {
	private String bindHost = null;
	private int port = 6667;
	private int backlog = 50;
   public long connNo = 0;

	public String toString() {
		return ((bindHost != null) ? bindHost : "*") + ":" + port;
	}

public void run() {
	ServerSocket sock = null;
	try {
		if (bindHost == null) {
			sock = new ServerSocket(port, backlog);
		} else {
			sock =
				new ServerSocket(port, backlog, java.net.InetAddress.getByName(bindHost));
		}
	} catch (java.net.UnknownHostException uhe) {
		// FIXME: Alert scheme, remove Listener from configuration.
		System.out.println("Could not find bindHost for Listener: " + bindHost);
	} catch (java.io.IOException ioe) {
		// FIXME: Alert scheme, remove Listener from configuration.
		System.out.println("Could not bind Listener to address: " + this);
		System.out.println(ioe);
	}

	while (sock != null) {
		try {
			Socket conn = sock.accept();
         connNo++;
			ConnectedTarget client = ConnectedTarget.newConnectedClient(server, conn);
			new Thread(client.getConnection(), "Connection #"+connNo).start();
		} catch (java.io.IOException ioe) {
			// FIXME: Alert scheme, remove Listener from configuration.
			System.out.println("Could not accept connection on Listener " + this);
			System.out.println(ioe);
			break;
		}

	}
}

	private static final int DEFAULT_BACKLOG = 50;
	private Server server = null;

public Listener(Server server, int port) {
	this(server, port, null);
}

public Listener(Server server, int port, int backlog, String bindHost) {
	this.port = port;
	this.bindHost = bindHost;
	this.backlog = backlog;
	this.server = server;
}

public Listener(Server server, int port, String bindHost) {
	this(server, port, DEFAULT_BACKLOG, bindHost);
}
}
