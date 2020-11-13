package to.thepiratebay.feeds.abst;

import java.util.List;
import java.util.function.Consumer;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.TextChannel;
import discord4j.core.object.util.Snowflake;
import discord4j.core.spec.EmbedCreateSpec;
import reactor.core.publisher.Mono;
import to.thepiratebay.ThePirateBay;
import to.thepiratebay.commands.abst.AbstractCmd;

public class AbstractChannelFeed extends AbstractFeed {

	private final Snowflake channelId;
	
	public AbstractChannelFeed(ThePirateBay bay, boolean acceptNonCommandMessages, List<AbstractCmd> cmds, Snowflake channelId) {
		super(bay, acceptNonCommandMessages, cmds);
		if(channelId == null) throw new IllegalArgumentException();
		this.channelId = channelId;
		bay.getClient().getEventDispatcher().on(MessageCreateEvent.class)
			.filter(msg -> msg.getMember().isPresent())
			.filter(msg -> !msg.getMember().get().isBot())
			.filter(msg -> msg.getMessage().getChannelId().equals(channelId))	
			.subscribe(event -> this.onMessage(event));
	}
	
	public Mono<Message> createEmbed(Consumer<? super EmbedCreateSpec> embed){
		return this.getChannel().flatMap(channel -> channel.createEmbed(embed));
	}
	
	public Mono<Message> createMessage(String message){
		return this.getChannel().flatMap(channel -> channel.createMessage(message));
	}
	
	public Mono<TextChannel> getChannel(){
		return this.bay.getClient().getChannelById(getChannelId()).cast(TextChannel.class);
	}

	public Snowflake getChannelId() {
		return channelId;
	}
	
}
