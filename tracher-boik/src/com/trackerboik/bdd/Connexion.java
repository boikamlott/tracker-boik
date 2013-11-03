package com.trackerboik.bdd;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

import com.trackerboik.exception.TBException;

public class Connexion {
	private String DBPath;
	private Connection connection = null;
	private Statement statement = null;

	public Connexion(String dBPath) {
		DBPath = dBPath;
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
			statement = connection.createStatement();
			System.out.println("Connexion avec succès");
		} catch (ClassNotFoundException notFoundException) {
			notFoundException.printStackTrace();
			throw new TBException(Level.SEVERE,
					"Impossible to access database: '"
							+ notFoundException.getMessage() + "'");
		} catch (SQLException sqlException) {
			sqlException.printStackTrace();
			throw new TBException(Level.SEVERE,
					"Impossible to access database: '"
							+ sqlException.getMessage() + "'");
		} catch (InstantiationException e) {
			e.printStackTrace();
			throw new TBException(Level.SEVERE,
					"Impossible to access database: '" + e.getMessage() + "'");
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			throw new TBException(Level.SEVERE,
					"Impossible to access database: '" + e.getMessage() + "'");
		}
	}

	/**
	 * Execute Query in database
	 * 
	 * @param requet
	 * @return
	 */
	public ResultSet query(String request) throws TBException {
		ResultSet resultat = null;

		try {
			resultat = statement.executeQuery(request);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new TBException(Level.SEVERE,
					"Erreur dans l'execution de la requete : '" + request
							+ "' : '" + e.getMessage() + "'");
		}

		return resultat;

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
			throw new TBException(Level.SEVERE,
					"Erreur dans la fermeture de la connection : '"
							+ e.getMessage() + "'");
		}
	}
}
