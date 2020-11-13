package to.thepiratebay.illegal;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import reactor.core.publisher.Flux;
import reactor.util.Logger;
import reactor.util.Loggers;
import to.thepiratebay.ThePirateBay;
import to.thepiratebay.users.PlatformUser;

public class IllegalDB {

	private static final File saveFile = new File("." + File.separator + "illegal.json");
	private final Logger log = Loggers.getLogger("IdDB");
	
	private final ThePirateBay bay;
	private ArrayList<PlatformIllegalS> illegalServices;
	
	public IllegalDB(ThePirateBay bay) throws JsonIOException, JsonSyntaxException, FileNotFoundException, NullPointerException {
		this.bay = bay;

		if(!saveFile.exists()) {
			saveFile.getParentFile().mkdirs();
			try {
				saveFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			this.illegalServices = new ArrayList<PlatformIllegalS>();
			this.save();
		}

		this.illegalServices = ThePirateBay.GSON.fromJson(new FileReader(saveFile), new TypeToken<ArrayList<PlatformIllegalS>>(){}.getType());
		if(this.illegalServices != null) {
			if(this.illegalServices.size() != IllegalService.values().length) {
				this.log.error("Missing services into IllegalDB !");	
				for(IllegalService s : IllegalService.values()) {
					boolean hasThisService = false;
					for(PlatformIllegalS ps : this.illegalServices) {
						if(ps.getService() == s) {
							hasThisService = true;
							break;
						}
					}
					if(!hasThisService) {
						this.log.info("Adding service : " + s.toString() + " to IllegalDB");
						this.illegalServices.add(new PlatformIllegalS(s));
					}
				}
				this.save();
			}
			
			this.log.info("Finished loading " + this.illegalServices.size() + " services from Json file into IllegalDB..");	
		} else {
			this.log.error("Null illegalServices !!! Not loaded correctly");
			throw new NullPointerException();
		}

	}
	
	private void save() {
		try {
			FileWriter writer = new FileWriter(saveFile);
			writer.write(ThePirateBay.GSON.toJson(illegalServices));
			writer.close();
			log.info("Succesfuly saved IllegalDB");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * 
	 */
	
	public Flux<PlatformIllegalS> all() {
		return Flux.fromIterable(this.illegalServices).doOnTerminate(() -> {
			this.save();
		});
	}

	public String getAllDescription(PlatformUser user) {
		String composedTxt = "";
		for (PlatformIllegalS il : this.illegalServices) {
			composedTxt += "\n\n" + il.getService().getEmoji() + "   __**" + il.getService().getName() + "**__\n";
			
			if(!user.isPremium() && il.isPrivate()) {
				composedTxt += "*Service réservé à nos membres spéciaux..*";
				continue;
			}
			
			if(!il.isActive()) {
				composedTxt += "*Indisponible...*\n";
				continue;
			}
			
			composedTxt += "*" + il.getDetails() + "*\n\n";
			if(il.getContactList().size() > 0) {
				composedTxt += "Contact disponible : ``!service " + il.getService().toString() + "``\n";
				continue;
			}
			
			composedTxt += "__Aucun contact disponible pour le moment..__\n";
			composedTxt += joinService(il, user);
			
		}
		return composedTxt;
	}

	public String getUniqueDescription(IllegalService s, PlatformUser user) {
		String txt = "";
		PlatformIllegalS platform = this.bay.getIllegalDB().all().filter(p -> p.getService() == s).blockFirst();
		
		if(!platform.isActive()) {
			return "*Indisponible...*";
		}
		
		if(!user.isPremium() && platform.isPrivate()) {
			return "*Indisponible pour vous...*";
		}

		txt += "*" + platform.getDetails() + "*\n\n";
		txt += "\n**Contacts :**\n";
		
		if (platform.getContactList().size() > 0) {
			List<String> contacts = platform.getContactList();
			contacts.sort(this.bay.getComparator().keyComparatorPerNote);
			for (String id : contacts) {
				txt += "``" + id + "`` (Note : " + this.bay.getKeyDB().getNoteOf(id) + ":star:)\n";
			}
		} else {
			txt += "__Aucun contact disponible pour ce service...__\n";
		}
		
		txt += "\n" + joinService(platform, user);
		return txt;
	}
	
	public String joinService(PlatformIllegalS s, PlatformUser user) {
		if(user.isTooRecent()) {
			return "*Impossible de rejoindre le service pour le moment...*\n";
		}
		
		if(user.getKeys().any(key -> s.getContactList().contains(key)).block()) {
			return "*Vous faites partie de ce service.* (Quitter le service ``!leave " + s.getService().toString() + "``\n";
		}
		
		if(s.isWhitelisted()) {
			if(user.isWhitelistedFor(s.getService())) {
				return "*Rejoindre ce service :* ``!join " + s.getService().toString() + "``\n";
			} else {
				return "*Rejoindre ce service :* Service privé, utilisez ``!report <MESSAGE>``\n";
			}
		}
		
		return "*Rejoindre ce service :* ``!join " + s.getService().toString() + "``\n";
	}

}
