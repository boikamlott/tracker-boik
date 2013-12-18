package controller.trackerboik.main;

import java.util.List;
import java.util.logging.Level;

import model.trackerboik.businessobject.ActionKind;
import model.trackerboik.businessobject.Hand;
import model.trackerboik.businessobject.PokerAction;
import model.trackerboik.businessobject.PokerPlayer;
import model.trackerboik.businessobject.PokerSession;
import model.trackerboik.dao.ActionDAO;
import model.trackerboik.dao.HandPlayerDAO;
import model.trackerboik.dao.PlayerDAO;
import model.trackerboik.dao.SessionDAO;
import model.trackerboik.dao.sql.ActionSQL;
import model.trackerboik.dao.sql.HandPLayerSQL;
import model.trackerboik.dao.sql.PlayerSQL;
import model.trackerboik.dao.sql.SessionSQL;

import com.trackerboik.appmngt.TrackerBoikLog;
import com.trackerboik.exception.TBException;

public class AggregateDataController {
	private static final Integer NB_CALCULATED_INDICATORS = 8;
	private static final Integer NB_CBET_POSSIBLE = 1, NB_CBET = 2, NB_FOLD_TO_CBET_POSSIBLE = 3,
									NB_FOLD_TO_CBET = 4, NB_SECOND_BARREL_POSSIBLE = 5, NB_SECOND_BARREL = 6,
									NB_FOLD_TO_SECOND_BARREL_POSSIBLE = 7, NB_FOLD_TO_SECOND_BARREL = 8;
	
	
	
	private TrackerBoikController parentController;
	
	public AggregateDataController(TrackerBoikController controller) {
		this.parentController = controller;
	}

	/**
	 * Central function of module
	 * Get all players which has played recent sessions
	 * And recalculate their indicators
	 */
	public void refreshIndicatorsData() throws TBException {
		parentController.getPlayers().clear();
		PlayerDAO pbdd = new PlayerSQL();
		List<PokerPlayer> playersToUpdate = pbdd.getPlayersWithIndicatorsToUpdate();
		
		for(PokerPlayer pp : playersToUpdate) {
			try {
				recalculatePlayerIndicators(pp);
				pbdd.updatePlayerData(pp);
				parentController.getPlayers().add(pp);
			} catch (TBException e) {
				TrackerBoikLog.getInstance().log(Level.WARNING, "Impossible to upadte player '" + 
								pp.getPlayerID() + "' indicators: " + e.getMessage());
			}
		}
		
		try {
			markAllSessionsAsCalculated();
		} catch (TBException e) {
			TrackerBoikLog.getInstance().log(Level.WARNING, "Data will be incoherent because " +
					"indicators for new sessions has been calculated and session could not be marked as calculated: " + 
					e.getMessage());
		}
	}

	private void recalculatePlayerIndicators(PokerPlayer pp) throws TBException {
		HandPlayerDAO hpbdd = new HandPLayerSQL();
		ActionDAO abdd = new ActionSQL();
		
		pp.nbHand += hpbdd.getNbHandsPlayedForNewSessions(pp);
		pp.nbHandVPIP += abdd.getNbHandsVPIPPlayedForNewSessions(pp);
		pp.nbHandPFR += abdd.getNbHandsPFRPlayedForNewSessions(pp);
		computeIndicatorForNewSessions(pp);
		
	}

	/**
	 * Mark all sessions as aggregated data calculated in database
	 */
	private void markAllSessionsAsCalculated() throws TBException {
		SessionDAO sbdd = new SessionSQL();
		sbdd.markAllSessionsAsCalculated();
	}
	
	/**
	 * Analyse all hands of unread sessions in memory to determine all active indicators
	 * for user given in parameters and the new sessions
	 * @param pp
	 * @return
	 * @throws TBException 
	 */
	private void computeIndicatorForNewSessions(PokerPlayer pp) throws TBException {
		for(PokerSession ps : parentController.getSessions()) {
			for(Hand h : ps.getHands()) {
				if(h.getPlayers().contains(pp)) {
					computeIndicatorForHandAndPlayer(h, pp);
				}
			}
		}
	}

	/**
	 * Compute all indicator for the player and the hand given in parameter
	 * PRE: Player has play the hand
	 * @param h
	 * @param pp
	 * @throws TBException 
	 */
	private void computeIndicatorForHandAndPlayer(Hand h, PokerPlayer pp) throws TBException {
		Double localBenefit = 0.0;
		Double amountToCall = h.getLimitBB();
		Integer nbPlayers = h.getPlayers().size();
		List<PokerAction> actions = h.getActions();
		boolean cbetPossible = false, continueRead = true;
		int actCurNo = 0;
		
		while(actCurNo < actions.size() && continueRead) {
			PokerAction a = actions.get(actCurNo);
			if(a.getKind() == ActionKind.BET) {
				amountToCall = a.getAmountBet();
			}
			
			if(a.getAssociatedPlayer().equals(pp)) {
				switch(a.getMoment()) {
					case PREFLOP:
						switch (a.getKind()) {
							case POSTSBLIND:
							case POSTBIGBLIND:
							case CALL:		
								localBenefit -= a.getAmountBet();
								break;
							case RAISE:
								cbetPossible = true;
								localBenefit -= amountToCall + a.getAmountBet();
							case FOLD:
								continueRead = false;
								break;
							case CHECK:
								break;
							case BET:
								throw new TBException("Hand " + h.getId() + " was not correctly formatted in BDD, " +
										"impossible to bet on flop !");
								
						}
				 
				}
			} else {
				
			}
			
			actCurNo++;
		}
	}
	
}
