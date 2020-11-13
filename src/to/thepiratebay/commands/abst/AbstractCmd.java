package to.thepiratebay.commands.abst;

import java.awt.Color;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.TextChannel;
import reactor.util.Logger;
import reactor.util.Loggers;
import to.thepiratebay.ThePirateBay;
import to.thepiratebay.users.PlatformUser;

public abstract class AbstractCmd {

	public final Logger log = Loggers.getLogger(this.getClass());
	protected final ThePirateBay bay;
	private final String cmd;
	private final Set<String> aliases;
	private final String usage;
	private final String details;
	private final boolean doesHelp;
	private Map<String, String> aliasesDetails;

	public AbstractCmd(ThePirateBay bay, String cmd, List<String> aliases, String usage, String details) {
		this(bay, cmd, aliases, usage, details, true);
	}
	public AbstractCmd(ThePirateBay bay, String cmd, List<String> aliases, String usage, String details, boolean doesHelp) {
		this.bay = bay;
		this.cmd = cmd.toLowerCase();
		this.usage = usage;
		this.details = details;
		this.doesHelp = doesHelp;
		if(details.length() > 36) log.warn("Details of !" + this.getCmd() + " are too long...");
		this.aliases = new HashSet<String>();
		aliases.forEach(alias -> this.aliases.add(alias.toLowerCase()));
		this.aliasesDetails = Map.of();
	}

	public AbstractCmd(ThePirateBay bay, String cmd, List<String> aliases, String usage, String details, Map<String, String> aliasesDetails) {
		this(bay, cmd, aliases, usage, details);
		aliasesDetails.forEach((key, value) -> {
			if(!this.aliases.contains(key.toLowerCase()) && this.getCmd() != key.toLowerCase()) {
				throw new IllegalArgumentException("Aliases details incompatible with aliases simple.");
			}
			
			if(key.length() >= 20) log.warn("Too long alias : "  + key);
			if(value.length() > 36) log.warn("Too long details for aliasesDetails : " + key);
		});
		this.aliasesDetails = aliasesDetails;
	}
	
	public abstract void run(TextChannel senderChannel, Message msg, PlatformUser sender, String senderKey, String[] args);

	/*
	 * Specials
	 */
	
	public String fromArgs(String[] args) {
		return fromArgsExcept(args, 0);
	}
	
	public String fromArgsExcept(String[] args, int nToIgnore) {
		String txt = "";
		int n = 0;
		for(String arg : args) {
			n++;
			if(n <= nToIgnore) continue;
			txt += arg + " ";
		}
		
		return txt;
	}

	protected void error(TextChannel channel) {
		channel.createEmbed(spec -> {
			spec.setTitle("**__MESSAGE PERDU..__**");
			spec.setDescription("Une erreur est survenue...\nUtilisez !report en cas de problème");
			spec.setColor(Color.RED);
		}).subscribe();
	}
	
	protected void successSent(TextChannel channel, String titleAdd, String description) {
		channel.createEmbed(spec -> {
			spec.setTitle(
					"**__ENVOYÉ AVEC SUCCES__**" + titleAdd);
			spec.setDescription(description);
			spec.setColor(Color.GREEN);
		}).subscribe();
	}
	
	/*
	 * Getters
	 */
	
	public String getCmd() {
		return cmd;
	}
	
	public boolean isCommand(String cmdName) {
		return cmd.equals(cmdName) || this.aliases.contains(cmdName);
	}

	protected String getArgUsage() {
		return usage;
	}

	protected String getCompleteUsage() {
		return ":x: :interrobang:\n" + 
				"**Utilisez :** " + this.getCommandUsage() + "\n" + 
				"Exemple : " + this.getExample();
	}

	protected String getCommandUsage() {
		return "``!" + this.getCmd() + " " + this.getArgUsage() + "``";
	}
	
	protected String getExample() {
		return "!" + this.getCmd() + " " + 
				this.getArgUsage()
							.replace("<ID>", "abc1")
							.replace("<MESSAGE>", "Un message plutôt cool..")
							.replace("<N/10>", "7")
							.replace("<SERVICE>", "WEED");
	}

	protected String getDetails() {
		return details;
	}
	
	protected Map<String, String> getAliasesDetails(){
		return this.aliasesDetails;
	}
	
	protected boolean doesHelp() {
		return this.doesHelp;
	}
	
}
