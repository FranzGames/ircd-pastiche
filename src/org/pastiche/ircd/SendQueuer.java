package org.pastiche.ircd;

import java.io.*;
import java.net.*;

public class SendQueuer extends Thread
   {
	public static final byte[] END_OF_LINE = { (byte)'\r', (byte)'\n' };

   boolean process = true;
   boolean processQueue;
   Queue queue = new Queue ();
   BufferedOutputStream buf;

   public SendQueuer (BufferedOutputStream buf)
      {
      this.buf = buf;
      setProcessQueue (true);
      }

   public void addMessageToQueue (String message)
      {
      queue.push (message);
      }

   public void addPriorityMessageToQueue (String message)
      {
      queue.push (message, true);
      }


   public void disconnect ()
      {
      setProcessQueue (false);
      setProcess (false);

      // If it is push the null to stop the thread.
      if (queue.isEmpty ())
         queue.push (null);

      setProcessQueue (true);
      }

   public synchronized void setProcessQueue (boolean b)
      {
      processQueue = b;
      }

   public synchronized boolean processQueue ()
      {
      return processQueue;
      }

   private synchronized void setProcess (boolean b)
      {
      process = b;
      }

   private synchronized boolean process ()
      {
      return process;
      }


   public void run ()
      {
      while (process ())
         {
         if (!processQueue ())
            continue;

         try
            {
            String str = (String) queue.pop ();

            // This is to cover the case on disconnect.
            if (str == null)
              continue;

      		buf.write(str.getBytes(), 0, str.length());
      		buf.write(END_OF_LINE, 0, END_OF_LINE.length);
      		buf.flush();
            }
         catch (SocketException se)
            {
            System.err.println ("Exception in thread "+Thread.currentThread ().getName ());
            se.printStackTrace ();
            break;
            }
         catch (IOException e)
            {
            System.err.println ("Exception in thread "+Thread.currentThread ().getName ());
            e.printStackTrace ();
            }
         }
      }
   }
