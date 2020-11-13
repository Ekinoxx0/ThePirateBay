package to.thepiratebay.stc;

import java.lang.reflect.Field;

import discord4j.core.DiscordClient;
import discord4j.core.object.entity.GuildEmoji;

public class StaticEmoji {
	
	public GuildEmoji loading;
	public GuildEmoji online;
	public GuildEmoji offline;
	
	public StaticEmoji(DiscordClient client) {
	    client.getGuildById(StaticID.GUILD).block().getEmojis().subscribe(emoji -> {
	    	for(Field f : this.getClass().getFields()) {
	    		if(emoji.getName().equals(f.getName())) {
	    			try {
						f.set(this, emoji);
					} catch (Exception e) {
						e.printStackTrace();
					}
	    		}
	    	}
	    });
	}
	
}
