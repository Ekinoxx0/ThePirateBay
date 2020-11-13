package to.thepiratebay.users;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import discord4j.core.object.ExtendedPermissionOverwrite;
import discord4j.core.object.PermissionOverwrite;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Role;
import discord4j.core.object.entity.TextChannel;
import discord4j.core.object.util.Permission;
import discord4j.core.object.util.PermissionSet;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.Logger;
import reactor.util.Loggers;
import to.thepiratebay.ThePirateBay;
import to.thepiratebay.commands.abst.HelpCommand;
import to.thepiratebay.stc.StaticID;

@SuppressWarnings("unused")
public class UsersDB {

	public static final File saveFolder = new File("." + File.separator + "users" + File.separator);
	private Logger log = Loggers.getLogger("UsersDB");
	private ArrayList<PlatformUser> users = null;
	private final ThePirateBay bay;

	public UsersDB(ThePirateBay bay)
			throws JsonIOException, JsonSyntaxException, FileNotFoundException {
		this.bay = bay;
		if (!saveFolder.exists()) {
			saveFolder.mkdirs();
		}

		this.load();
		this.log.info("Finished loading " + this.users.size() + " users file into UsersDB..");
	}

	private void load() throws JsonIOException, JsonSyntaxException, FileNotFoundException {
		this.users = new ArrayList<PlatformUser>();
		for (File userFile : saveFolder.listFiles()) {
			try {
				PlatformUser user = PlatformUser.loadFromFile(userFile);
				if (user != null) {
					users.add(user);
				} else {
					log.error("User " + userFile.getName() + " is null, failed to load.");
				}
			} catch (Throwable th) {
				th.printStackTrace();
				log.error("Failed to load user file : " + userFile.getName());
			}
		}
	}

	private void saveAll() {
		try {
			for (PlatformUser user : users) {
				user.save();
			}
			log.info("Succesfuly saved UsersDB");
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	/*
	 * Others
	 */
	
	public Flux<PlatformUser> getUsers(){
		return Flux.fromIterable(this.users);
	}
	
	public Mono<PlatformUser> getFromMember(Member member){
		return this.getFromID(member.getId().asLong());
	}

	public Mono<PlatformUser> getFromID(long id) {
		for (PlatformUser puser : users) {
			if (puser.getId() == id) {
				return Mono.just(puser);
			}
		}

		throw new NullPointerException();
	}

	public boolean exist(Member member) {
		try {
			this.getFromMember(member).block();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public PlatformUser createUser(Member member) {
		String key = bay.getKeyDB().notTakenRandom();
		Guild g = bay.getClient().getGuildById(StaticID.GUILD).block();

		Role r = g.createRole(spec -> {
			spec.setColor(Color.GRAY);
			spec.setName(member.getId().asString());
			spec.setPermissions(PermissionSet.of(Permission.VIEW_CHANNEL));
		}).block();

		member.addRole(r.getId()).subscribe();
		
		try {
			if(StaticID.POLICES.contains(member.getId())) {
				member.addRole(StaticID.POLICE_RANK);
			}
			if(StaticID.STAFFS.contains(member.getId())) {
				member.addRole(StaticID.STAFF_RANK);
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		
		bay.getClient().getChannelById(StaticID.PUBLIC)
			.cast(TextChannel.class)
			.flatMap(channel -> channel.addRoleOverwrite(r.getId(), PermissionOverwrite.forRole(r.getId(), PermissionSet.of(Permission.VIEW_CHANNEL), PermissionSet.none())))
			.subscribe();
		
		TextChannel tc = g.createTextChannel(spec -> {
			spec.setParentId(StaticID.PRIVATE_CAT);
			spec.setName(key);
			spec.setPermissionOverwrites(new HashSet<>(Arrays.asList(
					PermissionOverwrite.forRole(g.getEveryoneRole().block().getId(), PermissionSet.none(),
							PermissionSet.of(Permission.VIEW_CHANNEL)),
					PermissionOverwrite.forRole(r.getId(), PermissionSet.of(Permission.VIEW_CHANNEL),
							PermissionSet.none())
					)));
		}).block();
		
		PlatformUser user = new PlatformUser(member, r, tc);
		this.users.add(user);

		bay.getLive().newUser(member, key);
		bay.getKeyDB().inputKey(key, member);
		
		tc.createMessage(spec -> {
			spec.setEmbed(embed -> {
				embed.setTitle("**__Bienvenue sur le réseau ``" + key + "``__**");
				embed.setDescription("\n"
						+ "**Hey, vous venez d'intégrer le réseau ThePirateBay !**\n"
						+ "\n"
						+ "Votre identifiant unique est ``" + key + "``, "
						+ "partagez le avec les personnes de confiance afin qu'ils puisse communiquer avec vous ici.\n\n"
						+ "N'oubliez pas de lire les informations générales dans #informations");
			});
		}).subscribe();
		new HelpCommand(bay, bay.getUsersFeed()).run(tc, null, user, key, new String[] {});
		
		return user;
	}
	
	public void delete(long id) {
		this.getFromID(id)
			.doOnSuccess(user -> {
				user.delete();
				this.users.remove(user);
				log.info("Cleaned up " + id + " without errors");
			}).subscribe();
	}

}
