package model.trackerboik.dao;

import com.trackerboik.exception.TBException;

public interface GeneralDBOperationsDAO {
	
	public void eraseTableContent(String tableName) throws TBException;

}
