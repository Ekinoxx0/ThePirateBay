package to.thepiratebay;

import java.awt.Color;
import java.math.BigDecimal;
import java.math.RoundingMode;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import discord4j.core.DiscordClient;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.event.EventDispatcher;
import discord4j.core.event.domain.guild.BanEvent;
import discord4j.core.event.domain.guild.MemberJoinEvent;
import discord4j.core.event.domain.guild.MemberLeaveEvent;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.object.entity.Category;
import discord4j.core.object.entity.GuildChannel;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.TextChannel;
import discord4j.core.object.presence.Activity;
import discord4j.core.object.presence.Presence;
import discord4j.core.object.util.Snowflake;
import reactor.util.Logger;
import reactor.util.Loggers;
import to.thepiratebay.feeds.AdminFeed;
import to.thepiratebay.feeds.LiveFeed;
import to.thepiratebay.feeds.PoliceFeed;
import to.thepiratebay.feeds.UsersFeed;
import to.thepiratebay.illegal.IllegalDB;
import to.thepiratebay.stc.StaticEmoji;
import to.thepiratebay.stc.StaticFunction;
import to.thepiratebay.stc.StaticID;
import to.thepiratebay.users.KeyDB;
import to.thepiratebay.users.UsersComparator;
import to.thepiratebay.users.UsersDB;

@SuppressWarnings("unused")
public class ThePirateBay {
	
	public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private DiscordClient client;
	private Logger log;
	
	private AdminFeed admin;
	private LiveFeed live;
	private PoliceFeed police;
	private UsersFeed usersFeed;
	
	private UsersDB users;
	private KeyDB keys;
	private IllegalDB illegal;
	
	private UsersComparator comparator;
	private StaticEmoji emojis;
	
	public ThePirateBay() {
		this.log = Loggers.getLogger("TPB");
		
		try {
			this.users = new UsersDB(this);
		} catch (Exception e) {
			e.printStackTrace();
			this.log.error("Unable to proceed to UsersDB creation...");
			return;
		}
		
		try {
			this.keys = new KeyDB(this);
		} catch (Exception e) {
			e.printStackTrace();
			this.log.error("Unable to proceed to IdDB creation...");
			return;
		}
		
		try {
			this.illegal = new IllegalDB(this);
		} catch (Exception e) {
			e.printStackTrace();
			this.log.error("Unable to proceed to IllegalDB creation...");
			return;
		}
		
		this.comparator = new UsersComparator(this);
		
		this.client = new DiscordClientBuilder("NjEwMjYyMzU5OTc3NjIzNTcx.XVFrcQ.km-7iAyVrgL6HgXq9_k1zQjsSa4").build();

		try {
			this.admin = new AdminFeed(this);
			this.live = new LiveFeed(this);
			this.police = new PoliceFeed(this);
			this.usersFeed = new UsersFeed(this);
		} catch(Exception ex) {
			ex.printStackTrace();
			log.error("Unable to create feeds...");
			return;
		}

		EventDispatcher ed = this.client.getEventDispatcher();
		
		ed.on(ReadyEvent.class).subscribe(this::onReady);
		ed.on(MessageCreateEvent.class)
			.filter(msg -> msg.getMember().isPresent() && !msg.getMember().get().isBot())
			.subscribe(this::onMessage);
		ed.on(MemberJoinEvent.class).subscribe(this::onJoin);
		ed.on(MemberLeaveEvent.class).subscribe(leave -> {
			this.users.delete(leave.getUser().getId().asLong());
		});
		
		ed.on(BanEvent.class).subscribe(banevent -> {
			this.users.delete(banevent.getUser().getId().asLong());
			banevent.getUser().getPrivateChannel()
				.flatMap(pc -> pc.createEmbed(embed -> {
					embed.setTitle("**__Vous avez été banni.__**");
					embed.setColor(Color.RED);
					embed.setDescription("Vous êtes dès maintenant banni de la plateforme ThePirateBay.\n"
							+ "Cela signifie que vous n'aurez plus accès à cette plateforme et que vous ID est supprimé.");
				}));
		});
		ed.on(ReactionAddEvent.class).filter(r -> r.getGuildId().isPresent()).subscribe(this::onReaction);
	}
	
	public void login() {
		this.client.login().block();
	}
	
	private void onReady(ReadyEvent ready) {
		log.info("ThePirateBay is now logged.");
        client.updatePresence(Presence.online(Activity.watching("le réseau crypté"))).subscribe();
        //StaticFunction.updateInformation(client);
        StaticFunction.anonymeAllMembers(client);
        emojis = new StaticEmoji(this.getClient());
    }
	
	private void onReaction(ReactionAddEvent react) {
		if(react.getUser().block().isBot()) return;
		Member member = (Member) react.getUser().block().asMember(StaticID.GUILD).block();
		
		if(react.getChannelId().equals(StaticID.INFO)) {
			if(!this.getUsersDB().exist(member)) {
				log.info(member.getUsername() + " has reacted to #information message, creating new user.");
				this.getUsersDB().createUser(member);
				log.info(member.getUsername() + " created new user.");
			}
		}
	}
	
	private void onMessage(MessageCreateEvent msg) {
		Category cat = (Category) this.client.getChannelById(StaticID.PRIVATE_CAT).block();
		cat.getChannels().any(channel -> channel.equals((GuildChannel) msg.getMessage().getChannel().block())).subscribe(b -> {
			if(b) this.usersFeed.onMessage(msg);
		});
	}
	
	private void onJoin(MemberJoinEvent join) {
		join.getMember().edit(spec -> spec.setNickname("Anonyme")).subscribe();
	}
	
	/*
	 * 
	 */

	public Message msg(Snowflake channelID, String text) {
		return client.getChannelById(channelID).cast(TextChannel.class)
				.flatMap(channel -> channel.createMessage(text)).block();
	}
	
	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    BigDecimal bd = BigDecimal.valueOf(value);
	    bd = bd.setScale(places, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}
	
	/*
	 * GETTER
	 */

	public UsersDB getUsersDB() {
		return this.users;
	}
	
	public KeyDB getKeyDB() {
		return this.keys;
	}

	public LiveFeed getLive() {
		return this.live;
	}

	public DiscordClient getClient() {
		return this.client;
	}

	public UsersFeed getUsersFeed() {
		return this.usersFeed;
	}

	public IllegalDB getIllegalDB() {
		return this.illegal;
	}
	
	public UsersComparator getComparator() {
		return this.comparator;
	}

	public PoliceFeed getPolice() {
		return this.police;
	}
	
	public StaticEmoji getEmoji() {
		return this.emojis;
	}
}
