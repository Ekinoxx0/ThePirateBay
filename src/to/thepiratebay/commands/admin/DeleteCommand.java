package to.thepiratebay.commands.admin;

import java.util.Arrays;

import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.TextChannel;
import to.thepiratebay.ThePirateBay;
import to.thepiratebay.commands.abst.AbstractCmd;
import to.thepiratebay.users.PlatformUser;

public class DeleteCommand extends AbstractCmd {

	public DeleteCommand(ThePirateBay bay) {
		super(bay, 
				"delete", 
				Arrays.asList("cleanup"), 
				"<DISCORD_ID>", 
				"Supprime un utilisateur");
	}

	@Override
	public void run(TextChannel senderChannel, Message msg, PlatformUser sender, String senderKey, String[] args) {
		if(args.length == 1) {
			try {
				long id = Long.parseLong(args[0]);
				
				try {
					bay.getUsersDB().delete(id);
					senderChannel.createMessage("User cleanup... :white_check_mark:").subscribe();
				} catch(NullPointerException ex) {
					senderChannel.createMessage("Utilisateur inconnu").subscribe();
					return;
				}
			} catch(NumberFormatException e) {
				senderChannel.createMessage("Il faut un chiffre comme arg").subscribe();
				return;
			}
		} else {
			senderChannel.createMessage("Mauvais format").subscribe();
			return;
		}
	}
	
}
