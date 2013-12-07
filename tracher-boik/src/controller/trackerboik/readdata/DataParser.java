package controller.trackerboik.readdata;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.List;

import model.trackerboik.businessobject.ActionKind;
import model.trackerboik.businessobject.Hand;
import model.trackerboik.businessobject.HandMoment;
import model.trackerboik.businessobject.PokerAction;
import model.trackerboik.businessobject.PokerPlayer;
import model.trackerboik.businessobject.PokerSession;

import com.trackerboik.appmngt.TrackerBoikLog;
import com.trackerboik.exception.TBException;
import com.trackerboik.util.AppUtil;

public class DataParser {

	private static final String HOLE_CARDS = "HOLE_CARDS";
	private static final CharSequence UNCALLED_BET = "Uncalled";
	private static final CharSequence SHOWDOWN = "SHOW DOWN";
	private static final String SEAT = "Seat";
	
	private File f;
	private BufferedReader br;
	private String currentLine;
	
	public DataParser(File f) {
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
			if(date == null || handID.isEmpty()) {
				throw new TBException("Error in format of hand entry for line: '" + currentLine + "'");
			}
			
			Hand h = new Hand(handID, associatedSession);
			h.setDateTime(date);
			gotToNextLine(); // Set line on Table line
			
			String tableName = readTableName();
			gotToNextLine(); // Set on first player
			
			List<PokerPlayer> players = readPlayers();
			List<PokerAction> actions = new ArrayList<PokerAction>();
			//Read Post blinds actions
			actions.addAll(readActions(h, HandMoment.PREFLOP, players));
			
			//Read Hero hand
			consummeItems(HOLE_CARDS);
			readAndNoteCurrentPlayerHand(players);
			
			//Read real proflop actions
			actions.addAll(readActions(h, HandMoment.PREFLOP, players));

			while(currentLine != null && (!currentLine.contains(UNCALLED_BET) || 
					!currentLine.contains(SHOWDOWN))) {
				HandMoment moment = reandHandMoment();
				actions.addAll(readActions(h, moment, players));
			}
			
			readSummary(players, actions);
			
			
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
	private List<PokerAction> readActions(Hand h, HandMoment hm, List<PokerPlayer> players) throws TBException, IOException {
		PokerPlayer cp = readActionConcernedPlayer(players);
		ActionKind ak = readActionKind();
		return null;
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
		String playerID = currentLine.trim().split(":")[0];
		
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
	 * Reads all players of the hand and store from button to
	 * CO
	 * @return
	 * @throws IOException 
	 */
	private List<PokerPlayer> readPlayers() throws IOException {
		consumeEmptyLines();
		
		List<PokerPlayer> res = new ArrayList<>();
		
		while(currentLine != null && currentLine.startsWith(SEAT)) {
			res.add(new PokerPlayer(currentLine.split(":")[1].trim().split(" ")[0].trim()));
			gotToNextLine();
		}
		
		return res;
	}
	
	private void readAndNoteCurrentPlayerHand(List<PokerPlayer> players) {
		// TODO Auto-generated method stub
		
	}
	
	private HandMoment reandHandMoment() {
		// TODO Auto-generated method stub
		return null;
	}
	
	private void readSummary(List<PokerPlayer> players, List<PokerAction> actions) {
		// TODO Auto-generated method stub
		
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
