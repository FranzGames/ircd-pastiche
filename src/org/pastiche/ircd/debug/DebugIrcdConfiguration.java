package org.pastiche.ircd.debug;

import org.pastiche.ircd.*;

import org.pastiche.ircd.rfc1459.*;

/**
 * <p>A faked up configuration object, to pass for a real configuration
 * until I actually write the IrcdConfiguration class.
 */
public class DebugIrcdConfiguration extends IrcdConfiguration {
	private Listener[] listeners = new Listener[1];
   private SimpleAuthenticator authenticator = new SimpleAuthenticator ("nickpass.properties");

public DebugIrcdConfiguration() {
	Server server = new Server();
	listeners[0] = new Listener(server, Ircd.port, Ircd.server);

	unregisteredMap.put("USER", new UnregisteredUserCommand());
	unregisteredMap.put("NICK", new UnregisteredNickCommand());
	unregisteredMap.put("PASS", new PassCommand());

	connectedMap.put("PASS", new PassCommand());
	connectedMap.put("NICK", new RegisteredNickCommand());
	connectedMap.put("PRIVMSG", new PrivmsgCommand());
	connectedMap.put("NOTICE", new NoticeCommand());
	connectedMap.put("ISON", new IsonCommand());
	connectedMap.put("USERHOST", new UserhostCommand());
	connectedMap.put("LUSERS", new LusersCommand());
	connectedMap.put("MOTD", new MotdCommand());
	connectedMap.put("JOIN", new JoinCommand());
	connectedMap.put("PART", new PartCommand());
	connectedMap.put("QUIT", new QuitCommand());
	connectedMap.put("NAMES", new NamesCommand());
	connectedMap.put("HELP", new HelpCommand());
	connectedMap.put("TOPIC", new TopicCommand());
	connectedMap.put("MODE", new ModeCommand());
	connectedMap.put("INVITE", new InviteCommand());
	connectedMap.put("KICK", new KickCommand());
	connectedMap.put("PING", new NoOpCommand());
	connectedMap.put("PONG", new NoOpCommand());
	connectedMap.put("LIST", new ListCommand());
	connectedMap.put("WHOIS", new WhoisCommand());

	((Command)connectedMap.get("PRIVMSG")).setIdleCountResetter(true);

   // Allow the reloading of the authentication list every 5 minutes

   Scheduler.addTask (new ReloadAuthenticator (), 30000, 30000);
}

	public void checkConfiguration() throws ConfigurationException {
		return;
	}

	public Listener[] getListeners() {
		return listeners;
	}

	private NameNormalizer channelNormalizer = new ChannelNormalizer();
	private Command connectedDefaultCommand = new UnknownCommand();
	private java.util.Map connectedMap = new java.util.HashMap();
	private Command unregisteredDefaultCommand = new UnknownCommand();
	private java.util.Map unregisteredMap = new java.util.HashMap();
	private NameNormalizer userNormalizer = new NickNormalizer();

public NameNormalizer getChannelNormalizer() {
	return channelNormalizer;
}

public java.util.Map getCommandMap(String clientContext) {
	if (clientContext.equals(UnregisteredClient.COMMAND_FACTORY_ID)) {
		return unregisteredMap;
	} else if (clientContext.equals(org.pastiche.ircd.rfc1459.RegisteredUser.COMMAND_FACTORY_ID)) {
		return connectedMap;
	}

	return null;
}

public Command getDefaultCommand(String clientContext) {
	if (clientContext.equals(UnregisteredClient.COMMAND_FACTORY_ID)) {
		return unregisteredDefaultCommand;
	} else if (clientContext.equals(org.pastiche.ircd.rfc1459.RegisteredUser.COMMAND_FACTORY_ID)) {
		return connectedDefaultCommand;
	}

	return null;
}

public Class getInitialConnectionClass() {
	return UnregisteredClient.class;
}

	public String getServerName() {
		return "irc.test.org";
	}

public NameNormalizer getServerNormalizer(){
	return null;
}

public int getUnregisteredClientTimeout() {
	return 30;
}

public NameNormalizer getUserNormalizer(){
	return userNormalizer;
}

public Authenticator getNickPasswordAuthenticator ()
   {
   return authenticator;
   }

class ReloadAuthenticator extends java.util.TimerTask
   {
   public void run ()
      {
      authenticator.reload ();
      }
   }

}
