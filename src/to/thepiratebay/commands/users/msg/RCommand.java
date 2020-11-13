package to.thepiratebay.commands.users.msg;

import java.util.Arrays;

import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.TextChannel;
import to.thepiratebay.ThePirateBay;
import to.thepiratebay.stc.StaticFunction;
import to.thepiratebay.users.PlatformUser;

public class RCommand extends MsgCommand {

	public RCommand(ThePirateBay bay) {
		super(bay, "r", 
				Arrays.asList(), 
				"<MESSAGE>", 
				"Répondre au dernier message reçu");
	}

	@Override
	public void run(TextChannel senderChannel, Message msg, PlatformUser sender, String senderKey, String[] args) {
		if (args.length >= 1) {
			if (!bay.getKeyDB().existKey(sender.getLastMessageSender())) {
				senderChannel.createMessage("Vous n'avez personne à qui répondre...").subscribe();
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
				msgSent(senderChannel, senderKey, args[0], fromArgs(args));
				sender.sentMsgTo(args[0]);
			}).subscribe(targetUser -> {
				if(targetUser.getIgnores().contains(senderKey)) {
					this.bay.getLive().ignoredMsg(fromArgsExcept(args, 1), msg, senderKey, args[0]);
					return;
				}
				cryptedMsg(targetUser.getChannel().block(), fromArgs(args), msg, sender, args[0], sender.getMainKey());
				targetUser.receivedMsgFrom(sender.getMainKey());
			});
			
		} else {
			senderChannel.createMessage(this.getCompleteUsage()).subscribe();
		}
	}
	
	
	
}
