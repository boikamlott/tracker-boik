package controller.trackerboik.data;

import java.util.ArrayList;
import java.util.List;

import com.trackerboik.exception.TBException;

import model.trackerboik.businessobject.ActionKind;
import model.trackerboik.businessobject.Hand;
import model.trackerboik.businessobject.HandMoment;
import model.trackerboik.businessobject.PokerAction;
import model.trackerboik.businessobject.PokerPlayer;

/**
 * Class represent a hand calculator for a player
 * @author gboismal
 *
 */
public class HandDataCalculator {
	
	private static final Integer NB_CALCULATED_INDICATORS = 4;
	private static final Integer NB_CBET = 0, 
								 NB_FOLD_TO_CBET = 1, 
								 NB_SECOND_BARREL = 2,
								 NB_FOLD_TO_SECOND_BARREL = 3;
	
	public List<PokerPlayer> playersAlive;
	private Double benefitHand, amountToCall;
	private boolean preflopRaisedByHero, preflopRaisedBySomeone, heroCbetPossible, foldToCbetPossible,
						heroCbetOnFlop, heroCallCbetOnFlop, generalSecondBarrelPossible, foldToSndBarrelPossible, continueRead;
	private boolean[] dataCalculated;
	private PokerPlayer player, initiativePlayer;
	private Hand h;
	private HandMoment currentAnalysedMoment;
	private HandMoment lastAnalysedMoment;
	
	
	public HandDataCalculator(PokerPlayer pp, Hand h) {
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
		
		if(playersAlive.size() != 1) {
			throw new TBException("Hand " + h.getId() + " is uncorrectly formatted ! More than one players has won !");
		} else if(amountToCall > 0.0 && playersAlive.contains(player)) {
			//Uncalled bet return to player
			benefitHand += amountToCall;
		}
		benefitHand += h.getPlayerHandData(player).getAmountWin();
		//Update Benefit
		player.benefitGeneral += benefitHand;
	}

	/**
	 * 
	 * @param a
	 * @throws TBException
	 */
	private void computeAction(PokerAction a) throws TBException {
		Boolean isPlayerAction = a.getAssociatedPlayer().equals(player);
		if(a.getKind() == ActionKind.FOLD) { playersAlive.remove(a.getAssociatedPlayer());}
		
		if(isPlayerAction) {
			computeAmountInvestedByCurrentPlayer(a);
		}
		
		switch(a.getMoment()) {
		case PREFLOP:
			if(a.getKind() == ActionKind.RAISE) {
				preflopRaisedByHero = isPlayerAction;
				preflopRaisedBySomeone = !isPlayerAction;
				initiativePlayer = a.getAssociatedPlayer();				
			}
			break;
		
		case FLOP:
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
				foldToCbetPossible = a.getKind() == ActionKind.BET && a.getAssociatedPlayer().equals(initiativePlayer);
			} else if(preflopRaisedBySomeone && foldToCbetPossible) {
				//Always could call the CBet if others players don't raise
				foldToCbetPossible = a.getKind() == ActionKind.FOLD || a.getKind() == ActionKind.CALL;
			} else if((heroCbetOnFlop || heroCallCbetOnFlop) && generalSecondBarrelPossible) {
				generalSecondBarrelPossible = a.getKind() == ActionKind.FOLD || a.getKind() == ActionKind.CALL;
			}
		
		case TURN:
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
				foldToSndBarrelPossible = a.getKind() == ActionKind.BET && a.getAssociatedPlayer().equals(initiativePlayer);
			} else if(heroCallCbetOnFlop && generalSecondBarrelPossible && foldToSndBarrelPossible) {
				foldToSndBarrelPossible = a.getKind() == ActionKind.FOLD || a.getKind() == ActionKind.CALL;
			}
			break;
			
		default:
			break;
		
		}
	}

	/**
	 * Update the amount spend by current player and/or the amount to call
	 */
	private void computeAmountInvestedByCurrentPlayer(PokerAction a) {
		switch(a.getKind()) {
			case POSTSBLIND:
			case POSTBIGBLIND:
			case CALL:
			case BET:	
				benefitHand -= a.getAmountBet();
				break;
				
			case RAISE:
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
