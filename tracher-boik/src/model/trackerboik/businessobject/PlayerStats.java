package model.trackerboik.businessobject;

import java.util.HashMap;
import java.util.Map;

import model.trackerboik.dao.StatsDAO;

import com.trackerboik.exception.TBException;

public class PlayerStats {

	private String playerID;
	
	public Double benefitGeneral;
	/**
	 * Store all integer data related to user indicator
	 * Indicators key are BDD ones
	 */
	private Map<String, Integer> integerData;
	
	public PlayerStats(String playerID) throws TBException {
		this.playerID = playerID;
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

	
	public boolean equals(Object o) {
		return o instanceof PlayerStats && 
					((PlayerStats) o).getPlayerID().equals(this.playerID);
	}
	
	public String toString() {
		return playerID;
	}
}
