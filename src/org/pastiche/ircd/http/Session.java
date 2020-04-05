/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pastiche.ircd.http;

import java.util.HashMap;
import java.util.Map;
import org.pastiche.ircd.ConnectedTarget;

/**
 *
 * @author pfranz
 */
public class Session {
   String id;
   ConnectedTarget target;
   Map<String, Object> attributes = new HashMap();
   long lastAccess;
   
   public Session(String id) {
      this.id = id;
      updateLastAccess();
   }
   
   public void setTarget (ConnectedTarget t) {
      this.target = t;
   }
   
   public ConnectedTarget getTarget() {
      return target;
   }
   
   public final void updateLastAccess () {
      lastAccess = System.currentTimeMillis();
   }
   
   public long lastAccessTime() {
      return lastAccess;
   }
   
   public String getSessionId() {
      return id;
   }
   
   public void setAttribute (String attr, Object value) {
      attributes.put(attr, value);
   }
   
   public Object getAttribute (String attr) {
      return attributes.get(attr);
   }
   
   public Object removeAttribute (String attr) {
      return attributes.remove(attr);
   }
}
