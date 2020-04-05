/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pastiche.ircd.http;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author pfranz
 */
public abstract class EndPointProcessor {
   Server httpServer;
   
   public EndPointProcessor (Server server) {
      httpServer = server;
   }
   
   public Server getHttpServer() {
      return httpServer;
   }
   
   public abstract HttpResult process (String uri, String data);
   
   protected JSONObject parseJSON (String jsonStr) {
      try {
         JSONParser parser = new JSONParser();
         Object obj = parser.parse(jsonStr);
         if (!(obj instanceof JSONObject)) {
            return null;
         }
         return (JSONObject) obj;
      } catch (ParseException ex) {
         Logger.getLogger(EndPointProcessor.class.getName()).log(Level.SEVERE, null, ex);
         
         return null;
      }
   }
}
