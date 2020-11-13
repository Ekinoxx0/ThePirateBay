package to.thepiratebay.feeds;

import java.awt.Color;
import java.util.Arrays;

import discord4j.core.object.entity.Attachment;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import to.thepiratebay.ThePirateBay;
import to.thepiratebay.feeds.abst.AbstractChannelFeed;
import to.thepiratebay.illegal.IllegalService;
import to.thepiratebay.stc.StaticID;
import to.thepiratebay.users.PlatformUser;

public class LiveFeed extends AbstractChannelFeed {
	
	public LiveFeed(ThePirateBay bay) {
		super(bay, true, Arrays.asList(), StaticID.LIVE);
	}

	public void newUser(Member member, String key) {
		this.createEmbed(embed -> {
			embed.setTitle("**__Nouvel Utilisateur__**");
			embed.setDescription("" + member.getUsername() + " -> ``" + key + "``");
		}).subscribe();
	}

	public void processMsg(String txt, Message msg, String senderKey, String targetKey) {
		this.createEmbed(embed -> {
			String composedTxt = txt;
			if(msg.getAttachments().size() > 0) {
				composedTxt += "\n\n**__L'utilisateur à également ajouter des fichiers à ce message :__**\n";
			}
			for (Attachment a : msg.getAttachments()) {
				composedTxt += a.getProxyUrl();
			}
			embed.setTitle("**__MESSAGE ANONYME__  |  ** ``" + senderKey + "`` -> ``" + targetKey + "``");
			embed.setDescription(composedTxt);
		}).subscribe();
	}

	public void nowIgnore(String senderKey, String targetKey) {
		this.createEmbed(embed -> {
			embed.setTitle("**__IGNORE__  |  ** ``" + senderKey + "`` -> ``" + targetKey + "``");
			embed.setDescription("Suspicion harcèlement ?");
			embed.setColor(Color.YELLOW);
		}).subscribe();
	}

	public void ignoredMsg(String txt, Message msg, String senderKey, String targetKey) {
		this.createEmbed(embed -> {
			String composedTxt = txt;
			if(msg.getAttachments().size() > 0) {
				composedTxt += "\n\n**__L'utilisateur à également ajouter des fichiers à ce message :__**\n";
			}
			for (Attachment a : msg.getAttachments()) {
				composedTxt += a.getProxyUrl();
			}
			embed.setTitle("**__MESSAGE IGNORE__  |  ** ``" + senderKey + "`` -> ``" + targetKey + "``");
			embed.setDescription(composedTxt);
			embed.setColor(Color.YELLOW);
		}).subscribe();
	}

	public void addedNote(PlatformUser user, String senderKey, PlatformUser target, String targetKey, int n) {
		this.createEmbed(embed -> {
			embed.setTitle("**__Note__  |  ** ``" + senderKey + "`` -> ``" + targetKey + "``");
			embed.setDescription("**" + n + "/10** (Avg " + target.getNoteAverage() + "/10)");
		}).subscribe();
	}

	public void searchedProfil(PlatformUser user, String senderKey, PlatformUser target, String targetKey) {
		this.createEmbed(embed -> {
			embed.setTitle("**__Recherche de profil__**");
			embed.setDescription("``" + senderKey + "`` cherche ``" + targetKey + "``");
		}).subscribe();
	}

	public void joinedService(PlatformUser user, String senderKey, IllegalService s) {
		this.createEmbed(embed -> {
			embed.setTitle("**__Liste de contact service : " + s.toString() + "__**");
			embed.setDescription(s.getEmoji() + " ``" + senderKey + "`` rejoint **" + s.getName() + "**");
		}).subscribe();
	}

	public void leavedService(PlatformUser user, String senderKey, IllegalService s) {
		this.createEmbed(embed -> {
			embed.setTitle("**__Liste de contact service : " + s.toString() + "__**");
			embed.setDescription(s.getEmoji() + " ``" + senderKey + "`` quitte **" + s.getName() + "**");
		}).subscribe();
	}

}
