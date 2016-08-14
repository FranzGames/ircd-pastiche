/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pastiche.ircd;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.KeyManagerFactory;

/**
 *
 * @author pfranz
 */
public class KeyStoreConfiguration {
   private String keystore;
   private String password;
   private String keyPassword;
   private String storetype;
   
   public KeyStoreConfiguration (String file, String pass, String keyPass, String type) {
      this.keystore = file;
      this.password = pass;
      this.keyPassword = keyPass;
      this.storetype = type;
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

   /**
    * @return the password
    */
   public String getPassword() {
      return password;
   }

   /**
    * @param password the password to set
    */
   public void setPassword(String password) {
      this.password = password;
   }

   /**
    * @return the keyPassword
    */
   public String getKeyPassword() {
      return keyPassword;
   }

   /**
    * @param keyPassword the keyPassword to set
    */
   public void setKeyPassword(String keyPassword) {
      this.keyPassword = keyPassword;
   }

   /**
    * @return the storetype
    */
   public String getStoretype() {
      return storetype;
   }

   /**
    * @param storetype the storetype to set
    */
   public void setStoretype(String storetype) {
      this.storetype = storetype;
   }
   
   public KeyManagerFactory getKeyManagerFactory() {
      try {
         KeyStore ks = KeyStore.getInstance(this.getStoretype());
         char[] ksPass;
         char[] ctPass;
         
         if (this.getPassword() != null) {
            ksPass = this.getPassword().toCharArray();
         } else {
            ksPass = new char[0];
         }
         
         if (this.getKeyPassword() != null) {
            ctPass = this.getKeyPassword().toCharArray();
         } else {
            ctPass = new char[0];
         }
         
         ks.load(new FileInputStream(this.getKeystore()), ksPass);
         KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
         kmf.init(ks, ctPass);
         
         return kmf;
      } catch (IOException ex) {
         Logger.getLogger(KeyStoreConfiguration.class.getName()).log(Level.SEVERE, null, ex);
      } catch (NoSuchAlgorithmException ex) {
         Logger.getLogger(KeyStoreConfiguration.class.getName()).log(Level.SEVERE, null, ex);
      } catch (CertificateException ex) {
         Logger.getLogger(KeyStoreConfiguration.class.getName()).log(Level.SEVERE, null, ex);
      } catch (KeyStoreException ex) {
         Logger.getLogger(KeyStoreConfiguration.class.getName()).log(Level.SEVERE, null, ex);
      } catch (UnrecoverableKeyException ex) {
         Logger.getLogger(KeyStoreConfiguration.class.getName()).log(Level.SEVERE, null, ex);
      }
      
      return null;
   }
   
}
