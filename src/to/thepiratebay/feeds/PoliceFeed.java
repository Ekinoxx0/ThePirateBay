package to.thepiratebay.feeds;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import discord4j.core.object.entity.Message;
import to.thepiratebay.ThePirateBay;
import to.thepiratebay.commands.police.HackCommand;
import to.thepiratebay.feeds.abst.AbstractChannelFeed;
import to.thepiratebay.illegal.IllegalService;
import to.thepiratebay.stc.StaticID;
import to.thepiratebay.users.PlatformUser;

public class PoliceFeed extends AbstractChannelFeed {

	private Random random = new Random();

	private PoliceConfig config;
	private static File saveFile = new File("." + File.separator + "police.json");

	public PoliceFeed(ThePirateBay bay) throws JsonSyntaxException, JsonIOException, FileNotFoundException {
		super(bay, true, 
				Arrays.asList(
				new HackCommand(bay)), 
				StaticID.POLICE);

		if (!saveFile.exists()) {
			saveFile.getParentFile().mkdirs();
			try {
				saveFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			this.config = new PoliceConfig();
			this.save();
		}

		this.config = ThePirateBay.GSON.fromJson(new FileReader(saveFile), PoliceConfig.class);
		if (this.config != null) {
			this.log.info("Finished loading PoliceConfig from PoliceFeed..");
		} else {
			this.log.error("Null config !!! Not loaded correctly");
			throw new NullPointerException();
		}
	}

	private void save() {
		try {
			FileWriter writer = new FileWriter(saveFile);
			writer.write(ThePirateBay.GSON.toJson(this.config));
			writer.close();
			log.info("Succesfuly saved IdDB");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void receiveMsg(PlatformUser sender, String txt, Message msg, String senderKey, String targetKey) {
		if (!this.config.isActive)
			return;

		int hit = random.nextInt(100) + 1;

		if (hit <= this.config.decryptMsgChance * sender.getProtection().getMultiply()) {
		} else if (hit <= this.config.decryptMsgPartialChance) {
			List<String> words = Arrays.asList(txt.split(" "));
			Collections.shuffle(words);
			String shuffled = "";
			for (String word : words) {
				shuffled += word + " ";
			}
			txt = shuffled;
		} else {
			return;
		}

		hit = random.nextInt(100) + 1;
		if (hit > this.config.decryptSenderChance * sender.getProtection().getMultiply()) {
			senderKey = "????";
		}

		hit = random.nextInt(100) + 1;
		if (hit > this.config.decryptTargetChance * this.bay.getKeyDB().getUsers(targetKey).blockFirst().getProtection().getMultiply()) {
			targetKey = "????";
		}

		sendUncryptedMsg(txt, msg, senderKey, targetKey);
	}

	private void sendUncryptedMsg(String txt, Message msg, String senderKey, String targetKey) {
		this.createEmbed(embed -> {
			embed.setTitle(":police_car: **__POLICE REPORT__** :police_car:");
			embed.setColor(Color.BLUE);
			embed.setDescription("``" + senderKey + "`` -> ``" + targetKey + "``\n" + txt);
		}).subscribe();
	}

	public void addedNote(PlatformUser sender, String s, PlatformUser target, String t, int n) {
		if (!this.config.isActive)
			return;
		final String senderKey;
		final String targetKey;
		
		int hit = random.nextInt(100) + 1;
		if(hit > this.config.decryptNote * sender.getProtection().getMultiply()) return;
		
		hit = random.nextInt(100) + 1;
		if (hit > this.config.decryptSenderChance * sender.getProtection().getMultiply()) {
			senderKey = "????";
		} else {
			senderKey = s;
		}

		hit = random.nextInt(100) + 1;
		if (hit > this.config.decryptTargetChance * target.getProtection().getMultiply()) {
			targetKey = "????";
		} else {
			targetKey = t;
		}

		this.createEmbed(embed -> {
			embed.setTitle(":police_car: **__POLICE REPORT__** :police_car:");
			embed.setColor(Color.BLUE);
			embed.setDescription("``" + senderKey + "`` -> ``" + targetKey + "``\n"
					+ "Note de " + n + "/10");
		}).subscribe();
	}

	public void searchedProfil(PlatformUser sender, String s, PlatformUser target, String t) {
		if (!this.config.isActive)
			return;
		final String senderKey;
		final String targetKey;
		
		int hit = random.nextInt(100) + 1;
		if(hit > this.config.decryptSearch * sender.getProtection().getMultiply()) return;
		
		hit = random.nextInt(100) + 1;
		if (hit > this.config.decryptSenderChance * sender.getProtection().getMultiply()) {
			senderKey = "????";
		} else {
			senderKey = s;
		}

		hit = random.nextInt(100) + 1;
		if (hit > this.config.decryptTargetChance * target.getProtection().getMultiply()) {
			targetKey = "????";
		}else {
			targetKey = t;
		}

		this.createEmbed(embed -> {
			embed.setTitle(":police_car: **__POLICE REPORT__** :police_car:");
			embed.setColor(Color.BLUE);
			embed.setDescription("**Recherche de Profil :**\n"
					+ "``" + senderKey + "`` -> ``" + targetKey + "``");
		}).subscribe();
	}

	public void joinedService(PlatformUser sender, String se, IllegalService s) {
		if (!this.config.isActive)
			return;
		final String senderKey;
		
		int hit = random.nextInt(100) + 1;
		if(hit > this.config.decryptJoin * sender.getProtection().getMultiply()) return;
		
		hit = random.nextInt(100) + 1;
		if (hit > this.config.decryptSenderChance * sender.getProtection().getMultiply()) {
			senderKey = "????";
		} else {
			senderKey = se;
		}

		this.createEmbed(embed -> {
			embed.setTitle(":police_car: **__POLICE REPORT__** :police_car:");
			embed.setColor(Color.BLUE);
			embed.setDescription("``" + senderKey + "`` rejoint le service " + s.getName());
		}).subscribe();
	}

	/*
	 * 
	 */

	private class PoliceConfig {
		public boolean isActive = false;
		public int decryptMsgChance = 8;
		public int decryptMsgPartialChance = 20;
		public int decryptNote = 30;
		public int decryptSearch = 30;
		public int decryptJoin = 10;
		public int decryptSenderChance = 40;
		public int decryptTargetChance = 40;
	}

}
