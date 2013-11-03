package model.trackerboik.businessobject;

public enum HandMoment {
	PREFLOP("pre flop"),
	FLOP("flop"),
	TURN("turn"),
	RIVER("river");
	
	public String valueText;
	
	private HandMoment(String valTxt) {
		this.valueText = valTxt;
	}
}
