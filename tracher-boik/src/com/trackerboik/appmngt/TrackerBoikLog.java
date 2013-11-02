package com.trackerboik.appmngt;

import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Implements singleton pattern
 * Loggs messages in application files
 * @author Gaetan
 *
 */
public class TrackerBoikLog {

	private static TrackerBoikLog instance;
	private Logger logger;
	
	public static TrackerBoikLog getInstance() {
		if(instance == null) {
			instance = new TrackerBoikLog();
		}
		
		return instance;
	}
	
	private TrackerBoikLog() {
		try {
			logger = Logger.getLogger("trackerboik-logger");
			logger.setLevel(Level.ALL);
			FileHandler fh = new FileHandler("logs.txt");
			fh.setFormatter(new SimpleFormatter());
			
			logger.addHandler(fh);
		} catch (Exception e) {
			System.err.println("Impossible to get Logger !");
		}
	}

	public void log(Level level, String msg) {
		if(logger != null) {
			logger.log(level, msg);
		}
		
	}
}
