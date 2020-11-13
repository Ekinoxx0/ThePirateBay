package to.thepiratebay.commands.abst;

import java.awt.Color;
import java.util.Arrays;
import java.util.Map.Entry;

import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.TextChannel;
import to.thepiratebay.ThePirateBay;
import to.thepiratebay.feeds.abst.AbstractFeed;
import to.thepiratebay.users.PlatformUser;

public class HelpCommand extends AbstractCmd {

	private final AbstractFeed feed;
	
	public HelpCommand(ThePirateBay bay, AbstractFeed feed) {
		super(bay, "help", 
				Arrays.asList("aide", "helps", "aides", "aid", "hlp"), 
				"", 
				"Liste des commandes");
		this.feed = feed;
	}

	@Override
	public void run(TextChannel senderChannel, Message NULL, PlatformUser sender, String senderKey, String[] args) {
		senderChannel.createEmbed(spec -> {
			spec.setTitle("**__:question: Liste des commandes :question:__**");
			
			String desc = "```\n";
			
			for(AbstractCmd cmd : feed.getCmds()) {
				if(!cmd.doesHelp()) continue;
				String cmdDesc = "!" + cmd.getCmd() + " " + cmd.getArgUsage();
				while(cmdDesc.length() < 22) cmdDesc += " ";
				desc += cmdDesc + cmd.getDetails() + "\n";
				
				for(Entry<String, String> entry : cmd.getAliasesDetails().entrySet()) {
					String aliasDesc = "!" + entry.getKey();
					while(aliasDesc.length() < 22) aliasDesc += " ";
					desc += aliasDesc + entry.getValue() + "\n";
				}
			}
			
			desc += "\n```";
			spec.setDescription(desc);
			spec.setColor(Color.CYAN);
		}).subscribe();
		}

}
