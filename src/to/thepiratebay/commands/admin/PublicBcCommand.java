package to.thepiratebay.commands.admin;

import java.util.Arrays;

import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.TextChannel;
import to.thepiratebay.ThePirateBay;
import to.thepiratebay.commands.abst.AbstractCmd;
import to.thepiratebay.stc.StaticFunction;
import to.thepiratebay.users.PlatformUser;

public class PublicBcCommand extends AbstractCmd {

	public PublicBcCommand(ThePirateBay bay) {
		super(bay, 
				"publicbc", 
				Arrays.asList("bcpublic"), 
				"", 
				"Post les info de base du #public");
	}

	@Override
	public void run(TextChannel senderChannel, Message msg, PlatformUser sender, String senderKey, String[] args) {
		StaticFunction.sendNoticePublic(bay.getClient());
	}
	
}
