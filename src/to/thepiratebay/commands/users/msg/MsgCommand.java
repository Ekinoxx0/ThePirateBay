package to.thepiratebay.commands.users.msg;

import java.util.Arrays;
import java.util.List;

import discord4j.core.object.entity.Attachment;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.TextChannel;
import to.thepiratebay.ThePirateBay;
import to.thepiratebay.commands.abst.AbstractCmd;
import to.thepiratebay.stc.StaticFunction;
import to.thepiratebay.users.PlatformUser;

public class MsgCommand extends AbstractCmd {

	public MsgCommand(ThePirateBay bay) {
		super(bay, "msg", 
				Arrays.asList("t", "tell", "message", "messages", "msgs", "m"), 
				"<ID> <MESSAGE>", 
				"Envoyer des messages cryptés");
	}
	
	protected MsgCommand(ThePirateBay bay, String cmd, List<String> aliases, String usage, String details) {
		super(bay, cmd, aliases, usage, details);
	}
	
	@Override
	public void run(TextChannel senderChannel, Message msg, PlatformUser sender, String senderKey, String[] args) {
		if (args.length >= 2) {
			if(args[0].equalsIgnoreCase(senderKey)) {
				senderChannel.createMessage("**Impossible de vous envoyer un message à vous même !**").subscribe();
				return;
			}
			if (!bay.getKeyDB().existKey(args[0])) {
				senderChannel.createMessage("ID Inconnu...").subscribe();
				return;
			}
			
			if(StaticFunction.containsURL(fromArgs(args))) {
				senderChannel.createMessage("Impossible d'envoyer un lien...").subscribe();
				return;
			}

			this.bay.getKeyDB().getUsers(args[0])
			.doOnError(th -> {
				th.printStackTrace();
				error(senderChannel);
			}).doOnComplete(() -> {
				msgSent(senderChannel, senderKey, args[0], fromArgsExcept(args, 1));
				sender.sentMsgTo(args[0]);
			}).subscribe(targetUser -> {
				if(targetUser.getIgnores().contains(senderKey)) {
					this.bay.getLive().ignoredMsg(fromArgsExcept(args, 1), msg, senderKey, args[0]);
					return;
				}
				cryptedMsg(targetUser.getChannel().block(), fromArgsExcept(args, 1), msg, sender, args[0], senderKey);
				targetUser.receivedMsgFrom(senderKey);
			});
		} else {
			senderChannel.createMessage(this.getCompleteUsage()).subscribe();
		}
	}

	protected void msgSent(TextChannel senderChannel, String senderKey, String targetKey, String text) {
		this.successSent(senderChannel," | ``" + senderKey + "`` -> ``" + targetKey + "``", text);
	}

	protected void cryptedMsg(TextChannel tChannel, String txt, Message msg, PlatformUser sender, String targetKey, String senderKey) {
		tChannel.createMessage(spec -> {
			spec.setEmbed(embed -> {
				String composedTxt = txt;
				if (msg.getAttachments().size() > 0) {
					composedTxt += "\n\n**__L'utilisateur à également ajouter des fichiers à ce message :__**\n";
				}
				for (Attachment a : msg.getAttachments()) {
					composedTxt += a.getProxyUrl();
				}
				log.info("" + senderKey + " -> " + targetKey + " [" + msg.getAttachments().size() + "]: " + txt);
				embed.setTitle("**__MESSAGE ANONYME__  |  ** ``" + senderKey + "`` -> ``" + targetKey + "``");
				embed.setDescription(composedTxt);
			});
		}).doOnSuccess(createdMsg -> {
			this.bay.getPolice().receiveMsg(sender, txt, msg, senderKey, targetKey);
			this.bay.getLive().processMsg(txt, msg, senderKey, targetKey);
		}).subscribe();
	}
	
	
}
