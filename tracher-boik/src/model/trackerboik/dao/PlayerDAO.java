package model.trackerboik.dao;

import java.util.List;

import com.trackerboik.exception.TBException;

import model.trackerboik.businessobject.PokerPlayer;

public interface PlayerDAO extends GeneralDBOperationsDAO {
	public void insertPlayer(PokerPlayer pp) throws TBException;

	public boolean isPlayerExists(String playerID) throws TBException;

	public void addPlayerDetails(PokerPlayer p) throws TBException;

	public List<PokerPlayer> getPlayersWithIndicatorsToUpdate() throws TBException;

	public void updatePlayerData(PokerPlayer pp) throws TBException;
}
