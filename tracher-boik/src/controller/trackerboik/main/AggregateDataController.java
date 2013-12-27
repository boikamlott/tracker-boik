package controller.trackerboik.main;

import java.util.List;
import java.util.logging.Level;

import model.trackerboik.businessobject.Hand;
import model.trackerboik.businessobject.PlayerStats;
import model.trackerboik.dao.ActionDAO;
import model.trackerboik.dao.HandDAO;
import model.trackerboik.dao.HandPlayerDAO;
import model.trackerboik.dao.StatsDAO;
import model.trackerboik.dao.sql.ActionSQL;
import model.trackerboik.dao.sql.HandPLayerSQL;
import model.trackerboik.dao.sql.HandSQL;
import model.trackerboik.dao.sql.PlayerStatsSQL;
import model.trackerboik.data.HandDataCalculator;

import com.trackerboik.appmngt.TrackerBoikLog;
import com.trackerboik.exception.TBException;


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
		StatsDAO statBDD = new PlayerStatsSQL();
		
		List<PlayerStats> playersToUpdate = statBDD.getPlayersWithIndicatorsToUpdate();
		
		for(PlayerStats pp : playersToUpdate) {
			try {
				recalculatePlayerIndicators(pp);
				statBDD.updatePlayerStats(pp);
				parentController.getPlayerSessionsStats().add(pp);
			} catch (TBException e) {
				TrackerBoikLog.getInstance().log(Level.WARNING, "Impossible to upadte player '" + 
								pp.getPlayerID() + "' indicators: " + e.getMessage());
			}
		}
		
		
		try {
			markAllHandsAsCalculated();
		} catch (TBException e) {
			TrackerBoikLog.getInstance().log(Level.WARNING, "Data will be incoherent because " +
					"indicators for new sessions has been calculated and session could not be marked as calculated: " + 
					e.getMessage());
		}
	}

	private void recalculatePlayerIndicators(PlayerStats pp) throws TBException {
		HandPlayerDAO hpbdd = new HandPLayerSQL();
		ActionDAO abdd = new ActionSQL();
		
		pp.getIntegerData().put(StatsDAO.ATT_HANDS, 
				pp.getIntegerData().get(StatsDAO.ATT_HANDS) + hpbdd.getNbHandsPlayedForNewSessions(pp));
		pp.getIntegerData().put(StatsDAO.ATT_HANDS_VPIP, 
				pp.getIntegerData().get(StatsDAO.ATT_HANDS_VPIP) + abdd.getNbHandsVPIPPlayedForNewSessions(pp));
		pp.getIntegerData().put(StatsDAO.ATT_RAISE_PREFLOP, 
				pp.getIntegerData().get(StatsDAO.ATT_RAISE_PREFLOP) + abdd.getNbHandsPFRPlayedForNewSessions(pp));
		computeIndicatorForNewSessions(pp);
		
	}

	/**
	 * Mark all sessions as aggregated data calculated in database
	 */
	private void markAllHandsAsCalculated() throws TBException {
		HandDAO hbdd = new HandSQL();
		hbdd.markAllHandsAsCalculated();
	}
	
	/**
	 * Analyse all hands of unread sessions in memory to determine all active indicators
	 * for user given in parameters and the new sessions
	 * @param pp
	 * @return
	 * @throws TBException 
	 */
	private void computeIndicatorForNewSessions(PlayerStats pp) throws TBException {
		HandDataCalculator hdc;
		
		for(Hand h : parentController.getHands()) {
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
