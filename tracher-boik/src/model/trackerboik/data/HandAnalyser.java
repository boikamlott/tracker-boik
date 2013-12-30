package model.trackerboik.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.trackerboik.exception.TBException;

import model.trackerboik.businessobject.ActionKind;
import model.trackerboik.businessobject.Hand;
import model.trackerboik.businessobject.HandMoment;
import model.trackerboik.businessobject.PlayerStats;
import model.trackerboik.businessobject.PokerAction;
import model.trackerboik.businessobject.PokerPlayer;
import model.trackerboik.businessobject.PokerPosition;
import model.trackerboik.dao.StatsDAO;

public class HandAnalyser {
	/** All updatable player stats **/
	private Map<String, PlayerStats> playerStats;
	
	/** Hands player only **/
	public List<String> playersInGame;
	public List<String> limpers;
	
	public String initiativePlayer;
	
	private int nbRaisePF;
	private boolean atsRunning;
	/** Has Seen Moment Set **/
	public Map<HandMoment, List<String>> hasSeenMoment;
	public Double amountToCall;
	private Hand h;
	
	public HandAnalyser(Map<String, PlayerStats> playerStats, Hand h) {
		this.playerStats = playerStats;
		this.h = h;
		this.nbRaisePF = 0;
		this.atsRunning = false;
		this.amountToCall = h.getLimitBB();
		this.hasSeenMoment = new HashMap<HandMoment, List<String>>();
		this.hasSeenMoment.put(HandMoment.FLOP, new ArrayList<String>());
		this.hasSeenMoment.put(HandMoment.TURN, new ArrayList<String>());
		this.hasSeenMoment.put(HandMoment.RIVER, new ArrayList<String>());
		this.limpers = new ArrayList<String>();
		this.playersInGame = new ArrayList<String>();
		this.playersInGame.add(h.getPlayerInPosition(PokerPosition.BB));
	}
	
	/**
	 * Analyse hand by updating all player stats related to this hand
	 */
	public void analyse() throws TBException {
		//Compute all actions indicators
		for(PokerAction pa: h.getActions()) {
			computeBenefit(pa);
			computeHandAgressionFactor(pa);
			computeSeeingMomentStats(pa);
			
			switch(pa.getMoment()) {
			case PREFLOP:
				computePreflopActions(pa);
				break;
			case FLOP:
				computeFlopActions(pa);
				break;
			case TURN:
				computeTurnActions(pa);
				break;
			default:
				break;
			
			}
			
			//Remove player of game
			if(playersInGame.contains(pa.getAssociatedPlayer().getPlayerID()) && pa.getKind() == ActionKind.FOLD) {
				playersInGame.remove(pa.getAssociatedPlayer().getPlayerID());
			}
		}
		
		//Compute summary indicators
		for(PokerPlayer pp: h.getPlayers()) {
			playerStats.get(pp.getPlayerID()).benefitGeneral += h.getPlayerHandData(pp.getPlayerID()).getAmountWin();
		}
	}
	
	/**
	 * Compute action for the preflop moment
	 * @param a
	 * @param isHeroAction
	 * @throws TBException 
	 */
	private void computePreflopActions(PokerAction pa) throws TBException {
		String playerID = pa.getAssociatedPlayer().getPlayerID();

		check3BetSituations(pa);
		checkATSSituations(pa);
		checkLimpSituations(pa);
		
		//Register Entry in Pot
		if(pa.getKind() != ActionKind.CHECK && pa.getKind() != ActionKind.FOLD && 
				pa.getKind() != ActionKind.UNCALLED_BET && !playersInGame.contains(playerID)) {
			playersInGame.add(playerID);
		}
	}

	/**
	 * Compute Preflop 3Bet situations
	 * @param a
	 * @param isHeroAction
	 * @throws TBException 
	 */
	private void check3BetSituations(PokerAction a) throws TBException {
		String playerID = a.getAssociatedPlayer().getPlayerID();
		//3Bet possibility detection
		if(nbRaisePF == 1) {
			playerStats.get(playerID).addOneToIndicator(StatsDAO.ATT_3BET_POSSIBLE);
			if(a.getKind() == ActionKind.RAISE) { playerStats.get(playerID).addOneToIndicator(StatsDAO.ATT_3BET);}
		} else if(nbRaisePF == 2 && playersInGame.contains(playerID)) {
			playerStats.get(playerID).addOneToIndicator(StatsDAO.ATT_FOLD_TO_3BET_POSSIBLE);
			if(a.getKind() == ActionKind.FOLD) { playerStats.get(playerID).addOneToIndicator(StatsDAO.ATT_FOLD_TO_3BET);}
		}
		
		//Register Initiative
		if(a.getKind() == ActionKind.RAISE) {
			nbRaisePF++;
			initiativePlayer = playerID;
		}
	}
	
	/**
	 * Compute preflop attempt to steal situation
	 * @param a
	 * @param isHeroAction
	 * @param heroID
	 * @throws TBException 
	 */
	private void checkATSSituations(PokerAction a) throws TBException {
		String playerID = a.getAssociatedPlayer().getPlayerID();
		//ATS Detection
		if(playersInGame.size() == 1 && (h.getPlayerInPosition(PokerPosition.CO).equals(playerID) ||
											h.getPlayerInPosition(PokerPosition.BU).equals(playerID) ||
											h.getPlayerInPosition(PokerPosition.SB).equals(playerID))) {
			playerStats.get(playerID).addOneToIndicator(StatsDAO.ATT_ATS_POSSIBLE);
			if(a.getKind() == ActionKind.RAISE) { playerStats.get(playerID).addOneToIndicator(StatsDAO.ATT_ATS); }
			atsRunning = true;
		}
		
		//ATS Cancel by button action
		if(atsRunning && h.getPlayerInPosition(PokerPosition.BU).equals(playerID) && a.getKind() != ActionKind.FOLD) {
			atsRunning = false;
		}
		
		//ATS SB Reaction and possibly cancel if 3Bet
		if(atsRunning && h.getPlayerInPosition(PokerPosition.SB).equals(playerID)) {
			playerStats.get(playerID).addOneToIndicator(StatsDAO.ATT_FOLD_TO_ATS_SB_POSSIBLE);
			if(a.getKind() == ActionKind.FOLD) { playerStats.get(playerID).addOneToIndicator(StatsDAO.ATT_FOLD_TO_ATS_SB);}
			if(a.getKind() == ActionKind.RAISE) { atsRunning = false;}
		}
		
		//ATS BB Reaction and possibly cancel if 3Bet
		if(atsRunning && h.getPlayerInPosition(PokerPosition.BB).equals(playerID)) {
			playerStats.get(playerID).addOneToIndicator(StatsDAO.ATT_FOLD_TO_ATS_BB_POSSIBLE);
			if(a.getKind() == ActionKind.FOLD) { playerStats.get(playerID).addOneToIndicator(StatsDAO.ATT_FOLD_TO_ATS_BB);}
		}
	}
	
	/**
	 * Compute Preflop limp situation
	 * @param a
	 * @param isHeroAction
	 * @throws TBException 
	 */
	private void checkLimpSituations(PokerAction a) throws TBException {
		String playerID = a.getAssociatedPlayer().getPlayerID();
		
		//Check limp Situation
		if(nbRaisePF == 0 && a.getKind() == ActionKind.CALL) {
			playerStats.get(playerID).addOneToIndicator(StatsDAO.ATT_LIMP);
			limpers.add(playerID);
		}
		
		//Limp Raise reaction
		if(limpers.contains(playerID) && nbRaisePF > 0) {
			if(a.getKind() == ActionKind.FOLD) {
				playerStats.get(playerID).addOneToIndicator(StatsDAO.ATT_LIMP_THEN_FOLD);
			} else if(a.getKind() == ActionKind.CALL) {
				playerStats.get(playerID).addOneToIndicator(StatsDAO.ATT_LIMP_THEN_CALL);
			}
			limpers.remove(playerID);
		}
	}
	
	/**
	 * Set all flop indicators for action and is player action boolean given in parameters
	 * @param a
	 * @param isPlayerAction
	 * @throws TBException 
	 */
	private void computeFlopActions(PokerAction a) throws TBException {
		
	}

	/**
	 * Set all indicators for action and is player action boolean given in parameter
	 * @param a
	 * @param isPlayerAction
	 * @throws TBException 
	 */
	private void computeTurnActions(PokerAction a) throws TBException {
		
	}

	/**
	 * Compute Benefit of action given in parameter
	 */
	private void computeBenefit(PokerAction pa) {
		String playerID = pa.getAssociatedPlayer().getPlayerID();
		
		switch (pa.getKind()) {
		case CHECK:
		case FOLD:
			break;	
		case BET:
			playerStats.get(playerID).benefitGeneral -= pa.getAmount();
			amountToCall = pa.getAmount();
			break;
		case POSTSBLIND:
		case POSTBIGBLIND:
		case CALL:
			playerStats.get(playerID).benefitGeneral -= pa.getAmount();
			break;
		case RAISE:
			playerStats.get(playerID).benefitGeneral -= amountToCall + pa.getAmount();
			amountToCall = pa.getAmount();
			break;
		case UNCALLED_BET:
			playerStats.get(playerID).benefitGeneral += pa.getAmount();
			break;
		}
		
	}

	/**
	 * Compute agression factor for action given in parameter
	 * @param a The action associated
	 * @throws TBException 
	 */
	private void computeHandAgressionFactor(PokerAction pa) throws TBException {
		String playerID = pa.getAssociatedPlayer().getPlayerID();
			//Update AF data
			switch(pa.getKind()) {
			case BET:
			case RAISE:
				playerStats.get(playerID).addOneToIndicator(StatsDAO.ATT_AGRESSION_FACTOR_GENERAL_BET_RAISE);
				switch(pa.getMoment()) {
				case FLOP:
					playerStats.get(playerID).addOneToIndicator(StatsDAO.ATT_AF_FLOP_BR);
					break;
				case TURN:
					playerStats.get(playerID).addOneToIndicator(StatsDAO.ATT_AF_TURN_BR);
					break;
				case RIVER:
					playerStats.get(playerID).addOneToIndicator(StatsDAO.ATT_AF_RIVER_BR);
					break;
				default:
					break;
				}	
				break;
			case CALL:
				playerStats.get(playerID).addOneToIndicator(StatsDAO.ATT_AGRESSION_FACTOR_GENERAL_CALL);
				switch(pa.getMoment()) {
				case FLOP:
					playerStats.get(playerID).addOneToIndicator(StatsDAO.ATT_AF_FLOP_C);
					break;
				case TURN:
					playerStats.get(playerID).addOneToIndicator(StatsDAO.ATT_AF_TURN_C);
					break;
				case RIVER:
					playerStats.get(playerID).addOneToIndicator(StatsDAO.ATT_AF_RIVER_C);
					break;
				default:
					break;
				}
				break;
			default:
				break;
			}
	}
	
	/**
	 * Note if player has seen flop, turn or river
	 * @param pa
	 * @throws TBException 
	 */
	private void computeSeeingMomentStats(PokerAction pa) throws TBException {
		String playerID = pa.getAssociatedPlayer().getPlayerID();
		
		switch(pa.getMoment()) {
		case FLOP:
			setMomentAsSeen(HandMoment.FLOP, playerID, StatsDAO.ATT_HANDS_FLOP);
			break;
		case TURN:
			setMomentAsSeen(HandMoment.TURN, playerID, StatsDAO.ATT_HANDS_TURN);
			break;
		case RIVER:
			setMomentAsSeen(HandMoment.RIVER, playerID, StatsDAO.ATT_HANDS_RIVER);
			break;	
		default:
			break;
		}
		
	}

	/**
	 * Routine to prevent code redondancy, note if player has seen moment
	 * if it's not already done
	 * @param flop
	 * @param attHandsFlop
	 * @throws TBException 
	 */
	private void setMomentAsSeen(HandMoment moment, String playerID, String indicatorName) throws TBException {
		if(!hasSeenMoment.get(moment).contains(playerID)) {
			playerStats.get(playerID).addOneToIndicator(indicatorName);
			hasSeenMoment.get(moment).add(playerID);
		}
		
	}
}
