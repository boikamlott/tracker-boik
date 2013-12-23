package controller.trackerboik.main;

import java.util.List;
import java.util.logging.Level;

import model.trackerboik.businessobject.Hand;
import model.trackerboik.businessobject.PlayerSessionStats;
import model.trackerboik.businessobject.PokerSession;
import model.trackerboik.dao.ActionDAO;
import model.trackerboik.dao.HandPlayerDAO;
import model.trackerboik.dao.PlayerSessionStatsDAO;
import model.trackerboik.dao.SessionDAO;
import model.trackerboik.dao.sql.ActionSQL;
import model.trackerboik.dao.sql.HandPLayerSQL;
import model.trackerboik.dao.sql.PlayerSessionStatsSQL;
import model.trackerboik.dao.sql.SessionSQL;

import com.trackerboik.appmngt.TrackerBoikLog;
import com.trackerboik.exception.TBException;

import controller.trackerboik.data.HandDataCalculator;

public class AggregateDataController {
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
		parentController.getPlayerSessionsStats().clear();
		for(PokerSession ps : parentController.getSessions()) {
			PlayerSessionStatsDAO pssBDD = new PlayerSessionStatsSQL();
			List<PlayerSessionStats> playersToUpdate = pssBDD.getPlayersWithIndicatorsToUpdate(ps);
			
			for(PlayerSessionStats pp : playersToUpdate) {
				try {
					recalculatePlayerIndicators(pp);
					pssBDD.updatePlayerStats(pp);
					parentController.getPlayerSessionsStats().add(pp);
				} catch (TBException e) {
					TrackerBoikLog.getInstance().log(Level.WARNING, "Impossible to upadte player '" + 
									pp.getPlayerID() + "' indicators: " + e.getMessage());
				}
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

	private void recalculatePlayerIndicators(PlayerSessionStats pp) throws TBException {
		HandPlayerDAO hpbdd = new HandPLayerSQL();
		ActionDAO abdd = new ActionSQL();
		
		pp.getIntegerData().put(PlayerSessionStatsDAO.ATT_NB_HANDS, 
				pp.getIntegerData().get(PlayerSessionStatsDAO.ATT_NB_HANDS) + hpbdd.getNbHandsPlayedForNewSessions(pp));
		pp.getIntegerData().put(PlayerSessionStatsDAO.ATT_NB_HANDS_VPIP, 
				pp.getIntegerData().get(PlayerSessionStatsDAO.ATT_NB_HANDS_VPIP) + abdd.getNbHandsVPIPPlayedForNewSessions(pp));
		pp.getIntegerData().put(PlayerSessionStatsDAO.ATT_NB_RAISE_PREFLOP, 
				pp.getIntegerData().get(PlayerSessionStatsDAO.ATT_NB_RAISE_PREFLOP) + abdd.getNbHandsPFRPlayedForNewSessions(pp));
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
	private void computeIndicatorForNewSessions(PlayerSessionStats pp) throws TBException {
		HandDataCalculator hdc;
		
		for(PokerSession ps : parentController.getSessions()) {
			for(Hand h : ps.getHands()) {
				if(h.getPlayers().contains(pp.getPlayerID())) {
					try {
						hdc = new HandDataCalculator(pp, h);
						hdc.computeIndicatorForHandAndPlayer();
					} catch (Exception e) {
						TrackerBoikLog.getInstance().log(Level.SEVERE, "Impossible to analyse hand " + 
										h.getId() + " for player " + pp.getPlayerID() + " moves because: " + e.getMessage());
					}
				}
			}
		}
	}
	
}
