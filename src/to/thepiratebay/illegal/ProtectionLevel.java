package to.thepiratebay.illegal;

public enum ProtectionLevel {
	
	NOTHING(1),
	VPN(2);

	private int multiply;
	private ProtectionLevel(int multiply) {
		this.multiply = multiply;
	}
	
	public int getMultiply() {
		return multiply;
	}

}
