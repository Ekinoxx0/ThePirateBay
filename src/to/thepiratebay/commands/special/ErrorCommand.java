package to.thepiratebay.commands.special;

import java.util.Arrays;

import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.TextChannel;
import to.thepiratebay.ThePirateBay;
import to.thepiratebay.commands.abst.AbstractCmd;
import to.thepiratebay.users.PlatformUser;

public class ErrorCommand extends AbstractCmd {

	public ErrorCommand(ThePirateBay bay) {
		super(bay, "error", 
				Arrays.asList(), 
				"", 
				"Déclenche une erreur.",
				false);
	}

	@Override
	public void run(TextChannel senderChannel, Message msg, PlatformUser sender, String senderKey, String[] args) {
		throw new IllegalStateException("ERREUR DE TEST");
	}

}
