package model.trackerboik.dao;

import model.trackerboik.businessobject.Hand;
import model.trackerboik.businessobject.PokerPlayer;

import com.trackerboik.exception.TBException;

public interface HandPlayerDAO extends GeneralDBOperationsDAO {

	public void insertHandPlayer(Hand h, PokerPlayer pp) throws TBException;

	public void addPlayerDataForHand(Hand h) throws TBException;
}
