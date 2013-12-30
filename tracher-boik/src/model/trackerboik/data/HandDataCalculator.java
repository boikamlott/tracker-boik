package model.trackerboik.data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import model.trackerboik.businessobject.ActionKind;
import model.trackerboik.businessobject.Hand;
import model.trackerboik.businessobject.HandMoment;
import model.trackerboik.businessobject.HandResult;
import model.trackerboik.businessobject.PlayerStats;
import model.trackerboik.businessobject.PokerAction;
import model.trackerboik.businessobject.PokerPlayer;
import model.trackerboik.businessobject.PokerPosition;
import model.trackerboik.dao.StatsDAO;

import com.trackerboik.exception.TBException;

/**
 * Class represent a hand calculator for a player
 * @author gboismal
 *
 */
public class HandDataCalculator {
	
	private static final Integer NB_CALCULATED_INDICATORS = 6;
	private static final Integer CBET = 0, 
								 FOLD_TO_CBET = 1,
								 SECOND_BARREL = 2,
								 FOLD_TO_SECOND_BARREL = 3;
	
	public List<String> playersAlive;
	private Double benefitHand, amountToCall;
	private boolean preflopRaisedByHero, preflopRaisedBySomeone, heroCbetPossible, foldToCbetPossible,
						heroCbetOnFlop, heroCallCbetOnFlop, generalSecondBarrelPossible, 
						foldToSndBarrelPossible, continueRead,
						preflopOpen, atsRunning, heroHasBeen3Betted, heroHasLimp;
	private Set<HandMoment> hasSeenMoment;
	private int nbBetPreflop = 1;
	private boolean[] dataCalculated;
	private PlayerStats hero;
	private String initiativePlayerID;
	private Hand h;
	private HandMoment currentAnalysedMoment;
	private HandMoment lastAnalysedMoment;
	
	
	public HandDataCalculator(PlayerStats pp, Hand h) {
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
		this.hasSeenMoment = new HashSet<HandMoment>();
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
			hero.addOneToIndicator(StatsDAO.ATT_WENT_TO_SHOWDOWN);
			if(h.getPlayerHandData(hero.getPlayerID()).getResult() == HandResult.WIN) {
				hero.addOneToIndicator(StatsDAO.ATT_WIN_TO_SHOWDOWN_WHEN_SEEING_FLOP);
				hero.addOneToIndicator(StatsDAO.ATT_WIN_TO_SHOWDOWN); 
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
		Boolean isPlayerAction = a.getAssociatedPlayer().getPlayerID().equals(hero.getPlayerID());
		
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
					hero.addOneToIndicator(StatsDAO.ATT_3BET);
				} else if(playersAlive.contains(hero.getPlayerID())) {
					heroHasBeen3Betted = true;
					hero.addOneToIndicator(StatsDAO.ATT_FOLD_TO_3BET_POSSIBLE);				
				}
			} else if(nbBetPreflop == 1 && playersAlive.contains(hero.getPlayerID())) {
				hero.addOneToIndicator(StatsDAO.ATT_3BET_POSSIBLE);
			}
			preflopRaisedByHero = isHeroAction;
			preflopRaisedBySomeone = !isHeroAction;
			initiativePlayerID = a.getAssociatedPlayer().getPlayerID();
		}
		
		//3Bet fold
		if(isHeroAction && heroHasBeen3Betted && a.getKind() == ActionKind.FOLD) {
			hero.addOneToIndicator(StatsDAO.ATT_FOLD_TO_3BET);
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
						hero.addOneToIndicator(StatsDAO.ATT_ATS_POSSIBLE);
						if(a.getKind() == ActionKind.RAISE) { hero.addOneToIndicator(StatsDAO.ATT_ATS);}
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
							hero.addOneToIndicator(StatsDAO.ATT_FOLD_TO_ATS_SB_POSSIBLE);
						} else {
							hero.addOneToIndicator(StatsDAO.ATT_FOLD_TO_ATS_BB_POSSIBLE);
						}
						
						if(a.getKind() == ActionKind.FOLD && h.getPositionForPlayer(heroID) == PokerPosition.SB) {
							hero.addOneToIndicator(StatsDAO.ATT_FOLD_TO_ATS_SB);
						} else if(a.getKind() == ActionKind.FOLD && h.getPositionForPlayer(heroID) == PokerPosition.BB) {
							hero.addOneToIndicator(StatsDAO.ATT_FOLD_TO_ATS_BB);
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
			hero.addOneToIndicator(StatsDAO.ATT_LIMP);
		}
		
		//Detect Hero reaction after someone has raised it's limp
		if(heroHasLimp && preflopRaisedBySomeone && isHeroAction) {
			//Register limp reaction
			if(a.getKind() == ActionKind.CALL) {hero.addOneToIndicator(StatsDAO.ATT_LIMP_THEN_CALL);}
			if(a.getKind() == ActionKind.FOLD) {hero.addOneToIndicator(StatsDAO.ATT_LIMP_THEN_FOLD);}
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
		if(heroCbetOnFlop && generalSecondBarrelPossible && isPlayerAction && !dataCalculated[SECOND_BARREL]) {
			hero.addOneToIndicator(StatsDAO.ATT_SECOND_BARREL_POSSIBLE);
			if(a.getKind() == ActionKind.BET) {hero.addOneToIndicator(StatsDAO.ATT_SECOND_BARREL);}
			dataCalculated[SECOND_BARREL] = true;
		} else if(heroCallCbetOnFlop && generalSecondBarrelPossible && foldToSndBarrelPossible && 
				isPlayerAction && !dataCalculated[FOLD_TO_SECOND_BARREL]) {
			hero.addOneToIndicator(StatsDAO.ATT_FOLD_TO_SECOND_BARREL_POSSIBLE);
			if(a.getKind() == ActionKind.FOLD) {hero.addOneToIndicator(StatsDAO.ATT_FOLD_TO_SECOND_BARREL);}
			dataCalculated[FOLD_TO_SECOND_BARREL] = true;
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
		if(heroCbetPossible && preflopRaisedByHero && isPlayerAction && !dataCalculated[CBET]) {
			//With this action, player could perform a Cbet or not
			hero.addOneToIndicator(StatsDAO.ATT_CBET_POSSIBLE); 
			if(a.getKind() == ActionKind.BET) {hero.addOneToIndicator(StatsDAO.ATT_CBET);}
			heroCbetOnFlop = a.getKind() == ActionKind.BET;
			dataCalculated[CBET] = true;
		} else if(foldToCbetPossible && preflopRaisedBySomeone && isPlayerAction && !dataCalculated[FOLD_TO_CBET]) {
			hero.addOneToIndicator(StatsDAO.ATT_FOLD_TO_CBET_POSSIBLE);
			if(a.getKind() == ActionKind.FOLD) {hero.addOneToIndicator(StatsDAO.ATT_FOLD_TO_CBET);}
			heroCallCbetOnFlop = a.getKind() == ActionKind.CALL;
			dataCalculated[FOLD_TO_CBET] = true;
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
				if(a.getKind() == ActionKind.BET) {hero.addOneToIndicator(StatsDAO.ATT_AGRESSION_FACTOR_GENERAL_BET_RAISE);}
				if(a.getKind() == ActionKind.CALL) {hero.addOneToIndicator(StatsDAO.ATT_AGRESSION_FACTOR_GENERAL_CALL);}
				benefitHand -= a.getAmountBet();
				break;
				
			case RAISE:
				hero.addOneToIndicator(StatsDAO.ATT_AGRESSION_FACTOR_GENERAL_BET_RAISE);
				benefitHand -= amountToCall + a.getAmountBet();
				break;
			case FOLD:
				continueRead = false;
				break;
			case CHECK:
				break;
		}
		


				
	}

	
}
