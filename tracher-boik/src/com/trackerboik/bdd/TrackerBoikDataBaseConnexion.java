package com.trackerboik.bdd;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.trackerboik.exception.TBException;

import controller.trackerboik.main.TrackerBoikController;

public class TrackerBoikDataBaseConnexion {
	private static String DB_PATH_PROPERTY = "db_path";
	private String DBPath;
	private Connection connection = null;
	private Statement statement = null;

	public TrackerBoikDataBaseConnexion() throws TBException {
		try {
			DBPath = TrackerBoikController.getInstance()
					.getConfigurationController().getProperty(DB_PATH_PROPERTY);
		} catch (TBException e) {
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * Connect to database
	 * 
	 * @throws TBException
	 */
	public void connect() throws TBException {
		try {
			Class.forName("org.hsqldb.jdbcDriver").newInstance();
			connection = DriverManager.getConnection("jdbc:hsqldb:" + DBPath,
					"sa", "");
		} catch (ClassNotFoundException notFoundException) {
			notFoundException.printStackTrace();
			throw new TBException("Impossible to access database: '"
					+ notFoundException.getMessage() + "'");
		} catch (SQLException sqlException) {
			sqlException.printStackTrace();
			throw new TBException("Impossible to access database: '"
					+ sqlException.getMessage() + "'");
		} catch (InstantiationException e) {
			e.printStackTrace();
			throw new TBException("Impossible to access database: '"
					+ e.getMessage() + "'");
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			throw new TBException("Impossible to access database: '"
					+ e.getMessage() + "'");
		}
	}

	/**
	 * Execute Query in database
	 * 
	 * @param requet
	 * @return
	 */
	public ResultSet executeQuery(String request) throws TBException {
		ResultSet resultat = null;
		try {
			refreshConnection();
			statement = connection.createStatement();

			resultat = statement.executeQuery(request);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new TBException("Erreur dans l'execution de la requete : '"
					+ request + "' : '" + e.getMessage() + "'");
		}

		return resultat;

	}

	public void executeInstruction(String request) throws TBException {
		try {
			refreshConnection();
			statement = connection.createStatement();
			statement.executeUpdate(request);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new TBException(
					"Une erreur est survenue lors de la tentative d'execution de l'instruction '"
							+ request + "' en base de données.");
		}

	}

	private void refreshConnection() throws SQLException, TBException {
		if (connection == null || connection.isClosed()) {
			connect();
		}
	}

	/**
	 * Close connection
	 */
	public void close() throws TBException {
		try {
			connection.close();
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new TBException(
					"Erreur dans la fermeture de la connection : '"
							+ e.getMessage() + "'");
		}
	}
}
