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
public class SendCommand extends EndPointProcessor {
   
   public SendCommand (Server server) {
      super(server);
   }

   @Override
   public HttpResult process(String uri, String data) {
      JSONObject json = parseJSON(data);
      
      if (json == null) {
         return new HttpResult(400);
      }
      
      String id = (String) json.get("sessionid");
      String cmd = (String) json.get("command");
      String postfix = (String) json.get("postfix");
      JSONArray parms = (JSONArray) json.get("parameters");
      
      if (id == null || cmd == null) {
         return new HttpResult(403);
      }

      Session session = SessionManagement.getInstance().getSession(id);
      
      if (session == null || parms == null) {
         return new HttpResult(403);
      }
      
      StringBuilder line = new StringBuilder();
      
      line.append(cmd);
      
      for (Object obj : parms) {
         line.append(" ");
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
