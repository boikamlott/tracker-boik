package model.trackerboik.dao;

import java.util.List;

import model.trackerboik.businessobject.Hand;
import model.trackerboik.businessobject.PlayerSessionStats;
import model.trackerboik.businessobject.PokerAction;
import model.trackerboik.businessobject.PokerPlayer;

import com.trackerboik.exception.TBException;

public interface ActionDAO extends GeneralDBOperationsDAO {

	public void insertAction(PokerAction a) throws TBException;
	
	public List<PokerAction> getAllActionsForHand(Hand h) throws TBException;
	
	public Integer getNbHandsVPIPPlayedForNewSessions(PlayerSessionStats pp) throws TBException;

	public Integer getNbHandsPFRPlayedForNewSessions(PlayerSessionStats pp) throws TBException;

}
