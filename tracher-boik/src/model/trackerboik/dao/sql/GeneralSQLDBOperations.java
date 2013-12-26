package model.trackerboik.dao.sql;

import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import model.trackerboik.dao.GeneralDBOperationsDAO;

import com.trackerboik.appmngt.TrackerBoikLog;
import com.trackerboik.bdd.TrackerBoikDataBaseConnexion;
import com.trackerboik.exception.TBException;

public abstract class GeneralSQLDBOperations implements GeneralDBOperationsDAO {
	public static String TABLE_NAME = "";

	protected static final String GEN_ATT_HAND_ID = "hand_id",
			GEN_ATT_PLAYER_ID = "player_id", GEN_ATT_BOARD_ID = "board_id",
			GEN_ATT_SESSION_ID = "session_id", GEN_ATT_HAND_DATA_CALCULATED = "hand_data_computed";

	private static final String SQL_TYPE_TABLE = "TABLE_TYPE",
			SQL_TYPE_TABLE_VALUE = "TABLE", SQL_TABLE_NAME = "TABLE_NAME";

	private TrackerBoikDataBaseConnexion dbCon;
	protected PreparedStatement psInsert;
	protected PreparedStatement psQuery;
	
	public GeneralSQLDBOperations() throws TBException {
		psInsert = createPreparedStatement(getInsertPreCompiledRequest());
	}
	
	
	protected abstract String getInsertPreCompiledRequest();
	
	protected abstract String getExistenceTestPreCompiledRequest();
	
	protected abstract String getAllElementsForLoadSessionInMemoryRequest();

	@Override
	public void eraseTableContent(String tableName) throws TBException {
		try {
			executeSQLUpdate("DELETE FROM " + tableName);
			TrackerBoikLog.getInstance().log(Level.INFO, "Delete all content of table '" + tableName + "' !");
		} catch (TBException e) {
			throw new TBException(
					"Impossible de supprimer le contenu de la table: '"
							+ tableName + "': " + e.getMessage());
		}
	}

	public void dropTable(String tableName) throws TBException {
		try {
			executeSQLUpdate("DROP TABLE " + tableName);
			TrackerBoikLog.getInstance().log(Level.INFO, "Table '" + tableName + "' dropped !");
		} catch (TBException e) {
			throw new TBException("Impossible de supprimer la table: '"
					+ tableName + "': " + e.getMessage());
		}
	}

	@Override
	public List<String> getTableNames() throws TBException {
		List<String> res = new ArrayList<>();
		try {
			dbCon = new TrackerBoikDataBaseConnexion();
			// On récupère les métadonnées à partir de la connexion
			DatabaseMetaData dmd = dbCon.getMetaData();
			// Récupération des informations
			ResultSet tables = dmd.getTables(dbCon.getCatalog(), null, "%",
					null);
			// Affichage des informations
			while (tables.next()) {
				String tableType = "", tableName = "";
				for (int i = 0; i < tables.getMetaData().getColumnCount()
						&& (tableType.isEmpty() || tableType
								.equalsIgnoreCase(SQL_TYPE_TABLE_VALUE)); i++) {
					String nomColonne = tables.getMetaData().getColumnName(
							i + 1);
					if (nomColonne.equalsIgnoreCase(SQL_TYPE_TABLE)) {
						tableType = tables.getObject(i + 1).toString();
					}
					if (nomColonne.equalsIgnoreCase(SQL_TABLE_NAME)) {
						tableName = tables.getObject(i + 1).toString();
					}
				}

				if (tableType.equalsIgnoreCase(SQL_TYPE_TABLE_VALUE)
						&& !tableName.isEmpty()) {
					res.add(tableName.toUpperCase());
				}
			}
		} catch (TBException e) {
			throw new TBException("Impossible to get table list in database: '"
					+ e + "'");
		} catch (SQLException e) {
			throw new TBException(
					"Impossible to get table list in database: 'SQLException:'"
							+ e + "''");
		}

		return res;
	}

	@Override
	public abstract void createTable() throws TBException;

	public List<String> getNeededTableNamesInCorrectOrderForCreate() {
		List<String> res = new ArrayList<>();
		res.add(SessionSQL.TABLE_NAME.toUpperCase());
		res.add(BoardSQL.TABLE_NAME.toUpperCase());
		res.add(HandSQL.TABLE_NAME.toUpperCase());
		res.add(HandBoardSQL.TABLE_NAME.toUpperCase());
		res.add(PlayerSQL.TABLE_NAME.toUpperCase());
		res.add(PlayerStatsSQL.TABLE_NAME.toUpperCase());
		res.add(HandPLayerSQL.TABLE_NAME.toUpperCase());
		res.add(ActionSQL.TABLE_NAME.toUpperCase());

		return res;
	}
	

	public List<String> getNeededTableNamesInCorrectOrderForDrop() {
		List<String> res = new ArrayList<>();

		
		res.add(ActionSQL.TABLE_NAME.toUpperCase());
		res.add(HandPLayerSQL.TABLE_NAME.toUpperCase());
		res.add(HandBoardSQL.TABLE_NAME.toUpperCase());
		res.add(HandSQL.TABLE_NAME.toUpperCase());
		res.add(SessionSQL.TABLE_NAME.toUpperCase());
		res.add(BoardSQL.TABLE_NAME.toUpperCase());
		res.add(PlayerSQL.TABLE_NAME.toUpperCase());

		return res;
	}

	protected PreparedStatement createPreparedStatement(String rq) throws TBException {
		dbCon = new TrackerBoikDataBaseConnexion();
		return dbCon.getPreparedStatement(rq);
	}
	
	/**
	 * Execute SQL request
	 * 
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
	 * 
	 * @param request
	 * @throws TBException
	 */
	protected void executeSQLUpdate(String request) throws TBException {
		dbCon = new TrackerBoikDataBaseConnexion();
		dbCon.executeInstruction(request);
	}

}
