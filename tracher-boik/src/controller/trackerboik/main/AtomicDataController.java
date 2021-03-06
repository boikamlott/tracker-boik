package controller.trackerboik.main;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import model.trackerboik.businessobject.Hand;
import model.trackerboik.businessobject.PlayerStats;
import model.trackerboik.businessobject.PokerAction;
import model.trackerboik.businessobject.PokerPlayer;
import model.trackerboik.businessobject.PokerSession;
import model.trackerboik.dao.ActionDAO;
import model.trackerboik.dao.BoardDAO;
import model.trackerboik.dao.GeneralDBOperationsDAO;
import model.trackerboik.dao.HandBoardDAO;
import model.trackerboik.dao.HandDAO;
import model.trackerboik.dao.HandPlayerDAO;
import model.trackerboik.dao.PlayerDAO;
import model.trackerboik.dao.SessionDAO;
import model.trackerboik.dao.sql.ActionSQL;
import model.trackerboik.dao.sql.BoardSQL;
import model.trackerboik.dao.sql.HandBoardSQL;
import model.trackerboik.dao.sql.HandPLayerSQL;
import model.trackerboik.dao.sql.HandSQL;
import model.trackerboik.dao.sql.PlayerSQL;
import model.trackerboik.dao.sql.PlayerStatsSQL;
import model.trackerboik.dao.sql.SessionSQL;
import model.trackerboik.data.HandDataBDDReader;
import model.trackerboik.data.HandsDataFileReader;

import com.trackerboik.appmngt.TrackerBoikLog;
import com.trackerboik.exception.TBException;
import com.trackerboik.util.AppUtil;
import com.trackerboik.util.BDDUtil;


public class AtomicDataController {

	private TrackerBoikController parentController;

	public AtomicDataController(TrackerBoikController parent) throws TBException {
		this.parentController = parent;
		checkAtomicDataDBSchema();
	}

//************************************************** Public Features *********************************************//
	
	public void refreshCurrentFolder() {
		try {
			List<PokerSession> pss = refreshAllAtomicData();
			loadSessionsInMemoryToDataBase(pss);
			
			for(PokerSession ps : pss) {
				parentController.getHands().addAll(ps.getHands());
			}
			
			TrackerBoikLog.getInstance().log(
					Level.INFO,
					"Data of " + pss.size()
							+ " session(s) have been added into database");
		} catch (TBException e) {
			//TODO raise error window
		} catch (Exception e) {
			//TODO
			//raise unknow error window
		}
	}
	
	/**
	 * Load all database in memory
	 * Careful: Delete all hands in memory which is not saved
	 */
	public void loadNewAtomicData() {
		try {
			HandDataBDDReader reader = new HandDataBDDReader();
			parentController.getHands().clear();
			List<Hand> handsNotComputed = reader.getHandsNotComputed();
			parentController.getHands().addAll(handsNotComputed);
		} catch (TBException e) {
			//TODO raise error window
		}
	}
	
	/**
	 * Delete all data from atomic data DB PRE: Schema is correct
	 */
	public void eraseAllAtomicData() throws TBException {
		GeneralDBOperationsDAO db = new SessionSQL();
		List<String> tbNames = db.getNeededTableNamesInCorrectOrderForDrop();

		for (String table : tbNames) {
			db.eraseTableContent(table);
		}
	}

//************************************************** Private Routines **********************************************//
	/**
	 * Write all data contained in memory in database
	 */
	private void loadSessionsInMemoryToDataBase(List<PokerSession> pokSessions) throws TBException {
		for(PokerSession ps : pokSessions) {
			SessionDAO sessionDB = new SessionSQL();
			HandDAO handDB = new HandSQL();
			PlayerDAO playerDB = new PlayerSQL();
			PlayerStatsSQL playerSessionStatsDB = new PlayerStatsSQL();
			BoardDAO boardDB = new BoardSQL();
			HandBoardDAO hbDB = new HandBoardSQL();
			HandPlayerDAO hpDB = new HandPLayerSQL();
			ActionDAO actionDB = new ActionSQL();
			
			sessionDB.insertSession(ps);
			for(Hand h : ps.getHands()) {
				try {
					writeHandInDataBase(h, handDB, playerDB, playerSessionStatsDB, boardDB, hbDB, hpDB, actionDB);
				} catch (TBException e) {
					TrackerBoikLog.getInstance().log(Level.WARNING, "Impossible to write hand(" + h.getId() + ") in Database: " + e.getMessage() + "'");
				} catch (Exception e) {
					TrackerBoikLog.getInstance().log(Level.WARNING, "Impossible to write hand(" + h.getId() + ") in Database for unknow reason: " + e.getMessage() + "'");
				}
			}	
		}
		
	}

	/**
	 * Writes hand in database
	 * @param h
	 * @param hpDB 
	 * @param hbDB 
	 * @param boardDB 
	 * @param playerDB 
	 * @param actionDB 
	 */
	private void writeHandInDataBase(Hand h, HandDAO handDB, PlayerDAO playerDB, PlayerStatsSQL playerSessionDB, BoardDAO boardDB, 
			HandBoardDAO hbDB, HandPlayerDAO hpDB, ActionDAO actionDB) throws TBException{		
		if(handDB.isHandExists(h.getId())) {
			throw new TBException("Hand already exists in database");
		}
		
		//Insert Hand
		handDB.insertHand(h);
		//Insert Players and HandPlayer
		writesAllPlayersHand(h, playerDB, playerSessionDB, hpDB);
		//Insert Board and HandBoard(if any)
		writeHandBoardIfExists(h, boardDB, hbDB);
		//Insert Actions
		writeActionsForHand(h, actionDB);
	}

	/**
	 * Write all actions related to hand in database
	 * @param h
	 * @param actionDB 
	 */
	private void writeActionsForHand(Hand h, ActionDAO actionDB) throws TBException {
		for(PokerAction pa : h.getActions()) {
			actionDB.insertAction(pa);
		}
	}

	/**
	 * Write the board corresponding to hand in database if exists
	 * @param h
	 * @param hbDB 
	 * @param boardDB 
	 * @throws TBException 
	 */
	private void writeHandBoardIfExists(Hand h, BoardDAO boardDB, HandBoardDAO hbDB) throws TBException {
		if(!h.getBoard().isEmpty()) {			
			boardDB.insertBoard(h.getBoard());
			hbDB.insertHandBoard(h, h.getBoard());
		}
	}

	/**
	 * Writes all player if not already exists in database for the player list
	 * @param playerDB 
	 * @param hpDB 
	 * @param players
	 * @throws TBException
	 */
	private void writesAllPlayersHand(Hand h, PlayerDAO playerDB, PlayerStatsSQL playerStatsDB, HandPlayerDAO hpDB) throws TBException {		
		//Insert all players
		for(PokerPlayer pp : h.getPlayers()) {
			if(!playerDB.isPlayerExists(pp.getPlayerID())) {
				playerDB.insertPlayer(pp);
				playerStatsDB.insertPlayerStats(new PlayerStats(pp.getPlayerID()));
			}
			hpDB.insertHandPlayer(h, pp);
		}
		
	}

	/**
	 * Refresh the current data folder and parse files
	 */
	private List<PokerSession> refreshAllAtomicData() throws TBException {
		try {
			List<PokerSession> res = new ArrayList<PokerSession>();
			int nbSessionLoaded = 0;
			
			List<String> files = parentController.getFolderController().getAllNotComputedFilesPath();
			for (String fpath : files) {						
				try {
					PokerSession toAdd = parseDataOfFile(fpath);
					if(toAdd != null) {
						res.add(toAdd);
						TrackerBoikLog.getInstance().log(
								Level.INFO,
								"Data of file '" + fpath
										+ "' was successfully read");
						nbSessionLoaded++;
					}	
					parentController.getFolderController().markFileAsComputed(fpath);
				} catch (TBException e) {
					TrackerBoikLog.getInstance().log(Level.SEVERE,
							"Error while reading file '" + fpath + "'");
				}
			}
			TrackerBoikLog.getInstance().log(
					Level.INFO,
					"Data of " + nbSessionLoaded
							+ " file(s) have been readed successfully");
			
			return res;
		} catch (Exception e) {
			throw new TBException(
					"Impossible de lire le contenu du repertoire contenant les donn�es des mains jou�es + '"
							+ e.getMessage() + "'");
		}
	}

	/**
	 * Parse all data of the file which path is given in parameter
	 * 
	 * @param string
	 */
	private PokerSession parseDataOfFile(String filePath) throws TBException {
		File f = null;
		PokerSession ps = null;
		try {
			f = new File(filePath);
		} catch (Exception e) {
			throw new TBException("Impossible d'ouvrir le fichier '" + filePath
					+ "' !");
		}

		String[] filePathElems = filePath.split(File.pathSeparator);

		if (applyFilter(filePathElems[filePathElems.length - 1])) {
			HandsDataFileReader dp = new HandsDataFileReader(f);
			ps = dp.readHands();
		}
		
		return ps;
	}

	/**
	 * Here are defined all filters to know if the file contains data to import
	 * 
	 * @param string
	 * @return true if the file could be load
	 */
	private boolean applyFilter(String fileName) {
		String[] nameElems = fileName.split(" ");
		boolean isTournament = nameElems.length > 2
				&& nameElems[1].startsWith(AppUtil.TOURNAMENT_FILE_ID);

		return !fileName.contains(AppUtil.ARGENT_FICTIF) && !isTournament;
	}

	/**
	 * Check the Current DB schema and throw expression if problems
	 * @throws TBException
	 */
	public void checkAtomicDataDBSchema() throws TBException {
		GeneralDBOperationsDAO db = new SessionSQL();
		List<String> dbTableNames = db.getTableNames();
		List<String> appTablesNeededNames = db
				.getNeededTableNamesInCorrectOrderForDrop();
		List<String> diff = new ArrayList<String>(), toDelete = new ArrayList<String>();

		// Compare the list
		for (String tdbName : appTablesNeededNames) {
			if (!dbTableNames.contains(tdbName)) {
				diff.add(tdbName);
			} else {
				toDelete.add(tdbName);
			}
		}

		// If some tables are missing, delete existing tables and re-build
		// schema
		if (!diff.isEmpty()) {
			for (String tbName : toDelete) {
				db.dropTable(tbName);
			}
			createAtomicDatabaseSchema();
		}

	}

	/**
	 * Creates the atomic data databse schema PRE: Any table of this scema
	 * already exists
	 */
	private void createAtomicDatabaseSchema() throws TBException {
		GeneralDBOperationsDAO db = new SessionSQL();
		List<String> tbNames = db.getNeededTableNamesInCorrectOrderForCreate();

		for (String table : tbNames) {
			BDDUtil.createTableSwitchName(table);
		}

		TrackerBoikLog.getInstance().log(Level.INFO,
				"Atomic databaseSchema was successfully created !");

	}

}
