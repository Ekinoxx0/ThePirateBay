package to.thepiratebay.commands.users.contact;

import java.util.Arrays;
import java.util.Map.Entry;

import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.TextChannel;
import to.thepiratebay.ThePirateBay;
import to.thepiratebay.commands.abst.AbstractCmd;
import to.thepiratebay.users.PlatformUser;

public class ContactsCommand extends AbstractCmd {

	public ContactsCommand(ThePirateBay bay) {
		super(bay, "contact", 
				Arrays.asList("contacts", "contactlist", "listcontact", "contacte"), 
				"", 
				"Liste des contacts");
	}

	@Override
	public void run(TextChannel senderChannel, Message msg, PlatformUser sender, String senderKey, String[] args) {
		if(args.length != 0) senderChannel.createMessage(this.getCompleteUsage()).subscribe();
		
		senderChannel.createEmbed(embed -> {
			embed.setTitle("**__:family_wwgb: Liste des contacts :family_wwgb:__**");

			String contacts = "";
			for(Entry<String, String> entry : sender.getContacts().entrySet()) {
				contacts += entry.getValue() + " -> ``" + entry.getKey() + "``";
			}
			if(contacts.length() == 0) {
				contacts = "*Aucun contact...*\n ``!addcontact <ID> <NOM>``";
			}
			embed.setDescription(contacts);
		}).subscribe();
	}

}
