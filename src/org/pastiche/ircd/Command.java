package org.pastiche.ircd;

import java.util.regex.Pattern;

/*
 *   Pastiche IRCd - Java Internet Relay Chat
 *   Copyright (C) 2001 Charles Miller
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
/**
 * <p>
 * An abstract class creating a framework around which commands can be built.
 * It's probably the most important class to understand if you want to hack the
 * ircd.
 *
 * <p>
 * When the server is initialised, the IrcdConfiguration loads up a whole lot of
 * Command objects, and associates them with the various commands that the
 * server can receive. Mapping between a user and the set of available commands
 * is done via the CommandFactory, and since there's an unlimited different
 * number of CommandFactory objects that can be created and referenced, this
 * means that different classes of user can have totally different sets of
 * Commands made available to them.
 *
 * <p>
 * The lifecycle of a command:
 * <ul>
 * <li>At startup/rehash, the IrcdConfiguration will instantiate each command it
 * has been asked to map using its default, no-arg constructor. At this point,
 * Commands should not assume that the IrcdConfiguration is reliable, or that
 * the CommandQueue is able to process commands.
 * <li>At the end of startup/rehash, the IrcdConfiguration will call the init()
 * method of each command. This is done at the end of the rehash, so you can now
 * assume that the environment is working. <b>(not yet implemented)</b>
 * <li>When a command line is received from a socket, the object in charge of
 * that socket (a Target) will pass it to its relevant CommandFactory.
 * <li>The CommandFactory will parse the command line, and look up the
 * appropriate Command object. It will call the object's clone() method to spawn
 * off a copy of the object to handle the request. <b>Beware:</b> Remember, a
 * clone is a shallow copy, so modifying objects stored in instance variables
 * will have nasty side-effects unless you're careful. Note, for example, how
 * the arguments List is handled in the default implementation.
 * <li>The CommandFactory will populate the Command with arguments by calling
 * its addArgument() method for each remaining part of the command line. It will
 * also call setSource(String) if the command line being parsed contains a
 * source part (see RFC1459)
 * <li>The Target object handling the socket from which the command line was
 * received will call the Command's setImmediateSource() method with itself as
 * an argument.
 * <li>The same Target object will then call the Command's preProcess() method.
 * Anything that the command can do that will take a significant amount of time,
 * but that will not get in the way of thread safety should be done in
 * preProcess(), as this means it doesn't clog up the (single-theaded)
 * CommandQueue.
 * <li>If requiresProcess() returns true the Command will then be added to the
 * CommandQueue. The CommandQueue will call the Command's process() method.
 * Commands may be as thread-unsafe as they like in process(), but remember that
 * they're blocking the entire server while they're there.
 * <li>I'm not decided whether a 'postprocess' is a good idea or not. I don't
 * think it is.
 * </ul>
 *
 * <p>
 * Note: CommandQueue implements addAndWait() by putting the waiting thread in
 * the Command's waitQ, so if you ever do a notify() on a Command object, you'll
 * most likely break something important.
 */
public abstract class Command implements Cloneable {

   public static final int MAX_ARGS = 15; // from rfc1459, probably doesn't belong here.

   private Target source = null;
   private Target immediateSource = null;
   private Target[] destination = null;
   private java.util.List<String> args = null;
   private String name = null;

   public void init() {
   }

   public void preProcess() {
   }

   public void process() {
   }

   public boolean requiresProcess() {
      return false;
   }

   public void addArgument(String arg) {
      if (args == null) {
         args = new java.util.ArrayList<String>(MAX_ARGS);
      }

      args.add(arg);
   }

   public void setSource(String source) {
      return;
   }

   protected Target getSource() {
      if (source == null) {
         return immediateSource;
      }

      return source;
   }

   public void setSource(Target source) {
      this.source = source;
   }

   public void setImmediateSource(Target immediateSource) {
      this.immediateSource = immediateSource;
   }

   public void setImmediateSource(String immediateSource) {
      return;
   }

   public Object clone() {
      try {
         return super.clone();
      } catch (CloneNotSupportedException cnse) {
         throw new RuntimeException("CloneNotSupportedException " + cnse.getMessage());
      }
   }

   protected String[] calculateTargets(int argumentPosition) {
      return calculateTargets(argumentPosition, ",");
   }

   protected String[] calculateTargets(int argumentPosition, String delim) {
      if (getArguments() == null) {
         return new String[0];
      }

      java.util.StringTokenizer tokens = new java.util.StringTokenizer(getArguments().get(argumentPosition), delim);
      String[] targets = new String[tokens.countTokens()];

      for (int i = 0; i < targets.length; i++) {
         targets[i] = tokens.nextToken();
      }

      return targets;
   }

   protected java.util.List<String> getArguments() {
      return args;
   }

   protected String getArgument(int index) {
      if (args == null) {
         return null;
      }

      if ((index + 1) > args.size()) {
         return null;
      }

      return (String) args.get(index);
   }

   protected int getArgumentCount() {
      if (args == null) {
         return 0;
      }

      return args.size();
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   private boolean idleCountResetter = false;

   public boolean isIdleCountResetter() {
      return idleCountResetter;
   }

   public void setIdleCountResetter(boolean idleCountResetter) {
      this.idleCountResetter = idleCountResetter;
   }

   public static Pattern createPattern(String expression) {
      StringBuilder builder = new StringBuilder();
      builder.append("^");

      for (int i = 0; i < expression.length(); i++) {
         char ch = expression.charAt(i);

         if (ch == '.') {
            builder.append("\\.");
         } else if (ch == '*') {
            builder.append(".*");
         } else {
            builder.append(ch);
         }
      }

      builder.append("$");
      
      return Pattern.compile(builder.toString());
   }
}
