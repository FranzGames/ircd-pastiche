package org.pastiche.ircd;

import java.util.LinkedList;
import java.util.Collections;

/**
 * <p>This one behaves a lot like the event queue in Swing. To avoid
 * all the hassles that come alongside multi-threading, anything that
 * might be thread-unsafe gets put in the command queue and processed
 * in order of submission. This should really be the only class in the
 * entire program that has to deal with synchronization, which is why
 * there's such an annoying lot of it in here.
 */
public class CommandQueue implements Runnable {
	private static CommandQueue instance;

	private LinkedList queue = new LinkedList();

	public static CommandQueue getInstance() {
		if (instance == null) {
			instance = new CommandQueue();
			new Thread(instance, "CommandQueue").start();
		}

		return instance;
	}

	private CommandQueue() {
	}

	public void run() {
		Command command = null;

		while (true) {
			synchronized(queue) {
				if (queue.size() == 0) {
					try {
						queue.wait();
					} catch (InterruptedException ie) {
					}
					continue;
				}

				command = (Command) queue.removeFirst();
			}

         try
            {
            System.out.println ("Command Class = "+command.getClass ().getName ());
			   command.process();
            }
         catch (Throwable t)
            {
            t.printStackTrace ();
            }

			synchronized(command) {
				command.notifyAll();
			}
		}
	}

	public void add(Command command) {
		synchronized(queue) {
			queue.addLast(command);
			queue.notifyAll();
		}
	}

	public void addAndWait(Command command) {
		add(command);

		synchronized(command) {
			try {
				command.wait();
			} catch (InterruptedException ie) {
			}
		}
	}
}
