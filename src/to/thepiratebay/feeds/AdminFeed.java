package to.thepiratebay.feeds;

import java.util.Arrays;

import to.thepiratebay.ThePirateBay;
import to.thepiratebay.commands.admin.BroadcastCommand;
import to.thepiratebay.commands.admin.DeleteCommand;
import to.thepiratebay.commands.admin.InfoBcCommand;
import to.thepiratebay.commands.admin.MaintenanceCommand;
import to.thepiratebay.commands.admin.MsgCommand;
import to.thepiratebay.commands.admin.ProfilCommand;
import to.thepiratebay.commands.admin.PublicBcCommand;
import to.thepiratebay.feeds.abst.AbstractChannelFeed;
import to.thepiratebay.stc.StaticID;

public class AdminFeed extends AbstractChannelFeed {
	
	public AdminFeed(ThePirateBay bay) {
		super(bay, true, 
				Arrays.asList(
						new BroadcastCommand(bay),
						new DeleteCommand(bay),
						new InfoBcCommand(bay),
						new MaintenanceCommand(bay),
						new MsgCommand(bay),
						new ProfilCommand(bay),
						new PublicBcCommand(bay)
				), StaticID.ADMIN);
	}

}
