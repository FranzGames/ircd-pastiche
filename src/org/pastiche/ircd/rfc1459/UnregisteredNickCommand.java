package org.pastiche.ircd.rfc1459;

import org.pastiche.ircd.Command;
import org.pastiche.ircd.CommandFactory;
import org.pastiche.ircd.CommandQueue;


public class UnregisteredNickCommand extends Command {

public boolean requiresProcess() {
	return true;
}



public void process() {
	String newNick = getArgument(0);
	if (!org.pastiche.ircd.IrcdConfiguration.getInstance().getUserNormalizer().isValidName(newNick)) {
		ErrorHandler.getInstance().erroneousNickname(getSource(), newNick);
		return;
	}
	
	try {
		UnregisteredClient source = (UnregisteredClient) getSource();
		source.getServer().addUser(newNick, source);		
		source.setNick(newNick);
		
		if (source.getIrcname() != null) {
			RegisteredUser.convertToRegisteredUser(source);
		}
	} catch (org.pastiche.ircd.CollisionException ce) {
		ErrorHandler.getInstance().nicknameInUse(getSource(), newNick);
	}
}
}