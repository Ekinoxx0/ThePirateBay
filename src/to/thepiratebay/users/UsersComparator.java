package to.thepiratebay.users;

import java.util.Comparator;

import to.thepiratebay.ThePirateBay;

public class UsersComparator {
	
	private ThePirateBay bay;
	public UsersComparator(ThePirateBay bay) {
		this.bay = bay;
	}
	
	public Comparator<String> keyComparatorPerNote = (x, y) -> {
		return (int) (this.bay.getKeyDB().getNoteOf(x) - this.bay.getKeyDB().getNoteOf(y));
	};
	
}
