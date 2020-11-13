package to.thepiratebay.commands.users.profil;

import java.awt.Color;
import java.util.Arrays;
import java.util.Map;

import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.TextChannel;
import to.thepiratebay.ThePirateBay;
import to.thepiratebay.commands.abst.AbstractCmd;
import to.thepiratebay.users.PlatformUser;

public class ProfilCommand extends AbstractCmd {

	public ProfilCommand(ThePirateBay bay) {
		super(bay, "profil", 
				Arrays.asList("profils", "profile", "info"), 
				"<ID>", 
				"Informations de base sur un ID",
				Map.of("profil", "Informations de base sur vous"));
	}

	@Override
	public void run(TextChannel senderChannel, Message msg, PlatformUser sender, String senderKey, String[] args) {
		String targetKey;
		PlatformUser t = null;
		if(args.length == 0) {
			t = sender;
			targetKey = t.getMainKey();
		} else if(args.length == 1) {
			targetKey = args[0];
			if(!this.bay.getKeyDB().existKey(targetKey)) {
				senderChannel.createMessage("ID Inconnu...").subscribe();
				return;
			}
			
			if(this.bay.getKeyDB().isGroupKey(targetKey)) {
				senderChannel.createMessage("Information sur cet ID indisponible, il s'agit d'un groupement...").subscribe();//TODO Groupe
				return;
			}
			
			t = this.bay.getKeyDB().getUsers(targetKey).blockFirst();
		} else {
			senderChannel.createMessage(this.getCompleteUsage()).subscribe();
			return;
		}
		
		final PlatformUser target = t;
		
		senderChannel.createEmbed(embed -> {
			embed.setColor(Color.DARK_GRAY);
			embed.setTitle("**__Profil de __**``" + targetKey + "``");
			embed.setDescription("\n"
					+ " :family: " + target.getRelations().size() + " relation" + (target.getRelations().size() > 1 ? "s" : "") + "\n" 
					+ " :star: " + target.getNoteAverage() + "/10\n" 
					+ " :100: " + target.getNumberOfNotes() + " note" + (target.getNumberOfNotes() > 1 ? "s" : "") + " \n" 
					+ " :hourglass: " + target.onTPBFor() +  " depuis son arrivée\n"
					+ " :speech_balloon: " + target.talkative() + "\n"
					+ " :key: Gère " + target.getKeys().count().block() + " ID \n"
					+ "\n");
		})
		.doOnSuccess(m -> {
			this.bay.getLive().searchedProfil(sender, senderKey, target, targetKey);
			this.bay.getPolice().searchedProfil(sender, senderKey, target, targetKey);
		})
		.subscribe();
	}
	
}
