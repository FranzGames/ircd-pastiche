package org.pastiche.ircd.rfc1459;

import org.pastiche.ircd.IrcMessage;
import java.net.Socket;

import org.pastiche.ircd.Server;
import org.pastiche.ircd.Command;
import org.pastiche.ircd.CommandFactory;
import org.pastiche.ircd.CommandQueue;
import org.pastiche.ircd.ConnectedTarget;
import org.pastiche.ircd.Connection;
import org.pastiche.ircd.Target;

/**
 * An UnregisteredClient is a local connection that has not yet registered to
 * either be a user or a server.
 */
public class UnregisteredClient extends ConnectedTarget {

   public static final String COMMAND_FACTORY_ID = "unregistered";
   private String nick = null;
   private String passwd = null;
   private String username = null;

   public CommandFactory getCommandFactory() {
      return CommandFactory.getCommandFactory(COMMAND_FACTORY_ID);
   }

   public void doDisconnect(String reason) {
      if (getConnection().isDisconnected() == false) {
         getConnection().quietKill();
      }

      if (nick != null) {
         CommandQueue.getInstance().add(disconnect);
      }
   }

   public void doDisconnect(Exception error) {
      doDisconnect(error.getMessage());
   }

   private Command disconnect = new Command() {
      public void process() {
         getServer().removeUser(UnregisteredClient.this);
      }
   };
   private String hostname = null;
   private String ircname = null;
   public static final int MAX_MESSAGE_SIZE = 512;
   private java.util.Date timeConnected;

   /**
    * The connection notices in this constructor are not specifically rfc1459.
    * But they're not disallowed by it either, so there.
    */
   public UnregisteredClient(Server server, Socket socket) {
      super(server, socket);
      this.timeConnected = new java.util.Date();
      TargetIrcMessage msg = new TargetIrcMessage(new IrcMessage("NOTICE", "Looking up your hostname..."), "AUTH");

      super.send(server, msg);
      this.hostname = socket.getInetAddress().getHostName();
      if (Character.isDigit(this.hostname.charAt(this.hostname.length() - 1))) {
         msg = new TargetIrcMessage(new IrcMessage("NOTICE", "Hostname not found."), "AUTH");
         super.send(server, msg);
      } else {
         msg = new TargetIrcMessage(new IrcMessage("NOTICE", "Hostname found."), "AUTH");
         super.send(server, msg);
      }
   }

   public UnregisteredClient(Server server, Connection conn, String hostname) {
      super(server, conn);
      this.timeConnected = new java.util.Date();
      TargetIrcMessage msg = new TargetIrcMessage(new IrcMessage("NOTICE", "Looking up your hostname..."), "AUTH");

      super.send(server, msg);
      this.hostname = hostname;
      if (Character.isDigit(this.hostname.charAt(this.hostname.length() - 1))) {
         msg = new TargetIrcMessage(new IrcMessage("NOTICE", "Hostname not found."), "AUTH");
         super.send(server, msg);
      } else {
         msg = new TargetIrcMessage(new IrcMessage("NOTICE", "Hostname found."), "AUTH");
         super.send(server, msg);
      }
   }

   /**
    * Insert the method's description here. Creation date: (2/01/2001 8:21:40
    * PM)
    *
    * @return java.lang.String
    */
   public java.lang.String getHostname() {
      return hostname;
   }

   public Object getIdentifier() {
      return getNick();
   }

   /**
    * Insert the method's description here. Creation date: (2/01/2001 8:21:40
    * PM)
    *
    * @return java.lang.String
    */
   public java.lang.String getIrcname() {
      return ircname;
   }

   public java.lang.String getLongName() {
      return "UNKNOWN";
   }

   public int getMaximumMessageSize(org.pastiche.ircd.Target source) {
      return UnregisteredClient.MAX_MESSAGE_SIZE - source.getLongName().length() - 2;
   }

   /**
    * getName method comment.
    */
   public String getName() {
      if (getNick() == null) {
         return "UNKNOWN";
      }

      return getNick().toString();
   }

   public String getNick() {
      return nick;
   }

   public String getPassword() {
      return passwd;
   }

   /**
    * Insert the method's description here. Creation date: (2/01/2001 6:12:57
    * PM)
    *
    * @return java.lang.String
    */
   public java.lang.String getUsername() {
      return username;
   }

   /**
    * getVisibleLocalTargets method comment.
    */
   @Override
   public java.util.Set<Target> getVisibleLocalTargets() {
      return new java.util.HashSet<Target>();
   }

   public boolean isDead() {
      return new java.util.Date().compareTo(timeConnected) / 1000
              > org.pastiche.ircd.IrcdConfiguration.getInstance().getUnregisteredClientTimeout();
   }

   public boolean isIdle() {
      return false;
   }

   public boolean isAway() {
      return false;
   }

   public void remove() {
   }

   /**
    * Insert the method's description here. Creation date: (2/01/2001 8:21:40
    * PM)
    *
    * @param newHostname java.lang.String
    */
   public void setHostname(String newHostname) {
      hostname = newHostname;
   }

   /**
    * Insert the method's description here. Creation date: (2/01/2001 8:21:40
    * PM)
    *
    * @param newIrcname java.lang.String
    */
   public void setIrcname(java.lang.String newIrcname) {
      ircname = newIrcname;
   }

   public void setNick(String nick) {
      this.nick = nick;
   }

   public void setPassword(String pass) {
      this.passwd = pass;
   }

   /**
    * Insert the method's description here. Creation date: (2/01/2001 6:12:57
    * PM)
    *
    * @param newUsername java.lang.String
    */
   public void setUsername(String newUsername) {
      username = newUsername;
   }
}
