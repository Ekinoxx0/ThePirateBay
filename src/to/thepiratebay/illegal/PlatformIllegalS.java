package to.thepiratebay.illegal;

import java.util.ArrayList;

public class PlatformIllegalS {
	
	private final IllegalService service;
	private boolean active = false;
	private boolean isPrivate = false;
	private boolean isWhitelisted = false;
	private ArrayList<String> contactList;
	private String details;
	
	public PlatformIllegalS(IllegalService service) {
		this.service = service;
		this.contactList = new ArrayList<String>();
		this.details = "(ERREUR)";
	}
	
	public IllegalService getService() {
		return service;
	}

	public boolean isActive() {
		return active;
	}
	
	public boolean isWhitelisted() {
		return this.isWhitelisted;
	}
	
	public boolean isPrivate() {
		return this.isPrivate;
	}

	public ArrayList<String> getContactList() {
		return contactList;
	}

	public String getDetails() {
		return details;
	}
	
	public void setActive(boolean active) {
		this.active = active;
	}
	
}
