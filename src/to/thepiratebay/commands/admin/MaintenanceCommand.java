package to.thepiratebay.commands.admin;

import java.awt.Color;
import java.util.Arrays;

import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.TextChannel;
import to.thepiratebay.ThePirateBay;
import to.thepiratebay.commands.abst.AbstractCmd;
import to.thepiratebay.users.PlatformUser;

public class MaintenanceCommand extends AbstractCmd {

	public MaintenanceCommand(ThePirateBay bay) {
		super(bay, 
				"maintenance", 
				Arrays.asList(), 
				"", 
				"Diffuser un message de maintenance");
	}

	@Override
	public void run(TextChannel senderChannel, Message msg, PlatformUser sender, String senderKey, String[] args) {
		for(PlatformUser user : bay.getUsersDB().getUsers().toIterable()) {
			user.getChannel().flatMap(targetC -> targetC.createEmbed(embed -> {
				embed.setTitle("**Maintenance**");
				embed.setColor(Color.red);
				embed.setDescription("Lancement d'une maintenance à durée indéterminée...");
			})).subscribe();
		}
	}
	
}
