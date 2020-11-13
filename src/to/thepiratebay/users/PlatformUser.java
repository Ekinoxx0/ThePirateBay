package to.thepiratebay.users;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.joda.time.Interval;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import discord4j.core.object.entity.Channel;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Role;
import discord4j.core.object.entity.TextChannel;
import discord4j.core.object.util.Snowflake;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.Logger;
import reactor.util.Loggers;
import to.thepiratebay.Main;
import to.thepiratebay.ThePirateBay;
import to.thepiratebay.illegal.IllegalService;
import to.thepiratebay.illegal.ProtectionLevel;
import to.thepiratebay.stc.StaticID;
import to.thepiratebay.stc.StaticMinimum;

public class PlatformUser {

	private static final PeriodFormatter formatter = new PeriodFormatterBuilder()
			 .appendYears()
			 .appendSuffix(" année ", " années ")
			 .appendMonths()
			 .appendSuffix(" mois ")
		     .appendDays()
		     .appendSuffix(" jour ", " jours ")
		     .appendHours()
		     .appendSuffix(" heure ", " heures ")
		     .appendMinutes()
		     .appendSuffix(" minute ", " minutes ")
		     .toFormatter();
	private static final Logger log = Loggers.getLogger(PlatformUser.class);
	
	public static PlatformUser loadFromFile(File file) throws JsonSyntaxException, JsonIOException, FileNotFoundException, IllegalStateException {
		if(file == null || !file.exists() || !file.canRead()) throw new IllegalStateException("Unable to load from " + file.getName());
		PlatformUser user = ThePirateBay.GSON.fromJson(new FileReader(file), PlatformUser.class);
		
		//New fields

		if(user.notes == null) {
			user.notes = new HashMap<Long, Integer>();
		}
		
		if(user.relationWith == null) {
			user.relationWith = new HashSet<String>();
		}

		if(user.contactList == null) {
			user.contactList = new HashMap<String, String>();
		}
		
		if(user.arrivalDate == 0) {
			user.arrivalDate = System.currentTimeMillis();
		}
		
		if(user.whitelistedFor == null) {
			user.whitelistedFor = new ArrayList<IllegalService>();
		}
		
		if(user.protection == null) {
			user.protection = ProtectionLevel.NOTHING;
		}
		
		if(user.ignores == null) {
			user.ignores = new ArrayList<String>();
		}
		
		return user;
	}

	/*
	 * 
	 */
	
	
	private long id;
	private final String username;
	private final long roleId;
	private final long channelId;
	
	private String rpName = null;
	private long arrivalDate;
	
	private boolean isPremium = false;
	private boolean bypassOlderness = false;
	private ProtectionLevel protection = ProtectionLevel.NOTHING;
	private List<IllegalService> whitelistedFor = new ArrayList<IllegalService>();
	private List<String> ignores = new ArrayList<String>();
	
	private int sentMsgNumber = 0;
	private int receivedMsgNumber = 0;
	private HashMap<Long, Integer> notes;
	
	private String lastMessageSender;
	private Set<String> relationWith;
	private HashMap<String, String> contactList;
	

	public PlatformUser(Member member, Role r, TextChannel tc) throws IllegalStateException {
		this.id = member.getId().asLong();
		this.username = member.getUsername();
		this.roleId = r.getId().asLong();
		this.channelId = tc.getId().asLong();
		this.save();
	}
	
	public void save() {
		try {
			FileWriter writer = new FileWriter(getSaveFile());
			writer.write(ThePirateBay.GSON.toJson(this));
			writer.close();
			log.info("Saved " + this.getUsername() + " successfuly");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String getMainKey() {
		String channelKey = this.getChannel().block().getName();
		if(Main.BAY.getKeyDB().isValid(channelKey)) {
			return channelKey;
		} else {
			log.error("Not matching main key and channel name, correct that immediatly !!!");
			Iterator<String> keys = Main.BAY.getKeyDB().getKeysFromUser(this).toIterable().iterator();
			if(keys.hasNext()) {
				return keys.next();
			}
			return null;
		}
	}
	
	public File getSaveFile() {
		return new File(UsersDB.saveFolder, this.id + ".json");
	}

	public long getId() {
		return this.id;
	}

	public String getUsername() {
		return this.username;
	}
	
	public String getRPName() {
		return rpName == null ? "Inconnu" : rpName;
	}
	
	public Mono<Member> getMember(){
		return Main.BAY.getClient().getMemberById(StaticID.GUILD, Snowflake.of(this.id));
	}
	
	public Mono<Role> getRole(){
		return Main.BAY.getClient().getRoleById(StaticID.GUILD, Snowflake.of(roleId));
	}
	
	public Mono<TextChannel> getChannel(){
		return Main.BAY.getClient().getChannelById(Snowflake.of(channelId)).cast(TextChannel.class);
	}
	
	public void setRPName(String name) {
		this.rpName = name;
		this.save();
	}
	
	public int getNumberOfNotes() {
		return this.notes.size();
	}
	
	public double getNoteAverage() {
		double avg = 0;
		for(Entry<Long, Integer> i : this.notes.entrySet()) {
			avg += i.getValue();
		}
		return ThePirateBay.round(avg / (this.getNumberOfNotes() > 0 ? this.getNumberOfNotes() : 1), 2);
	}
	
	public boolean hasAlreadyNoted(Long id) {
		return this.notes.containsKey(id);
	}
	
	public void addNote(Long id, int note) {
		this.notes.put(id, note);
		this.save();
	}
	
	public long getArrivalDate() {
		return this.arrivalDate;
	}
	
	public String onTPBFor() {
		return formatter.print(new Interval(this.getArrivalDate(), System.currentTimeMillis()).toPeriod());
	}

	public int getSentMsgNumber() {
		return sentMsgNumber;
	}

	public int getReceivedMsgNumber() {
		return receivedMsgNumber;
	}
	
	public void sentMsgTo(String key) {
		this.relationWith.add(key);
		this.sentMsgNumber++;
		this.save();
	}
	
	public void receivedMsgFrom(String key) {
		lastMessageSender = key;
		this.relationWith.add(key);
		this.receivedMsgNumber++;
		this.save();
	}
	
	public String getLastMessageSender() {
		return lastMessageSender;
	}
	
	public Set<String> getRelations(){
		return this.relationWith;
	}
	
	public Flux<String> getKeys(){
		return Main.BAY.getKeyDB().getKeysFromUser(this);
	}

	public Mono<Void> delete() {
		this.id = -1;
		this.getRole().flatMap(Role::delete).subscribe();
		this.getChannel().flatMap(Channel::delete).subscribe();
		this.getKeys()
			.subscribe(key -> {
				if(!Main.BAY.getKeyDB().isGroupKey(key)) {
					for(String relationKey : this.getRelations()) {
						Main.BAY.getKeyDB().getUsers(relationKey)
							.flatMap(PlatformUser::getChannel)
							.flatMap(relationChannel -> 
								relationChannel.createEmbed(spec -> {
									spec.setTitle("**__Disparition d'un ID : ``" + key + "``__**");
									spec.setColor(Color.RED);
									spec.setDescription(
											"Vous étiez en contact avec l'id " + key + " qui est dès maintenant supprimé.\n"
											+ "Il est donc inutile d'essayer de le contacter car il n'est plus présent sur la plateforme...");
								})
							).subscribe();
					}
				}
				Main.BAY.getKeyDB().removeIdFromKey(key, id);
			});
		this.getSaveFile().delete();
		return Mono.empty();
	}

	public boolean isPremium() {
		return isPremium;
	}
	
	public boolean isTooRecent() {
		return !bypassOlderness && (this.getRelations().size() < StaticMinimum.RELATION ||
				this.getSentMsgNumber() < StaticMinimum.SENDEDMSG ||
				this.getReceivedMsgNumber() < StaticMinimum.RECEIVEDMSG ||
				System.currentTimeMillis() - this.getArrivalDate() < StaticMinimum.HERE_SINCE);
	}
	
	public String talkative() {
		if(isPremium) return "Premium";
		int total = this.getSentMsgNumber() + this.getReceivedMsgNumber();
		
		if(total < 10) {
			return "Petit Nouveau";
		} else if(total < 50) {
			return "Nouveau";
		} else if(total < 100) {
			return "Membre";
		} else if(total < 500) {
			return "Habitué";
		} else if(total < 1000) {
			return "Vieux habitué";
		} else {
			return "Connu";
		}
	}
	
	public boolean hasAnyWhitelist() {
		return whitelistedFor != null && whitelistedFor.size() > 0;
	}

	public boolean isWhitelistedFor(IllegalService service) {
		return whitelistedFor.contains(service);
	}
	
	public ProtectionLevel getProtection() {
		return this.protection;
	}
	
	public HashMap<String, String> getContacts(){
		return this.contactList;
	}
	
	public List<String> getIgnores(){
		return this.ignores;
	}
}
