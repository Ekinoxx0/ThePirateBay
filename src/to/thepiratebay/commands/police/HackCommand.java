package to.thepiratebay.commands.police;

import java.util.Arrays;
import java.util.Map;

import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.TextChannel;
import to.thepiratebay.ThePirateBay;
import to.thepiratebay.commands.abst.AbstractCmd;
import to.thepiratebay.users.PlatformUser;

public class HackCommand extends AbstractCmd {

	public HackCommand(ThePirateBay bay) {
		super(bay, "hack", 
				Arrays.asList("hacks", "pirate", "piratage"), 
				"<ID>", 
				"Permet de pirater un utilisateur",
				Map.of("hacks", "Voir les informations de piratage"));
	}

	@Override
	public void run(TextChannel senderChannel, Message msg, PlatformUser sender, String senderKey, String[] args) {
		senderChannel.createMessage("En construction...").subscribe();
		//TODO
	}
	
	
	
}
