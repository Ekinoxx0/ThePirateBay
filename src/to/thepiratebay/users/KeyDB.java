package to.thepiratebay.users;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import discord4j.core.object.entity.Member;
import discord4j.core.object.util.Snowflake;
import reactor.core.publisher.Flux;
import reactor.util.Logger;
import reactor.util.Loggers;
import to.thepiratebay.ThePirateBay;
import to.thepiratebay.stc.StaticID;

public class KeyDB {

	private static final File saveFile = new File("." + File.separator + "keys.json");
	private Logger log = Loggers.getLogger("KeyDB");
	private HashMap<String, ArrayList<Long>> keys = null;
	private final ThePirateBay bay;
	
	public KeyDB(ThePirateBay bay) throws JsonIOException, JsonSyntaxException, FileNotFoundException {
		this.bay = bay;
		if(!saveFile.exists()) {
			saveFile.getParentFile().mkdirs();
			try {
				saveFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			this.keys = new HashMap<String, ArrayList<Long>>();
			this.save();
		}

		this.keys = ThePirateBay.GSON.fromJson(new FileReader(saveFile), new TypeToken<HashMap<String, ArrayList<Long>>>(){}.getType());
		if(this.keys != null) {
			this.log.info("Finished loading " + this.keys.size() + " keys from Json file into KeyDB..");	
		} else {
			this.log.error("Null keys !!! Not loaded correctly");
			throw new NullPointerException();
		}
	}

	private void save() {
		try {
			FileWriter writer = new FileWriter(saveFile);
			writer.write(ThePirateBay.GSON.toJson(keys));
			writer.close();
			log.info("Succesfuly saved IdDB");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * Others
	 */
	
	public Flux<String> getKeysFromUser(PlatformUser user){
		List<String> keys = new ArrayList<String>();
		for(Entry<String, ArrayList<Long>> set : this.keys.entrySet()) {
			for(Long l : set.getValue()) {
				if(l == user.getId()) {
					keys.add(set.getKey());
				}
			}
		}
		return Flux.fromIterable(keys);
	}
	
	public void removeKey(String key) {
		if(!existKey(key)) throw new IllegalArgumentException();
		this.keys.remove(key);
		this.save();
	}
	
	public boolean isValid(String key) {
		return key != null && key.matches("[A-Za-z0-9]{4}");
	}
	
	public boolean existKey(String key) {
		return isValid(key) && this.keys.containsKey(key) && this.keys.get(key) != null && this.keys.get(key).size() > 0;
	}
	
	public boolean isGroupKey(String key) {
		return this.existKey(key) && this.keys.get(key).size() > 1;
	}
	
	public void inputKey(String key, Member member) {
		if(isValid(key)) throw new IllegalArgumentException("Key Invalid :" + key + "");
		if(member == null || member.isBot()) throw new IllegalArgumentException("Null Member");
		
		if(this.keys.containsKey(key)) {
			this.keys.get(key).add(member.getId().asLong());
		} else {
			this.keys.put(key, new ArrayList<Long>(Arrays.asList(member.getId().asLong())));
		}
		
		if(!this.existKey(key)) throw new IllegalStateException("!existKey(" + key + ");");
		this.save();
	}
	
	public void removeIdFromKey(String key, Long id) {
		if(this.existKey(key)) {
			ArrayList<Long> ids = this.keys.get(key);
			ids.remove(id);
			if(!ids.isEmpty()) {
				this.keys.put(key, ids);
			}
		}
	}
	
	public List<Long> getMembersId(String key){
		if(!this.existKey(key)) return Collections.emptyList();
		return this.keys.get(key);
	}
	
	public Flux<Member> getMembers(String key){
		if(!this.existKey(key)) return Flux.empty();
		
		return Flux.fromIterable(this.keys.get(key))
				.filter(k -> k != null)
				.flatMap(k -> bay.getClient().getMemberById(StaticID.GUILD, Snowflake.of(k)));
	}
	
	public Flux<PlatformUser> getUsers(String key){
		if(!this.existKey(key)) return Flux.empty();
		
		return Flux.fromIterable(this.keys.get(key))
				.filter(k -> k != null)
				.flatMap(k -> bay.getUsersDB().getFromID(k));
	}
	
	public double getNoteOf(String key) {
		int avg = 0;
		List<PlatformUser> users = this.getUsers(key).collectList().block();
		for(PlatformUser user : users) {
			avg += user.getNoteAverage();
		}
		return ThePirateBay.round(avg / (users.size() > 0 ? users.size() : 1), 2);
	}
	
	public List<Member> getMembersList(String key){
		return this.getMembers(key).collectList().block();
	}
	
	public String notTakenRandom() {
		String random;
		while(this.existKey(random = random())) {}
		return random;
	}

	private String random() {
		return UUID.randomUUID().toString().replace("-", "").substring(0, 4);
	}
	
}
