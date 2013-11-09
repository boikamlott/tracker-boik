package controller.trackerboik.main;

import com.trackerboik.exception.TBException;

import model.trackerboik.dao.BoardDAO;
import model.trackerboik.dao.hsqldb.GeneralHSQLDBOperations;

public class BoardHSQLDB extends GeneralHSQLDBOperations implements BoardDAO {

	public void createTable() throws TBException {
		String rq = "CREATE TABLE board (";
		rq += "board_id varchar(10) PRIMARY KEY,";
		rq += "flop_1 varchar(2) NOT NULL,";
		rq += "flop_2 varchar(2) NOT NULL,";
		rq += "flop_3 varchar(2) NOT NULL,";
		rq += "turn varchar(2),";
		rq += "river varchar(2))";
		
		executeSQLUpdate(rq);
	}
}
