package com.trackerboik.util;

import java.util.List;

import model.trackerboik.dao.ActionDAO;
import model.trackerboik.dao.GeneralDBOperationsDAO;
import model.trackerboik.dao.hsqldb.ActionHSQL;
import model.trackerboik.dao.hsqldb.BoardHSQL;
import model.trackerboik.dao.hsqldb.HandBoardHSQL;
import model.trackerboik.dao.hsqldb.HandHSQL;
import model.trackerboik.dao.hsqldb.HandPLayerHSQL;
import model.trackerboik.dao.hsqldb.PlayerHSQL;
import model.trackerboik.dao.hsqldb.SessionHSQL;

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
	 */
	public static GeneralDBOperationsDAO getDBObjectSwitchName(String tbName) {
		if(tbName == null) return null;
		
		tbName = tbName.toLowerCase();
		switch (tbName) {
		case ActionHSQL.TABLE_NAME:
			return new ActionHSQL();	
		case BoardHSQL.TABLE_NAME:
			return new BoardHSQL();
		case HandBoardHSQL.TABLE_NAME:
			return new HandBoardHSQL();
		case HandHSQL.TABLE_NAME:
			return new HandHSQL();
		case HandPLayerHSQL.TABLE_NAME:
			return new HandPLayerHSQL();
		case PlayerHSQL.TABLE_NAME:
			return new PlayerHSQL();
		case SessionHSQL.TABLE_NAME:
			return new SessionHSQL();
		default :
			return null;
		}
	}
}
