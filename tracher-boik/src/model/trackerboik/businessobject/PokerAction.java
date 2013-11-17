package model.trackerboik.businessobject;

public class PokerAction {

	private PokerPlayer associatedPlayer;
	private Integer posInHand;
	private Double amountBet;
	private ActionKind kind;
	private HandMoment moment;
	
	public PokerAction(PokerPlayer pp) {
		setAssociatedPlayer(pp);
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
