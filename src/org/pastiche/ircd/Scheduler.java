package org.pastiche.ircd;

import java.util.*;

public class Scheduler
   {
   static java.util.Timer timer = new java.util.Timer (true);

   public static void addTask (TimerTask task, long delay, long period)
      {
      timer.schedule (task, delay, period);
      }
   }
