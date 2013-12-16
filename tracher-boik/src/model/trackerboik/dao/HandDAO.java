package model.trackerboik.dao;

import java.util.List;

import model.trackerboik.businessobject.Hand;
import model.trackerboik.businessobject.PokerSession;

import com.trackerboik.exception.TBException;

public interface HandDAO extends GeneralDBOperationsDAO {
	public void insertHand(Hand h) throws TBException;

	public boolean isHandExists(String id) throws TBException;

	public List<Hand> getAllHandsForSession(
			PokerSession ps) throws TBException;
	
}
