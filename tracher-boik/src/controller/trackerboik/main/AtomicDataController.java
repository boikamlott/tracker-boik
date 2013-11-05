package controller.trackerboik.main;

import java.util.List;

import model.trackerboik.dao.GeneralDBOperationsDAO;
import model.trackerboik.dao.hsqldb.GeneralDBOperations;

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
		GeneralDBOperationsDAO bdd = new GeneralDBOperations();
		
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
		GeneralDBOperationsDAO db = new GeneralDBOperations();
		List<String> tableNames = db.getTableNames();
	}
}
