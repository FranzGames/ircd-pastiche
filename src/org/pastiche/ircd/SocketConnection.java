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
public class SocketConnection extends Connection implements Runnable {

   private java.net.Socket socket;
   private SendQueuer sendQueuer;

   public void run() {
      try {
         sendQueuer.setName(Thread.currentThread().getName() + " Send Queue");

         String line;
         java.io.BufferedReader in
            = new java.io.BufferedReader(
               new java.io.InputStreamReader(socket.getInputStream()));

         while (((line = in.readLine()) != null) && !this.isDisconnected()) {
            // System.out.println("Rec: " + line);
            try {
               processCommand(line);
            } catch (Throwable t) {
               System.out.println("Problem in thread " + Thread.currentThread().getName() + " user = " + getOwner().getName());
               t.printStackTrace();
            }
         }
         System.out.println("Disconnect in thread " + Thread.currentThread().getName());
         System.out.println("User disconnected. Time = " + (new Date()) + " user = " + getOwner().getName());
         getOwner().doDisconnect(this.getDisconnectMessage());
      } catch (java.io.IOException ioe) {
         System.err.println("Exception in thread " + Thread.currentThread().getName() + " Time = " + (new Date()));
         System.out.println("IOException in user " + getOwner().getName() + ": " + ioe);
         getOwner().doDisconnect(ioe);
      } catch (Throwable thr) {
         System.err.println("Exception in thread " + Thread.currentThread().getName() + " Time = " + (new Date()));
         System.out.println("Generic Exception in user " + getOwner().getName() + ": " + thr);
         getOwner().doDisconnect(thr.toString());
      } finally {
         try {
            // Stop the Queuer thread.

            if (sendQueuer != null) {
               sendQueuer.disconnect();
            }

            if (socket != null) {
               socket.close();
            }
         } catch (java.io.IOException ioe) {
         }
      }
   }

   /**
    * Shouldn't be called except from within ConnectedTarget.
    */
   protected void send(String message) {
      if (!this.isDisconnected()) {
         sendQueuer.addMessageToQueue(message);
      }
   }

   protected void sendPriority(String message) {
      if (!this.isDisconnected()) {
         sendQueuer.addPriorityMessageToQueue(message);
      }
   }

   public SocketConnection(ConnectedTarget owner, java.net.Socket socket) {
      super (owner);
      this.socket = socket;

      try {
         socket.setSoTimeout(IrcdConfiguration.getInstance().getDeadClientTimeout() * 1000);
      } catch (java.net.SocketException se) {
         se.printStackTrace();
      }

      try {
         // Add thread for sending so that one slow connection will not
         // slow the rest of the connections.
         sendQueuer = new SendQueuer(new java.io.BufferedOutputStream(
            socket.getOutputStream()));
         sendQueuer.start();
      } catch (java.io.IOException ioe) {
         ioe.printStackTrace();
         System.out.println("This is bad");
      }
   }

   public void quietKill() {
      try {
         super.quietKill();
         if (socket != null) {
            socket.close();
         }
      } catch (Exception e) {
      }
   }
}
