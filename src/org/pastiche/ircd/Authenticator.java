package org.pastiche.ircd;

public interface Authenticator
   {

   /* This is used to autheticate the NICK/PASS combo and
      sends out the appropriate result code if it fails.
      This is done this way to support extended types of
      authentication and error results */
   boolean authenicate (Target target, String nick, String password);
   }

