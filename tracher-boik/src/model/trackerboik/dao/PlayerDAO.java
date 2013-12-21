package model.trackerboik.dao;

import model.trackerboik.businessobject.Hand;
import model.trackerboik.businessobject.PokerPlayer;

import com.trackerboik.exception.TBException;

public interface PlayerDAO extends GeneralDBOperationsDAO {
	public void insertPlayer(PokerPlayer pp) throws TBException;

	public boolean isPlayerExists(String playerID) throws TBException;

	public void updatePlayerData(PokerPlayer pp) throws TBException;

	public void addPlayersDetails(PokerPlayer p)  throws TBException;
}
