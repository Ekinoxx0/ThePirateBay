package to.thepiratebay.commands.users.profil;

import java.util.Arrays;

import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.TextChannel;
import to.thepiratebay.ThePirateBay;
import to.thepiratebay.commands.abst.AbstractCmd;
import to.thepiratebay.users.PlatformUser;

public class NoteCommand extends AbstractCmd {

	public NoteCommand(ThePirateBay bay) {
		super(bay, "note", 
				Arrays.asList("notes", "nots"), 
				"<ID> <N/10>", 
				"Donner une note à un membre");
	}

	@Override
	public void run(TextChannel senderChannel, Message msg, PlatformUser sender, String senderKey, String[] args) {
		if(args.length != 2) {
			senderChannel.createMessage(this.getCompleteUsage()).subscribe();
			return;
		}
		String targetKey = args[0];
		
		if(!this.bay.getKeyDB().existKey(targetKey)) {
			senderChannel.createMessage("ID Inconnu...").subscribe();
			return;
		}
		
		if(sender.getIgnores().contains(targetKey)) {
			senderChannel.createMessage("Un problème est survenue...").subscribe();
			return;
		}
			
		if(this.bay.getKeyDB().getUsers(targetKey).any(tr -> sender.getId() == tr.getId()).block()) {
			senderChannel.createMessage("Impossible de vous noter vous même...").subscribe();
			return;
		}
			
		PlatformUser t = this.bay.getKeyDB().getUsers(targetKey).blockFirst();

		if(t.getIgnores().contains(senderKey)) {
			senderChannel.createMessage("Un problème est survenue...").subscribe();
			return;
		}
		
		int n;
		try {
			n = Integer.parseInt(args[1]);
			if(n < 0 || n > 10) {
				throw new NumberFormatException();
			}
		} catch(NumberFormatException ex) {
			senderChannel.createMessage("Arguments invalides... Utilisez ``!note <ID> <NOTE/10>``\n"
					+ "**Respectez le format, note sur 10 __sans virgule__.**").subscribe();
			return;
		}
		
		final PlatformUser target = t;
		boolean alreadyVoted = target.hasAlreadyNoted(sender.getId());
		target.addNote(sender.getId(), n);
		
		senderChannel.createEmbed(embed -> {
			embed.setTitle("**__Note pour __**``" + targetKey + "``");
			String txt = "Vous avez noté ce membre **" + n + "/10** \n\n";
			
			if(alreadyVoted) {
				txt += "*Vous aviez déjà noté ``" + targetKey + "`` donc votre note va simplement remplacer l'ancienne*\n\n";
			}
			
			if(n < 4) {
				txt += "**Quelque chose c'est mal passé avec ``" + targetKey + "`` ?**\n"
						+ "N'hésitez pas à nous le faire savoir à travers ``!report <MESSAGE>`` et nous le sanctionnerons.";
			} else if(n > 8) {
				txt += "**Parfait !** Votre note va probablement rendre joyeux ``" + targetKey + "``";
			} else {
				txt += "__Bon à savoir__, n'hésitez pas à dire à ``" + targetKey + "`` comment il pourrait s'améliorer.";
			}
			
			embed.setDescription(txt);
		})
		.doOnSuccess(m -> {
			this.bay.getLive().addedNote(sender, senderKey, target, targetKey, n);
			this.bay.getPolice().addedNote(sender, senderKey, target, targetKey, n);
		})
		.subscribe();
		
		target.getChannel().flatMap(targetC -> targetC.createEmbed(embed -> {
				embed.setTitle("**__Nouvelle note reçu !__**");
				embed.setDescription("Vous avez reçu une note de " + n + "/10\n"
						+ "Vous avez donc une moyenne de " + target.getNoteAverage() + "/10\n\n"
						+ "Consultez votre profil : ``!profil``\n"
						+ "N'hésitez pas à utilisez ``!report <MESSAGE>`` en cas d'abus.");
			})).subscribe();
	}
	
}
