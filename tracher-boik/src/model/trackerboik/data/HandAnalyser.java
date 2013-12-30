package model.trackerboik.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.trackerboik.exception.TBException;

import model.trackerboik.businessobject.Hand;
import model.trackerboik.businessobject.HandMoment;
import model.trackerboik.businessobject.PlayerStats;
import model.trackerboik.businessobject.PokerAction;
import model.trackerboik.businessobject.PokerPlayer;
import model.trackerboik.dao.StatsDAO;

public class HandAnalyser {
	private Map<String, PlayerStats> playerStats;
	public List<String> playersAlive;
	private Hand h;
	
	public HandAnalyser(Map<String, PlayerStats> playerStats, Hand h) {
		this.playerStats = playerStats;
		this.h = h;
		this.playersAlive = new ArrayList<String>();
		for(PokerPlayer p : h.getPlayers()) {this.playersAlive.add(p.getPlayerID());}
	}
	
	/**
	 * Analyse hand by updating all player stats related to this hand
	 */
	public void analyse() throws TBException {
		for(PokerAction pa: h.getActions()) {
			//AF for moment calc
			computeHandsAFForMoment(a, HandMoment.FLOP, StatsDAO.ATT_AF_FLOP_BR, 
					StatsDAO.ATT_AF_FLOP_C);
			computeHandsAFForMoment(a, HandMoment.TURN, StatsDAO.ATT_AF_TURN_BR, 
					StatsDAO.ATT_AF_TURN_C);
			computeHandsAFForMoment(a, HandMoment.RIVER, StatsDAO.ATT_AF_RIVER_BR, 
					StatsDAO.ATT_AF_RIVER_C);
		}
	}
	
	/**
	 * Compute agression factor for action given in parameter
	 * Attributes names for BetRaise and Call are given in parameters
	 * @param a
	 * @param attNbAfFlopBr
	 * @param attNbAfFlopC
	 * @throws TBException 
	 */
	private void computeHandsAFForMoment(PokerAction a, HandMoment moment, String nbBetRaiseAttName,
			String nbCallAttName) throws TBException {
		if(a.getMoment() == moment) {
			//Update NB Hands
			String attNbHandsName = moment == HandMoment.FLOP ? StatsDAO.ATT_HANDS_FLOP : 
									moment == HandMoment.TURN ? StatsDAO.ATT_HANDS_TURN :
									moment == HandMoment.TURN ? StatsDAO.ATT_HANDS_RIVER : "";
			if(!hasSeenMoment.contains(moment)) {
				hero.addOneToIndicator(attNbHandsName);
				hasSeenMoment.add(moment);
			}
			
			//Update AF data
			switch(a.getKind()) {
			case BET:
			case RAISE:
				hero.addOneToIndicator(nbBetRaiseAttName);
				break;
			case CALL:
				hero.addOneToIndicator(nbCallAttName);
				break;
			default:
				break;
			}
		}
	}
}
