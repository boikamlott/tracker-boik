package model.trackerboik.dao;

import java.util.List;

import com.trackerboik.exception.TBException;

public interface GeneralDBOperationsDAO {
	
	public void eraseTableContent(String tableName) throws TBException;
	
	public List<String> getTableNames() throws TBException;

}
