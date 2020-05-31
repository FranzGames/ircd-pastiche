/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pastiche.ircd.rfc1459;

import java.util.List;
import org.pastiche.ircd.IrcMessage;

/**
 *
 * @author pfranz
 */
public class SourceIrcMessage extends TargetIrcMessage {
   String sourceName;

   public SourceIrcMessage (TargetIrcMessage msg, String name) {
      super((IrcMessage)msg, msg.targetName);
      
      this.sourceName = name;
   }
   
   public SourceIrcMessage (IrcMessage msg, String name) {
      super (msg, null);
      
      this.sourceName = name;
   }
   
   public String getSourceName () {
      return sourceName;
   }
   
   @Override
   public String createIrcMessage () {
      StringBuilder builder = new StringBuilder();

      builder.append(":");
      builder.append(this.sourceName);
      builder.append(" ");
      builder.append(command);
      builder.append(" ");
      
      if (targetName != null) {
         builder.append(targetName);
      }

      boolean first = true;
      for (String param : parameters) {
         if (!first) {
            builder.append(parameterSep);
         } else {
            builder.append(" ");
            first = false;
         }

         builder.append(param);
      }

      if (this.message != null) {
         builder.append(" :");
         builder.append(this.message);
      } else if (postParameters.size() > 0) {
         first = true;
         for (String param : postParameters) {
            if (!first) {
               builder.append(parameterSep);
            } else {
               builder.append(" : ");
               first = false;
            }

            builder.append(param);
         }
      }

      return builder.toString();      
   }
   
   public String toJson() {
      StringBuilder builder = new StringBuilder();
      
      builder.append("{\n");
      builder.append("\"prefix\" : \"");
      builder.append(getSourceName());
      builder.append("\",\n");
      builder.append("\"command\" : \"");
      builder.append(getCommand());
      builder.append("\",\n");
      builder.append("\"target\" : \"");
      builder.append(this.targetName == null ? "" : this.targetName);
      builder.append("\",\n");
      builder.append("\"parameters\" : [");
      boolean first = true;
      
      List<String> params = this.getParameters();
      for (String param: params) {
         if (!first) {
            builder.append(",");
         } else {
            first = false;
         }
         
         builder.append("\"");
         builder.append(param);
         builder.append("\"");
      }
      builder.append("],\n");
      
      builder.append("\"post_parameters\" : [");
      params = this.getPostParameters();
      for (int i = 0; i < params.size(); i++) {
         if (i > 0) {
            builder.append(",");
         }
         builder.append("\"");
         builder.append(params.get(i));
         builder.append("\"");
      }
      builder.append("],\n");

      builder.append("\"trailing\" : \"");
      builder.append(getMessage());
      builder.append("\"\n");
      
      
      builder.append("}\n");
      
      return builder.toString();
   }     
   
}
