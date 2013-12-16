package model.trackerboik.dao;

import model.trackerboik.businessobject.Hand;
import model.trackerboik.businessobject.PokerBoard;

import com.trackerboik.exception.TBException;

public interface HandBoardDAO extends GeneralDBOperationsDAO {
	public void insertHandBoard(Hand h, PokerBoard pb) throws TBException ;

	public String getBoardIDForHand(Hand h) throws TBException ;
}
