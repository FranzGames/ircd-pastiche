/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pastiche.ircd.http;

/**
 *
 * @author pfranz
 */
public class HttpResult {
   private int code;
   private String contentType;
   private String data;
   
   public HttpResult (int code) {
      this(code, null, null);
   }
   
   public HttpResult (int code, String type, String d) {
      this.code = code;
      contentType = type;
      data = d;
   }

   /**
    * @return the code
    */
   public int getCode() {
      return code;
   }

   /**
    * @return the contentType
    */
   public String getContentType() {
      return contentType;
   }

   /**
    * @return the data
    */
   public String getData() {
      return data;
   }
}
