package to.thepiratebay.commands.admin;

import java.util.Arrays;

import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.TextChannel;
import to.thepiratebay.ThePirateBay;
import to.thepiratebay.commands.abst.AbstractCmd;
import to.thepiratebay.stc.StaticFunction;
import to.thepiratebay.users.PlatformUser;

public class InfoBcCommand extends AbstractCmd {

	public InfoBcCommand(ThePirateBay bay) {
		super(bay, 
				"infobc", 
				Arrays.asList("bcinfo"), 
				"", 
				"Mettre à jour les #informations");
	}

	@Override
	public void run(TextChannel senderChannel, Message msg, PlatformUser sender, String senderKey, String[] args) {
		StaticFunction.updateInformation(this.bay.getClient());
	}

}
