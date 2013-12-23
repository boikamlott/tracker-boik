package controller.trackerboik.data;

import com.trackerboik.exception.TBException;

import model.trackerboik.businessobject.PlayerSessionStats;
import model.trackerboik.businessobject.PokerSession;
import model.trackerboik.dao.PlayerSessionStatsDAO;
import model.trackerboik.dao.sql.PlayerSessionStatsSQL;

public class PlayerFinalStat {

	/**
	 * Player stats for all sessions
	 */
	private PlayerSessionStats playerStats;
	
	public PlayerFinalStat(String playerID) throws TBException {
		playerStats = new PlayerSessionStats(playerID, PokerSession.ALL);
		PlayerSessionStatsDAO statsBDD = new PlayerSessionStatsSQL();
		statsBDD.getAggregatedDataForAllSession(playerStats);
	}
}
