package org.pastiche.ircd.rfc1459;

import org.pastiche.ircd.Target;

/**
 * <p>A bunch of methods we can use to format numeric replies.
 * I've got a neat idea that you could pretty easily implement
 * a multi-lingual ircd by giving each client a locale. The method
 * names are based on the constant names in the regular C ircd.
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
	sendTo(target, "462", ":You may not reregister");
}
public void badChannelKey(Target target, String channel) {
	sendTo(target, "475", channel + " :Cannot join channel (+k)");
}
public void bannedFromChan(Target target, String channel) {
	sendTo(target, "474", channel + " :Cannot join channel (+b)");
}

public void cantKillServer(Target target) {
	sendTo(target, "483", ":You cant kill a server!");
}


public void erroneousNickname(Target target, String nick) {
	sendTo(target, "432", nick + " :Erroneous nickname");
}
public void noNicknameGiven(Target target) {
	sendTo(target, "431", ":No nickname given");
}
public void fileError(Target target, String operation, String filename) {
	sendTo(target, "424", ":File error doing " + operation + " on " + filename);
}

public void keySet(Target target, String channel) {
	sendTo(target, "467", channel + " :Channel key already set");
}
public void needMoreParams(Target target, String commandName) {
	sendTo(target, "461", commandName + " :Not enough parameters");
}
public void nickCollision(Target target, String nick) {
	sendTo(target, "436", nick + " :Nickname collision KILL");
}
public void nicknameInUse(Target target, String nick) {
	sendTo(target, "433", nick + " :Nickname is already in use");
}
public void noAdminInfo(Target target, String serverName) {
	sendTo(target, "423", serverName + " :No administrative info available");
}
public void noLogin(Target target, String username) {
	sendTo(target, "444", username + " :User not logged in");
}
public void noMotd(Target target) {
	sendTo(target, "422", ":MOTD file is missing");
}
public void noOperHost(Target target) {
	sendTo(target, "491", ":No O-lines for your host");
}
public void noPermForHost(Target target) {
	sendTo(target, "463", ":Your host isn't among the privileged");
}
public void noPrivileges(Target target) {
	sendTo(target, "481", ":Permission Denied:- You're not an IRC operator");
}
public void noRecipient(Target target, String commandName) {
	sendTo(target, "411",":No recipient given ("+commandName+")");
}
public void noSuchChannel(Target target, String channel) {
	sendTo(target, "403", channel + " :No such channel");
}
public void noSuchNick(Target target, String nick) {
	sendTo(target, "401", nick + " :No such nick/channel");
}
public void noSuchServer(Target target, String server) {
	sendTo(target, "402", server + " :No such server");
}
public void noTextToSend(Target target) {
	sendTo(target, "412",":No text to send");
}
public void notInChannel(Target target, String channel) {
	sendTo(target, "442", channel + " :You're not on that channel");
}
public void noTopLevel(Target target, String mask) {
	sendTo(target, "413", mask + " :No toplevel domain specified");
}
public void notRegistered(Target target) {
	sendTo(target, "451", ":You have not registered");
}
public void passwdMismatch(Target target) {
	sendTo(target, "464", ":Password incorrect");
}
public void summonDisabled(Target target) {
	sendTo(target, "445", ":SUMMON has been disabled");
}
public void tooManyChannels(Target target, String channel) {
	sendTo(target, "405", channel + " :You have joined too many channels");
}
public void tooManyTargets(Target target, String targetName) {
	sendTo(target, "409",":No origin specified");
}
public void unknownCommand(Target target, String commandName) {
	sendTo(target, "421", commandName + " :Unknown command");
}
public void unknownMode(Target target, char modeChar) {
	sendTo(target, "472", modeChar + " :is unknown mode char to me");
}
public void unknownModeFlag(Target target) {
	sendTo(target, "501", ":Unknown MODE flag");
}
public void userNotInChannel(Target target, String nick, String channel) {
	sendTo(target, "441", nick + " " + channel + " :is already on channel");
}
public void userOnChannel(Target target, String nick, String channel) {
	sendTo(target, "443", nick + " " + channel + " :is already on channel");
}
public void usersDisabled(Target target) {
	sendTo(target, "446", ":USERS has been disabled");
}
public void usersDontMatch(Target target) {
	sendTo(target, "502", ":Cant change mode for other users");
}
public void wasNoSuchNick(Target target, String nick) {
	sendTo(target, "406", nick + " :There was no such nickname");
}
public void wildTopLevel(Target target, String mask) {
	sendTo(target, "414", mask + " :Wildcard in toplevel domain");
}
public void youreBannedCreep(Target target) {
	sendTo(target, "465", ":You are banned from this server");
}
public static ErrorHandler getInstance() {
	if (instance == null) {
		instance = new ErrorHandler();
	}

	return instance;
}


public void badChannelKey(Target target, Channel channel) {
	sendTo(target, "475", channel.getName() + " :Cannot join channel (+k)");
}

public void cannotSendToChan(Target target, Channel channel) {
	sendTo(target, "404", channel.getName() + " :Cannot send to channel");
}

public void channelIsFull(Target target, Channel channel) {
	sendTo(target, "471", channel.getName() + " :Cannot join channel (+l)");
}

public void chanopPrivsNeeded(Target target, Channel channel) {
	sendTo(target, "482", channel.getName() + " :You're not channel operator");
}

public void inviteOnlyChan(Target target, Channel channel) {
	sendTo(target, "473", channel.getName() + " :Cannot join channel (+i)");
}
}