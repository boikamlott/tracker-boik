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
}
