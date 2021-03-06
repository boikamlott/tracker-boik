package com.trackerboik.util;

import model.trackerboik.dao.GeneralDBOperationsDAO;
import model.trackerboik.dao.sql.ActionSQL;
import model.trackerboik.dao.sql.BoardSQL;
import model.trackerboik.dao.sql.HandBoardSQL;
import model.trackerboik.dao.sql.HandPLayerSQL;
import model.trackerboik.dao.sql.HandSQL;
import model.trackerboik.dao.sql.PlayerSQL;
import model.trackerboik.dao.sql.PlayerStatsSQL;
import model.trackerboik.dao.sql.SessionSQL;

import com.trackerboik.exception.TBException;


public class BDDUtil {

	public static String getEnumValuesToStringForBDD(Object[] elems) {
		String res = "";
		
		for(Object o : elems) {
			res += "'" + o.toString() + "',";
		}
		
		if(!res.isEmpty()) {
			res = res.substring(0, res.length() - 1);
		}
		
		return res;
	}

	/**
	 * Create the table in database for name given in param
	 * If the refers to an unknow table, raise exception
	 * @param tbName
	 * @throws TBException
	 */
	public static void createTableSwitchName(String tbName) throws TBException {
		GeneralDBOperationsDAO db = getDBObjectSwitchName(tbName);
		if(db == null) {
			throw new TBException("Unknow table name '" + tbName + "'");
		} else {
			db.createTable();
		}
	}

	/**
	 * Get the DB object refers to the table name given in parameter
	 * Return null if table name unknow
	 * @param tbName
	 * @return
	 * @throws TBException 
	 */
	public static GeneralDBOperationsDAO getDBObjectSwitchName(String tbName) throws TBException {
		if(tbName == null) return null;
		
		tbName = tbName.toLowerCase();
		switch (tbName) {
		case ActionSQL.TABLE_NAME:
			return new ActionSQL();	
		case BoardSQL.TABLE_NAME:
			return new BoardSQL();
		case HandBoardSQL.TABLE_NAME:
			return new HandBoardSQL();
		case HandSQL.TABLE_NAME:
			return new HandSQL();
		case HandPLayerSQL.TABLE_NAME:
			return new HandPLayerSQL();
		case PlayerSQL.TABLE_NAME:
			return new PlayerSQL();
		case SessionSQL.TABLE_NAME:
			return new SessionSQL();
		case PlayerStatsSQL.TABLE_NAME:
			return new PlayerStatsSQL();
		default :
			return null;
		}
	}
}
