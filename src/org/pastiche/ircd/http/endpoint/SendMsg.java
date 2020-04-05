/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pastiche.ircd.http.endpoint;

import org.json.simple.JSONArray;
import org.pastiche.ircd.http.EndPointProcessor;
import org.pastiche.ircd.http.HttpResult;
import org.pastiche.ircd.http.Session;
import org.pastiche.ircd.http.SessionManagement;
import org.json.simple.JSONObject;
import org.pastiche.ircd.http.Server;

/**
 *
 * @author pfranz
 */
public class SendMsg extends EndPointProcessor {
   
   public SendMsg (Server server) {
      super(server);
   }

   @Override
   public HttpResult process(String uri, String data) {
      JSONObject json = parseJSON(data);
      
      if (json == null) {
         return new HttpResult(400);
      }
      
      String id = (String) json.get("sessionid");
      String postfix = (String) json.get("msg");
      JSONArray parms = (JSONArray) json.get("targets");
      
      if (id == null) {
         return new HttpResult(403);
      }

      Session session = SessionManagement.getInstance().getSession(id);
      
      if (session == null || parms == null) {
         return new HttpResult(403);
      }
      
      StringBuilder line = new StringBuilder();
      
      line.append("privmsg ");
      
      boolean first = true;
      
      for (Object obj : parms) {
         if (first) {
            first = false;
         } else {
            line.append(",");
         }
         line.append((String) obj);
      }
      
      if (postfix != null && !postfix.isEmpty()) {
         line.append(" :");
         line.append(postfix);
      }
      
      session.updateLastAccess();
      session.getTarget().processCommand (line.toString());
      
      return new HttpResult (200, "application/json", "{\"result\": \"success\"}");
   }
   
}
