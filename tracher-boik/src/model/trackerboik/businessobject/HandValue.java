package model.trackerboik.businessobject;

public enum HandValue {
	TWO("2"),
	THREE("3"),
	FOUR("4"),
	FIVE("5"),
	SIX("6"),
	SEVEN("7"),
	HEIGHT("8"),
	NINE("9"),
	TEN("T"),
	JACK("J"),
	QUEEN("Q"),
	KING("K"),
	AS("A");
	
	
	public String valueText;
	
	private HandValue(String valTxt) {
		this.valueText = valTxt;
	}
	
	/**
	 * Return HandValue associated to character
	 * @param val
	 * @return
	 */
	public static HandValue getHandValue(char val)  {
		switch (val) {
		case '2':
			return TWO;
		case '3':
			return THREE;
		case '4':
			return FOUR;
		case '5':
			return FIVE;
		case '6':
			return SIX;
		case '7':
			return SEVEN;
		case '8':
			return HEIGHT;
		case '9':
			return NINE;
		case 'T':
			return TEN;
		case 'J':
			return JACK;
		case 'Q':
			return QUEEN;
		case 'K':
			return KING;
		case 'A':
			return AS;
		default:
			return null;
		}
		
	}
	
	public String toString() {
		return valueText;
	}
}
