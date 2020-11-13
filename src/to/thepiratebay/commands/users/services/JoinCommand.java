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

public class JoinCommand extends AbstractCmd {

	public JoinCommand(ThePirateBay bay) {
		super(bay, "join", 
				Arrays.asList(), 
				"<SERVICE>", 
				"Rejoindre un service");
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

			if (!platform.isActive()) {
				spec.setDescription("**Impossible de joindre un service indisponible...**");
				return;
			}

			if (platform.isWhitelisted() && !sender.isWhitelistedFor(platform.getService())) {
				spec.setDescription("**Impossible de joindre un service privé...**\n"
						+ "Pour rejoindre ce service vous devez être whitelist par un administrateur ThePirateBay\n\n"
						+ "Utilisez ``!report <MESSAGE>``");
				return;
			}

			if (platform.getContactList().contains(senderKey)) {
				spec.setDescription("Vous êtes déjà un contact de ce service...");
				return;
			}

			if (!this.bay.getKeyDB().existKey(senderKey)) {
				spec.setDescription("Une erreur est survenue... #3453");
				return;
			}
			
			if(sender.isTooRecent()) {
				spec.setDescription(
						"Vous êtes sur ThePirateBay depuis **trop peu de temps**.\n"
						+ "Commencez d'abord par vous familiariser avec le système, \n"
						+ "faites vous quelques contacts et discuter avec d'autres personnes.\n"
						+ "\n"
						+ "**Si vous êtes quelqu'un de spécial, utilisez ``!report <MESSAGE>``**");
				return;
			}
			
			platform.getContactList().add(senderKey);
			spec.setDescription("Vous avez rejoint la liste de contact de ce service ! :white_check_mark:\n\n"
					+ "*Pour quitter la liste, utilisez* ``!leave <SERVICE>``");
			this.bay.getLive().joinedService(sender, senderKey, s);
			this.bay.getPolice().joinedService(sender, senderKey, s);
		})
		.subscribe();

	}

}
