package model.trackerboik.businessobject;

public class PlayerHandData {

	private Integer position;
	private PokerHand cards;
	private Double stackBefore;
	private HandResult result;
	
	public PlayerHandData() {
		
	}

	public Integer getPosition() {
		return position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}

	public PokerHand getCards() {
		return cards;
	}

	public void setCards(PokerHand cards) {
		this.cards = cards;
	}

	public Double getStackBefore() {
		return stackBefore;
	}

	public void setStartStack(Double stackBefore) {
		this.stackBefore = stackBefore;
	}

	public HandResult getResult() {
		return result;
	}

	public void setResult(HandResult result) {
		this.result = result;
	}
	
	
}
