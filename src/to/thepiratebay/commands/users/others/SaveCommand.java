package to.thepiratebay.commands.users.others;

import java.awt.Color;
import java.util.Arrays;

import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.TextChannel;
import to.thepiratebay.ThePirateBay;
import to.thepiratebay.commands.abst.AbstractCmd;
import to.thepiratebay.users.PlatformUser;

public class SaveCommand extends AbstractCmd {

	public SaveCommand(ThePirateBay bay) {
		super(bay, "save", 
				Arrays.asList(), 
				"", 
				"Commande de debug");
	}

	@Override
	public void run(TextChannel senderChannel, Message msg, PlatformUser sender, String senderKey, String[] args) {
		try {
			sender.save();
			senderChannel.createEmbed(embed -> {
				embed.setColor(Color.GREEN);
				embed.setTitle("**__Sauvegarde de vos données__**");
				embed.setDescription(":white_check_mark:");
			}).subscribe();
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}

}
