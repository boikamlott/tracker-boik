package model.trackerboik.dao.hsqldb;

import model.trackerboik.dao.GeneralDBOperationsDAO;

import com.trackerboik.bdd.TrackerBoikDataBaseConnexion;
import com.trackerboik.exception.TBException;

public class GeneralDBOperations implements GeneralDBOperationsDAO {

	private TrackerBoikDataBaseConnexion dbCon;
	
	@Override
	public void eraseTableContent(String tableName) throws TBException {
		try {
			executeSQLUpdate("DELETE * FROM '" + tableName + "'");
		} catch (TBException e) {
			throw new TBException("Impossible de supprimer le contenu de la table: '" + tableName + "': " + e.getMessage());
		}
	}

	private void executeSQLUpdate(String request) throws TBException {
		dbCon = new TrackerBoikDataBaseConnexion();
		dbCon.executeInstruction(request);
	}

}
