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
	
	/**
	 * Read card from the string format: with length of 2
	 * first char is the value second is the color
	 * @param card
	 * @return
	 */
	public static PokerCard readCard(String card) {
		card = card.trim();
		if(card.length() != 2) {
			return null;
		} else {
			HandValue hv = HandValue.getHandValue(card.charAt(0));
			HandColor hc = HandColor.getHandColor(card.charAt(1));
			if(hv == null || hc == null) {
				return null;
			} else {
				return new PokerCard(hv, hc);
			}
		}
	}

	/**
	 * Return true if card could be readable
	 * @return
	 */
	public boolean isCorrect() {
		return value != null && color != null;
	}
	
}
