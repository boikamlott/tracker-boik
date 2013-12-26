package model.trackerboik.businessobject;

import java.util.HashMap;
import java.util.Map;

import model.trackerboik.dao.StatsDAO;

import com.trackerboik.exception.TBException;

public class PlayerSessionStats {

	private String playerID;
	private PokerSession session;
	
	public Double benefitGeneral;
	/**
	 * Store all integer data related to user indicator
	 * Indicators key are BDD ones
	 */
	private Map<String, Integer> integerData;
	
	public PlayerSessionStats(String playerID, PokerSession ps) throws TBException {
		this.playerID = playerID;
		this.session = ps;
		this.integerData = new HashMap<String, Integer>();
		for(String att : StatsDAO.INT_ATTRIBUTES) {
			this.integerData.put(att, 0);
		}
	}

	public Map<String, Integer> getIntegerData() {
		return integerData;
	}
	
	public Integer getStats(String statName) {
		return integerData.get(statName);
	}
	
	/**
	 * Just shortcut to add one (current use) to an indicator
	 * throws exception if error
	 * @param indicatorName
	 * @throws TBException
	 */
	public void addOneToIndicator(String indicatorName) throws TBException {
		if(integerData.get(indicatorName) == null) {
			throw new TBException("unknow indicator '" + indicatorName + "' !");
		} else {
			integerData.put(indicatorName, integerData.get(indicatorName) + 1);
		}
	}
	
	public String getPlayerID() {
		return playerID;
	}

	public PokerSession getSession() {
		return session;
	}
	
	public boolean equals(Object o) {
		return o instanceof PlayerSessionStats && 
					((PlayerSessionStats) o).getPlayerID().equals(this.playerID) &&
					((PlayerSessionStats) o).getSession().equals(this.session);
	}
	
	public String toString() {
		return session + " -> " + playerID;
	}
}
