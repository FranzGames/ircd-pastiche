package org.pastiche.ircd;

import org.pastiche.ircd.debug.Debug;
import org.pastiche.ircd.debug.DebugIrcdConfiguration;

/**
 * <p>A Singleton that encapsulates the configuration of the IRC
 * server. Eventually, this will be done via an XML document, but
 * for the purposes of testing, the getInstance() method returns
 * a subclass that contains compiled-in defaults.
 */
public class IrcdConfiguration {
	private static IrcdConfiguration instance = null;

	public static IrcdConfiguration getInstance() {
		if (instance == null) {
			if (Debug.DEBUG) {
				instance = new DebugIrcdConfiguration();
			} else {
				instance = new IrcdConfiguration();
			}
		}

		return instance;
	}

	protected IrcdConfiguration() {
	}

	public void checkConfiguration() throws ConfigurationException {
		throw new ConfigurationException("IrcdConfiguration not implemented");
	}

	public Listener[] getListeners() {
		return null;
	}

	public java.util.Map getCommandMap(String clientContext) {
		return null;
	}

public NameNormalizer getChannelNormalizer() {
	return null;
}

public Command getDefaultCommand(String clientContext) {
	return null;
}

public String getFullServerName() {
	return null;
}

	public Class getInitialConnectionClass() {
		return null;
	}

	public String getServerName() {
		return null;
	}

public NameNormalizer getServerNormalizer(){
	return null;
}

	public int getUnregisteredClientTimeout() {
		return 0;
	}

   // Return the amount of time in seconds
	public int getDeadClientTimeout() {
		return 120;
	}

public NameNormalizer getUserNormalizer(){
	return null;
}

/* If the result of the method call is null then
   there is no authenticator and therefore disabled */

public Authenticator getNickPasswordAuthenticator ()
   {
   return null;
   }
}
