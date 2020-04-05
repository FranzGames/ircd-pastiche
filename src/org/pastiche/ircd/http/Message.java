/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pastiche.ircd.http;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

/**
 *
 * @author pfranz
 */
public class Message {

   String prefix;
   String command;
   List<String> parameters;

   public Message(String msg) throws ParseException {
      StringTokenizer st = new StringTokenizer(msg, " ");
      String t;

      if (!st.hasMoreTokens()) {
         throw (new ParseException("Empty message", 0));
      }
      t = st.nextToken();

      // Let's see if there's a prefix
      if (t.charAt(0) == ':') {
         if (t.length() == 1) {
            throw (new ParseException("Empty prefix", 1));
         }
         prefix = t.substring(1);
         if (!st.hasMoreTokens()) {
            throw (new ParseException("Command expected after prefix", 0));
         }
         t = st.nextToken();
      } else {
         prefix = "";
      }

      // Then we should have received the command
      command = t;
      if (!st.hasMoreTokens()) {
         throw (new ParseException("Parameters expected after command", 0));
      }

      parameters = new ArrayList<String>();
      st = new StringTokenizer(st.nextToken(""), " ");
      while (st.hasMoreTokens()) {
         t = st.nextToken();
         if (t.charAt(0) == ':') { // Last parameter.
            t = t.substring(1); // Remove the colon.
            if (st.hasMoreTokens()) {
               t = t.concat(" ").concat(st.nextToken("").trim());
            }
         }
         parameters.add(t);
      }
   }

   public Message(String command, String parameters[]) {
      this("", command, parameters);
   }

   public Message(String prefix, String command, String parameters[]) {
      this.prefix = prefix;
      this.command = command;
      this.parameters = new ArrayList<String>();
      for (int i = 0; i < parameters.length; i++) {
         this.parameters.add(parameters[i]);
      }
   }

   public String toString() {
      String t = "";

      if (0 != prefix.length()) {
         t = t.concat(":".concat(prefix).concat(" "));
      }
      t = t.concat(command);
      Iterator<String> iter = parameters.iterator();

      while (iter.hasNext()) {
         t = t.concat(" ");
         // Force fetch here to see if this is the last parameter
         String t2 = (String) iter.next();
         if (!iter.hasNext()) {
            t = t.concat(":");
         }
         t = t.concat(t2);
      }

      return (t.concat("\r\n"));
   }

   public String getPrefix() {
      return (prefix);
   }

   public String getCommand() {
      return command;
   }

   public String[] getParameters() {
      String a[] = new String[parameters.size()];
      a = parameters.toArray(a);

      return (a);
   }
   
   public String toJson() {
      StringBuilder builder = new StringBuilder();
      
      builder.append("{\n");
      builder.append("\"prefix\" : \"");
      builder.append(getPrefix());
      builder.append("\",\n");
      builder.append("\"command\" : \"");
      builder.append(getCommand());
      builder.append("\",\n");
      builder.append("\"parameters\" : [");
      String params[] = this.getParameters();
      for (int i = 0; i < params.length; i++) {
         if (i > 0) {
            builder.append(",");
         }
         builder.append("\"");
         builder.append(params[i]);
         builder.append("\"");
      }
      builder.append("],\n");
      
      builder.append("}\n");
      
      return builder.toString();
   }

}
