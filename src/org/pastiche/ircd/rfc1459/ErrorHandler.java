package org.pastiche.ircd.rfc1459;

import org.pastiche.ircd.IrcMessage;
import org.pastiche.ircd.Target;

/**
 * <p>
 * A bunch of methods we can use to format numeric replies. I've got a neat idea
 * that you could pretty easily implement a multi-lingual ircd by giving each
 * client a locale. The method names are based on the constant names in the
 * regular C ircd.
 */
public class ErrorHandler extends NumericHandler {

   private static ErrorHandler instance = null;

   /**
    * NumericHandler constructor comment.
    */
   protected ErrorHandler() {
      super();
   }

   public void alreadyRegistered(Target target) {
      sendTo(target, new IrcMessage(462, "You may not reregister"));
   }

   public void badChannelKey(Target target, String channel) {
      IrcMessage msg = new IrcMessage(475, "Cannot join channel (+k)");
      msg.addParameter(channel);
      sendTo(target, msg);
   }

   public void bannedFromChan(Target target, String channel) {
      IrcMessage msg = new IrcMessage(474, "Cannot join channel (+b)");
      msg.addParameter(channel);
      sendTo(target, msg);      
   }

   public void cantKillServer(Target target) {
      sendTo(target, new IrcMessage(483, "You cant kill a server!"));
   }

   public void erroneousNickname(Target target, String nick) {
      IrcMessage msg = new IrcMessage(432, "Erroneous nickname");
      msg.addParameter(nick);
      sendTo(target, msg);      
   }

   public void noNicknameGiven(Target target) {
      sendTo(target, new IrcMessage(431, "No nickname given"));
   }

   public void fileError(Target target, String operation, String filename) {
      sendTo(target, new IrcMessage(424, "File error doing " + operation + " on " + filename));
   }

   public void keySet(Target target, String channel) {
      sendTo(target, new IrcMessage(467, "Channel key already set"));
   }

   public void needMoreParams(Target target, String commandName) {
      IrcMessage msg = new IrcMessage(461, "Not enough parameters");
      
      msg.addParameter(commandName);
      sendTo(target, msg);
   }

   public void nickCollision(Target target, String nick) {
      IrcMessage msg = new IrcMessage(436, "Nickname collision KILL");
      
      msg.addParameter(nick);
      sendTo(target, msg);
   }

   public void nicknameInUse(Target target, String nick) {
      IrcMessage msg = new IrcMessage(433, "Nickname is already in use");
      
      msg.addParameter(nick);
      sendTo(target, msg);
   }

   public void noAdminInfo(Target target, String serverName) {
      IrcMessage msg = new IrcMessage(423, "No administrative info available");
      
      msg.addParameter(serverName);
      sendTo(target, msg);
   }

   public void noLogin(Target target, String username) {
      IrcMessage msg = new IrcMessage(444, "User not logged in");
      
      msg.addParameter(username);
      sendTo(target, msg);
   }

   public void noMotd(Target target) {
      sendTo(target, new IrcMessage(422, "MOTD file is missing"));
   }

   public void noOperHost(Target target) {
      sendTo(target, new IrcMessage(491, "No O-lines for your host"));
   }

   public void noPermForHost(Target target) {
      sendTo(target, new IrcMessage(463, "Your host isn't among the privileged"));
   }

   public void noPrivileges(Target target) {
      sendTo(target, new IrcMessage(481, "Permission Denied:- You're not an IRC operator"));
   }

   public void noRecipient(Target target, String commandName) {
      sendTo(target, new IrcMessage(411, "No recipient given (" + commandName + ")"));
   }

   public void noSuchChannel(Target target, String channel) {
      IrcMessage msg = new IrcMessage(403, "No such channel");
      
      msg.addParameter(channel);
      sendTo(target, msg);
   }

   public void noSuchNick(Target target, String nick) {
      IrcMessage msg = new IrcMessage(401, "No such nick/channel");
      
      msg.addParameter(nick);
      sendTo(target, msg);
   }

   public void noSuchServer(Target target, String server) {
      IrcMessage msg = new IrcMessage(402, "No such server");
      
      msg.addParameter(server);
      sendTo(target, msg);
   }

   public void noTextToSend(Target target) {
      sendTo(target, new IrcMessage(412, "No text to send"));
   }

   public void notInChannel(Target target, String channel) {
      IrcMessage msg = new IrcMessage(442, "You're not on that channel");
      
      msg.addParameter(channel);
      sendTo(target, msg);
   }

   public void noTopLevel(Target target, String mask) {
      IrcMessage msg = new IrcMessage(413, "No toplevel domain specified");
      
      msg.addParameter(mask);
      sendTo(target, msg);
   }

   public void notRegistered(Target target) {
      sendTo(target, new IrcMessage(451, "You have not registered"));
   }

   public void passwdMismatch(Target target) {
      sendTo(target, new IrcMessage(464, "Password incorrect"));
   }

   public void summonDisabled(Target target) {
      sendTo(target, new IrcMessage(445, "SUMMON has been disabled"));
   }

   public void tooManyChannels(Target target, String channel) {
      IrcMessage msg = new IrcMessage(405, "You have joined too many channels");
      
      msg.addParameter(channel);
      sendTo(target, msg);
   }

   public void tooManyTargets(Target target, String targetName) {
      sendTo(target, new IrcMessage(409, "No origin specified"));
   }

   public void unknownCommand(Target target, String commandName) {
      IrcMessage msg = new IrcMessage(421, "Unknown command");
      
      msg.addParameter(commandName);
      sendTo(target, msg);
   }

   public void unknownMode(Target target, char modeChar) {
      IrcMessage msg = new IrcMessage(472, "is unknown mode char to me");
      
      msg.addParameter(""+modeChar);
      sendTo(target, msg);
   }

   public void unknownModeFlag(Target target) {
      sendTo(target, new IrcMessage(501, "Unknown MODE flag"));
   }

   public void userNotInChannel(Target target, String nick, String channel) {
      IrcMessage msg = new IrcMessage(441, "They aren’t on that channel");
      
      msg.addParameter(nick);
      msg.addParameter(channel);
      sendTo(target, msg);
   }

   public void userOnChannel(Target target, String nick, String channel) {
      IrcMessage msg = new IrcMessage(443, "is already on channel");
      
      msg.addParameter(nick);
      msg.addParameter(channel);
      sendTo(target, msg);
   }

   public void usersDisabled(Target target) {
      sendTo(target, new IrcMessage(446, "USERS has been disabled"));
   }

   public void usersDontMatch(Target target) {
      sendTo(target, new IrcMessage(502, "Cant change mode for other users"));
   }

   public void wasNoSuchNick(Target target, String nick) {
      IrcMessage msg = new IrcMessage(406, "There was no such nickname");
      
      msg.addParameter(nick);
      sendTo(target, msg);
   }

   public void wildTopLevel(Target target, String mask) {
      IrcMessage msg = new IrcMessage(414, "Wildcard in toplevel domain");
      
      msg.addParameter(mask);
      sendTo(target, msg);
   }

   public void youreBannedCreep(Target target) {
      sendTo(target, new IrcMessage(465, "You are banned from this server"));
   }

   public static ErrorHandler getInstance() {
      if (instance == null) {
         instance = new ErrorHandler();
      }

      return instance;
   }

   public void badChannelKey(Target target, Channel channel) {
      IrcMessage msg = new IrcMessage(475, "Cannot join channel (+k)");
      
      msg.addParameter(channel.getName());
      sendTo(target, msg);
   }

   public void cannotSendToChan(Target target, Channel channel) {
      IrcMessage msg = new IrcMessage(404, "Cannot send to channel");
      
      msg.addParameter(channel.getName());
      sendTo(target, msg);
   }

   public void channelIsFull(Target target, Channel channel) {
      IrcMessage msg = new IrcMessage(471, "Cannot join channel (+l)");
      
      msg.addParameter(channel.getName());
      sendTo(target, msg);
   }

   public void chanopPrivsNeeded(Target target, Channel channel) {
      IrcMessage msg = new IrcMessage(482, "You're not channel operator");
      
      msg.addParameter(channel.getName());
      sendTo(target, msg);
   }

   public void inviteOnlyChan(Target target, Channel channel) {
      IrcMessage msg = new IrcMessage(473, "Cannot join channel (+i)");
      
      msg.addParameter(channel.getName());
      sendTo(target, msg);
   }
}
