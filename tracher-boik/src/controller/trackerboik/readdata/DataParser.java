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
	private File f;
	
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
			BufferedReader br = new BufferedReader(new FileReader(f));
			String currentLine = br.readLine();
			while(currentLine != null) {
				try {
				readHand(br, currentLine, associatedSession);
				currentLine = br.readLine();
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
	 * @param br
	 * @param associatedSession
	 */
	private void readHand(BufferedReader br, String currentLine, PokerSession associatedSession) throws TBException {
		try {
			consumeEmptyLines(br, currentLine);
			
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
			String tableName = readTableName(br, currentLine);
			List<PokerPlayer> players = readPlayers(br, currentLine);
			List<PokerAction> actions = new ArrayList<PokerAction>();
			actions.addAll(readActions(br, currentLine, HandMoment.PREFLOP));
			consummeItems(br, currentLine, HOLE_CARDS);
			readAndNoteCurrentPlayerHand(players);
			
			while(currentLine != null && (!currentLine.contains(UNCALLED_BET) || 
					!currentLine.contains(SHOWDOWN))) {
				HandMoment moment = reandHandMoment(br, currentLine);
				actions.addAll(readActions(br, currentLine, moment));
			}
			
			readSummary(br, currentLine, players, actions);
			
			
		} catch (Exception e) {
			throw new TBException(e.getMessage());
		}
		
	}

	private void readSummary(BufferedReader br, String currentLine,
			List<PokerPlayer> players, List<PokerAction> actions) {
		// TODO Auto-generated method stub
		
	}

	private HandMoment reandHandMoment(BufferedReader br, String currentLine) {
		// TODO Auto-generated method stub
		return null;
	}

	private void readAndNoteCurrentPlayerHand(List<PokerPlayer> players) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Check and consume one of item given in parameter if exists
	 * @param strings
	 */
	private void consummeItems(BufferedReader br, String currentLine, String elem) throws TBException {
		if(br != null && currentLine != null && currentLine.contains(elem)) {
			
		} else {
			throw new TBException("Expected elem '" + elem + "' not found, found '" + currentLine + "'.");
		}
	}

	/**
	 * Read poker actions from the buffer and the current line
	 * @param br
	 * @param currentLine
	 * @param preflop
	 * @return
	 */
	private List<PokerAction> readActions(BufferedReader br,
			String currentLine, HandMoment preflop) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Get table name on the line
	 * @return
	 * @throws IOException 
	 */
	private String readTableName(BufferedReader br, String currentLine) throws IOException {
		consumeEmptyLines(br, currentLine);
		return null;
	}

	/**
	 * Reads all players of the hand
	 * @return
	 * @throws IOException 
	 */
	private List<PokerPlayer> readPlayers(BufferedReader br, String currentLine) throws IOException {
		consumeEmptyLines(br, currentLine);
		return null;
	}

	/**
	 * Consumme empty lines of file buffer
	 * @param br
	 * @param currentLine
	 * @throws IOException 
	 */
	private void consumeEmptyLines(BufferedReader br, String currentLine) throws IOException {
		while(currentLine != null && currentLine.replace(" ", "").isEmpty()) {
			currentLine = br.readLine();
		}
	} 
	
	
}
