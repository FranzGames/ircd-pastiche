/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pastiche.ircd.http;

import org.pastiche.ircd.ConnectedTarget;
import org.pastiche.ircd.Connection;
import org.pastiche.ircd.Queue;

/**
 *
 * @author pfranz
 */
public class HttpConnection extends Connection {
   static final String PING = "PING ";
   
   Queue queue = new Queue();
   
   public HttpConnection (ConnectedTarget owner) {
      super(owner);
   }

   @Override
   protected void send(String message) {
      queue.push(message);
   }

   @Override
   protected void sendPriority(String message) {
      if (message.startsWith(PING)){
         String pong = "PONG :"+message.substring(PING.length());
         getOwner().processCommand(pong);
      }
      queue.push(message, true);
   }
   
   public Queue getSendQueue () {
      return queue;
   }
}
