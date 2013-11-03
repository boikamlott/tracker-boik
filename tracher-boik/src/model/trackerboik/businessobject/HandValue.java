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
}
