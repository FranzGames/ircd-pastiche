package org.pastiche.ircd.rfc1459;

import org.pastiche.ircd.Command;
import org.pastiche.ircd.CommandFactory;
import org.pastiche.ircd.CommandQueue;


import java.io.BufferedReader;
import java.io.InputStreamReader;

public class MotdCommand extends org.pastiche.ircd.Command {


public void preProcess() {
	BufferedReader motd = null;
	
	try {
		java.io.InputStream motdin = MotdCommand.class.getResourceAsStream("/motd.txt");

		if (motdin == null)
			throw new java.io.FileNotFoundException("MOTD file not found");

		motd = new BufferedReader(new InputStreamReader(
			motdin));

		String line = null;
		ReplyHandler.getInstance().motdStart(getSource().getServer(), getSource());

		while ((line = motd.readLine()) != null) {
			ReplyHandler.getInstance().motd(getSource(), line);
		}
		ReplyHandler.getInstance().endOfMotd(getSource());
	} catch (java.io.IOException ioe) {
		ErrorHandler.getInstance().noMotd(getSource());
	} finally {
		try { if (motd != null) motd.close(); } catch (Exception e) {}
	}
}


}