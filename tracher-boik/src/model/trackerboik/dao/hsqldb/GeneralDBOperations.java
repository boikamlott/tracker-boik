package model.trackerboik.dao.hsqldb;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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

	protected ResultSet executeSQLQuery(String request) throws TBException {
		dbCon = new TrackerBoikDataBaseConnexion();
		return dbCon.executeQuery(request);
	}
	
	protected void executeSQLUpdate(String request) throws TBException {
		dbCon = new TrackerBoikDataBaseConnexion();
		dbCon.executeInstruction(request);
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
	public void createTable() throws TBException {
		throw new TBException("Method is not implemented here !");
	}

}
