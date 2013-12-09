package model.trackerboik.businessobject;

public class PlayerHandData {

	private Integer position;
	private PokerHand cards;
	private Double stackBefore;
	private HandResult result;
	private Boolean wasAllIn;
	private Double amountWin;
	
	public PlayerHandData() {
		//Default value in all-in case
		this.wasAllIn = false;
		this.amountWin = 0.0;
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

	public Boolean isAllIn() {
		return wasAllIn;
	}

	public void upWasAllIn() {
		this.wasAllIn = true;
	}

	public Double getAmountWin() {
		return amountWin;
	}

	public void setAmountWin(Double amountWin) {
		this.amountWin = amountWin;
	}
	
	
	
	
}
