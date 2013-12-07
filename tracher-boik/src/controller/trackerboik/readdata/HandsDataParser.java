package controller.trackerboik.readdata;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;

import model.trackerboik.businessobject.ActionKind;
import model.trackerboik.businessobject.Hand;
import model.trackerboik.businessobject.HandMoment;
import model.trackerboik.businessobject.HandResult;
import model.trackerboik.businessobject.PokerAction;
import model.trackerboik.businessobject.PokerBoard;
import model.trackerboik.businessobject.PokerCard;
import model.trackerboik.businessobject.PokerHand;
import model.trackerboik.businessobject.PokerPlayer;
import model.trackerboik.businessobject.PokerSession;

import com.trackerboik.appmngt.TrackerBoikLog;
import com.trackerboik.exception.TBException;
import com.trackerboik.util.AppUtil;

public class HandsDataParser {

	private static final String HOLE_CARDS = "HOLE_CARDS";
	private static final CharSequence UNCALLED_BET = "Uncalled";
	private static final CharSequence SHOWDOWN = "SHOW DOWN";
	private static final String SEAT = "Seat";
	private static final CharSequence FLOP = "FLOP";
	private static final CharSequence TURN = "TURN";
	private static final CharSequence RIVER = "RIVER";
	
	/** Summary constants **/
	private static final String SUMMARY = "SUMMARY";
	private static final String SUMMARY_SHOWED = "showed";
	private static final String SUMMARY_MUCKED = "mucked";
	private static final String SUMMARY_DIDNT_BET = "didn't bet";
	private static final String SUMMARY_FOLD_PREFLOP = "folded before Flop";
	private static final String SUMMARY_FOLD_FLOP = "folded on the Flop";
	private static final String SUMMARY_FOLD_TURN = "folded on the Turn";
	private static final String SUMMARY_FOLD_RIVER = "folded on the River";
	private static final String SUMMARY_COLLECTED = "collected";
	
	private File f;
	private BufferedReader br;
	private String currentLine;
	private Integer actionNoInHand;
	
	public HandsDataParser(File f) {
		this.f = f;
	}

	/**
	 * Main function read all hands contained in the file
	 * @throws TBException
	 */
	public void readHands() throws TBException {
		if(f == null || !f.exists()) {
			throw new TBException("File '" + (f != null ? f.getAbsolutePath() : "Unknow file") + "' is unreadable or doesn't exists !");
		}
		
		PokerSession associatedSession = new PokerSession(f.getName().split("-")[0], f.getName(), "Hold'em poker");
		
		try {
			this.br = new BufferedReader(new FileReader(f));
			gotToNextLine();
			
			while(currentLine != null) {
				try {
				this.actionNoInHand = 0;
				readHand(associatedSession);
				gotToNextLine();
				} catch (TBException e) {
					TrackerBoikLog.getInstance().log(Level.WARNING, "Impossible to read a hand in file '" + f.getName() + "': '" + e.getMessage() + "'");
				}
			}
		} catch (Exception e) {
			throw new TBException("Error while reading data in file '" + f.getAbsolutePath() + "': '" + e.getMessage() + "'");
		}
	}

	/**
	 * Read hand and load data in memory for the associated session
	 * The data was not associated with controller until getting all information
	 * @param br
	 * @param associatedSession
	 */
	private void readHand(PokerSession associatedSession) throws TBException {
		try {
			consumeEmptyLines();
			
			//Read hand metadata
			if(!currentLine.startsWith(AppUtil.POKERSTARS)) {
				throw new TBException("Unupported tracker or error in file at line '" + currentLine + "'");
			}
			
			String handID = currentLine.split("#")[1].split(":")[0].trim();
			Calendar date = AppUtil.parseCalendar(currentLine.split("-")[1].trim().split("[")[0].trim());
			Double bbValue = Double.parseDouble(currentLine.split(AppUtil.CURRENCY)[2].split(")")[0].trim());
			if(date == null || handID.isEmpty()) {
				throw new TBException("Error in format of hand entry for line: '" + currentLine + "'");
			}
			
			Hand h = new Hand(handID, associatedSession);
			h.setDateTime(date);
			h.setLimitBB(bbValue);
			gotToNextLine(); // Set line on Table line
			
			h.setTableName(readTableName());
			gotToNextLine(); // Set on first player
			
			addPlayerAndStackToHand(h);
			//Read Post blinds actions
			h.addActions(readActions(h, HandMoment.PREFLOP));
			
			//Read Hero hand
			consummeItems(HOLE_CARDS);
			addHeroPlayerHand(h);
			
			//Read real proflop actions
			h.addActions(readActions(h, HandMoment.PREFLOP));

			while(currentLine != null && (!currentLine.contains(UNCALLED_BET) || 
					!currentLine.contains(SHOWDOWN))) {
				HandMoment moment = reandHandMomentAndUpdateBoard(h);
				h.addActions(readActions(h, moment));
			}
			
			readHandSummary(h);
			
			
		} catch (Exception e) {
			throw new TBException(e.getMessage());
		}
		
	}

	/**-------------------------------------------- Read actions part analysis grammar module -----------------------------------*/
	
	/**
	 * ACTION = POKER_PLAYER: ACTION_KIND
	 * POKER_PLAYER = player_id
	 * ACTION_KIND = action_kind
	 * Read poker actions from the buffer and the current line
	 * @param br
	 * @param currentLine
	 * @param preflop
	 * @return
	 */
	private List<PokerAction> readActions(Hand h, HandMoment hm) throws TBException, IOException {
		List<PokerPlayer> players = h.getPlayers();
		List<PokerAction> res = new ArrayList<PokerAction>();
		
		while(currentLine != null && (!currentLine.contains(UNCALLED_BET) || 
				!currentLine.contains(SHOWDOWN) || !currentLine.contains(HOLE_CARDS)
				|| !currentLine.contains(FLOP) || !currentLine.contains(TURN) || 
				!currentLine.contains(RIVER))) {
			PokerPlayer cp = readActionConcernedPlayer(players);
			ActionKind ak = readActionKind();
			Double amount = readAmount(ak);
			res.add(new PokerAction(cp, h, actionNoInHand++, amount, ak, hm));
			gotToNextLine();
		}
		
		
		return res;
	}
	
	/**
	 * Gets Amount of action kind switch action kind
	 * Folds return null
	 * @param ak
	 * @return
	 */
	private Double readAmount(ActionKind ak) throws TBException, IOException {
		consumeEmptyLines();
		Double res = null;
		switch(ak) {
		case FOLD:
			break;
		default:
			try {
				String amountStr = currentLine.split(AppUtil.CURRENCY)[1];
				amountStr.replace("to", "");
				res = Double.parseDouble(amountStr.trim());
			} catch (Exception e) {
				throw new TBException("Error while reading amount action in line: " + currentLine);
			}
		
		}
		
		return res;
	}

	/**
	 * Just read the action kind in current line
	 * Throw error if no action kind are found or bad action kind id
	 * @return
	 */
	private ActionKind readActionKind() throws TBException, IOException {
		consumeEmptyLines();
		ActionKind res = null;
		String actionKindStr = currentLine.trim().split(":")[1].trim();
		
		if(actionKindStr.equals(ActionKind.CALL.getFileValue())) {
			res = ActionKind.CALL;
		} else if(actionKindStr.equals(ActionKind.CHECK.getFileValue())) {
			res = ActionKind.CHECK;
		} else if(actionKindStr.equals(ActionKind.RAISE.getFileValue())) {
			res = ActionKind.RAISE;
		} else if(actionKindStr.equals(ActionKind.FOLD.getFileValue())) {
			res = ActionKind.FOLD;
		} else if(actionKindStr.equals(ActionKind.BET.getFileValue())) {
			res = ActionKind.BET;
		} else if(actionKindStr.equals(ActionKind.POSTSBLIND.getFileValue())) {
			res = ActionKind.POSTSBLIND;
		} else if(actionKindStr.equals(ActionKind.POSTBIGBLIND.getFileValue())) {
			res = ActionKind.POSTBIGBLIND;
		} else {
			throw new TBException("Impossible to read actionKind in line: '" + currentLine + "'");
		}
		
		return res;
	}

	/**
	 * Just get the concerned player name, if synthax error in the expected line or if
	 * action player found is not on the list throw an error
	 * @param players
	 * @return
	 */
	private PokerPlayer readActionConcernedPlayer(List<PokerPlayer> players) throws TBException, IOException {
		consumeEmptyLines();
		String playerID = currentLine.trim().split(":")[0].trim();
		
		if(!players.contains(new PokerPlayer(playerID))) {
			throw new TBException("Invalid player name in action '" + currentLine + "'");
		} else {
			return players.get(players.indexOf(new PokerPlayer(playerID)));
		}
		
	}
	
	/**-------------------------------------------- Others parsing functions ------------------------------------------------------*/

	/**
	 * Get table name on the line
	 * @return
	 * @throws IOException 
	 * @throws TBException 
	 */
	private String readTableName() throws IOException, TBException {
		try {
			consumeEmptyLines();
			String tableName = currentLine.split("'")[1];
			return tableName;
		} catch (Exception e) {
			throw new TBException("Impossible to read table name, bad file format !");
		}
	}

	/**
	 * Reads all players and their stack at begining of the hand
	 * @return
	 * @throws IOException 
	 */
	private void addPlayerAndStackToHand(Hand h) throws TBException, IOException {
		consumeEmptyLines();
		try {
			List<PokerPlayer> players = new ArrayList<PokerPlayer>();
			List<Double> playerStack = new ArrayList<Double>();
			
			//Get data on file
			while(currentLine != null && currentLine.startsWith(SEAT)) {
				String playerID = currentLine.split(":")[1].trim().split("(")[0].trim();
				Double stack = Double.parseDouble(currentLine.split(AppUtil.CURRENCY)[1].replace(")", "").trim());				
				
				players.add(new PokerPlayer(playerID));
				playerStack.add(stack);
				gotToNextLine();
			}
			
			//Store data to hand
			PokerPlayer pp;
			for(int i = 0; i < players.size(); i++) {
				pp = players.get(i);
				h.addPlayerToHand(pp);
				h.setPositionForPlayer(pp, i + 1);
				h.setStartStackForPlayer(pp, playerStack.get(i));
			}
		} catch (Exception e) {
			throw new TBException("Error while parsing line for player and stack: " + currentLine);
		}
	}
	
	/**
	 * Read Hand of hero player and add it to the hand
	 * throw exception if hand is not correctly formatted
	 * @param players
	 */
	private void addHeroPlayerHand(Hand h) throws TBException, IOException {
		consumeEmptyLines();
		try {
			String player = currentLine.split("to")[1].split("[")[0].trim();
			String[] cards = currentLine.split("to")[1].split("[")[1].replace("]", "").trim().split(" ");
			
			PokerPlayer hero = h.getPlayers().get(h.getPlayers().indexOf(new PokerPlayer(player)));
			if(hero == null) {
				throw new TBException("Impossible to retrieve hero's name in line: " + currentLine);
			}
			
			PokerCard firstCard = PokerCard.readCard(cards[0]);
			PokerCard secondCard = PokerCard.readCard(cards[1]);
			if(firstCard == null || secondCard == null) {
				throw new TBException("Impossible to read hero's hand in String: " + currentLine);
			}
			PokerHand ph = new PokerHand();
			ph.setHand(firstCard, secondCard);
			
			h.setHandForPlayer(hero, ph);
		} catch (Exception e) {
			throw new TBException("Impossible to read hero player on line: " + currentLine);
		}
	}
	
	/**
	 * Read hand moment and set hand board relative to that moment
	 * @param h
	 * @return
	 * @throws TBException
	 * @throws IOException
	 */
	private HandMoment reandHandMomentAndUpdateBoard(Hand h) throws TBException, IOException {
		consumeEmptyLines();
		try {
			HandMoment res = null;
			if(currentLine.contains(FLOP)) {
				res = HandMoment.FLOP;
				String[] flopStr = currentLine.split("[")[1].replace("]", "").split(" ");
				
				List<PokerCard> flop = new ArrayList<PokerCard>();
				for(int i = PokerBoard.FLOP_1; i < PokerBoard.FLOP_3; i++) {
					flop.add(PokerCard.readCard(flopStr[i].trim()));
				}
				
				h.getBoard().setFlop(flop);
			} else if(currentLine.contains(TURN) || currentLine.contains(RIVER)) {
				res = currentLine.contains(TURN) ? HandMoment.TURN : HandMoment.RIVER;
				PokerCard card = PokerCard.readCard(currentLine.split("[")[2].replace("]", "").trim());
				
				if(res == HandMoment.TURN) {
					h.getBoard().setTurn(card);
				} else {
					h.getBoard().setRiver(card);
				}
			} else {
				throw new TBException("Impossible to read hand moment in line: " + currentLine);
			}
			
			return res;
		} catch (Exception e) {
			throw new TBException("Invalid moment line: " + currentLine);
		}
	}
	
/**------------------------------------- Read Hand Summary functions ------------------------------------**/	
	private void readHandSummary(Hand h) throws TBException, IOException {
		consumeEmptyLines();
		try {
			while(currentLine != null && !currentLine.contains(SUMMARY)) {
				gotToNextLine();
			}
			
			if(currentLine == null) {
				throw new TBException("No summary found for hand '" + h.getId() + "'");
			}
			
			//Move to summary starts
			gotToNextLine();
			//Read Total Pot and Rake
			Double totalPot = Double.parseDouble(currentLine.split(AppUtil.CURRENCY)[1].split("|")[0].trim());
			Double rake = Double.parseDouble(currentLine.split(AppUtil.CURRENCY)[2].trim());
			
			h.setPot(totalPot);
			h.setSiteRake(rake);
			
			while (currentLine != null && !currentLine.contains(SEAT)) {
				gotToNextLine();
			}
			
			//Get players status
			while (currentLine != null && currentLine.contains(SEAT)) {
				updatePlayerResultsForHand(h);
				gotToNextLine();
			}
			
		} catch (Exception e) {
			throw new TBException("Error while parsing hand summary at line: " + currentLine);
		}
	}

	/**
	 * Update player results contained in line for hand given in parameter
	 * PRE: Next current non empty line is a player result one
	 * @param h
	 */
	private void updatePlayerResultsForHand(Hand h) throws TBException, IOException{
		consumeEmptyLines();
		//Retrieve player
		String playerIDStartStr = currentLine.split(":")[0].trim();
		PokerPlayer pp = null;
		for(PokerPlayer hp : h.getPlayers()) {
			if(playerIDStartStr.startsWith(hp.getPlayerID())) {
				pp = hp;
				break;
			}
		}
		
		if(pp == null) {
			throw new TBException("Impossible to retrieve player name in player result summary line: " + currentLine);
		}
		
		HandResult hr;
		PokerHand ph = null;
		
		//Determine status
		if(currentLine.contains(SUMMARY_SHOWED) || currentLine.contains(SUMMARY_MUCKED)) {
			//Add win status and winning hand
			hr = currentLine.contains(SUMMARY_SHOWED) ? HandResult.WIN : HandResult.LOOSE;
			String beforeCard;
			if(hr == HandResult.WIN) {
				beforeCard = SUMMARY_SHOWED;
			} else {
				beforeCard = SUMMARY_MUCKED;
			}
			
			String[] cards = currentLine.split(beforeCard)[1].split("]")[0].replace("[", "").trim().split(" ");
			ph = new PokerHand();
			ph.setHand(PokerCard.readCard(cards[0]), PokerCard.readCard(cards[1]));
		} else if(currentLine.contains(SUMMARY_COLLECTED)) {
			hr = HandResult.WIN;
		} else if(currentLine.contains(SUMMARY_DIDNT_BET)) {
			hr = HandResult.NO_BET;
		} else if(currentLine.contains(SUMMARY_FOLD_PREFLOP)) {
			hr = HandResult.FOLD_PREFLOP;
		}else if(currentLine.contains(SUMMARY_FOLD_FLOP)) {
			hr = HandResult.FOLD_FLOP;
		} else if(currentLine.contains(SUMMARY_FOLD_TURN)) {
			hr = HandResult.FOLD_TURN;
		} else if(currentLine.contains(SUMMARY_FOLD_RIVER)) {
			hr = HandResult.FOLD_RIVER;
		} else {
			throw new TBException("Impossible to read result hand on line: " + currentLine);
		}
		
		h.setResultHandForPlayer(pp, hr);
		if(ph != null) {
			h.setHandForPlayer(pp, ph);
		}
	}	
/**------------------------------------- Low-Level parsing functions -------------------------------------**/
	


	/**
	 * Consumme empty lines of file buffer
	 * @param br
	 * @param currentLine
	 * @throws IOException 
	 */
	private void consumeEmptyLines() throws IOException {
		while(currentLine != null && currentLine.replace(" ", "").isEmpty()) {
			currentLine = br.readLine();
		}
	} 
	
	/**
	 * Check and consume one of item given in parameter if exists
	 * @param strings
	 */
	private void consummeItems(String elem) throws TBException, IOException {
		if(br != null && currentLine != null && currentLine.contains(elem)) {
			currentLine = br.readLine();
		} else {
			throw new TBException("Expected elem '" + elem + "' not found, found '" + currentLine + "'.");
		}
	}
	
	/**
	 * Shortcut to an expression often used
	 * @throws IOException
	 */
	private void gotToNextLine() throws IOException {
		currentLine = br.readLine();
	}

	
}
