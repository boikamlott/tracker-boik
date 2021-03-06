package model.trackerboik.businessobject;


public class PokerPlayer {

	private String playerID;
	private String comment;
	
	public PokerPlayer(String playerID) {
		this.playerID = playerID;
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
	
	public boolean equals(Object o) {
		return (o instanceof PokerPlayer) && ((PokerPlayer) o).getPlayerID().equals(this.playerID);
	}
	
	public String toString() {
		return playerID;
	}
	
}
