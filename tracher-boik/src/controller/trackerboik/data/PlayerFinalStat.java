package controller.trackerboik.data;

import java.util.HashMap;
import java.util.Map;

import com.trackerboik.exception.TBException;

import model.trackerboik.businessobject.PlayerSessionStats;
import model.trackerboik.businessobject.PokerIndicator;
import model.trackerboik.businessobject.PokerIndicatorValue;
import model.trackerboik.businessobject.PokerSession;
import model.trackerboik.dao.PlayerSessionStatsDAO;
import model.trackerboik.dao.sql.PlayerSessionStatsSQL;

public class PlayerFinalStat {

	/**
	 * Player stats for all sessions
	 */
	private PlayerSessionStats playerStats;
	private Map<PokerIndicator, PokerIndicatorValue> indicators;
	
	public PlayerFinalStat(String playerID) throws TBException {
		playerStats = new PlayerSessionStats(playerID, PokerSession.ALL);
		PlayerSessionStatsDAO statsBDD = new PlayerSessionStatsSQL();
		statsBDD.getAggregatedDataForAllSession(playerStats);
		computeIndicatorsForPlayer();
	}

	/**
	 * Compute all indicators value from player
	 */
	private void computeIndicatorsForPlayer() {
		indicators = new HashMap<PokerIndicator, PokerIndicatorValue>();
		
		for(PokerIndicator pi : PokerIndicator.values()) {
			switch (pi) {
			case NB_HANDS:
				indicators.put(pi, new PokerIndicatorValue());
				
			}
		}
	}
}
