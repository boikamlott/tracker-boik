package model.trackerboik.data;

import java.util.HashMap;
import java.util.Map;

import model.trackerboik.businessobject.PlayerStats;
import model.trackerboik.businessobject.PokerIndicator;
import model.trackerboik.businessobject.PokerIndicatorValue;
import model.trackerboik.dao.StatsDAO;
import model.trackerboik.dao.sql.PlayerStatsSQL;

import com.trackerboik.exception.TBException;

public class PlayerFinalStat {

	/**
	 * Player stats for all sessions
	 */
	private PlayerStats playerStats;
	private Map<PokerIndicator, PokerIndicatorValue> indicators;
	
	public PlayerFinalStat(String playerID) throws TBException {
		playerStats = new PlayerStats(playerID);
		StatsDAO statsBDD = new PlayerStatsSQL();
		statsBDD.getAggregatedDataForAllSession(playerStats);
		computeIndicatorsForPlayer();
	}

	/**
	 * Compute all indicators value from player
	 */
	private void computeIndicatorsForPlayer() {
		indicators = new HashMap<PokerIndicator, PokerIndicatorValue>();
		Integer nbHands = playerStats.getStats(StatsDAO.ATT_NB_HANDS);
		
		for(PokerIndicator pi : PokerIndicator.values()) {
			switch (pi) {
			//General Data
			case NB_HANDS:
				indicators.put(pi, new PokerIndicatorValue(nbHands, nbHands.doubleValue()));
				break;
				
			case NB_FLOP_SEEN:
				indicators.put(pi, new PokerIndicatorValue(playerStats.getStats(StatsDAO.ATT_NB_HANDS_FLOP), nbHands));
				break;
			
			case NB_TURN_SEEN:
				indicators.put(pi, new PokerIndicatorValue(playerStats.getStats(StatsDAO.ATT_NB_HANDS_TURN), nbHands));
				break;
				
			case NB_RIVER_SEEN:
				indicators.put(pi, new PokerIndicatorValue(playerStats.getStats(StatsDAO.ATT_NB_HANDS_RIVER), nbHands));
				break;	
				
			case WINRATE :
				indicators.put(pi, new PokerIndicatorValue(playerStats.benefitGeneral.intValue(), nbHands));
				break;
				
			case VPIP:
				indicators.put(pi, new PokerIndicatorValue(playerStats.getStats(StatsDAO.ATT_NB_HANDS_VPIP), nbHands));
				break;
				
			case PREFLOP_RAISE:
				indicators.put(pi, new PokerIndicatorValue(playerStats.getStats(StatsDAO.ATT_NB_RAISE_PREFLOP), nbHands));
				break;
			
			//AF Data
			case AGRESSION_FACTOR_GENERAL:
				indicators.put(pi, new PokerIndicatorValue(nbHands, 
						playerStats.getStats(StatsDAO.ATT_NB_AGRESSION_FACTOR_GENERAL_BET_RAISE).doubleValue() / 
						playerStats.getStats(StatsDAO.ATT_NB_AGRESSION_FACTOR_GENERAL_CALL).doubleValue()));
				break;
				
			case AGRESSION_FACTOR_FLOP :
				indicators.put(pi, new PokerIndicatorValue(playerStats.getStats(StatsDAO.ATT_NB_HANDS_FLOP), 
						playerStats.getStats(StatsDAO.ATT_NB_AF_FLOP_BR).doubleValue() / 
						playerStats.getStats(StatsDAO.ATT_NB_AF_FLOP_C).doubleValue()));
				break;
				
			case AGRESSION_FACTOR_TURN :
				indicators.put(pi, new PokerIndicatorValue(playerStats.getStats(StatsDAO.ATT_NB_HANDS_TURN), 
						playerStats.getStats(StatsDAO.ATT_NB_AF_TURN_BR).doubleValue() / 
						playerStats.getStats(StatsDAO.ATT_NB_AF_TURN_C).doubleValue()));
				break;
				
			case AGRESSION_FACTOR_RIVER :
				indicators.put(pi, new PokerIndicatorValue(playerStats.getStats(StatsDAO.ATT_NB_HANDS_RIVER), 
						playerStats.getStats(StatsDAO.ATT_NB_AF_RIVER_BR).doubleValue() / 
						playerStats.getStats(StatsDAO.ATT_NB_AF_RIVER_C).doubleValue()));
				break;
			//PREFLOP Data
			case LIMP_THEN_CALL:
				indicators.put(pi, new PokerIndicatorValue(playerStats.getStats(StatsDAO.ATT_NB_LIMP), 
															playerStats.getStats(StatsDAO.ATT_NB_LIMP_THEN_CALL)));
				break;
				
			case LIMP_THEN_FOLD:
				indicators.put(pi, new PokerIndicatorValue(playerStats.getStats(StatsDAO.ATT_NB_LIMP), 
															playerStats.getStats(StatsDAO.ATT_NB_LIMP_THEN_FOLD)));
				break;
			
			case THREE_BET:
				indicators.put(pi, new PokerIndicatorValue(playerStats.getStats(StatsDAO.ATT_NB_3BET), 
						playerStats.getStats(StatsDAO.ATT_NB_3BET_POSSIBLE)));
				break;
				
			case FOLD_TO_THREE_BET:
				indicators.put(pi, new PokerIndicatorValue(playerStats.getStats(StatsDAO.ATT_NB_FOLD_TO_3BET), 
						playerStats.getStats(StatsDAO.ATT_NB_FOLD_TO_3BET_POSSIBLE)));
				break;
				
			//ATS Data
			case ATTEMPT_TO_STEAL_BLINDS:
				indicators.put(pi, new PokerIndicatorValue(playerStats.getStats(StatsDAO.ATT_NB_ATS), 
						playerStats.getStats(StatsDAO.ATT_NB_ATS_POSSIBLE)));
				break;
				
			case FOLD_TO_ATS_SB:
				indicators.put(pi, new PokerIndicatorValue(playerStats.getStats(StatsDAO.ATT_NB_FOLD_TO_ATS_SB), 
						playerStats.getStats(StatsDAO.ATT_NB_FOLD_TO_ATS_SB_POSSIBLE)));
				break;
				
			case FOLD_TO_ATS_BB:
				indicators.put(pi, new PokerIndicatorValue(playerStats.getStats(StatsDAO.ATT_NB_FOLD_TO_ATS_BB), 
						playerStats.getStats(StatsDAO.ATT_NB_FOLD_TO_ATS_BB_POSSIBLE)));
				break;	
			
			//FLOP Data	
			case CBET:
				indicators.put(pi, new PokerIndicatorValue(playerStats.getStats(StatsDAO.ATT_NB_CBET), 
						playerStats.getStats(StatsDAO.ATT_NB_CBET_POSSIBLE)));
				break;
				
			case FOLD_TO_CBET:
				indicators.put(pi, new PokerIndicatorValue(playerStats.getStats(StatsDAO.ATT_NB_FOLD_TO_CBET), 
						playerStats.getStats(StatsDAO.ATT_NB_FOLD_TO_CBET_POSSIBLE)));
				break;	
			
			//Turn Data
			case SECOND_BARREL:
				indicators.put(pi, new PokerIndicatorValue(playerStats.getStats(StatsDAO.ATT_NB_SECOND_BARREL), 
						playerStats.getStats(StatsDAO.ATT_NB_SECOND_BARREL_POSSIBLE)));
				break;
				
			case FOLD_TO_SECOND_BARREl:
				indicators.put(pi, new PokerIndicatorValue(playerStats.getStats(StatsDAO.ATT_NB_FOLD_TO_SECOND_BARREL), 
						playerStats.getStats(StatsDAO.ATT_NB_FOLD_TO_SECOND_BARREL_POSSIBLE)));
				break;		
			//Showdown data
			case WENT_TO_SHOWDOWN:
				indicators.put(pi, new PokerIndicatorValue(playerStats.getStats(StatsDAO.ATT_NB_WENT_TO_SHOWDOWN), nbHands));
				break;
				
			case WIN_TO_SHOWDOWN:
				indicators.put(pi, new PokerIndicatorValue(playerStats.getStats(StatsDAO.ATT_NB_WIN_TO_SHOWDOWN), 
						playerStats.getStats(StatsDAO.ATT_NB_WENT_TO_SHOWDOWN)));
				break;
				
			case WIN_TO_SHOWDOWN_WHEN_SEEING_FLOP:
				indicators.put(pi, new PokerIndicatorValue(
						playerStats.getStats(StatsDAO.ATT_NB_WIN_TO_SHOWDOWN_WHEN_SEEING_FLOP),
						playerStats.getStats(StatsDAO.ATT_NB_HANDS_FLOP)));
				break;
			}
		}
	}
}
