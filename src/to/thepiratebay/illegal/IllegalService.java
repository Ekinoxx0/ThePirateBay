package to.thepiratebay.illegal;

public enum IllegalService {
	
	WEED("Weed", "herb"),
	TUEUR_A_GAGE("Tueur à gages", "knife"),
	BLANCHISSEUR("Blanchiment", "money_with_wings"),
	ARMES_ILLEGAL("Ventes d'armes illégales", "gun"),
	GANG("Gang", "shark"),
	PARIS("Paris illégaux", "slot_machine");
	
	private String name;
	private String emoji;
	private IllegalService(String name, String emoji) {
		this.name = name;
		this.emoji = emoji;
	}
	
	public String getName() {
		return this.name;
	}

	public String getEmoji() {
		return ":" + this.emoji + ":";
	}
	
	/*
	 * 
	 */
	
	public static IllegalService searchService(String txt) {
		for (IllegalService i : IllegalService.values()) {
			if (i.toString().equalsIgnoreCase(txt)) {
				return i;
			} else if (i.toString().toLowerCase().contains(txt)) {
				return i;
			} else if (i.getName().toLowerCase().contains(txt)) {
				return i;
			}
		}
		return null;
	}
	
}
