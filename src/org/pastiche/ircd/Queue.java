package org.pastiche.ircd;

public class Queue extends java.util.Vector
   {
   public Queue ()
      {
      super ();
      }

   public Queue (int initialCap, int inc)
      {
      super (initialCap, inc);
      }

   public Queue (int initialCap)
      {
      this (initialCap, 0);
      }

   public synchronized void clear ()
      {
      setSize (0);
      }

   public synchronized void push (String obj)
      {
      push (obj, false);
      }

   public synchronized void push (String obj, boolean priority)
      {
      if (priority)
         insertElementAt (obj, 0);
      else
         addElement (obj);

      notifyAll ();
      }

   public synchronized String pop ()
      {
      try
         {
         while (isEmpty ())
            wait ();
         }
      catch (Throwable t)
         {
         t.printStackTrace ();
         System.err.println (t);
         }


      if (size () == 0)
         return null;

      Object obj = elementAt (0);

      removeElementAt (0);

      return (String) obj;
      }
   }
