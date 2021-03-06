package model.trackerboik.businessobject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.trackerboik.exception.TBException;
import com.trackerboik.util.AppUtil;

public class Hand {

	/* Properties */
	private String id;
	private Double pot;
	private Double siteRake;
	private String tableName;
	private Double limitBB;
	private Calendar dateTime;
	private Integer buttonSeatNumber;
	private Integer nbPlayersOnTable;

	/* Relation */
	/* Board */
	private PokerBoard board;

	/* Player: Min 2, Max 9 */
	private List<PokerPlayer> handPlayers;

	/* Actions which compose hands, min 2 */
	private List<PokerAction> handActions;

	/* Player data for this hand */
	private Map<String, PlayerHandData> handDataForPlayer;

	/* Session associated */
	private PokerSession associatedSession;

	public Hand(String id, PokerSession s) {
		setId(id);
		setAssociatedSession(s);
		board = new PokerBoard(id);
		handPlayers = new LinkedList<PokerPlayer>();
		handActions = new LinkedList<PokerAction>();
		handDataForPlayer = new HashMap<String, PlayerHandData>();
	}
	
	/**
	 * Constructor just used for test equals function
	 * @param id
	 */
	public Hand(String id) {
		this.id = id;
	}

	/**
	 * Entry point to add players to the hand create entry in player list and
	 * entry to maps associated to players
	 * 
	 * @param players
	 */
	public void addPlayers(List<PokerPlayer> players) throws TBException {
		if (players == null) {
			throw new TBException(
					"Internal error in Hand module: impossible to add players because given list is null !");
		}

		for (PokerPlayer pp : players) {
			addPlayerToHand(pp);
		}

	}

	/**
	 * Entry point to add player to a hand create entry in payers list and
	 * associated map
	 * 
	 * @param pp
	 */
	public void addPlayerToHand(PokerPlayer pp) throws TBException {
		if (handPlayers == null || pp == null) {
			throw new TBException(
					"Internal error in Hand module: impossible to add player because some data structures are null !");
		}

		if (handPlayers.contains(pp)) {
			throw new TBException("Player '" + pp.getPlayerID()
					+ "' already registred for hand '" + this.id + "'");
		}

		handPlayers.add(pp);
		handDataForPlayer.put(pp.getPlayerID(), new PlayerHandData());
	}

	/**
	 * Set the position of player given in parameter Position one is the button,
	 * max pos is 9
	 * 
	 * @param pp
	 * @param position
	 * @throws TBException
	 */
	public void setPositionForPlayer(PokerPlayer pp, Integer position)
			throws TBException {
		if (handPlayers == null || pp == null || position == null
				|| !(0 < position && position < AppUtil.MAX_PLAYERS)) {
			throw new TBException(
					"Internal error in Hand Module: Invalid data structure or parameter(s) in set position player function");
		} else if (!handPlayers.contains(pp)) {
			throw new TBException("Impossible to find player '"
					+ pp.getPlayerID() + "' for hand '" + this.id);
		}

		handDataForPlayer.get(pp.getPlayerID()).setPosition(position);
	}

	/**
	 * Set the hand for player given in parameter Hands has to be not null
	 * 
	 * @param pp
	 * @param ph
	 * @throws TBException
	 */
	public void setHandForPlayer(PokerPlayer pp, PokerHand ph)
			throws TBException {
		if (handPlayers == null || pp == null || ph == null || !ph.isCorrect()) {
			throw new TBException(
					"Internal error in Hand Module: Invalid data structure "
							+ "or parameter(s) in set hand for player function");
		} else if (!handPlayers.contains(pp)) {
			throw new TBException("Impossible to find player '"
					+ pp.getPlayerID() + "' for hand '" + this.id + "'");
		}

		handDataForPlayer.get(pp.getPlayerID()).setCards(ph);
	}

	/**
	 * Set the start stack of player given in parameter
	 * 
	 * @param pp
	 * @param double1
	 * @throws TBException
	 */
	public void setStartStackForPlayer(PokerPlayer pp, Double stack)
			throws TBException {
		if (handPlayers == null || pp == null || stack == null || stack <= 0.0) {
			throw new TBException(
					"Internal error in Hand Module: Invalid data structure or " +
					"parameter(s) in set start stack for player function");
		} else if (!handPlayers.contains(pp)) {
			throw new TBException("Impossible to find player '"
					+ pp.getPlayerID() + "' for hand '" + this.id + "'");
		}

		handDataForPlayer.get(pp.getPlayerID()).setStartStack(stack);

	}

	/**
	 * Set Hand result for player
	 * 
	 * @param pp
	 * @param hr
	 * @throws TBException 
	 */
	public void setResultHandForPlayer(PokerPlayer pp, HandResult hr) throws TBException {
		if (handPlayers == null || pp == null || hr == null) {
			throw new TBException(
					"Internal error in Hand Module: Invalid data structure or parameter(s) " +
					"in set hand result for player function");
		} else if (!handPlayers.contains(pp)) {
			throw new TBException("Impossible to find player '"
					+ pp.getPlayerID() + "' for hand '" + this.id + "'");
		}

		handDataForPlayer.get(pp.getPlayerID()).setResult(hr);

	}
	
	/**
	 * Set all-in flag for player in this hand
	 * @param pp
	 * @throws TBException
	 */
	public void upAllInFlagForPlayer(PokerPlayer pp) throws TBException {
		if (handPlayers == null || pp == null) {
			throw new TBException(
					"Internal error in Hand Module: Invalid data structure or parameter(s) " +
					"in up AllIn flag for player function");
		} else if (!handPlayers.contains(pp)) {
			throw new TBException("Impossible to find player '"
					+ pp.getPlayerID() + "' for hand '" + this.id + "'");
		}

		handDataForPlayer.get(pp.getPlayerID()).upWasAllIn();
		
	}

	/**
	 * Set amount won by player on this hand
	 * @param pp
	 * @param amountWon
	 * @throws TBException 
	 */
	public void setAmountWonForPlayer(PokerPlayer pp, Double amountWon) throws TBException {
		if (handPlayers == null || pp == null || amountWon == null || amountWon < 0.0) {
			throw new TBException(
					"Internal error in Hand Module: Invalid data structure or parameter(s) " +
					"in set amount won for player function");
		} else if (!handPlayers.contains(pp)) {
			throw new TBException("Impossible to find player '"
					+ pp.getPlayerID() + "' for hand '" + this.id + "'");
		}

		handDataForPlayer.get(pp.getPlayerID()).setAmountWin(amountWon);		
	}
	
	/**
	 * Add actions to the hand Must be use after add all players hand
	 * 
	 * @param actions
	 */
	public void addActions(List<PokerAction> actions) throws TBException {
		if (handActions == null || actions == null) {
			throw new TBException(
					"Internal error in Hand Module: Invalid data structure or parameter(s) in add actions function");
		}

		for (PokerAction pa : actions) {
			addAction(pa);
		}
	}

	/**
	 * Entry point to add Action from a hand
	 * 
	 * @param pa
	 * @throws TBException
	 */
	public void addAction(PokerAction pa) throws TBException {
		try {
			validateAction(pa);
			handActions.add(pa);
		} catch (TBException e) {
			throw new TBException("Impossible to add action to hand '"
					+ this.id + " because: " + e.getMessage());
		}
	}

	/**
	 * Validate action related to hand players and data structure
	 * 
	 * @param pa
	 * @throws TBException
	 */
	private void validateAction(PokerAction pa) throws TBException {
		if (pa == null || handActions == null || handPlayers == null) {
			throw new TBException("action or local data structure are null !");
		}

		// Check action attributes
		if (pa.getActNoForHand() == null || pa.getAssociatedPlayer() == null
				|| pa.getHand() == null || pa.getKind() == null
				|| pa.getMoment() == null) {
			throw new TBException(
					"Mandatory(ies) attribute(s) of action is(are) null !");
		}

		// Check business validity
		if (!pa.getHand().getId().equals(this.id)) {
			throw new TBException("Invalid hands reference in action");
		} else if (!(pa.getAmount() == null
				&& pa.getKind().equals(ActionKind.FOLD) || pa.getAmount() != null
				&& !pa.getKind().equals(ActionKind.FOLD)) && !(pa.getAmount() == null
						&& pa.getKind().equals(ActionKind.CHECK) || pa.getAmount() != null
						&& !pa.getKind().equals(ActionKind.CHECK))) {
			throw new TBException(
					"Invalid relation beetween kind and amount for action");
		} else if (!handPlayers.contains(pa.getAssociatedPlayer())) {
			throw new TBException("Unknow player for action");
		} else if (handActions.size() + 1 != pa.getActNoForHand()) {
			throw new TBException(
					"Invalid action number, missing actions or action already exists !");
		}

	}

	/** Getters and setters **/

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

	public void addPokerBoard(String id) {
		this.board = new PokerBoard(id);
	}

	public PokerBoard getBoard() {
		return this.board;
	}
	
	public Integer getButtonSeatNumber() {
		return buttonSeatNumber;
	}

	public void setButtonSeatNumber(Integer buttonSeatNumber) {
		this.buttonSeatNumber = buttonSeatNumber;
	}
	
	public int getNbPlayers() {
		return nbPlayersOnTable;
	}

	public void setNbPlayers(int nbPlayer) {
		this.nbPlayersOnTable = nbPlayer;
		
	}

	public String getSQLFormattedMoment() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(this.dateTime.getTime());
	}

	public PokerHand getHandForPlayer(PokerPlayer pp) {
		return this.handDataForPlayer.get(pp.getPlayerID()).getCards();
	}

	public Integer getIntPositionForPlayer(String playerID) {
		return this.handDataForPlayer.get(playerID).getPosition();
	}

	public PokerPosition getPositionForPlayer(String playerID) {
		return PokerPosition.getPositionOfPlayer(this.getIntPositionForPlayer(playerID), getButtonSeatNumber(), getNbPlayers());
	}
	
	public List<PokerPlayer> getPlayers() {
		return handPlayers;
	}
	
	public boolean equals(Object o) {
		return (o instanceof Hand) && ((Hand) o).getId().equals(id);
	}

	/**
	 * Return player hands data for player given in parameter
	 * @param pp
	 * @return
	 */
	public PlayerHandData getPlayerHandData(String playerID) throws TBException {
		if(handDataForPlayer == null) {
			throw new TBException("Impossible to get player data because internal error");
		} else if(!handPlayers.contains(new PokerPlayer(playerID))) {
			throw new TBException("Impossible to get player data because unknow player for hand");
		} else if(handDataForPlayer.get(playerID) == null) {
			throw new TBException("Impossible to get player data because no data for player for hand");
		}
		
		return handDataForPlayer.get(playerID);
	}
	
	/**
	 * Return hands actions
	 * @return
	 */
	public List<PokerAction> getActions() {
		Collections.sort(handActions);
		return handActions;
	}

	/**
	 * Set the poker board only use for BDD import
	 * @param pb
	 */
	public void setBoard(PokerBoard pb) {
		this.board = pb;
	}

	/**
	 * Return the Player ID which is in position given in parameter
	 * @param bb
	 * @return
	 */
	public String getPlayerInPosition(PokerPosition position) {
		for(PokerPlayer pp : getPlayers()) {
			if(getPositionForPlayer(pp.getPlayerID()).equals(position)) {
				return pp.getPlayerID();
			}
		}
		
		return null;
	}


}
