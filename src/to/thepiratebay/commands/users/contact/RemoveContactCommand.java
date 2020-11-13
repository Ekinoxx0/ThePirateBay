package to.thepiratebay.commands.users.contact;

import java.util.Arrays;

import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.TextChannel;
import to.thepiratebay.ThePirateBay;
import to.thepiratebay.commands.abst.AbstractCmd;
import to.thepiratebay.users.PlatformUser;

public class RemoveContactCommand extends AbstractCmd {

	public RemoveContactCommand(ThePirateBay bay) {
		super(bay, "rmcontact", 
				Arrays.asList("contactrm", "rmcontacts", "contactsrm", "delcontact", "contactdel", "delcontacts", "contactsdel"), 
				"<ID>", 
				"Supprimer un contact");
	}

	@Override
	public void run(TextChannel senderChannel, Message msg, PlatformUser sender, String senderKey, String[] args) {
		if (args.length >= 2) {
			if(args[0].equalsIgnoreCase(senderKey)) {
				senderChannel.createMessage("**Impossible de vous supprimer des contact vous même...**").subscribe();
				return;
			}
			if (!bay.getKeyDB().existKey(args[0])) {
				senderChannel.createMessage("ID Inconnu...").subscribe();
				return;
			}
			
			if(!sender.getContacts().containsKey(args[0])) {
				senderChannel.createMessage("**Vous n'avez pas cette personne en contact...**").subscribe();
				return;
			}
			
			sender.getContacts().remove(args[0]);
			sender.save();
			
			senderChannel.createEmbed(embed -> {
				embed.setTitle("**__:family_wwgb: Liste des contacts :family_wwgb:__**");
				embed.setDescription("Suppresion d'un contact ``" + args[0] + "`` : " + fromArgsExcept(args, 1));
			}).subscribe();
		} else {
			senderChannel.createMessage(this.getCompleteUsage()).subscribe();
		}
	}

}
