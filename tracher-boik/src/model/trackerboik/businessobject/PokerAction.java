package model.trackerboik.businessobject;

public class PokerAction {

	private Hand associatedHand;
	private PokerPlayer associatedPlayer;
	private Integer posInHand;
	private Double amountBet;
	private ActionKind kind;
	private HandMoment moment;
	
	public PokerAction(Hand h, PokerPlayer pp) {
		setAssociatedHand(h);
		setAssociatedPlayer(pp);
	}

	public Hand getAssociatedHand() {
		return associatedHand;
	}

	public void setAssociatedHand(Hand associatedHand) {
		this.associatedHand = associatedHand;
	}

	public PokerPlayer getAssociatedPlayer() {
		return associatedPlayer;
	}

	public void setAssociatedPlayer(PokerPlayer associatedPlayer) {
		this.associatedPlayer = associatedPlayer;
	}

	public Integer getPosInHand() {
		return posInHand;
	}

	public void setPosInHand(Integer posInHand) {
		this.posInHand = posInHand;
	}

	public Double getAmountBet() {
		return amountBet;
	}

	public void setAmountBet(Double amountBet) {
		this.amountBet = amountBet;
	}

	public ActionKind getKind() {
		return kind;
	}

	public void setKind(ActionKind kind) {
		this.kind = kind;
	}

	public HandMoment getMoment() {
		return moment;
	}

	public void setMoment(HandMoment moment) {
		this.moment = moment;
	}
	
	
	
	
	
}
