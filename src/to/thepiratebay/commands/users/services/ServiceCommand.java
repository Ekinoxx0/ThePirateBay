package to.thepiratebay.commands.users.services;

import java.awt.Color;
import java.util.Arrays;
import java.util.Map;

import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.TextChannel;
import to.thepiratebay.ThePirateBay;
import to.thepiratebay.commands.abst.AbstractCmd;
import to.thepiratebay.illegal.IllegalService;
import to.thepiratebay.users.PlatformUser;

public class ServiceCommand extends AbstractCmd {
	
	public ServiceCommand(ThePirateBay bay) {
		super(bay, "service", 
				Arrays.asList("services"), 
				"<SERVICE>", 
				"Voir des informations sur un service",
				Map.of("services", "Voir la liste des services"));
	}

	@Override
	public void run(TextChannel senderChannel, Message msg, PlatformUser sender, String senderKey, String[] args) {
		if (args.length == 0) {
			senderChannel.createEmbed(spec -> {
				spec.setTitle(":spy:   **__SERVICES ILLEGAUX__**   :spy:");
				spec.setColor(new Color(238, 130, 238));
				spec.setDescription(bay.getIllegalDB().getAllDescription(sender));
			}).subscribe();
		} else if (args.length == 1) {
			IllegalService s = IllegalService.searchService(args[0].toLowerCase());
			if (s == null) {
				senderChannel.createMessage("Service inconnu... Utilisez les noms indiquer dans ``!services``").subscribe();
				return;
			}
			senderChannel.createEmbed(spec -> {
				spec.setColor(new Color(238, 130, 238));
				spec.setTitle(s.getEmoji() + " **__" + s.getName() + "__**");
				spec.setDescription(this.bay.getIllegalDB().getUniqueDescription(s, sender));
			}).subscribe();
		} else {
			senderChannel.createMessage(this.getCompleteUsage()).subscribe();
		}
	}
	
}
