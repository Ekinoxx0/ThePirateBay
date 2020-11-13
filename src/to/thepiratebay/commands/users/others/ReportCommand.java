package to.thepiratebay.commands.users.others;

import java.awt.Color;
import java.util.Arrays;

import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.TextChannel;
import to.thepiratebay.ThePirateBay;
import to.thepiratebay.commands.abst.AbstractCmd;
import to.thepiratebay.stc.StaticID;
import to.thepiratebay.users.PlatformUser;

public class ReportCommand extends AbstractCmd {

	public ReportCommand(ThePirateBay bay) {
		super(bay, "report", 
				Arrays.asList("reporte", "rport", "reports"), 
				"<MESSAGE>", 
				"Report un problème");
	}

	@Override
	public void run(TextChannel senderChannel, Message msg, PlatformUser sender, String senderKey, String[] args) {
		if(args.length == 0) {
			senderChannel.createMessage(this.getCompleteUsage()).subscribe();
			return;
		}
		
		final String r = fromArgs(args);
		this.bay.getClient().getChannelById(StaticID.ADMIN)
			.cast(TextChannel.class)
			.flatMap(c -> 
				c.createEmbed(embed -> {
					embed.setTitle("**Report de** ``" + senderKey + "``");
					embed.setColor(Color.ORANGE);
					embed.setDescription(r);
				})
			)
			.doOnSuccess(m -> {
				senderChannel.createEmbed(embed -> {
					embed.setTitle("Report effectué");
					embed.setDescription(r);
					embed.setColor(Color.ORANGE);
				}).subscribe();
			})
			.subscribe();
	}
	
}
