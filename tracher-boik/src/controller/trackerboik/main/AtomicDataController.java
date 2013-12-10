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
import model.trackerboik.dao.sql.HandPLayerHSQL;
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
	}

	/**
	 * Refresh the whole database atomic content
	 */
	public void refreshAllData() {
		try {
			checkAtomicDataDBSchema();
			eraseAllAtomicData();
			computeAllAtomicData();
			loadAllSessionsInMemoryToDataBase();
		} catch (TBException e) {
			// TODO raise error window
		}
	}
	
	/**
	 * Write all data contained in memory in database
	 */
	private void loadAllSessionsInMemoryToDataBase() throws TBException {
		for(PokerSession ps : parentController.getSessions()) {
			SessionDAO sessionDB = new SessionSQL();
			sessionDB.insertSession(ps);
			for(Hand h : ps.getHands()) {
				try {
					writeHandInDataBase(h);
				} catch (TBException e) {
					TrackerBoikLog.getInstance().log(Level.WARNING, "Impossible to write hand(" + h.getId() + " in Database: " + e.getMessage() + "'");
				} catch (Exception e) {
					TrackerBoikLog.getInstance().log(Level.WARNING, "Impossible to write hand(" + h.getId() + " in Database for unknow reason: " + e.getMessage() + "'");
				}
			}	
		}
		
	}

	/**
	 * Writes hand in database
	 * @param h
	 */
	private void writeHandInDataBase(Hand h) throws TBException{
		HandDAO handDB = new HandSQL();
		
		if(handDB.isHandExists(h.getId())) {
			throw new TBException("Hand already exists in database");
		}
		
		//Insert Hand
		handDB.insertHand(h);
		//Insert Players and HandPlayer
		writesAllPlayersHand(h);
		//Insert Board and HandBoard(if any)
		writeHandBoardIfExists(h);
		//Insert Actions
		writeActionsForHand(h);
	}

	/**
	 * Write all actions related to hand in database
	 * @param h
	 */
	private void writeActionsForHand(Hand h) throws TBException {
		for(PokerAction pa : h.getActions()) {
			ActionDAO actionDB = new ActionSQL();
			actionDB.insertAction(pa);
		}
	}

	/**
	 * Write the board corresponding to hand in database if exists
	 * @param h
	 * @throws TBException 
	 */
	private void writeHandBoardIfExists(Hand h) throws TBException {
		if(!h.getBoard().isEmpty()) {
			BoardDAO boardDB = new BoardSQL();
			HandBoardDAO handBoardDB = new HandBoardSQL();
			
			boardDB.insertBoard(h.getBoard());
			handBoardDB.insertHandBoard(h, h.getBoard());
		}
	}

	/**
	 * Writes all player if not already exists in database for the player list
	 * @param players
	 * @throws TBException
	 */
	private void writesAllPlayersHand(Hand h) throws TBException {
		PlayerDAO playerDB = new PlayerSQL();
		
		//Insert all players
		for(PokerPlayer pp : h.getPlayers()) {
			if(!playerDB.isPlayerExists(pp.getPlayerID())) {
				playerDB.insertPlayer(pp);
			}
			HandPlayerDAO handPlayerDB = new HandPLayerHSQL();
			handPlayerDB.insertHandPlayer(h, pp);
		}
		
	}

	/**
	 * Refresh the current data folder and parse files
	 */
	private void computeAllAtomicData() throws TBException {
		try {
			File f = new File(parentController.getConfigurationController()
					.getProperty(AppUtil.ATOMIC_DATA_FOLDER));
			String[] files = f.list();
			for (String file : files) {
				String fpath = f.getAbsolutePath().endsWith(File.separator) ? f
						.getAbsolutePath() + file
						: f.getAbsolutePath() + File.separator + file;
				try {
					parseDataOfFile(fpath);
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
	private void parseDataOfFile(String filePath) throws TBException {
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
			dp.readHands();
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

	/**
	 * Delete all data from atomic data DB PRE: Schema is correct
	 */
	private void eraseAllAtomicData() throws TBException {
		GeneralDBOperationsDAO db = new SessionSQL();
		List<String> tbNames = db.getNeededTableNamesInCorrectOrderForDrop();

		for (String table : tbNames) {
			db.eraseTableContent(table);
		}
	}
}
