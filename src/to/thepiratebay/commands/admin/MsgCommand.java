package to.thepiratebay.commands.admin;

import java.awt.Color;
import java.util.Arrays;

import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.TextChannel;
import to.thepiratebay.ThePirateBay;
import to.thepiratebay.commands.abst.AbstractCmd;
import to.thepiratebay.users.PlatformUser;

public class MsgCommand extends AbstractCmd {

	public MsgCommand(ThePirateBay bay) {
		super(bay, 
				"msg", 
				Arrays.asList("t", "tell", "pm", "mp", "m"), 
				"<MESSAGE>", 
				"Envoyer un msg individuel");
	}

	@Override
	public void run(TextChannel senderChannel, Message msg, PlatformUser sender, String senderKey, String[] args) {
		if (args.length >= 2) {
			if (!bay.getKeyDB().existKey(args[0])) {
				senderChannel.createMessage("ID Inconnu...").subscribe();
				return;
			}

			this.bay.getKeyDB().getUsers(args[0])
			.doOnError(th -> {
				th.printStackTrace();
			}).doOnComplete(() -> {
				senderChannel.createEmbed(embed -> {
					embed.setTitle("Message envoyé à ``" + args[0] + "``");
					embed.setDescription(fromArgs(args));
				}).subscribe();
			}).subscribe(targetUser -> {
				targetUser.getChannel()
					.flatMap(targetC -> targetC.createEmbed(embed -> {
						embed.setColor(Color.ORANGE);
						embed.setTitle("**__Réponse Administrateurs__**");
						embed.setDescription("**" + fromArgsExcept(args, 1) + "**\n\n*Répondre avec ``!report <MESSAGE>``*");
					}))
					.subscribe();
			});
		} else {
			senderChannel.createMessage(this.getCompleteUsage()).subscribe();
		}
	}

}
