package controller.trackerboik.main;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import model.trackerboik.businessobject.Hand;
import model.trackerboik.businessobject.PlayerStats;
import model.trackerboik.businessobject.PokerPlayer;
import model.trackerboik.dao.ActionDAO;
import model.trackerboik.dao.HandDAO;
import model.trackerboik.dao.HandPlayerDAO;
import model.trackerboik.dao.StatsDAO;
import model.trackerboik.dao.sql.ActionSQL;
import model.trackerboik.dao.sql.HandPLayerSQL;
import model.trackerboik.dao.sql.HandSQL;
import model.trackerboik.dao.sql.PlayerStatsSQL;
import model.trackerboik.data.HandAnalyser;
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
		
		Map<String, PlayerStats> playersToUpdate = statBDD.getPlayersWithIndicatorsToUpdate();
		
		//Compute all players stats data with hand analyser
		for(Hand h : parentController.getHands()) {
			HandAnalyser ha = new HandAnalyser(playersToUpdate, h);
			try {
				ha.analyse();
			} catch (TBException e) {
				TrackerBoikLog.getInstance().log(Level.WARNING, "Impossible to analyse data of hand '" + 
						h.getId() + "' : " + e.getMessage());
			}
		}
		
		//Update General Indicators From DataBase And save data
		for(String pp : playersToUpdate.keySet()) {
			try {
				PlayerStats currentPlayer = playersToUpdate.get(pp);
				recalculatePlayerIndicatorsFromDB(currentPlayer);
				statBDD.updatePlayerStats(currentPlayer);
				parentController.getPlayerSessionsStats().add(currentPlayer);
			} catch (TBException e) {
				TrackerBoikLog.getInstance().log(Level.WARNING, "Impossible to upadte player '" + 
								pp + "' indicators from DataBase: " + e.getMessage());
			}
		}

		
		//TODO Uncomment when running correctly
//		try {
//			markAllHandsAsCalculated();
//		} catch (TBException e) {
//			TrackerBoikLog.getInstance().log(Level.WARNING, "Data will be incoherent because " +
//					"indicators for new sessions has been calculated and session could not be marked as calculated: " + 
//					e.getMessage());
//		}
	}

	private void recalculatePlayerIndicatorsFromDB(PlayerStats pp) throws TBException {
		HandPlayerDAO hpbdd = new HandPLayerSQL();
		ActionDAO abdd = new ActionSQL();
		
		pp.getIntegerData().put(StatsDAO.ATT_HANDS, 
				pp.getIntegerData().get(StatsDAO.ATT_HANDS) + hpbdd.getNbHandsPlayedForNewSessions(pp));
		pp.getIntegerData().put(StatsDAO.ATT_HANDS_VPIP, 
				pp.getIntegerData().get(StatsDAO.ATT_HANDS_VPIP) + abdd.getNbHandsVPIPPlayedForNewSessions(pp));
		pp.getIntegerData().put(StatsDAO.ATT_RAISE_PREFLOP, 
				pp.getIntegerData().get(StatsDAO.ATT_RAISE_PREFLOP) + abdd.getNbHandsPFRPlayedForNewSessions(pp));
		
	}

	/**
	 * Mark all sessions as aggregated data calculated in database
	 */
	private void markAllHandsAsCalculated() throws TBException {
		HandDAO hbdd = new HandSQL();
		hbdd.markAllHandsAsCalculated();
	}


	/**
	 * Reset all players stats data in database
	 */
	public void resetAllPlayersStatsData() throws TBException {
		try {
			StatsDAO sBDD = new PlayerStatsSQL();
			sBDD.resetAllData();
		} catch (TBException e) {
			throw new TBException("Impossible de remettre à zéro les données en base: " + e.getMessage());
		}
		
	}
	
}
