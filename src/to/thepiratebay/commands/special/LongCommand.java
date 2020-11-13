package to.thepiratebay.commands.special;

import java.util.Arrays;

import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.TextChannel;
import to.thepiratebay.ThePirateBay;
import to.thepiratebay.commands.abst.AbstractCmd;
import to.thepiratebay.users.PlatformUser;

public class LongCommand extends AbstractCmd {

	public LongCommand(ThePirateBay bay) {
		super(bay, "long", 
				Arrays.asList(), 
				"", 
				"Commande très longue.",
				false);
	}

	@Override
	public void run(TextChannel senderChannel, Message msg, PlatformUser sender, String senderKey, String[] args) {
		try {
			Thread.sleep(8000L);
			senderChannel.createMessage("Fin du test (8sec)").subscribe();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
