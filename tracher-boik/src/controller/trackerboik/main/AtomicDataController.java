package controller.trackerboik.main;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import model.trackerboik.businessobject.Hand;
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
import model.trackerboik.dao.sql.SessionSQL;

import com.trackerboik.appmngt.TrackerBoikLog;
import com.trackerboik.exception.TBException;
import com.trackerboik.util.AppUtil;
import com.trackerboik.util.BDDUtil;

import controller.trackerboik.readdata.HandsDataParser;

public class AtomicDataController {

	private TrackerBoikController parentController;

	public AtomicDataController(TrackerBoikController parent) {
		this.parentController = parent;
		checkAtomicDataDBSchema();
	}

//************************************************** Public Features *********************************************//
	/**
	 * Refresh the whole database atomic content
	 */
	private void refreshAllData() {
		try {
			checkAtomicDataDBSchema();
			eraseAllAtomicData();
			computeAllAtomicData();
			loadAllSessionsInMemoryToDataBase();
		} catch (TBException e) {
			// TODO raise error window
		}
	}
	
	public void refreshCurrentFolder() {
		try {
			
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
	private void loadAllSessionsInMemoryToDataBase() throws TBException {
		for(PokerSession ps : parentController.getSessions()) {
			SessionDAO sessionDB = new SessionSQL();
			HandDAO handDB = new HandSQL();
			PlayerDAO playerDB = new PlayerSQL();
			BoardDAO boardDB = new BoardSQL();
			HandBoardDAO hbDB = new HandBoardSQL();
			HandPlayerDAO hpDB = new HandPLayerSQL();
			ActionDAO actionDB = new ActionSQL();
			
			sessionDB.insertSession(ps);
			for(Hand h : ps.getHands()) {
				try {
					writeHandInDataBase(h, handDB, playerDB, boardDB, hbDB, hpDB, actionDB);
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
	private void writeHandInDataBase(Hand h, HandDAO handDB, PlayerDAO playerDB, BoardDAO boardDB, 
			HandBoardDAO hbDB, HandPlayerDAO hpDB, ActionDAO actionDB) throws TBException{		
		if(handDB.isHandExists(h.getId())) {
			throw new TBException("Hand already exists in database");
		}
		
		//Insert Hand
		handDB.insertHand(h);
		//Insert Players and HandPlayer
		writesAllPlayersHand(h, playerDB, hpDB);
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
	private void writesAllPlayersHand(Hand h, PlayerDAO playerDB, HandPlayerDAO hpDB) throws TBException {		
		//Insert all players
		for(PokerPlayer pp : h.getPlayers()) {
			if(!playerDB.isPlayerExists(pp.getPlayerID())) {
				playerDB.insertPlayer(pp);
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
			File f = new File(parentController.getConfigurationController()
					.getProperty(AppUtil.ATOMIC_DATA_FOLDER));
			SessionDAO sdbb = new SessionSQL();
			
			String[] files = f.list();
			for (String file : files) {
				String fpath = f.getAbsolutePath().endsWith(File.separator) ? f
						.getAbsolutePath() + file
						: f.getAbsolutePath() + File.separator + file;
				try {
					res.add(parseDataOfFile(fpath));
					TrackerBoikLog.getInstance().log(
							Level.INFO,
							"Data of file '" + fpath
									+ "' was successfully load");
				} catch (TBException e) {
					TrackerBoikLog.getInstance().log(Level.SEVERE,
							"Error while reading file '" + fpath + "'");
				}
			}
			TrackerBoikLog.getInstance().log(
					Level.INFO,
					"Data of " + files.length
							+ " file(s) have been added into database");
		} catch (Exception e) {
			throw new TBException(
					"Impossible de lire le contenu du repertoire contenant les données des mains jouées + '"
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
		try {
			f = new File(filePath);
		} catch (Exception e) {
			throw new TBException("Impossible d'ouvrir le fichier '" + filePath
					+ "' !");
		}

		String[] filePathElems = filePath.split(File.pathSeparator);

		if (applyFilter(filePathElems[filePathElems.length - 1])) {
			HandsDataParser dp = new HandsDataParser(f);
			return dp.readHands();
		}
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
