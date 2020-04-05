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
public class ServerConfiguration {
   private String bindHost = null;
   private int port = 8080;
   private String secureType = null;
   private String keystore = null;
   
   public ServerConfiguration(int portNo) {
      port = portNo;
   }

   public ServerConfiguration(String host, int portNo) {
      bindHost = host;
      port = portNo;
   }
   
   public ServerConfiguration(String host, int portNo, String store) {
      secureType = "true";
      keystore = store;
      bindHost = host;
      port = portNo;
   }

      public ServerConfiguration(String host, int portNo, String type, String store) {
      secureType = type;
      keystore = store;
      bindHost = host;
      port = portNo;
   }

   
   /**
    * @return the bindHost
    */
   public String getBindHost() {
      return bindHost;
   }

   /**
    * @param bindHost the bindHost to set
    */
   public void setBindHost(String bindHost) {
      this.bindHost = bindHost;
   }

   /**
    * @return the port
    */
   public int getPort() {
      return port;
   }

   /**
    * @param port the port to set
    */
   public void setPort(int port) {
      this.port = port;
   }

   /**
    * @return the secureType
    */
   public String getSecureType() {
      return secureType;
   }

   /**
    * @param secureType the secureType to set
    */
   public void setSecureType(String secureType) {
      this.secureType = secureType;
   }

   /**
    * @return the keystore
    */
   public String getKeystore() {
      return keystore;
   }

   /**
    * @param keystore the keystore to set
    */
   public void setKeystore(String keystore) {
      this.keystore = keystore;
   }
   
   public String toString() {
      StringBuilder builder = new StringBuilder();
      
      if (getBindHost() != null) {
         builder.append (getBindHost());
      } else {
         builder.append ("<all>");
      }
      
      builder.append(":");
      builder.append(this.getPort());
      
      if ("true".equals(this.getSecureType())) {
         builder.append(" (secure:");
         builder.append(this.getKeystore());
         builder.append(")");
      }
      
      return builder.toString();
   }
}
