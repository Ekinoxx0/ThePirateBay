package to.thepiratebay.commands.admin;

import java.awt.Color;
import java.util.Arrays;

import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.TextChannel;
import to.thepiratebay.ThePirateBay;
import to.thepiratebay.commands.abst.AbstractCmd;
import to.thepiratebay.users.PlatformUser;

public class ProfilCommand extends AbstractCmd {

	public ProfilCommand(ThePirateBay bay) {
		super(bay, 
				"profil", 
				Arrays.asList("info", "profile"), 
				"<ID>", 
				"Information admin de l'ID");
	}

	@Override
	public void run(TextChannel senderChannel, Message msg, PlatformUser sender, String senderKey, String[] args) {
		String targetKey;
		PlatformUser t = null;
		if(args.length == 1) {
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
			String keys = "";
			for(String key : target.getKeys().toIterable()) {
				keys += " :id: ``" + key + "`` " + 
						(this.bay.getKeyDB().isGroupKey(key) ? "(GROUP)" : (key.equals(target.getMainKey()) ? "(MAIN)" : "(SOLO)")) + "\n";
			}
			embed.setDescription("\n"
					+ " :hash: Discord: @" + target.getUsername() + "#" + target.getMember().block().getDiscriminator() + "\n"
					+ " :hash: Nom RP: " + target.getRPName() + "\n"
					+ " :signal_strength: Protection: " + target.getProtection() + "\n"
					+ " :yellow_heart: " + (target.isPremium() ? "PREMIUM" : "Non Premium") + "\n"
					+ " :clipboard: " + (target.hasAnyWhitelist() ? "Whitelist" : "Aucune Whitelist") + "\n"
					+ "\n"
					+ " :key: Gère " + target.getKeys().count().block() + " ID : \n"
					+ keys
					+ "\n"
					+ " :speech_balloon: " + target.talkative() + "\n"
					+ " :eye_in_speech_bubble: " + target.getSentMsgNumber() + " messages envoyés\n"
					+ " :eye_in_speech_bubble: " + target.getReceivedMsgNumber() + " messages reçus\n"
					+ "\n"
					+ " :family: " + target.getRelations().size() + " relation" + (target.getRelations().size() > 1 ? "s" : "") + "\n" 
					+ " :star: " + target.getNoteAverage() + "/10\n" 
					+ " :100: " + target.getNumberOfNotes() + " note" + (target.getNumberOfNotes() > 1 ? "s" : "") + " \n" 
					+ " :hourglass: " + target.onTPBFor() +  " depuis son arrivée\n"
					+ "\n");
		})
		.doOnSuccess(m -> {
			this.bay.getLive().searchedProfil(sender, senderKey, target, targetKey);
			this.bay.getPolice().searchedProfil(sender, senderKey, target, targetKey);
		})
		.subscribe();
	}
	
}
