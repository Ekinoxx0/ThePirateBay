package to.thepiratebay.commands.users.contact;

import java.util.Arrays;

import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.TextChannel;
import to.thepiratebay.ThePirateBay;
import to.thepiratebay.commands.abst.AbstractCmd;
import to.thepiratebay.users.PlatformUser;

public class AddContactCommand extends AbstractCmd {

	public AddContactCommand(ThePirateBay bay) {
		super(bay, "addcontact", 
				Arrays.asList("contactadd", "addcontacts", "contactsadd"), 
				"<ID> <NOM>", 
				"Ajouter un contact");
	}

	@Override
	public void run(TextChannel senderChannel, Message msg, PlatformUser sender, String senderKey, String[] args) {
		if (args.length >= 2) {
			if(args[0].equalsIgnoreCase(senderKey)) {
				senderChannel.createMessage("**Impossible de vous ajouter en contact vous même...**").subscribe();
				return;
			}
			if (!bay.getKeyDB().existKey(args[0])) {
				senderChannel.createMessage("ID Inconnu...").subscribe();
				return;
			}
			
			sender.getContacts().put(args[0], fromArgsExcept(args, 1));
			sender.save();
			
			senderChannel.createEmbed(embed -> {
				embed.setTitle("**__:family_wwgb: Liste des contacts :family_wwgb:__**");
				embed.setDescription("Ajout d'un contact ``" + args[0] + "`` : " + fromArgsExcept(args, 1));
			}).subscribe();
		} else {
			senderChannel.createMessage(this.getCompleteUsage()).subscribe();
		}
	}

}
