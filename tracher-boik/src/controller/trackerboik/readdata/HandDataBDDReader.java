package controller.trackerboik.readdata;

import java.util.List;
import java.util.logging.Level;

import com.trackerboik.appmngt.TrackerBoikLog;
import com.trackerboik.exception.TBException;

import controller.trackerboik.main.TrackerBoikController;

import model.trackerboik.businessobject.Hand;
import model.trackerboik.businessobject.PokerPlayer;
import model.trackerboik.businessobject.PokerSession;
import model.trackerboik.dao.ActionDAO;
import model.trackerboik.dao.BoardDAO;
import model.trackerboik.dao.HandBoardDAO;
import model.trackerboik.dao.HandDAO;
import model.trackerboik.dao.HandPlayerDAO;
import model.trackerboik.dao.PlayerDAO;
import model.trackerboik.dao.SessionDAO;
import model.trackerboik.dao.sql.ActionSQL;
import model.trackerboik.dao.sql.BoardSQL;
import model.trackerboik.dao.sql.HandBoardSQL;
import model.trackerboik.dao.sql.HandPLayerSQL;
import model.trackerboik.dao.sql.HandSQL;
import model.trackerboik.dao.sql.PlayerSQL;
import model.trackerboik.dao.sql.SessionSQL;

public class HandDataBDDReader {
	
	public HandDataBDDReader() {
		
	}

	/**
	 * Return all sessions stored in database
	 * @return
	 * @throws TBException
	 */
	public List<PokerSession> getAllSessions() throws TBException {
		SessionDAO sbdd = new SessionSQL();
		List<PokerSession> pss = sbdd.getAllSesssions();
		for(PokerSession ps : pss) {
			addSessionsHands(ps);
		}
		
		return pss;
	}

	/**
	 * Add to poker session given in parameter all hands corresponding in database
	 * @param ps
	 * @throws TBException 
	 */
	private void addSessionsHands(PokerSession ps) throws TBException {
		HandDAO hbdd = new HandSQL();
		
		//Get related hands data
		List<Hand> hands = hbdd.getAllHandsForSession(ps);
		
		for(Hand h : hands) {
			try {
				addBoardToHandIfExists(h);
				addPlayersDataToHand(h);
				addActionsToHand(h);
				ps.addHand(h);
			} catch (TBException e) {
				TrackerBoikLog.getInstance().log(
						Level.WARNING, "Data of hand " + h.getId() + " could not be loaded from database because: " + e.getMessage());
			}
		}
		
	}

	/**
	 * Add Board to Hand if board associated exists in database
	 * @param h
	 */
	private void addBoardToHandIfExists(Hand h) throws TBException {
		HandBoardDAO hbbdd = new HandBoardSQL();
		String boardID = hbbdd.getBoardIDForHand(h);
		if(boardID != null) {
			BoardDAO bbdd = new BoardSQL();
			bbdd.addBoardToHand(boardID, h);
		}
		
	}
	
	/**
	 * Add playersDataToHand
	 * @param h
	 */
	private void addPlayersDataToHand(Hand h) throws TBException {
		HandPlayerDAO hpbdd = new HandPLayerSQL();
		hpbdd.addPlayerDataForHand(h);
		for(PokerPlayer p : h.getPlayers()) {
			if(!TrackerBoikController.getInstance().getPlayers().contains(p)) {
				PlayerDAO pbdd = new PlayerSQL();
				pbdd.addPlayerDetails(p);
				TrackerBoikController.getInstance().getPlayers().add(p);
			}
		}
		
	}
	
	/**
	 * Add all actions to hand
	 * @param h
	 */
	private void addActionsToHand(Hand h) throws TBException {
		ActionDAO abdd = new ActionSQL();
		h.getActions().addAll(abdd.getAllActionsForHand(h));
	}
	
}
