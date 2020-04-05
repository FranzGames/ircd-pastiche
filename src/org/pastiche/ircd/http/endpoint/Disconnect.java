/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pastiche.ircd.http.endpoint;

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
public class Disconnect extends EndPointProcessor {

   public Disconnect (Server server) {
      super(server);
   }
   
   @Override
   public HttpResult process(String uri, String data) {
      JSONObject json = parseJSON(data);
      
      if (json == null) {
         return new HttpResult(400);
      }
      
      String id = (String) json.get("sessionid");
      String password = (String) json.get("pass");
      
      if (id == null || password == null) {
         return new HttpResult(403);
      }

      Session session = SessionManagement.getInstance().getSession(id);
      
      if (session == null) {
         return new HttpResult(403);
      }
      
      if (!password.equals(session.getAttribute("pass"))) {
         return new HttpResult(403);
      }
      
      SessionManagement.getInstance().removeSession(id);
      
      return new HttpResult (200);
   }
   
}
