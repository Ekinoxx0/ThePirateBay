package to.thepiratebay.commands.users.others;

import java.util.Arrays;

import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.TextChannel;
import to.thepiratebay.ThePirateBay;
import to.thepiratebay.commands.abst.AbstractCmd;
import to.thepiratebay.stc.StaticFunction;
import to.thepiratebay.stc.StaticID;
import to.thepiratebay.users.PlatformUser;

public class PublicCommand extends AbstractCmd {

	public PublicCommand(ThePirateBay bay) {
		super(bay, "public", 
				Arrays.asList("pub"), 
				"<MESSAGE>", 
				"Publier un message #public");
	}

	@Override
	public void run(TextChannel senderChannel, Message msg, PlatformUser sender, String senderKey, String[] args) {
		if(args.length == 0) {
			senderChannel.createMessage(this.getCompleteUsage()).subscribe();
			return;
		}
		
		if(StaticFunction.containsURL(fromArgs(args))) {
			senderChannel.createMessage("Impossible d'envoyer un lien...").subscribe();
			return;
		}
		
		this.bay.getClient().getChannelById(StaticID.PUBLIC).cast(TextChannel.class)
		.flatMap(targetC -> targetC.createEmbed(spec -> {
			spec.setTitle("**Message public de** ``" + senderKey + "``");
			spec.setDescription(fromArgs(args).replace("@", "[@]") + 
					"\n\n*Répondez à lui avec ``!public <MSG>`` ou ``!msg " + senderKey + " <MSG>``*");
		}))
		.doOnSuccess(m -> {
			successSent(senderChannel, "", fromArgs(args));
		})
		.subscribe();
	}

}
