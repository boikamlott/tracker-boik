package model.trackerboik.businessobject;

public class PokerCard {

	private HandValue value;
	private HandColor color;
	
	public PokerCard(HandValue val, HandColor color) {
		setValue(val);
		setColor(color);
	}

	public HandValue getValue() {
		return value;
	}

	public void setValue(HandValue value) {
		this.value = value;
	}

	public HandColor getColor() {
		return color;
	}

	public void setColor(HandColor color) {
		this.color = color;
	}
	
	
}
