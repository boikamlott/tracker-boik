package model.trackerboik.businessobject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Hand {

	/* Properties */
	private String id;
	private Double pot;
	private Double siteRake;
	private String tableName;
	private Double limitBB;
	private Calendar dateTime;
	
	/* Relation */
	/* Board */
	private PokerBoard board;
	
	/* Player: Min 2, Max 9 */
	private List<PokerPlayer> handPlayers;
	
	/* Actions which compose hands, min 2 */
	private List<PokerAction> handActions;
	
	/* Hands of pokerPlayer during the hand */
	private Map<PokerPlayer, PokerHand> handForPlayer;
	
	/* Session associated */
	private PokerSession associatedSession;
	
	public Hand(String id, PokerSession s) {
		setId(id);
		setAssociatedSession(s);
		board = new PokerBoard();
		handPlayers = new LinkedList<PokerPlayer>();
		handActions = new LinkedList<PokerAction>();
		handForPlayer = new HashMap<PokerPlayer, PokerHand>();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Double getPot() {
		return pot;
	}

	public void setPot(Double pot) {
		this.pot = pot;
	}

	public Double getSiteRake() {
		return siteRake;
	}

	public void setSiteRake(Double siteRake) {
		this.siteRake = siteRake;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public Double getLimitBB() {
		return limitBB;
	}

	public void setLimitBB(Double limitBB) {
		this.limitBB = limitBB;
	}

	public Calendar getDateTime() {
		return dateTime;
	}

	public void setDateTime(Calendar dateTime) {
		this.dateTime = dateTime;
	}

	public PokerSession getAssociatedSession() {
		return associatedSession;
	}

	public void setAssociatedSession(PokerSession associatedSession) {
		this.associatedSession = associatedSession;
	}
	
	
}
