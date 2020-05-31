/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pastiche.ircd.http;

import org.pastiche.ircd.ConnectedTarget;
import org.pastiche.ircd.Connection;
import org.pastiche.ircd.IrcMessage;
import org.pastiche.ircd.Queue;

/**
 *
 * @author pfranz
 */
public class HttpConnection extends Connection {

   static final String PING = "PING";

   Queue<IrcMessage> queue = new Queue();

   public HttpConnection(ConnectedTarget owner) {
      super(owner);
   }

   @Override
   protected void send(IrcMessage message) {
      //Filter Out the PING messages
      if (!message.getCommand().equals(PING)) {
         queue.push(message);
      }
   }

   @Override
   protected void sendPriority(IrcMessage message) {

      if (message.getCommand().equals(PING)) {
         String pong = "PONG :" + message.createIrcMessage().substring(PING.length() + 1); //Remove the space after the PING
         getOwner().processCommand(pong);
      } else {
         //Filter Out the PING messages
         queue.push(message, true);
      }
   }

   public Queue<IrcMessage> getSendQueue() {
      return queue;
   }
   
   public void disconnect () {
      getOwner().doDisconnect("disconnect");
   }
}
