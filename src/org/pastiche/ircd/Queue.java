package org.pastiche.ircd;

public class Queue<T> extends java.util.Vector<T>
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

   public synchronized void push (T obj)
      {
      push (obj, false);
      }

   public synchronized void push (T obj, boolean priority)
      {
      if (priority)
         insertElementAt (obj, 0);
      else
         addElement (obj);

      notifyAll ();
      }

   public synchronized T pop ()
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

      if (isEmpty())
         return null;

      T obj = elementAt (0);

      removeElementAt (0);

      return obj;
      }
   }
