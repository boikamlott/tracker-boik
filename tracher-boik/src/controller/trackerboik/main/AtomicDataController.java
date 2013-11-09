package controller.trackerboik.main;

import java.util.List;

import model.trackerboik.dao.BoardDAO;
import model.trackerboik.dao.GeneralDBOperationsDAO;
import model.trackerboik.dao.hsqldb.GeneralHSQLDBOperations;
import model.trackerboik.dao.hsqldb.SessionHSQL;

import com.trackerboik.exception.TBException;

public class AtomicDataController {

	private TrackerBoikController parentController;
	
	public AtomicDataController(TrackerBoikController parent) {
		this.parentController = parent;
	}
	
	/**
	 * Check the current database by verifying all tables
	 * If some tables are missing, we create it
	 */
	public void checkCurrentDataBase() {
		GeneralDBOperationsDAO bdd = new SessionHSQL();
		
	}
	
	/**
	 * Refresh the whole database atomic content
	 */
	public void refreshAllData() {
		try {
			eraseAllAtomicData();
			computeAllAtomicData();
		} catch (TBException e) {
			//TODO raise error window
		}
	}
	
	private void computeAllAtomicData() {
		// TODO Auto-generated method stub
		
	}

	public void eraseAllAtomicData() throws TBException {
		GeneralDBOperationsDAO db = new SessionHSQL();
//		BoardDAO boardDB = new BoardHSQLDB();
//		boardDB.createTable();
		List<String> tableNames = db.getTableNames();
	}
}
