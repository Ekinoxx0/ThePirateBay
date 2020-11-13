package to.thepiratebay.commands.admin;

import java.util.Arrays;

import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.TextChannel;
import to.thepiratebay.ThePirateBay;
import to.thepiratebay.commands.abst.AbstractCmd;
import to.thepiratebay.stc.StaticID;
import to.thepiratebay.users.PlatformUser;

public class BroadcastCommand extends AbstractCmd {

	public BroadcastCommand(ThePirateBay bay) {
		super(bay, 
				"broadcast", 
				Arrays.asList("bc"), 
				"<MESSAGE>", 
				"Diffuser un message dans #annonces");
	}

	@Override
	public void run(TextChannel senderChannel, Message msg, PlatformUser sender, String senderKey, String[] args) {
		this.bay.getClient().getChannelById(StaticID.ANNONCE)
				.cast(TextChannel.class)
				.flatMap(targetC -> 
					targetC.createMessage(fromArgs(args).replace("/", ":"))
				)
				.subscribe();		
	}

}
