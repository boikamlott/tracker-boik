package model.trackerboik.dao;

import java.util.List;

import com.trackerboik.exception.TBException;

public interface GeneralDBOperationsDAO {
	
	/**
	 * Delete all table content
	 * @param tableName the table to delete all content
	 * @throws TBException
	 */
	public void eraseTableContent(String tableName) throws TBException;
	
	/**
	 * Drop the table
	 * @param tableName na me of the table to drop
	 * @throws TBException
	 */
	public void dropTable(String tableName) throws TBException;
	
	/**
	 * Return all PUBLIC table name of the database
	 * @return All tables name
	 * @throws TBException
	 */
	public List<String> getTableNames() throws TBException;
	
	/**
	 * Create table
	 * @throws TBException
	 */
	public void createTable() throws TBException;

}
