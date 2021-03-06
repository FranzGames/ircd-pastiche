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
import org.pastiche.ircd.IrcMessage;
import org.pastiche.ircd.Connection;
import org.pastiche.ircd.Server;
import org.pastiche.ircd.Target;

/**
 * After an UnregisteredClient has supplied a USER and NICK command, it is
 * passed to the constructor of the ConnectedUser.
 */
public class RegisteredUser extends org.pastiche.ircd.ConnectedTarget {

   public static final String COMMAND_FACTORY_ID = "connectedUser";
   private String nick = null;
   private String username = null;
   private String hostname = null;
   private String realName = null;
   private String awayMsg = null;
   private java.util.Set myChannels = new java.util.HashSet();
   private String disconnectReason = null;
   private long lastUnidle = 0;

   private org.pastiche.ircd.Command disconnect = new org.pastiche.ircd.Command() {
      public void process() {
//      Channel[] chans = getChannels ();

         java.util.Iterator<Target> i = getVisibleLocalTargets().iterator();

         /*         if (chans != null)
            {
            for (int j = 0; j < chans.length; j++)
               {
               try
                  {
                  System.out.println ("chan = "+chans[j].getName ());
                  chans[j].remove (RegisteredUser.this);
                  }
               catch (org.pastiche.ircd.NotOnChannelException t)
                  {
                  }
               }
            }*/
         
         IrcMessage msg = new IrcMessage ("QUIT", disconnectReason);
         
         while (i.hasNext()) {
            i.next().send(RegisteredUser.this, msg);
         }

         synchronized (myChannels) {
            i = myChannels.iterator();

            while (i.hasNext()) {
               try {
                  Channel chan = (Channel) i.next();
                  System.out.println("chan = " + chan.getName());
                  chan.remove(RegisteredUser.this);
               } catch (org.pastiche.ircd.NotOnChannelException noce) {
               }
            }
         }

         getServer().removeUser(RegisteredUser.this);
      }
   };

   public RegisteredUser(UnregisteredClient initialConnection) {
      super(initialConnection.getServer(), initialConnection.getConnection());
      initialConnection.remove();

      System.out.println("User connected: " + initialConnection.getNick());

      super.getConnection().setOwner(this);
      this.nick = initialConnection.getNick();
      this.username = initialConnection.getUsername();
      this.hostname = initialConnection.getHostname();
      this.realName = initialConnection.getIrcname();
      this.lastUnidle = System.currentTimeMillis();
      super.setConnection(initialConnection.getConnection());
   }

   public RegisteredUser(Server server, Connection initialConnection, String nick, String hostname,
           String userName, String ircName) {
      super(server, initialConnection);

      System.out.println("User connected: " + nick);

      super.getConnection().setOwner(this);
      this.nick = nick;
      this.username = userName;
      this.hostname = hostname;
      this.realName = ircName;
      this.lastUnidle = System.currentTimeMillis();
   }

   public void addChannel(Channel channel) {
      synchronized (myChannels) {
         myChannels.add(channel);
      }
   }

   /**
    * Does this belong elsewhere?
    */
   public static RegisteredUser convertToRegisteredUser(UnregisteredClient user) {
      Target targ = user.getServer().getUser(user.getName());

      // Has the UnregisterClient become a registered client will on
      // the command queue?
      if (targ != null && targ != user) {
         return (RegisteredUser) targ;
      }

      RegisteredUser registeredUser = new RegisteredUser(user);
      registeredUser.getServer().replaceUser(registeredUser);

      registeredUser.sendWelcomeMessages();
      return registeredUser;
   }

   public void sendWelcomeMessages() {
      ReplyHandler.getInstance().welcome(getServer(), this);
      ReplyHandler.getInstance().yourHost(getServer(), this);
      ReplyHandler.getInstance().created(this);
      ReplyHandler.getInstance().myInfo(getServer(), this);
      processCommand("LUSERS");
      processCommand("MOTD");
   }

   @Override
   public void doDisconnect(Exception error) {
      doDisconnect(error.getMessage());
   }

   @Override
   public void doDisconnect(String reason) {
      if (!getConnection().isDisconnected()) {
         getConnection().quietKill();
      }

      this.disconnectReason = reason;
      org.pastiche.ircd.CommandQueue.getInstance().add(disconnect);
   }

   public Channel[] getChannels() {
      synchronized (myChannels) {
         Channel[] channels = new Channel[myChannels.size()];
         myChannels.toArray(channels);

         return channels;
      }
   }

   public org.pastiche.ircd.CommandFactory getCommandFactory() {
      return org.pastiche.ircd.CommandFactory.getCommandFactory(COMMAND_FACTORY_ID);
   }

   public String getHostname() {
      return hostname;
   }

   public java.lang.Object getIdentifier() {
      return getNick();
   }

   public java.lang.String getLongName() {
      return getNick() + "!" + getUsername() + "@" + getHostname();
   }

   public int getMaximumMessageSize(Target source) {
      return UnregisteredClient.MAX_MESSAGE_SIZE - source.getLongName().length() - 2;
   }

   public java.lang.String getName() {
      return getNick().toString();
   }

   public String getNick() {
      return nick;
   }

   public String getNickUserAtHost() {
      return getNick() + "!" + getUsername() + "@" + getHostname();
   }

   public String getRealName() {
      return realName;
   }

   public String getUsername() {
      return username;
   }

   /**
    * getVisibleLocalTargets method comment.
    */
   @Override
   public java.util.Set<Target> getVisibleLocalTargets() {
      synchronized (myChannels) {
         java.util.Set<Target> targets = new java.util.HashSet();
         java.util.Iterator<Channel> i = myChannels.iterator();

         while (i.hasNext()) {
            targets.add(i.next());
         }
         return targets;
      }
   }

   /**
    * isDead method comment.
    */
   public boolean isDead() {
      return false;
   }

   /**
    * isIdle method comment.
    */
   public boolean isIdle() {
      return false;
   }

   public boolean isAway() {
      return (awayMsg != null);
   }

   public void setAway(String msg) {
      awayMsg = msg;
   }

   public String getAwayMsg() {
      return awayMsg;
   }

   public void part(org.pastiche.ircd.rfc1459.Channel channel, String channelName)
           throws org.pastiche.ircd.NotOnChannelException {

      try {
         IrcMessage msg = new IrcMessage ("PART", channelName);
         
         channel.remove(this, msg);
      } catch (org.pastiche.ircd.NotOnChannelException e) {
         throw e;
      } finally {
         synchronized (myChannels) {
            myChannels.remove(channel);
         }
      }
   }

   public void remove() {
   }

   public void setNick(String newNick) throws org.pastiche.ircd.CollisionException {
      getServer().replaceUser(newNick, this);

      java.util.Iterator<Target> i = getVisibleLocalTargets().iterator();
      IrcMessage msg = new IrcMessage ("NICK");
      msg.addParameter(newNick);
      

      while (i.hasNext()) {
         i.next().send(this, msg);
      }

      send(this, msg);
      this.nick = newNick;
   }
}
