package model.trackerboik.businessobject;

public enum HandColor {
	SPADE("s"),
	HEART("h"),
	DIAMOND("d"),
	CLUB("c");
	
	
	public String valueText;
	
	private HandColor(String valTxt) {
		this.valueText = valTxt;
	}

	/**
	 * Return hand color switch character given in parameter
	 * @param charAt
	 * @return
	 */
	public static HandColor getHandColor(char color) {
		switch (color) {
			case 's':
				return SPADE;
			case 'h':
				return HEART;
			case 'd':
				return DIAMOND;
			case 'c':
				return CLUB;
			default:
				return null;
		}
	}
}
