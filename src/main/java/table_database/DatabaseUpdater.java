package table_database;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import table_skeleton.TableColumn;
import xlsx_reader.TableSchema;
import xlsx_reader.TableSchemaList;

public class DatabaseUpdater {

	private static final Logger LOGGER = LogManager.getLogger(DatabaseUpdater.class);

	private IDatabaseBuilder dbBuilder;

	public DatabaseUpdater(IDatabaseBuilder dbBuilder) {
		this.dbBuilder = dbBuilder;
	}

	/**
	 * Update a database from the old schema to the new schema.
	 * 
	 * @param oldSchema
	 * @param newSchema
	 * @throws IOException
	 * @throws SQLException
	 */
	public void update(File oldSchema, File newSchema) throws IOException, SQLException {

		LOGGER.info("Updating database...");

		TableSchemaList newList = TableSchemaList.getAll(newSchema.getAbsolutePath());
		TableSchemaList oldList = TableSchemaList.getAll(oldSchema.getAbsolutePath());

		// for each table in the new configuration
		for (TableSchema newTable : newList) {

			// if the table is already contained in the old list
			if (oldList.contains(newTable)) {

				// get the old table version
				TableSchema oldTable = oldList.getSchemaByName(newTable.getSheetName());

				// update the table to the new one
				LOGGER.info("Updating table " + oldTable.getSheetName());
				updateTable(oldTable, newTable);
			} else {
				// if not, just create the new table
				LOGGER.info("Creating new table " + newTable.getSheetName());
				addTable(newTable);
			}
		}

		LOGGER.info("Database updated!");
	}

	/**
	 * Update a table from old to new
	 * 
	 * @param oldTable
	 * @param newTable
	 * @throws IOException
	 * @throws SQLException
	 */
	private void updateTable(TableSchema oldTable, TableSchema newTable) throws IOException, SQLException {
		addAndUpdateColumns(oldTable, newTable);
		removeOldConstraints(oldTable, newTable);
	}

	/**
	 * Add new columns if needed and update the old ones if they were updated in the
	 * new table
	 * 
	 * @param oldTable
	 * @param newTable
	 * @throws IOException
	 * @throws SQLException
	 */
	private void addAndUpdateColumns(TableSchema oldTable, TableSchema newTable) throws IOException, SQLException {
		
		// for each column defined in the table
		for (TableColumn newCol : newTable) {

			// if the old table contained the newCol we need to update
			if (oldTable.contains(newCol)) {
				LOGGER.info("Updating column " + newCol);
				TableColumn oldCol = oldTable.getById(newCol.getId());
				updateColumn(oldTable, oldCol, newCol);
			} else {
				LOGGER.info("Add new column " + newCol);
				addColumn(newTable, newCol);
			}
		}
	}

	/**
	 * Remove constraints that are not anymore in the new table
	 * 
	 * @param oldTable
	 * @param newTable
	 * @throws IOException
	 * @throws SQLException
	 */
	private void removeOldConstraints(TableSchema oldTable, TableSchema newTable) throws IOException, SQLException {

		// for each old column
		for (TableColumn oldCol : oldTable) {

			// if a column of the old table is not present
			// anymore in the new
			if (!newTable.contains(oldCol)) {

				// if it was a foreign key, we need to remove
				// the constraint in the database
				if (oldCol.isForeignKey()) {

					LOGGER.info("Removing foreign key constraint " + oldCol.getId());

					// delete foreign key constraint
					removeForeignKey(oldTable, oldCol);
				}
			}
		}
	}

	/**
	 * Add a new table in the db
	 * 
	 * @param table
	 * @throws SQLException
	 * @throws IOException
	 */
	private void addTable(TableSchema table) throws SQLException, IOException {
		dbBuilder.createTable(table);
	}

	/**
	 * Add a new column in the db
	 * 
	 * @param newTable
	 * @param newCol
	 * @throws IOException
	 * @throws SQLException
	 */
	private void addColumn(TableSchema newTable, TableColumn newCol) throws IOException, SQLException {
		dbBuilder.addColumnToTable(newTable, newCol);
	}

	/**
	 * Update a column from the old to the new
	 * 
	 * @param table
	 * @param oldCol
	 * @param newCol
	 * @throws IOException
	 * @throws SQLException
	 */
	private void updateColumn(TableSchema table, TableColumn oldCol, TableColumn newCol)
			throws IOException, SQLException {

		// if a foreign key was removed, remove it from the db
		if (oldCol.isForeignKey() && !newCol.isForeignKey()) {
			LOGGER.info("Removing foreign key constraint " + oldCol.getId());
			removeForeignKey(table, oldCol);
		}

		// if a foreign key was added using an old field, add it to the db
		else if (!oldCol.isForeignKey() && newCol.isForeignKey()) {
			LOGGER.info("Converting an existing column to a foreign key is not supported: " + oldCol.getId());
			// NOT SUPPORTED, it can lead to errors due to
			// different data types (integers/strings casts)
		}
	}

	/**
	 * Remove a foreign key from the db
	 * 
	 * @param table
	 * @param fk
	 * @throws IOException
	 * @throws SQLException
	 */
	private void removeForeignKey(TableSchema table, TableColumn fk) throws IOException, SQLException {
		// delete foreign key constraint
		dbBuilder.removeForeignKey(table, fk);
	}
}
