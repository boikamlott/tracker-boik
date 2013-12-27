package com.trackerboik.util;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AppUtil {

	public static final String ATOMIC_DATA_FOLDER = "data.folder";
	public static final String CURRENCY = "€";
	public static final String TOURNAMENT_FILE_ID = "T";
	public static final CharSequence ARGENT_FICTIF = "Argent fictif";
	public static final String POKERSTARS = "PokerStars";
	public static final String POKERSTARS_ZOOM = "PokerStars Zoom Hand";
	public static final Integer MAX_PLAYERS = 9;
	public static final Integer NB_PLAYER_6_MAX = 6;
	public static final Integer NB_PLAYER_FULL_RING = 9;
	
	/**
	 * Return associated calendar
	 * PRE: string format: yyyy/MM/dd hh:mm:ss
	 * @param trim
	 * @return Associated calendar or null if error
	 */
	public static Calendar parseCalendar(String date) {
		if(date == null || date.isEmpty()) {
			return null;
		}
		
		return parseCalendar(date, "yyyy/MM/dd HH:mm:ss");
	}

	public static Calendar parseCalendar(String date, String format) {
		Calendar calendar = Calendar.getInstance();
		calendar = resetCalendarTime(calendar);
		try {
			calendar.setTime(parseDate(date, format));
			return calendar;
		} catch (RuntimeException e) {
			return null;
		}
	}
	
	public static Calendar resetCalendarTime(Calendar calendar){
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar;
	}
	
	public static Date parseDate(String date, String format) {
		Date result = null;
		try {
			SimpleDateFormat formatter = new SimpleDateFormat(format);
			result = formatter.parse(date);
			return result;
		} catch (ParseException e) {
			return null;
		}
	}

	/**
	 * Create a correct formmated file path with all elems of array given in parameter
	 * @param strings
	 * @return
	 */
	public static String createFilePath(String[] fpathItems) {
		String fpath = "";
		for(String s : fpathItems) {
			fpath += fpath.endsWith(File.separator) ? s : File.separator + s;
		}
		
		return fpath;
	}
}


