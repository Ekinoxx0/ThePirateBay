package to.thepiratebay.feeds;

import java.util.Arrays;

import to.thepiratebay.ThePirateBay;
import to.thepiratebay.commands.users.contact.AddContactCommand;
import to.thepiratebay.commands.users.contact.ContactsCommand;
import to.thepiratebay.commands.users.contact.RemoveContactCommand;
import to.thepiratebay.commands.users.msg.MsgCommand;
import to.thepiratebay.commands.users.msg.RCommand;
import to.thepiratebay.commands.users.others.IgnoreCommand;
import to.thepiratebay.commands.users.others.PublicCommand;
import to.thepiratebay.commands.users.others.ReportCommand;
import to.thepiratebay.commands.users.others.SaveCommand;
import to.thepiratebay.commands.users.profil.NoteCommand;
import to.thepiratebay.commands.users.profil.ProfilCommand;
import to.thepiratebay.commands.users.services.JoinCommand;
import to.thepiratebay.commands.users.services.LeaveCommand;
import to.thepiratebay.commands.users.services.ServiceCommand;
import to.thepiratebay.feeds.abst.AbstractFeed;

public class UsersFeed extends AbstractFeed {

	public UsersFeed(ThePirateBay bay) {
		super(bay, false, Arrays.asList(
				new MsgCommand(bay),
				new RCommand(bay),
				new PublicCommand(bay),
				new ReportCommand(bay),
				new ProfilCommand(bay),
				new NoteCommand(bay),
				new JoinCommand(bay),
				new LeaveCommand(bay),
				new ServiceCommand(bay),
				new SaveCommand(bay),
				new ContactsCommand(bay),
				new AddContactCommand(bay),
				new RemoveContactCommand(bay),
				new IgnoreCommand(bay)
				));
	}


}
