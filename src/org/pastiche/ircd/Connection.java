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
 * <p>
 * A Connection handles the actual sockety things to do with sending and
 * receiving stuff.
 */
public abstract class Connection {

   public static final byte[] END_OF_LINE = {(byte) '\r', (byte) '\n'};
   private String disconnectMessage = "Connection reset by peer";
   private ConnectedTarget owner;
   private boolean disconnect = false;

   /**
    * Shouldn't be called except from within ConnectedTarget.
    */
   protected abstract void send(String message);
   protected abstract void sendPriority(String message);

   public void disconnect(String disconnectMessage) {
      this.disconnectMessage = (disconnectMessage == null) ? "" : disconnectMessage;
      disconnect = true;
   }
   
   public void processCommand (String line) {
      getOwner().processCommand(line);
   }
   
   public String getDisconnectMessage () {
      return this.disconnectMessage;
   }

   public Connection(ConnectedTarget owner) {
      this.owner = owner;
   }

   public ConnectedTarget getOwner() {
      return owner;
   }

   public boolean isDisconnected() {
      return disconnect;
   }

   public void quietKill() {
      disconnect = true;
   }

   public void setOwner(ConnectedTarget owner) {
      this.owner = owner;
   }
}
