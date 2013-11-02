package com.trackerboik.appmngt;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;

import com.trackerboik.exception.TBException;

/**
 * Singleton pattern
 * Manage all properties variables of application
 * @author Gaetan
 *
 */
public class ConfigurationMgr {

	private final String propertiesFileName = "config.properties";
	private static ConfigurationMgr instance;
	private Properties tbProp;
	
	private ConfigurationMgr() throws TBException {
		try {
			tbProp = new Properties();
			tbProp.load(new FileInputStream(propertiesFileName));
		} catch (Exception e) {
			throw new TBException(Level.SEVERE, "Impossible de lire le fichier de configuration '" + propertiesFileName + "' !");
		}
	}
	
	public static ConfigurationMgr getInstance() throws TBException {
		if(instance == null) {
			instance = new ConfigurationMgr();
		}
		
		return instance;
	}
	
	public String getProperty(String propertyName) throws TBException {
		if(tbProp != null && tbProp.contains(propertyName)) {
			return tbProp.getProperty(propertyName);
		} else {
			throw new TBException(Level.SEVERE, "Impossible de récupérer la propriété '" + propertyName + "' !");
		}
	}
	
	public void setProperty(String name, String value) throws TBException {
		if(tbProp != null) {
			tbProp.setProperty(name, value);
		} else {
			throw new TBException(Level.SEVERE, "Impossible de lire le fichier de configuration '" + propertiesFileName + "' !");
		}
	}
	
	public void saveProperties() throws TBException {
		try {
			tbProp.store(new FileOutputStream(propertiesFileName), "Config updated on " + new Date().toString());
		} catch (FileNotFoundException e) {
			throw new TBException(Level.SEVERE, "Impossible de sauvegarder la configuration dans '" + propertiesFileName + "' !");
		} catch (IOException e) {
			throw new TBException(Level.SEVERE, "Impossible de sauvegarder la configuration dans '" + propertiesFileName + "' !");
		}
		
	}
}
