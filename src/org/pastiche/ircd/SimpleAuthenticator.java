package org.pastiche.ircd;

/* The SimpleAuthenticator uses a property file to
   list the available nicks and their passwords.

   <nick>=<password>

   For example,

   admin=secret

*/

import java.io.*;
import org.pastiche.ircd.rfc1459.ErrorHandler;

public class SimpleAuthenticator implements Authenticator
   {
   java.util.Properties props;
   String filename;


   public SimpleAuthenticator (String propFilename)
      {
      BufferedInputStream input = null;

      try
         {
         props = new java.util.Properties ();

         filename = propFilename;

         input = new BufferedInputStream (new FileInputStream (propFilename));

         props.load (input);
      }
      catch (IOException io)
         {
         props = null;

         io.printStackTrace ();
         }
      finally
         {
         if (input != null)
            {
            try {input.close ();} catch (Throwable t) {}
            }
         }
      }

   public synchronized void reload ()
      {
      BufferedInputStream input = null;

      try
         {
         java.util.Properties newProps = new java.util.Properties ();

         input = new BufferedInputStream (new FileInputStream (filename));

         newProps.load (input);

         props = newProps;
         }
      catch (IOException io)
         {
         io.printStackTrace ();
         }
      finally
         {
         if (input != null)
            {
            try {input.close ();} catch (Throwable t) {}
            }
         }
      }

   public synchronized boolean authenicate (Target target, String nick, String password)
      {
      if (props == null)
         return true;

      String value = (String) props.get (nick);

      // User does not exist
      if (value == null)
         {
         ErrorHandler.getInstance().passwdMismatch (target);
         return false;
         }

      // Password not enterred or wrong
      if (password == null || !password.equals (value))
         {
         ErrorHandler.getInstance().passwdMismatch (target);
         return false;
         }

      return true;
      }
   }

