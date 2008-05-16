package org.pastiche.ircd.debug;

import org.pastiche.ircd.*;

import org.pastiche.ircd.rfc1459.*;

/**
 * <p>A faked up configuration object, to pass for a real configuration
 * until I actually write the IrcdConfiguration class.
 */
public class DebugIrcdConfiguration extends IrcdConfiguration {

   public DebugIrcdConfiguration() {
   }

	public void checkConfiguration() throws ConfigurationException {
		return;
	}
}
