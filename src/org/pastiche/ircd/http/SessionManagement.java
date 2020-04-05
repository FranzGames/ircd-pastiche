/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pastiche.ircd.http;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author pfranz
 */
public class SessionManagement {
   static private SessionManagement _inst;
   
   int sessionNum = 0;
   long sessionTimeout = 120000;
   
   Map<String, Session> sessions = new HashMap();
   Timer timeoutTimer = new Timer();
   
   public static SessionManagement getInstance() {
      if (_inst == null) {
         _inst = new SessionManagement();
      }
      
      return _inst;
   }
   
   public SessionManagement() {
      // Schedule session timeout check for once a second.
      
      //Turnoff session timeout: timeoutTimer.schedule(new SessionTimoutTask () , 1000, 1000);
   }
   
   public Session getSession(String id) {
      return sessions.get(id);
   }
   
   public Session removeSession (String id) {
      Session session = sessions.get(id);
      session.getTarget().processCommand("QUIT");
      session.getTarget().getServer().removeUser(session.getTarget()); //When the session times out remove the user.
      return sessions.remove(id);
   }
   
   // TODO: Replace with a more secure id
   protected String generateId () {
      return "session"+(sessionNum++);
   }
   
   public Session createSession () {
      Session session = new Session (generateId());
      
      sessions.put(session.getSessionId(), session);
      
      return session;
   }
   
   protected void removeTimedOutSessions () {
      Iterator<String> iter = sessions.keySet().iterator();
      long currentTime = System.currentTimeMillis();
      
      while (iter.hasNext()) {
         String id = iter.next();
         Session session = getSession(id);
         
         if (currentTime - session.lastAccessTime() > this.sessionTimeout) {
            System.out.println ("Session Timed Out: Remove Session id: "+id);
            this.removeSession(id);
         }
      }
   }
   
   class SessionTimoutTask extends TimerTask {

      @Override
      public void run() {
         getInstance().removeTimedOutSessions();
      }
   }
   
}
