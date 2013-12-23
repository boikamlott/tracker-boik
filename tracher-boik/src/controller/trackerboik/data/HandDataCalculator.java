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
						foldToSndBarrelPossible, continueRead,
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
			hero.nbWentToShowdownHand++;
			if(h.getPlayerHandData(hero.getPlayerID()).getResult() == HandResult.WIN) { hero.nbWinToShowdownHand++; }
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
	 */
	private void computePreflopActions(PokerAction a, Boolean isHeroAction) {
		String heroID = hero.getPlayerID();

		checkPFRSituations(a, isHeroAction);
		checkATSSituations(a, isHeroAction, heroID);
		checkLimpSituations(a, isHeroAction);
	}

	/**
	 * Compute Preflop 3Bet situations
	 * @param a
	 * @param isHeroAction
	 */
	private void checkPFRSituations(PokerAction a, Boolean isHeroAction) {
		//PreFlop Raise and 3Bet
		if(a.getKind() == ActionKind.RAISE) {
			nbBetPreflop++;
			if(nbBetPreflop == 2) {
				if(isHeroAction) {
					hero.nb3bet++;
				} else if(playersAlive.contains(hero.getPlayerID())) {
					heroHasBeen3Betted = true;
					hero.nbFoldTo3betPossible++;
				}
			} else if(nbBetPreflop == 1 && playersAlive.contains(hero.getPlayerID())) {
				hero.nb3betPossible++;
			}
			preflopRaisedByHero = isHeroAction;
			preflopRaisedBySomeone = !isHeroAction;
			initiativePlayerID = a.getAssociatedPlayer().getPlayerID();
		}
		
		//3Bet fold
		if(isHeroAction && heroHasBeen3Betted && a.getKind() == ActionKind.FOLD) {
			hero.nbFoldTo3bet++;
		}
	}
	
	/**
	 * Compute preflop attempt to steal situation
	 * @param a
	 * @param isHeroAction
	 * @param heroID
	 */
	private void checkATSSituations(PokerAction a, Boolean isHeroAction, String heroID) {
		//Check ATS Action
				if(a.getKind() == ActionKind.CALL || a.getKind() == ActionKind.RAISE) {
					//Check ATS
					if(!preflopOpen && isHeroAction && (h.getPositionForPlayer(heroID) == PokerPosition.CO ||
							h.getPositionForPlayer(heroID) == PokerPosition.BU)) {
						hero.nbATSPossible++;
						if(a.getKind() == ActionKind.RAISE) { hero.nbATS++;}
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
							hero.nbFoldToATSSBPossible++;
						} else {
							hero.nbFoldToATSBBPossible++;
						}
						
						if(a.getKind() == ActionKind.FOLD && h.getPositionForPlayer(heroID) == PokerPosition.SB) {
							hero.nbFoldToATSSB++;
						} else if(a.getKind() == ActionKind.FOLD && h.getPositionForPlayer(heroID) == PokerPosition.BB) {
							hero.nbFoldToATSBB++;
						}
					}
					preflopOpen = true;
				}
		
	}
	
	/**
	 * Compute Preflop limp situation
	 * @param a
	 * @param isHeroAction
	 */
	private void checkLimpSituations(PokerAction a, Boolean isHeroAction) {
		//Detect Hero's limp
		if(!preflopRaisedBySomeone && isHeroAction && a.getKind() == ActionKind.CALL) {
			heroHasLimp = true;
			hero.nbLimpTotal++;
		}
		
		//Detect Hero reaction after someone has raised it's limp
		if(heroHasLimp && preflopRaisedBySomeone && isHeroAction) {
			//Register limp reaction
			if(a.getKind() == ActionKind.CALL) {hero.nbLimpThenCall++;}
			if(a.getKind() == ActionKind.FOLD) {hero.nbLimpThenFold++;}
			//Ensure that we could not count twice call for one limp
			heroHasLimp = false;
		}
	}
	


	/**
	 * Set all indicators for action and is player action boolean given in parameter
	 * @param a
	 * @param isPlayerAction
	 */
	private void computeTurnActions(PokerAction a, Boolean isPlayerAction) {		
		if(heroCbetOnFlop && generalSecondBarrelPossible && isPlayerAction && !dataCalculated[NB_SECOND_BARREL]) {
			hero.nbSecondBarrelPossible++;
			hero.nbSecondBarrel += a.getKind() == ActionKind.BET ? 1 : 0;
			dataCalculated[NB_SECOND_BARREL] = true;
		} else if(heroCallCbetOnFlop && generalSecondBarrelPossible && foldToSndBarrelPossible && 
				isPlayerAction && !dataCalculated[NB_FOLD_TO_SECOND_BARREL]) {
			hero.nbFoldToSecondBarrelPossible++;
			hero.nbFoldToSecondBarrel += a.getKind() == ActionKind.FOLD ? 1 : 0;
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
	 */
	private void computeFlopActions(PokerAction a, Boolean isPlayerAction) {		
		if(heroCbetPossible && preflopRaisedByHero && isPlayerAction && !dataCalculated[NB_CBET]) {
			//With this action, player could perform a Cbet or not
			hero.nbCbetPossible++; 
			hero.nbCbet += a.getKind() == ActionKind.BET ? 1 : 0;
			heroCbetOnFlop = a.getKind() == ActionKind.BET;
			dataCalculated[NB_CBET] = true;
		} else if(foldToCbetPossible && preflopRaisedBySomeone && isPlayerAction && !dataCalculated[NB_FOLD_TO_CBET]) {
			hero.nbFoldToCbetPossible++;
			hero.nbFoldToCbet += a.getKind() == ActionKind.FOLD ? 1 : 0;
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
	 */
	private void computeGeneralIndicators(PokerAction a) {
		switch(a.getKind()) {
			case POSTSBLIND:
			case POSTBIGBLIND:
			case CALL:
			case BET:
				if(a.getKind() == ActionKind.BET) {hero.nbAFHandBetAndRaise++;}
				if(a.getKind() == ActionKind.CALL) {hero.nbAFHandCalled++;}
				benefitHand -= a.getAmountBet();
				break;
				
			case RAISE:
				hero.nbAFHandBetAndRaise++;
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
