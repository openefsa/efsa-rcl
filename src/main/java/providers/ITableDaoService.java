package providers;

import table_skeleton.TableRow;
import table_skeleton.TableRowList;
import xlsx_reader.TableSchema;

public interface ITableDaoService {

	/**
	 * Add a row into the database
	 * @param row
	 * @return
	 */
	public int add(TableRow row);
	
	/**
	 * Update a row in the database
	 * @param row
	 * @return
	 */
	public boolean update(TableRow row);
	
	/**
	 * Get all the records of a table
	 * @param schema
	 * @return
	 */
	public TableRowList getAll(TableSchema schema);
	
	/**
	 * Get a row by its id in the chosen table
	 * @param schema
	 * @return
	 */
	public TableRow getById(TableSchema schema, int id);
	
	/**
	 * Get rows by parent
	 * @param schema
	 * @param parentTable
	 * @param parentId
	 * @param solveFormulas
	 * @return
	 */
	public TableRowList getByParentId(TableSchema schema, String parentTable, int parentId, boolean solveFormulas);

	/**
	 * Get rows by parent
	 * @param schema
	 * @param parentTable
	 * @param parentId
	 * @param solveFormulas
	 * @param order
	 * @return
	 */
	public TableRowList getByParentId(TableSchema schema, String parentTable, int parentId, boolean solveFormulas, String order);
	
	/**
	 * Delete all the records with the same parent id in the selected schema.
	 * @param schema
	 * @param parentTable
	 * @param parentId
	 * @return
	 */
	public boolean deleteByParentId(TableSchema schema, String parentTable, int parentId);
	
	/**
	 * Get rows by string field value
	 * @param schema
	 * @param fieldName
	 * @param value
	 * @return
	 */
	public TableRowList getByStringField(TableSchema schema, String fieldName, String value);
	
	/**
	 * Delete all the rows
	 * @param list
	 * @return
	 */
	public boolean delete(TableRowList list);
	
	/**
	 * delete a row
	 * @param schema
	 * @param rowId
	 * @return
	 */
	public boolean delete(TableSchema schema, int rowId);
	
	/**
	 * Delete all the records in the schema which match the string field
	 * @param schema
	 * @param fieldName
	 * @param value
	 * @return
	 */
	public boolean deleteByStringField(TableSchema schema, String fieldName, String value);

	boolean delete(TableRow var1);
}
