package model.trackerboik.dao;

import java.util.List;

import model.trackerboik.businessobject.Hand;
import model.trackerboik.businessobject.PokerAction;

import com.trackerboik.exception.TBException;

public interface ActionDAO extends GeneralDBOperationsDAO {

	public void insertAction(PokerAction a) throws TBException;
	
	public List<PokerAction> getAllActionsForHand(Hand h) throws TBException;
}
