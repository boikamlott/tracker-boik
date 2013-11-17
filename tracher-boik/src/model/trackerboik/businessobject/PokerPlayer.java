package model.trackerboik.businessobject;

import java.util.HashMap;
import java.util.Map;

public class PokerPlayer {

	private Map<PokerAction, Hand> actionsHands;
	private String playerID;
	private String comment;
	
	public PokerPlayer(String playerID) {
		this.playerID = playerID;
		actionsHands = new HashMap<PokerAction, Hand>();
	}

	public String getPlayerID() {
		return playerID;
	}

	public void setPlayerID(String playerID) {
		this.playerID = playerID;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
	
	public void addActionHand(PokerAction pa, Hand h) {
		this.actionsHands.put(pa, h);
	}
	
	
	
	
}
