package org.pastiche.ircd.rfc1459;

import org.pastiche.ircd.Command;
import org.pastiche.ircd.CommandFactory;
import org.pastiche.ircd.CommandQueue;


public class RegisteredNickCommand extends Command {
public boolean requiresProcess() {
	return requiresProcess;
}

public void preProcess() {
	if (getArgument(0).equals(((RegisteredUser)getSource()).getNick().toString())) {
		requiresProcess = false;
	}
		
}

public void process() {
	RegisteredUser source = (RegisteredUser) getSource();
	String newNick = (String) getArgument(0);

	if (!org.pastiche.ircd.IrcdConfiguration.getInstance().getUserNormalizer().isValidName(newNick)) {
		ErrorHandler.getInstance().erroneousNickname(getSource(), newNick);
		return;
	}
	
	try {
		source.setNick(newNick);
	} catch (org.pastiche.ircd.CollisionException ce) {
		ErrorHandler.getInstance().nicknameInUse(getSource(), newNick); 
	}
}

	private boolean requiresProcess = true;
}