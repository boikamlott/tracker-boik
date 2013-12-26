package controller.trackerboik.data;

import java.util.ArrayList;
import java.util.List;

import model.trackerboik.businessobject.ActionKind;
import model.trackerboik.businessobject.Hand;
import model.trackerboik.businessobject.HandMoment;
import model.trackerboik.businessobject.HandResult;
import model.trackerboik.businessobject.PlayerSessionStats;
import model.trackerboik.businessobject.PokerAction;
import model.trackerboik.businessobject.PokerPlayer;
import model.trackerboik.businessobject.PokerPosition;
import model.trackerboik.dao.PlayerSessionStatsDAO;

import com.trackerboik.exception.TBException;

/**
 * Class represent a hand calculator for a player
 * @author gboismal
 *
 */
public class HandDataCalculator {
	
	private static final Integer NB_CALCULATED_INDICATORS = 6;
	private static final Integer NB_CBET = 0, 
								 NB_FOLD_TO_CBET = 1,
								 NB_SECOND_BARREL = 2,
								 NB_FOLD_TO_SECOND_BARREL = 3;
	
	public List<String> playersAlive;
	private Double benefitHand, amountToCall;
	private boolean preflopRaisedByHero, preflopRaisedBySomeone, heroCbetPossible, foldToCbetPossible,
						heroCbetOnFlop, heroCallCbetOnFlop, generalSecondBarrelPossible, 
						foldToSndBarrelPossible, continueRead, heroHasSeenFlop,
						preflopOpen, atsRunning, heroHasBeen3Betted, heroHasLimp;
	private int nbBetPreflop = 1;
	private boolean[] dataCalculated;
	private PlayerSessionStats hero;
	private String initiativePlayerID;
	private Hand h;
	private HandMoment currentAnalysedMoment;
	private HandMoment lastAnalysedMoment;
	
	
	public HandDataCalculator(PlayerSessionStats pp, Hand h) {
		this.hero = pp;
		this.h = h;
		this.benefitHand = 0.0;
		this.amountToCall = h.getLimitBB();
		this.lastAnalysedMoment = HandMoment.PREFLOP;
		this.currentAnalysedMoment = HandMoment.PREFLOP;
		this.continueRead = true;
		this.heroCbetPossible = true;
		this.generalSecondBarrelPossible = true;
		this.dataCalculated = new boolean[NB_CALCULATED_INDICATORS];
		this.playersAlive = new ArrayList<String>();
		for(PokerPlayer p : h.getPlayers()) {this.playersAlive.add(p.getPlayerID());}
	}
	
	/**
	 * Compute all indicator for the player and the hand given in parameter
	 * PRE: Player has play the hand
	 * @param h
	 * @param pp
	 * @throws TBException 
	 */
	public void computeIndicatorForHandAndPlayer() throws TBException {
		List<PokerAction> actions = h.getActions();
		int actCurNo = 0;
		
		while(actCurNo < actions.size() && continueRead) {
			PokerAction a = actions.get(actCurNo);
			this.currentAnalysedMoment = a.getMoment();
			
			//Update current moment and analyse/set all flags
			if(this.currentAnalysedMoment != this.lastAnalysedMoment) {
				amountToCall = 0.0;
				this.lastAnalysedMoment = this.currentAnalysedMoment;
			}
			
			//Update amount to call if needed
			if(a.getKind() == ActionKind.BET) {
				amountToCall = a.getAmountBet();
			}
			
			computeAction(a);
			
			//Update amount to call if needed
			if(a.getKind() == ActionKind.RAISE) {
				amountToCall += a.getAmountBet(); 
			}
			actCurNo++;
		}
		
		computeGlobalStatAndBenefit();

	}

	/**
	 * On the Hand end analysis, compute benefit and global stats
	 */
	private void computeGlobalStatAndBenefit() throws TBException {
		//Uncalled bet return to player
		if(playersAlive.size() == 1 && amountToCall > 0.0 && playersAlive.contains(hero.getPlayerID())) {
			benefitHand += amountToCall;
		}
		
		//Went/Win to showdown
		if(playersAlive.size() > 1 && playersAlive.contains(hero.getPlayerID())) {
			hero.addOneToIndicator(PlayerSessionStatsDAO.ATT_NB_WENT_TO_SHOWDOWN);
			if(h.getPlayerHandData(hero.getPlayerID()).getResult() == HandResult.WIN) {
				hero.addOneToIndicator(PlayerSessionStatsDAO.ATT_NB_WIN_TO_SHOWDOWN_WHEN_SEEING_FLOP);
				hero.addOneToIndicator(PlayerSessionStatsDAO.ATT_NB_WIN_TO_SHOWDOWN); 
			}
		}
		
		//Compute benefit
		if(h.getPot() == h.getLimitBB() && h.getPlayerHandData(hero.getPlayerID()).getResult() == HandResult.WIN) {
			//Hero on BB, others fold win small blind
			benefitHand += h.getLimitBB() / 2.0;
		} else {
			benefitHand += h.getPlayerHandData(hero.getPlayerID()).getAmountWin();
		}
		
		//Update Benefit
		hero.benefitGeneral += benefitHand;
		
	}

	/**
	 * 
	 * @param a
	 * @throws TBException
	 */
	private void computeAction(PokerAction a) throws TBException {
		Boolean isPlayerAction = a.getAssociatedPlayer().equals(hero);
		
		if(isPlayerAction) {
			computeGeneralIndicators(a);
		}
		
		if(a.getKind() == ActionKind.FOLD) { playersAlive.remove(a.getAssociatedPlayer().getPlayerID());}
		
		switch(a.getMoment()) {
		case PREFLOP:
			computePreflopActions(a, isPlayerAction);
			break;
		
		case FLOP:
			computeFlopActions(a, isPlayerAction);
			break;
		
		case TURN:
			computeTurnActions(a, isPlayerAction);
			break;
			
		default:
			break;
		
		}
	}

	/**
	 * Compute action for the preflop moment
	 * @param a
	 * @param isHeroAction
	 * @throws TBException 
	 */
	private void computePreflopActions(PokerAction a, Boolean isHeroAction) throws TBException {
		String heroID = hero.getPlayerID();

		checkPFRSituations(a, isHeroAction);
		checkATSSituations(a, isHeroAction, heroID);
		checkLimpSituations(a, isHeroAction);
	}

	/**
	 * Compute Preflop 3Bet situations
	 * @param a
	 * @param isHeroAction
	 * @throws TBException 
	 */
	private void checkPFRSituations(PokerAction a, Boolean isHeroAction) throws TBException {
		//PreFlop Raise and 3Bet
		if(a.getKind() == ActionKind.RAISE) {
			nbBetPreflop++;
			if(nbBetPreflop == 2) {
				if(isHeroAction) {
					hero.addOneToIndicator(PlayerSessionStatsDAO.ATT_NB_3BET);
				} else if(playersAlive.contains(hero.getPlayerID())) {
					heroHasBeen3Betted = true;
					hero.addOneToIndicator(PlayerSessionStatsDAO.ATT_NB_FOLD_TO_3BET_POSSIBLE);				
				}
			} else if(nbBetPreflop == 1 && playersAlive.contains(hero.getPlayerID())) {
				hero.addOneToIndicator(PlayerSessionStatsDAO.ATT_NB_3BET_POSSIBLE);
			}
			preflopRaisedByHero = isHeroAction;
			preflopRaisedBySomeone = !isHeroAction;
			initiativePlayerID = a.getAssociatedPlayer().getPlayerID();
		}
		
		//3Bet fold
		if(isHeroAction && heroHasBeen3Betted && a.getKind() == ActionKind.FOLD) {
			hero.addOneToIndicator(PlayerSessionStatsDAO.ATT_NB_FOLD_TO_3BET);
		}
	}
	
	/**
	 * Compute preflop attempt to steal situation
	 * @param a
	 * @param isHeroAction
	 * @param heroID
	 * @throws TBException 
	 */
	private void checkATSSituations(PokerAction a, Boolean isHeroAction, String heroID) throws TBException {
		//Check ATS Action
				if(a.getKind() == ActionKind.CALL || a.getKind() == ActionKind.RAISE) {
					//Check ATS
					if(!preflopOpen && isHeroAction && (h.getPositionForPlayer(heroID) == PokerPosition.CO ||
							h.getPositionForPlayer(heroID) == PokerPosition.BU)) {
						hero.addOneToIndicator(PlayerSessionStatsDAO.ATT_NB_ATS_POSSIBLE);
						if(a.getKind() == ActionKind.RAISE) { hero.addOneToIndicator(PlayerSessionStatsDAO.ATT_NB_ATS);}
					} else if(!preflopOpen && !isHeroAction && 
							(h.getPositionForPlayer(a.getAssociatedPlayer().getPlayerID()) == PokerPosition.CO ||
							h.getPositionForPlayer(a.getAssociatedPlayer().getPlayerID()) == PokerPosition.BU) && 
							(h.getPositionForPlayer(heroID) == PokerPosition.SB || 
							h.getPositionForPlayer(heroID) == PokerPosition.BB)) {
						//Register a ATS
						atsRunning = true;
						
					} else if(atsRunning && !isHeroAction && a.getKind() == ActionKind.RAISE) {
						//ATS reaction was done by CO or SB
						atsRunning = false;
					} else if(atsRunning && isHeroAction) {
						//Register ATS reaction of current player
						if(h.getPositionForPlayer(heroID) == PokerPosition.SB) { 
							hero.addOneToIndicator(PlayerSessionStatsDAO.ATT_NB_FOLD_TO_ATS_SB_POSSIBLE);
						} else {
							hero.addOneToIndicator(PlayerSessionStatsDAO.ATT_NB_FOLD_TO_ATS_BB_POSSIBLE);
						}
						
						if(a.getKind() == ActionKind.FOLD && h.getPositionForPlayer(heroID) == PokerPosition.SB) {
							hero.addOneToIndicator(PlayerSessionStatsDAO.ATT_NB_FOLD_TO_ATS_SB);
						} else if(a.getKind() == ActionKind.FOLD && h.getPositionForPlayer(heroID) == PokerPosition.BB) {
							hero.addOneToIndicator(PlayerSessionStatsDAO.ATT_NB_FOLD_TO_ATS_BB);
						}
					}
					preflopOpen = true;
				}
		
	}
	
	/**
	 * Compute Preflop limp situation
	 * @param a
	 * @param isHeroAction
	 * @throws TBException 
	 */
	private void checkLimpSituations(PokerAction a, Boolean isHeroAction) throws TBException {
		//Detect Hero's limp
		if(!preflopRaisedBySomeone && isHeroAction && a.getKind() == ActionKind.CALL) {
			heroHasLimp = true;
			hero.addOneToIndicator(PlayerSessionStatsDAO.ATT_NB_LIMP);
		}
		
		//Detect Hero reaction after someone has raised it's limp
		if(heroHasLimp && preflopRaisedBySomeone && isHeroAction) {
			//Register limp reaction
			if(a.getKind() == ActionKind.CALL) {hero.addOneToIndicator(PlayerSessionStatsDAO.ATT_NB_LIMP_THEN_CALL);}
			if(a.getKind() == ActionKind.FOLD) {hero.addOneToIndicator(PlayerSessionStatsDAO.ATT_NB_LIMP_THEN_FOLD);}
			//Ensure that we could not count twice call for one limp
			heroHasLimp = false;
		}
	}
	


	/**
	 * Set all indicators for action and is player action boolean given in parameter
	 * @param a
	 * @param isPlayerAction
	 * @throws TBException 
	 */
	private void computeTurnActions(PokerAction a, Boolean isPlayerAction) throws TBException {		
		if(heroCbetOnFlop && generalSecondBarrelPossible && isPlayerAction && !dataCalculated[NB_SECOND_BARREL]) {
			hero.addOneToIndicator(PlayerSessionStatsDAO.ATT_NB_SECOND_BARREL_POSSIBLE);
			if(a.getKind() == ActionKind.BET) {hero.addOneToIndicator(PlayerSessionStatsDAO.ATT_NB_SECOND_BARREL);}
			dataCalculated[NB_SECOND_BARREL] = true;
		} else if(heroCallCbetOnFlop && generalSecondBarrelPossible && foldToSndBarrelPossible && 
				isPlayerAction && !dataCalculated[NB_FOLD_TO_SECOND_BARREL]) {
			hero.addOneToIndicator(PlayerSessionStatsDAO.ATT_NB_FOLD_TO_SECOND_BARREL_POSSIBLE);
			if(a.getKind() == ActionKind.FOLD) {hero.addOneToIndicator(PlayerSessionStatsDAO.ATT_NB_FOLD_TO_SECOND_BARREL);}
			dataCalculated[NB_FOLD_TO_SECOND_BARREL] = true;
		} else if(heroCbetOnFlop && generalSecondBarrelPossible) {
			//Action of another player, check if current player could be 21 barrel after that
			generalSecondBarrelPossible = a.getKind() == ActionKind.CHECK || 
					(a.getKind() == ActionKind.FOLD && playersAlive.size() >= 2);
		} else if(heroCallCbetOnFlop && generalSecondBarrelPossible && !foldToSndBarrelPossible) {
			//Register The 2nd Barrel of another player
			foldToSndBarrelPossible = a.getKind() == ActionKind.BET && a.getAssociatedPlayer().getPlayerID().equals(initiativePlayerID);
		} else if(heroCallCbetOnFlop && generalSecondBarrelPossible && foldToSndBarrelPossible) {
			foldToSndBarrelPossible = a.getKind() == ActionKind.FOLD || a.getKind() == ActionKind.CALL;
		}
		
	}

	/**
	 * Set all flop indicators for action and is player action boolean given in parameters
	 * @param a
	 * @param isPlayerAction
	 * @throws TBException 
	 */
	private void computeFlopActions(PokerAction a, Boolean isPlayerAction) throws TBException {		
		if(heroCbetPossible && preflopRaisedByHero && isPlayerAction && !dataCalculated[NB_CBET]) {
			//With this action, player could perform a Cbet or not
			hero.addOneToIndicator(PlayerSessionStatsDAO.ATT_NB_CBET_POSSIBLE); 
			if(a.getKind() == ActionKind.BET) {hero.addOneToIndicator(PlayerSessionStatsDAO.ATT_NB_CBET);}
			heroCbetOnFlop = a.getKind() == ActionKind.BET;
			dataCalculated[NB_CBET] = true;
		} else if(foldToCbetPossible && preflopRaisedBySomeone && isPlayerAction && !dataCalculated[NB_FOLD_TO_CBET]) {
			hero.addOneToIndicator(PlayerSessionStatsDAO.ATT_NB_FOLD_TO_CBET_POSSIBLE);
			if(a.getKind() == ActionKind.FOLD) {hero.addOneToIndicator(PlayerSessionStatsDAO.ATT_NB_FOLD_TO_CBET);}
			heroCallCbetOnFlop = a.getKind() == ActionKind.CALL;
			dataCalculated[NB_FOLD_TO_CBET] = true;
		} else if(preflopRaisedByHero && heroCbetPossible) {
			//Action of another player, check if current player could be cbet afeter that
			heroCbetPossible = a.getKind() == ActionKind.CHECK || 
					(a.getKind() == ActionKind.FOLD && playersAlive.size() >= 2);
		} else if(preflopRaisedBySomeone && !foldToCbetPossible) {
			//Register The CBet of initiative player
			foldToCbetPossible = a.getKind() == ActionKind.BET && 
					a.getAssociatedPlayer().getPlayerID().equals(initiativePlayerID);
		} else if(preflopRaisedBySomeone && foldToCbetPossible) {
			//Always could call the CBet if others players don't raise
			foldToCbetPossible = a.getKind() == ActionKind.FOLD || a.getKind() == ActionKind.CALL;
		} else if((heroCbetOnFlop || heroCallCbetOnFlop) && generalSecondBarrelPossible) {
			generalSecondBarrelPossible = a.getKind() == ActionKind.FOLD || a.getKind() == ActionKind.CALL;
		}
		
	}

	/**
	 * Update the amount spend by current player and/or the amount to call
	 * @throws TBException 
	 */
	private void computeGeneralIndicators(PokerAction a) throws TBException {
		//AF General and Benefit
		switch(a.getKind()) {
			case POSTSBLIND:
			case POSTBIGBLIND:
			case CALL:
			case BET:
				if(a.getKind() == ActionKind.BET) {hero.addOneToIndicator(PlayerSessionStatsDAO.ATT_NB_AGRESSION_FACTOR_GENERAL_BET_RAISE);}
				if(a.getKind() == ActionKind.CALL) {hero.addOneToIndicator(PlayerSessionStatsDAO.ATT_NB_AGRESSION_FACTOR_GENERAL_CALL);}
				benefitHand -= a.getAmountBet();
				break;
				
			case RAISE:
				hero.addOneToIndicator(PlayerSessionStatsDAO.ATT_NB_AGRESSION_FACTOR_GENERAL_BET_RAISE);
				benefitHand -= amountToCall + a.getAmountBet();
				break;
			case FOLD:
				continueRead = false;
				break;
			case CHECK:
				break;
		}
		
		//Check if Hero has seen the flop
		if(a.getMoment() == HandMoment.FLOP && !heroHasSeenFlop) {
			hero.addOneToIndicator(PlayerSessionStatsDAO.ATT_NB_WIN_TO_SHOWDOWN_WHEN_SEEING_FLOP_POSSIBLE);
			heroHasSeenFlop = true;
		}
		
		//AF for moment calc
		computeAFForMoment(a, HandMoment.FLOP, PlayerSessionStatsDAO.ATT_NB_AF_FLOP_BR, 
				PlayerSessionStatsDAO.ATT_NB_AF_FLOP_C);
		computeAFForMoment(a, HandMoment.TURN, PlayerSessionStatsDAO.ATT_NB_AF_TURN_BR, 
				PlayerSessionStatsDAO.ATT_NB_AF_TURN_C);
		computeAFForMoment(a, HandMoment.RIVER, PlayerSessionStatsDAO.ATT_NB_AF_RIVER_BR, 
				PlayerSessionStatsDAO.ATT_NB_AF_RIVER_C);

				
	}

	/**
	 * Compute agression factor for action given in parameter
	 * Attributes names for BetRaise and Call are given in parameters
	 * @param a
	 * @param attNbAfFlopBr
	 * @param attNbAfFlopC
	 * @throws TBException 
	 */
	private void computeAFForMoment(PokerAction a, HandMoment moment, String nbBetRaiseAttName,
			String nbCallAttName) throws TBException {
		if(a.getMoment() == moment) {
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
