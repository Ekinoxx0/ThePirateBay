package to.thepiratebay.feeds.abst;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.TextChannel;
import discord4j.core.object.reaction.ReactionEmoji;
import reactor.util.Logger;
import reactor.util.Loggers;
import to.thepiratebay.ThePirateBay;
import to.thepiratebay.commands.abst.AbstractCmd;
import to.thepiratebay.commands.abst.HelpCommand;
import to.thepiratebay.commands.special.CleanChannelCommand;
import to.thepiratebay.commands.special.ErrorCommand;
import to.thepiratebay.commands.special.LongCommand;
import to.thepiratebay.users.PlatformUser;

public abstract class AbstractFeed {
	
	private static final List<Character> cmdCharacter = Arrays.asList('!', ',', '.', '/', '\\', '_');
	public final Logger log = Loggers.getLogger(this.getClass());
	protected final ThePirateBay bay;
	private final List<AbstractCmd> cmds;
	private final boolean acceptNonCommandMessages;
	
	public AbstractFeed(ThePirateBay bay, boolean acceptNonCommandMessages, List<AbstractCmd> cmds) {
		if(bay == null) throw new IllegalArgumentException("Cannot have null bay");
		if(cmds == null) throw new IllegalArgumentException("Cannot have null cmds");
		this.bay = bay;
		this.acceptNonCommandMessages = acceptNonCommandMessages;
		this.cmds = new ArrayList<AbstractCmd>(cmds);
		this.cmds.add(new ErrorCommand(bay));
		this.cmds.add(new LongCommand(bay));
		this.cmds.add(new CleanChannelCommand(bay));
		this.cmds.add(new HelpCommand(bay, this));
	}
	
	public void onMessage(MessageCreateEvent e) {
		if(e == null)
			return;
		if (!e.getMessage().getContent().isPresent())
			return;
		if(!e.getMember().isPresent())
			return;
		if(e.getMember().get().isBot())
			return;
		
		this.processMsg(
				e.getMessage().getChannel().cast(TextChannel.class).block(),
				e.getMessage(), 
				e.getMember().get());
	}
	
	private void processMsg(TextChannel senderChannel, Message msg, Member sender) {
		String rawText = msg.getContent().get();
		log.info(sender.getUsername() + "[" + senderChannel.getName() + "]:" + rawText);

		if (!cmdCharacter.contains(rawText.charAt(0))) {
			if(!acceptNonCommandMessages) {
				msg.delete().subscribe();
				senderChannel.createMessage("Utilisez ``!help`` pour connaître les commandes...").subscribe();
			}
			return;
		}

		String[] splitted = rawText.substring(1).split(" ");
		String cmdName = splitted[0].toLowerCase();
		String[] args = Arrays.copyOfRange(splitted, 1, splitted.length);
		final PlatformUser senderUser = this.bay.getUsersDB().getFromMember(sender).block();
		String senderKey = senderChannel.getName();
		if(!this.bay.getKeyDB().isValid(senderKey))
			senderKey = senderUser.getMainKey();
		
		for(AbstractCmd cmd : this.cmds) {
			if(cmd.isCommand(cmdName)) {
				try {
					msg.addReaction(ReactionEmoji.custom(bay.getEmoji().loading)).subscribe();
					cmd.run(senderChannel, msg, senderUser, senderKey, args);
					
					msg.delete().subscribe();
				} catch(Throwable ex) {
					ex.printStackTrace();
					try {
						msg.removeAllReactions()
							.doOnSuccess(m -> {
								msg.addReaction(ReactionEmoji.unicode("❌")).subscribe();
							})
							.subscribe();
					} catch(Throwable e) {}
				}
				return;
			}
		}

		senderChannel.createMessage(
					"Commande ``" + rawText + "`` inconnue ! \n" +
					"Utilisez ``!help`` pour connaître les commandes...").subscribe();
		try {
			msg.delete().subscribe();
		} catch(Throwable ex) {}
	}
	
	/*
	 * GETTERS
	 */
	
	public List<AbstractCmd> getCmds(){
		return this.cmds;
	}
	
	
}
