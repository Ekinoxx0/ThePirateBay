package to.thepiratebay.commands.users.services;

import java.awt.Color;
import java.util.Arrays;

import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.TextChannel;
import to.thepiratebay.ThePirateBay;
import to.thepiratebay.commands.abst.AbstractCmd;
import to.thepiratebay.illegal.IllegalService;
import to.thepiratebay.illegal.PlatformIllegalS;
import to.thepiratebay.users.PlatformUser;

public class LeaveCommand extends AbstractCmd {

	public LeaveCommand(ThePirateBay bay) {
		super(bay, "leave", 
				Arrays.asList("quit"), 
				"<SERVICE>", 
				"Quitter un service");
	}

	@Override
	public void run(TextChannel senderChannel, Message msg, PlatformUser sender, String senderKey, String[] args) {
		if (args.length != 1) {
			senderChannel.createMessage(this.getCompleteUsage()).subscribe();
			return;
		}

		IllegalService s = IllegalService.searchService(args[0].toLowerCase());
		if (s == null) {
			senderChannel.createMessage("Service inconnu... Utilisez les noms indiqués dans ``!services``").subscribe();
			return;
		}
		PlatformIllegalS platform = this.bay.getIllegalDB().all().filter(p -> p.getService() == s).blockFirst();

		senderChannel.createEmbed(spec -> {
			spec.setColor(new Color(238, 130, 238));
			spec.setTitle(s.getEmoji() + " **__" + s.getName() + "__**");

			if (!platform.getContactList().contains(senderKey)) {
				spec.setDescription("Vous n'êtes pas un contact de ce service...");
				return;
			}

			if (!this.bay.getKeyDB().existKey(senderKey)) {
				spec.setDescription("Une erreur est survenue... #3453");
				return;
			}
			
			platform.getContactList().remove(senderKey);
			spec.setDescription("Vous avez quitté la liste de contact de ce service ! :white_check_mark:");
			this.bay.getLive().leavedService(sender, senderKey, s);
		})
		.subscribe();

	}

}
