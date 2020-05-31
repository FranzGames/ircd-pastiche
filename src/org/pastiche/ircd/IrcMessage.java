/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pastiche.ircd;

/**
 *
 * @author pfranz
 */
import java.text.NumberFormat;
import java.util.List;
import java.util.ArrayList;

public class IrcMessage {

   static final NumberFormat replyNumberFormat;

   static {
      replyNumberFormat = NumberFormat.getInstance();

      replyNumberFormat.setMaximumIntegerDigits(3);
      replyNumberFormat.setMinimumIntegerDigits(3);
   }

   protected String command;
   protected List<String> parameters = new ArrayList<>();
   protected List<String> postParameters = new ArrayList<>();
   protected String message;
   protected String parameterSep = " ";

   public IrcMessage(int num) {
      this.command = replyNumberFormat.format(num);
   }
   
   public IrcMessage(String cmd) {
      this.command = cmd;
   }

   public IrcMessage(int num, String msg) {
      this.command = replyNumberFormat.format(num);
      this.message = msg;
   }
   
   public IrcMessage(String cmd, String msg) {
      this.command = cmd;
      this.message = msg;
   }
   
   public String getCommand () {
      return command;
   }
   
   public void addParameter(String parameter) {
      this.parameters.add(parameter);
   }

   public void addPostParameter(String parameter) {
      this.postParameters.add(parameter);
   }
   
   public List<String> getParameters() {
      return parameters;
   }

   public List<String> getPostParameters() {
      return postParameters;
   }
   
   
   public String getMessage () {
      return message;
   }

   public void setMessage(String msg) {
      this.message = msg;
   }

   public String getParameterSeparator() {
      return this.parameterSep;
   }
   
   public void setParameterSeparator(String sep) {
      this.parameterSep = sep;
   }

   public String createIrcMessage() {
      StringBuilder builder = new StringBuilder();

      builder.append(command);
      builder.append(" ");
      
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
      builder.append("\",\n");
      builder.append("\"command\" : \"");
      builder.append(getCommand());
      builder.append("\",\n");
      builder.append("\"target\" : \"");
      builder.append("\",\n");
      builder.append("\"parameters\" : [");
      List<String> params = this.getParameters();
      for (int i = 0; i < params.size(); i++) {
         if (i > 0) {
            builder.append(",");
         }
         builder.append("\"");
         builder.append(params.get(i));
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
