package org.pastiche.ircd.rfc1459;

/**
 * Insert the type's description here.
 * Creation date: (8/04/2001 4:00:58 PM)
 * @author: Charles Miller
 */
public class ListCommand extends org.pastiche.ircd.Command {
public void process() {
	java.util.Iterator channels = getSource().getServer().getChannels();

	while (channels.hasNext()) {
		Channel channel = (Channel)channels.next();

		if (channel.isVisible() || channel.isOnChannel(getSource())) {
			ReplyHandler.getInstance().list(getSource(), channel.getName(), channel.getMembers().length, channel.getTopic());
		} else if (channel.isPrivate()) {
			ReplyHandler.getInstance().list(getSource(), "Prv", channel.getMembers().length, null);
		}
	}
	ReplyHandler.getInstance().endOfList(getSource());
}
public boolean requiresProcess() {
	return true;
}
}
