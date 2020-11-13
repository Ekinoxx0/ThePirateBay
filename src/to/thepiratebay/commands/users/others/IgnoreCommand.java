package to.thepiratebay.commands.users.others;

import java.awt.Color;
import java.util.Arrays;
import java.util.Map;

import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.TextChannel;
import to.thepiratebay.ThePirateBay;
import to.thepiratebay.commands.abst.AbstractCmd;
import to.thepiratebay.users.PlatformUser;

public class IgnoreCommand extends AbstractCmd {

	public IgnoreCommand(ThePirateBay bay) {
		super(bay, "ignore", 
				Arrays.asList("ignores", "ignorer", "block", "bloquer", "bloc", "bloque"), 
				"<ID>", 
				"Ignorer un ID",
				Map.of("ignores", "Liste des ID ignorés"));
	}

	@Override
	public void run(TextChannel senderChannel, Message msg, PlatformUser sender, String senderKey, String[] args) {
		if(args.length == 0) {
			senderChannel.createEmbed(embed -> {
				embed.setTitle("**__:family_wwgb: Liste des personnes ignorées :family_wwgb:__**");

				String ignores = "";
				for(String ignore : sender.getIgnores()) {
					ignores += "``" + ignore + "``";
				}
				if(ignores.length() == 0) {
					ignores = "*Personne n'a été ignoré ...*\n ``!ignore <ID>``";
				}
				embed.setDescription(ignores);
			}).subscribe();
		} else if(args.length == 1) {
			if(args[0].equalsIgnoreCase(senderKey)) {
				senderChannel.createMessage("Vous ne pouvez pas vous ignorer vous même !").subscribe();
				return;
			}
			if (!bay.getKeyDB().existKey(args[0])) {
				senderChannel.createMessage("ID Inconnu...").subscribe();
				return;
			}
			
			if(sender.getIgnores().contains(args[0].toLowerCase())) {
				sender.getIgnores().remove(args[0].toLowerCase());
				senderChannel.createEmbed(embed -> {
					embed.setTitle("**__Vous n'ignorez plus __**``" + args[0] + "``");
					embed.setDescription("Cela signifie que vous recevrez ses prochains messages.");
					embed.setColor(Color.YELLOW);
				}).subscribe();
			} else {
				sender.getIgnores().add(args[0].toLowerCase());
				senderChannel.createEmbed(embed -> {
					embed.setTitle("**__Vous ignorez maintenant __**``" + args[0] + "``");
					embed.setDescription("Cela signifie que vous ne recevrez plus ses prochains messages.\n"
							+ "Nous ne le mettrons pas au courant de cette action...\n"
							+ "**N'hésitez pas à utiliser ``!report <MESSAGE>`` en cas d'abus !**");
					embed.setColor(Color.YELLOW);
				}).subscribe();
				sender.getContacts().remove(args[0].toLowerCase());
				this.bay.getLive().nowIgnore(senderKey, args[0].toLowerCase());
			}
			sender.save();
		} else {
			senderChannel.createMessage(this.getCompleteUsage()).subscribe();
		}
	}

}
