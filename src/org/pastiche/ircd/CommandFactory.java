package org.pastiche.ircd;

import java.util.Map;

/**
 * <p>The CommandFactory creates Command objects by parsing a String
 * that contains a (possibly) valid ircd command. The Factory pattern
 * allows us to plug in, chop, change and whatever the entire operations
 * of a server even between rehashes.
 *
 * <p>There is a possible performance hit in getCommandFactory(String) -
 * it adds an extra hashtable lookup every time someone needs to access
 * the factory. Perhaps there's a better way to do this? Certainly, we
 * don't want any other structures caching CommandFactory objects, or
 * a rehash could send the server insane.
 *
 */
public class CommandFactory {
	private static java.util.Map instances = new java.util.HashMap();

	private Map commandMap = null;

public static CommandFactory getCommandFactory(String identifier) {
	CommandFactory factory = null;

	if ((factory = (CommandFactory) instances.get(identifier)) != null)
		return factory;

	factory = new CommandFactory(IrcdConfiguration.getInstance().getCommandMap(identifier), 
			IrcdConfiguration.getInstance().getDefaultCommand(identifier)); 

	instances.put(identifier, factory);
	return factory;
}



/** REFACTOR ME */
public Command createCommand(String commandLine) {
	if (commandLine.length() == 0)
		return null;

	// I Hate Writing String Parsers. If anyone wants to clean this
	// up, please do.

	// Using a StringTokenizer here seems like using the corner of
	// a flat-headed screwdriver on a phillips screw. It'd work,
	// but it's still the Wrong Tool.

	String nextToken = null;
	String source = null;
	Command command = null;

	int pos = 0;
	int start = -1;

	if (commandLine.charAt(0) == ':') {
		pos = commandLine.indexOf(' ');
		source = commandLine.substring(1, pos);
	}

	for (; pos < commandLine.length(); pos++) {
		if (commandLine.charAt(pos) == ' ') {
			if (start != -1)
				break;
			else
				continue;
		}

		if (start == -1)
			start = pos;
	}
	if (start == -1)
		return null;

	nextToken = commandLine.substring(start, pos);

	if (Character.isDigit(nextToken.charAt(start))) {
		throw new RuntimeException("Numerics not supported");
	} else {
		command = getCommand(commandLine.substring(start, pos));
		command.setName(commandLine.substring(start, pos));
	}

	while (pos < commandLine.length()) {
		start = -1;

		for (; pos < commandLine.length(); pos++) {
			if (commandLine.charAt(pos) == ' ') {
				if (start != -1)
					break;
				else
					continue;
			}

			if ((commandLine.charAt(pos) == ':') && (start == -1)) {

				start = pos + 1;
				pos = commandLine.length() - 1;

				while (commandLine.charAt(pos) == ' ') {
					pos--;
				}
			}

			if (start == -1)
				start = pos;
		}
		if (start != -1) {
			command.addArgument(commandLine.substring(start, pos));
		}
	}

	return command;
}

	private Command defaultCommand = null;

private CommandFactory(Map commandMap, Command defaultCommand) {
	this.commandMap = commandMap;
	this.defaultCommand = defaultCommand;
}

public java.util.Iterator getAvailableCommands() {
	return commandMap.keySet().iterator();
}

private Command getCommand(String commandName) {
	Command command = (Command) commandMap.get(commandName.toUpperCase());

	if (command == null) {
		return (Command) defaultCommand.clone();
	}

	return (Command) command.clone();
}
}