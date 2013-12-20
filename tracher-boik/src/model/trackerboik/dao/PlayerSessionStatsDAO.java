package model.trackerboik.dao;

import java.util.List;

import model.trackerboik.businessobject.PlayerSessionStats;
import model.trackerboik.businessobject.PokerSession;

import com.trackerboik.exception.TBException;

public interface PlayerSessionStatsDAO extends GeneralDBOperationsDAO {
	
	public void insertPlayerStats(PlayerSessionStats pss) throws TBException;

	public boolean isStatsExists(String playerID, String sessionID) throws TBException;

	public List<PlayerSessionStats> getPlayersWithIndicatorsToUpdate(PokerSession ps) throws TBException;

	public void updatePlayerStats(PlayerSessionStats pss) throws TBException;
}
