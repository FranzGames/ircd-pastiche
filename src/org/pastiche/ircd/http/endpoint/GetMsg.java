/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pastiche.ircd.http.endpoint;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import org.pastiche.ircd.http.EndPointProcessor;
import org.pastiche.ircd.http.HttpResult;
import org.pastiche.ircd.http.Session;
import org.pastiche.ircd.http.SessionManagement;
import org.json.simple.JSONObject;
import org.pastiche.ircd.IrcMessage;
import org.pastiche.ircd.Queue;
import org.pastiche.ircd.http.HttpConnection;
import org.pastiche.ircd.http.Message;
import org.pastiche.ircd.http.Server;

/**
 *
 * @author pfranz
 */
public class GetMsg extends EndPointProcessor {
   
   public GetMsg (Server server) {
      super(server);
   }


   @Override
   public HttpResult process(String uri, String data) {
      JSONObject json = parseJSON(data);
      
      if (json == null) {
         return new HttpResult(400);
      }
      
      String id = (String) json.get("sessionid");
      
      if (id == null) {
         return new HttpResult(403);
      }

      Session session = SessionManagement.getInstance().getSession(id);
      
      if (session == null) {
         return new HttpResult(403);
      }
      
      session.updateLastAccess();
      
      HttpConnection conn = (HttpConnection) session.getTarget().getConnection();
      
      StringBuilder buf = new StringBuilder ("{ \"msgs\" : [\r\n");
      
      Queue<IrcMessage> q = conn.getSendQueue();
      boolean first = true;
      
      while (!q.isEmpty()) {
         if (!first) {
            buf.append (",");
         } else {
            first = false;
         }
         
         buf.append(q.pop().toJson());
      }
      
      buf.append("]\r\n");
      buf.append("}\r\n");
      
      return new HttpResult (200, "application/json", buf.toString());
   }
   
}
