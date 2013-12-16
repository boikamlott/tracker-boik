package model.trackerboik.dao;

import com.trackerboik.exception.TBException;

import model.trackerboik.businessobject.PokerPlayer;

public interface PlayerDAO extends GeneralDBOperationsDAO {
	public void insertPlayer(PokerPlayer pp) throws TBException;

	public boolean isPlayerExists(String playerID) throws TBException;

	public void addPlayerDetails(PokerPlayer p) throws TBException;
}
