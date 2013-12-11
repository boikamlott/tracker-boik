package com.trackerboik.bdd;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
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
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			connection = DriverManager.getConnection("jdbc:mysql:" + DBPath,
					"root", "");
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

	/**
	 * Get SQL Metadata
	 * @return
	 * @throws TBException
	 */
	public DatabaseMetaData getMetaData() throws TBException {
		try {
			refreshConnection();

			return connection.getMetaData();
		} catch (SQLException e) {
			throw new TBException("SQLException: '" + e.getMessage() + "'");
		}
	}

	/**
	 * Get Database catalog
	 * @return
	 * @throws TBException
	 */
	public String getCatalog() throws TBException {
		try {
			refreshConnection();

			return connection.getCatalog();
		} catch (SQLException e) {
			throw new TBException("SQLException: '" + e.getMessage() + "'");
		}
	}

	/**
	 * Return a prepared statement for the query given in parameter
	 * @param rq
	 * @return
	 */
	public PreparedStatement getPreparedStatement(String rq) throws TBException {
		try {
			refreshConnection();
			return connection.prepareStatement(rq);
		} catch (SQLException e) {
			throw new TBException("Error with DB when preparing statement for query: " + rq);
		}
	}
}
