package org.pastiche.ircd;

public class DeadClientTask extends java.util.TimerTask
   {
   Server server;
   int idle;

   public DeadClientTask (Server server, int idle)
      {
      this.server = server;
      this.idle = idle;
      }

   public void run ()
      {
      if (server != null)
         server.pingNecessaryUsers (idle);
      }
   }
