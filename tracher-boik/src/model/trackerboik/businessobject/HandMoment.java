package model.trackerboik.businessobject;

public enum HandMoment {
	PREFLOP("preflop"),
	FLOP("flop"),
	TURN("turn"),
	RIVER("river");
	
	public String valueText;
	
	private HandMoment(String valTxt) {
		this.valueText = valTxt;
	}
	
	public String toString() {
		return valueText;
	}
 }
