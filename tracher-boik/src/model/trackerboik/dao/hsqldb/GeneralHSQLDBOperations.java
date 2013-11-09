package model.trackerboik.dao.hsqldb;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.trackerboik.dao.GeneralDBOperationsDAO;

import com.trackerboik.bdd.TrackerBoikDataBaseConnexion;
import com.trackerboik.exception.TBException;

public abstract class GeneralHSQLDBOperations implements GeneralDBOperationsDAO {
	public static final String TABLE_NAME = "";
	
	protected static final String GEN_ATT_HAND_ID = "hand_id",
								  GEN_ATT_PLAYER_ID = "player_id",
								  GEN_ATT_BOARD_ID = "board_id",
								  GEN_ATT_SESSION_ID = "session_id";
	
	private TrackerBoikDataBaseConnexion dbCon;
	
	@Override
	public void eraseTableContent(String tableName) throws TBException {
		try {
			executeSQLUpdate("DELETE * FROM " + tableName);
		} catch (TBException e) {
			throw new TBException("Impossible de supprimer le contenu de la table: '" + tableName + "': " + e.getMessage());
		}
	}

	
	public void dropTable(String tableName) throws TBException {
		try {
			executeSQLUpdate("DROP TABLE " + tableName);
		} catch (TBException e) {
			throw new TBException("Impossible de supprimer la table: '" + tableName + "': " + e.getMessage());
		}
	}
	
	@Override
	public List<String> getTableNames() throws TBException {
		List<String> res = new ArrayList<>();
		try {
			dbCon = new TrackerBoikDataBaseConnexion();
			//On récupère les métadonnées à partir de la connexion
			DatabaseMetaData dmd = dbCon.getMetaData();
			//Récupération des informations
			ResultSet tables = dmd.getTables(dbCon.getCatalog(), null, "%", null);
			//Affichage des informations
			while(tables.next()){
				   System.out.println("###################################");
				   for(int i=0; i<tables.getMetaData().getColumnCount();i++){
				      String nomColonne = tables.getMetaData().getColumnName(i+1);
				      Object valeurColonne = tables.getObject(i+1);
				      System.out.println(nomColonne+" = "+valeurColonne);
				   }
			}
		} catch (TBException e) {
			throw new TBException("Impossible to get table list in database: '" + e + "'");
		} catch (SQLException e) {
			throw new TBException("Impossible to get table list in database: 'SQLException:'" + e + "''");
		}
		
		return res;
	}

	@Override
	public abstract void createTable() throws TBException; 
	
	/**
	 * Execute SQL request
	 * @param request
	 * @return
	 * @throws TBException
	 */
	protected ResultSet executeSQLQuery(String request) throws TBException {
		dbCon = new TrackerBoikDataBaseConnexion();
		return dbCon.executeQuery(request);
	}
	
	/**
	 * Execute SQL update
	 * @param request
	 * @throws TBException
	 */
	protected void executeSQLUpdate(String request) throws TBException {
		dbCon = new TrackerBoikDataBaseConnexion();
		dbCon.executeInstruction(request);
	}



}
