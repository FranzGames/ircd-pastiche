/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pastiche.ircd.http.endpoint;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.pastiche.ircd.http.EndPointProcessor;
import org.pastiche.ircd.http.HttpResult;
import org.pastiche.ircd.http.Session;
import org.pastiche.ircd.http.SessionManagement;
import org.json.simple.JSONObject;
import org.pastiche.ircd.Authenticator;
import org.pastiche.ircd.CollisionException;
import org.pastiche.ircd.IrcdConfiguration;
import org.pastiche.ircd.http.HttpConnection;
import org.pastiche.ircd.http.Server;
import org.pastiche.ircd.rfc1459.RegisteredUser;

/**
 *
 * @author pfranz
 */
public class Connect extends EndPointProcessor {

   public Connect(Server server) {
      super(server);
   }

   @Override
   public HttpResult process(String uri, String data) {
      try {
         JSONObject json = parseJSON(data);

         if (json == null) {
            return new HttpResult(400);
         }

         String nick = (String) json.get("nick");
         String realname = (String) json.get("name");
         String password = (String) json.get("pass");

         if (nick == null || password == null) {
            return new HttpResult(403);
         }

         Authenticator authenticator = IrcdConfiguration.getInstance().getNickPasswordAuthenticator();

         RegisteredUser user = new RegisteredUser(getHttpServer().getIrcServer(),
                 new HttpConnection(null), nick, "localhost", nick, realname);

         if (authenticator != null) {
            if (!authenticator.authenicate(user, nick, password)) {
               return new HttpResult(403);
            }
         }

         Session session = SessionManagement.getInstance().createSession();

         session.setAttribute("nick", nick);
         session.setAttribute("realname", realname);
         session.setAttribute("pass", password);

         getHttpServer().getIrcServer().addUser(nick, user);

         user.sendWelcomeMessages();

         session.setTarget(user);

         return new HttpResult(200, "application/json", "{ \"sessionid\": \"" + session.getSessionId() + "\"}");
      } catch (CollisionException ex) {
         Logger.getLogger(Connect.class.getName()).log(Level.SEVERE, null, ex);
         return new HttpResult(501);
      }

   }

}
