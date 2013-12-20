package controller.trackerboik.data;

import java.util.ArrayList;
import java.util.List;

import com.trackerboik.exception.TBException;

import model.trackerboik.businessobject.ActionKind;
import model.trackerboik.businessobject.Hand;
import model.trackerboik.businessobject.HandMoment;
import model.trackerboik.businessobject.PlayerSessionStats;
import model.trackerboik.businessobject.PokerAction;
import model.trackerboik.businessobject.PokerPlayer;
import model.trackerboik.businessobject.PokerPosition;

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
								 NB_FOLD_TO_SECOND_BARREL = 3,
								 NB_3BET = 4,
								 NB_FOLD_TO_3BET = 5;
	
	public List<PokerPlayer> playersAlive;
	private Double benefitHand, amountToCall;
	private boolean preflopRaisedByHero, preflopRaisedBySomeone, heroCbetPossible, foldToCbetPossible,
						heroCbetOnFlop, heroCallCbetOnFlop, generalSecondBarrelPossible, 
						foldToSndBarrelPossible, continueRead,
						preflopOpen, atsRunning;
	private boolean[] dataCalculated;
	private PlayerSessionStats player;
	private String initiativePlayerID;
	private Hand h;
	private HandMoment currentAnalysedMoment;
	private HandMoment lastAnalysedMoment;
	
	
	public HandDataCalculator(PlayerSessionStats pp, Hand h) {
		this.player = pp;
		this.h = h;
		this.benefitHand = 0.0;
		this.amountToCall = h.getLimitBB();
		this.lastAnalysedMoment = HandMoment.PREFLOP;
		this.currentAnalysedMoment = HandMoment.PREFLOP;
		this.continueRead = true;
		this.heroCbetPossible = true;
		this.generalSecondBarrelPossible = true;
		this.dataCalculated = new boolean[NB_CALCULATED_INDICATORS];
		this.playersAlive = new ArrayList<PokerPlayer>();
		for(PokerPlayer p : h.getPlayers()) {this.playersAlive.add(p);}
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
		
		if(playersAlive.size() == 1 && amountToCall > 0.0 && playersAlive.contains(player)) {
			//Uncalled bet return to player
			benefitHand += amountToCall;
		}
		benefitHand += h.getPlayerHandData(player.getPlayerID()).getAmountWin();
		//Update Benefit
		player.benefitGeneral += benefitHand;
	}

	/**
	 * 
	 * @param a
	 * @throws TBException
	 */
	private void computeAction(PokerAction a) throws TBException {
		String playerID = player.getPlayerID();
		Boolean isPlayerAction = a.getAssociatedPlayer().equals(player);
		if(a.getKind() == ActionKind.FOLD) { playersAlive.remove(a.getAssociatedPlayer());}
		
		if(isPlayerAction) {
			computeGeneralIndicators(a);
		}
		
		switch(a.getMoment()) {
		case PREFLOP:
			if(a.getKind() == ActionKind.CALL || a.getKind() == ActionKind.RAISE) {
				//Check ATS
				if(!preflopOpen && isPlayerAction && (h.getPositionForPlayer(playerID) == PokerPosition.CO ||
						h.getPositionForPlayer(playerID) == PokerPosition.BU)) {
					player.nbATSPossible++;
					if(a.getKind() == ActionKind.RAISE) { player.nbATS++;}
				} else if(!preflopOpen && !isPlayerAction && 
						(h.getPositionForPlayer(a.getAssociatedPlayer().getPlayerID()) == PokerPosition.CO ||
						h.getPositionForPlayer(a.getAssociatedPlayer().getPlayerID()) == PokerPosition.BU) && 
						(h.getPositionForPlayer(playerID) == PokerPosition.SB || 
						h.getPositionForPlayer(playerID) == PokerPosition.BB)) {
					//Register a ATS
					atsRunning = true;
					
				} else if(atsRunning && !isPlayerAction && a.getKind() == ActionKind.RAISE) {
					//ATS reaction was done by CO or SB
					atsRunning = false;
				} else if(atsRunning && isPlayerAction) {
					//Register ATS reaction of current player
					if(h.getPositionForPlayer(playerID) == PokerPosition.SB) { 
						player.nbFoldToATSSBPossible++;
					} else {
						player.nbFoldToATSBBPossible++;
					}
					
					if(a.getKind() == ActionKind.FOLD && h.getPositionForPlayer(playerID) == PokerPosition.SB) {
						player.nbFoldToATSSB++;
					} else if(a.getKind() == ActionKind.FOLD && h.getPositionForPlayer(playerID) == PokerPosition.BB) {
						player.nbFoldToATSBB++;
					}
				}
				preflopOpen = true;
			}
			
			if(a.getKind() == ActionKind.RAISE) {
				preflopRaisedByHero = isPlayerAction;
				preflopRaisedBySomeone = !isPlayerAction;
				initiativePlayerID = a.getAssociatedPlayer().getPlayerID();
				
			}
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
	 * Set all indicators for action and is player action boolean given in parameter
	 * @param a
	 * @param isPlayerAction
	 */
	private void computeTurnActions(PokerAction a, Boolean isPlayerAction) {
		if(heroCbetOnFlop && generalSecondBarrelPossible && isPlayerAction && !dataCalculated[NB_SECOND_BARREL]) {
			player.nbSecondBarrelPossible++;
			player.nbSecondBarrel += a.getKind() == ActionKind.BET ? 1 : 0;
			dataCalculated[NB_SECOND_BARREL] = true;
		} else if(heroCallCbetOnFlop && generalSecondBarrelPossible && foldToSndBarrelPossible && 
				isPlayerAction && !dataCalculated[NB_FOLD_TO_SECOND_BARREL]) {
			player.nbFoldToSecondBarrelPossible++;
			player.nbFoldToSecondBarrel += a.getKind() == ActionKind.FOLD ? 1 : 0;
			dataCalculated[NB_FOLD_TO_SECOND_BARREL] = true;
		} else if(heroCbetOnFlop && generalSecondBarrelPossible) {
			//Action of another player, check if current player could be 21 barrel after that
			generalSecondBarrelPossible = a.getKind() == ActionKind.CHECK || (a.getKind() == ActionKind.FOLD && playersAlive.size() >= 2);
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
			player.nbCbetPossible++; 
			player.nbCbet += a.getKind() == ActionKind.BET ? 1 : 0;
			heroCbetOnFlop = a.getKind() == ActionKind.BET;
			dataCalculated[NB_CBET] = true;
		} else if(foldToCbetPossible && preflopRaisedBySomeone && isPlayerAction && !dataCalculated[NB_FOLD_TO_CBET]) {
			player.nbFoldToCbetPossible++;
			player.nbFoldToCbet += a.getKind() == ActionKind.FOLD ? 1 : 0;
			heroCallCbetOnFlop = a.getKind() == ActionKind.CALL;
			dataCalculated[NB_FOLD_TO_CBET] = true;
		} else if(preflopRaisedByHero && heroCbetPossible) {
			//Action of another player, check if current player could be cbet afeter that
			heroCbetPossible = a.getKind() == ActionKind.CHECK || (a.getKind() == ActionKind.FOLD && playersAlive.size() >= 2);
		} else if(preflopRaisedBySomeone && !foldToCbetPossible) {
			//Register The CBet of initiative player
			foldToCbetPossible = a.getKind() == ActionKind.BET && a.getAssociatedPlayer().getPlayerID().equals(initiativePlayerID);
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
				if(a.getKind() == ActionKind.BET) {player.nbAFHandBetAndRaise++;}
				if(a.getKind() == ActionKind.CALL) {player.nbAFHandCalled++;}
				benefitHand -= a.getAmountBet();
				break;
				
			case RAISE:
				player.nbAFHandBetAndRaise++;
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
