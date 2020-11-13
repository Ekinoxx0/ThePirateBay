package to.thepiratebay.commands.special;

import java.util.Arrays;

import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.TextChannel;
import to.thepiratebay.ThePirateBay;
import to.thepiratebay.commands.abst.AbstractCmd;
import to.thepiratebay.users.PlatformUser;

public class CleanChannelCommand extends AbstractCmd {

	public CleanChannelCommand(ThePirateBay bay) {
		super(bay, "cleanchannel", 
				Arrays.asList("pop"), 
				"", 
				"Nettoie un salon.",
				false);
	}

	@Override
	public void run(TextChannel senderChannel, Message msg, PlatformUser sender, String senderKey, String[] args) {
		int i = Integer.MAX_VALUE;
		if(args.length == 1) {
			try {
				i = Integer.parseInt(args[0]);
			} catch(NumberFormatException ex) {}
		}
		for(Message m : senderChannel.getMessagesBefore(msg.getId()).collectList().block()) {
			if(i-- <= 0) return;
			m.delete().subscribe();
		}
	}

}
