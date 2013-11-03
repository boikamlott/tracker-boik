package com.trackerboik.exception;

import java.util.logging.Level;

import com.trackerboik.appmngt.TrackerBoikLog;

public class TBException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TBException(String msg) {
		super(msg);
		TrackerBoikLog.getInstance().log(Level.SEVERE, msg);
	}
}
