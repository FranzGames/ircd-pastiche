package org.pastiche.ircd;

import java.net.Socket;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.BufferedOutputStream;
import java.io.IOException;

/**
 * <p>A ConnectedTarget is some target that is directly connected to
 * the server. This includes local users, locally linked servers, but
 * <em>not</em> local channels.
 */
public abstract class ConnectedTarget implements Target {
	private Connection connection = null;




public void processCommand(String line) {
	Command command = getCommandFactory().createCommand(line);
	if (command != null)
	   {
		command.setImmediateSource(this);
		command.preProcess();
		if (command.requiresProcess())
		   {
			CommandQueue.getInstance().add(command);
		   }

		if (command.isIdleCountResetter())
		   {
			resetIdleCount();
		   }

		resetLastCommandTime();
	   }
}

protected void send(String message) {
	if (!connection.isDisconnected()) {
//		System.out.println("Sending to " + getName() + ": " + message);
		connection.send(message);
	}
}

protected void sendPriority(String message) {
	if (!connection.isDisconnected()) {
//		System.out.println("Sending Priority to " + getName() + ": " + message);
		connection.sendPriority(message);
	}
}

   public void ping ()
      {
      sendPriority ("PING :"+IrcdConfiguration.getInstance().getServerName());
      }

	public abstract CommandFactory getCommandFactory();
	protected abstract void doDisconnect(String reason);
	protected abstract void doDisconnect(Exception error);

	private long lastCommandTime = 0;
	private long lastUnidleTime = 0;
	private Server server = null;
	private java.util.Date signonTime = null;

private ConnectedTarget(Server server) {
	this.server = server;
	this.lastUnidleTime = System.currentTimeMillis();
	this.lastCommandTime = System.currentTimeMillis();
	this.signonTime = new java.util.Date();
}

public ConnectedTarget(Server server, Socket socket) {
	this(server);
	this.connection = new Connection(this, socket);
}

public ConnectedTarget(Server server, Connection connection) {
	this(server);
	this.connection = connection;
}

public boolean canSend(Target source) {
   if (source == this)
      System.out.println ("Can you send to yourself");

//   System.out.println ("canSend: this.getName () = "+getName ()+" source.name = "+getName ());
	return true;
}

public Connection getConnection() {
	return connection;
}

public int getSecondsIdle() {
	return (int) ((System.currentTimeMillis() - lastUnidleTime) / 1000);
}

public Server getServer() {
	return server;
}

public java.util.Date getSignonTime() {
	return signonTime;
}

	public abstract boolean isDead();

	public abstract boolean isIdle();

	public abstract boolean isAway();

public static ConnectedTarget newConnectedClient(Server server, Socket conn) {
	ConnectedTarget target = null;
	try {
		target = (ConnectedTarget)
			IrcdConfiguration.getInstance().getInitialConnectionClass().getConstructor(new Class[] {Server.class, Socket.class}).newInstance(new Object[] {server, conn});
      server.newConnectedTarget (target);
	} catch (NoSuchMethodException nsme) {
		// FIXME: BIG CRAZY ERROR CONDITION.
	} catch (java.lang.reflect.InvocationTargetException ite) {
		// FIXME: BIG CRAZY ERROR CONDITION.
	} catch (InstantiationException ie) {
		// FIXME: BIG CRAZY ERROR CONDITION.
	} catch (IllegalAccessException iae) {
		// FIXME: BIG CRAZY ERROR CONDITION.
	}
	return target;
}

public void resetIdleCount() {
	this.lastUnidleTime = System.currentTimeMillis();
}

public void resetLastCommandTime() {
	this.lastCommandTime = System.currentTimeMillis();
}

public void send(Target source, java.lang.String commandLine) {
	send(":" + source.getLongName() + " " + commandLine);
}

public void setConnection(Connection connection) {
	this.connection = connection;
}
}
