package er.extensions.migration;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Types;

import com.webobjects.eoaccess.EOAttribute;
import com.webobjects.eoaccess.EOEntity;
import com.webobjects.eoaccess.EOJoin;
import com.webobjects.eoaccess.EOModel;
import com.webobjects.eoaccess.EORelationship;
import com.webobjects.eoaccess.EOSQLExpression;
import com.webobjects.eoaccess.EOSchemaGeneration;
import com.webobjects.eoaccess.EOSchemaSynchronization;
import com.webobjects.eocontrol.EOKeyValueQualifier;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSData;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSTimestamp;

import er.extensions.jdbc.ERXJDBCUtilities;
import er.extensions.jdbc.ERXSQLHelper;

/**
 * ERXMigrationTable provides table-level migration API's.  To obtain a table, you
 * should call ERXMigrationDatabase.existingTableNamed or ERXMigrationDatabase.newTableNamed.
 * Note: The .newXxxColumn API's cannot reference prototypes for the same reason that migrations
 * in general cannot reference EOModels.
 * 
 * @author mschrag
 */
public class ERXMigrationTable {
	private ERXMigrationDatabase _database;
	private NSMutableArray<ERXMigrationColumn> _columns;
	private NSMutableArray<ERXMigrationIndex> _indexes;
	private String _name;
	private boolean _new;

	/**
	 * Constructs an ERXMigrationTable.
	 * 
	 * @param database the database this table is within
	 * @param name the name of this table
	 */
	protected ERXMigrationTable(ERXMigrationDatabase database, String name) {
		_database = database;
		_columns = new NSMutableArray<ERXMigrationColumn>();
		_indexes = new NSMutableArray<ERXMigrationIndex>();
		_name = name;
		_new = true;
	}

	/**
	 * Returns the ERXMigrationDatabase parent of this table.
	 * 
	 * @return the ERXMigrationDatabase parent of this table
	 */
	public ERXMigrationDatabase database() {
		return _database;
	}
	
	/**
	 * Returns the configured default languages for this migration.
	 */
	public NSArray<String> languages() {
		//TODO AK have a local override
		return database().languages();
	}

	/**
	 * Sets the name of this table.  This does not perform a table rename operation.
	 * 
	 * @param name the name of this table
	 */
	public void _setName(String name) {
		_name = name;
	}

	/**
	 * Returns the name of this table.
	 * 
	 * @return the name of this table
	 */
	public String name() {
		return _name;
	}
	
	/**
	 * Returns true if this table has not yet been created in the database.
	 * 
	 * @return if this table has not yet been created in the database
	 */
	public boolean isNew() {
		return _new;
	}
	
	/**
	 * Sets whether or not this table has been created in the database.
	 * 
	 * @param isNew if true, the table has been created
	 */
	public void _setNew(boolean isNew) {
		_new = isNew;
	}

	/**
	 * Returns an EOEntity representing this table with no
	 * EOAttributes in it.
	 *  
	 * @return a shell of an EOEntity for this table
	 */
	public EOEntity _blankEntity() {
		EOModel newModel = _database._blankModel();
		EOEntity newEntity = new EOEntity();
		newEntity.setExternalName(_name);
		newEntity.setName(_name);
		newModel.addEntity(newEntity);
		return newEntity;
	}

	/**
	 * Returns an EOEntity representing this table that
	 * contains all of the EOAttributes for any 
	 * ERXMigrationColumn that has been created or
	 * retrieved from this table.
	 * 
	 * @return an EOAttributeful EOEntity for this table 
	 */
	public EOEntity _newEntity() {
		EOEntity entity = _blankEntity();
		NSMutableArray<EOAttribute> primaryKeyAttributes = new NSMutableArray<EOAttribute>();
		for (ERXMigrationColumn column : _columns) {
			EOAttribute attribute = column._newAttribute(entity);
			if (column.isPrimaryKey()) {
				primaryKeyAttributes.addObject(attribute);
			}
		}
		entity.setPrimaryKeyAttributes(primaryKeyAttributes);
		return entity;
	}

	/**
	 * Returns the ERMigrationColumn for the column with the given name.  If 
	 * no column has already been created via a newColumn call, then this
	 * will simply return a shell ERXMigrationColumn that should be sufficient
	 * for performing drop, rename, and other reference operations.
	 * 
	 * @param name the name of the column to retrieve
	 * @return the ERXMigrationColumn for the column name
	 */
	@SuppressWarnings("unchecked")
	public ERXMigrationColumn existingColumnNamed(String name) {
		NSArray<ERXMigrationColumn> existingColumns = EOQualifier.filteredArrayWithQualifier(_columns, new EOKeyValueQualifier("name", EOQualifier.QualifierOperatorCaseInsensitiveLike, name));
		ERXMigrationColumn column;
		if (existingColumns.count() == 0) {
			if (_new) {
				throw new IllegalStateException("You requested the column named '" + name + "' in the table '" + _name + "', but that column hasn't been created yet.");
			}
			try {
				column = _newColumn(name, 0, 0, 0, 0, false, null, null, false);
			}
			catch (SQLException e) {
				throw new IllegalStateException("This should never have executed a database operation.", e);
			}
			column._setNew(false);
		}
		else {
			column = existingColumns.objectAtIndex(0);
		}
		return column;
	}

	/**
	 * Returns a simple single-attribute-mapping EORelationship between two columns.  This
	 * is called by the foreign key generator.
	 *  
	 * @param sourceColumn the source attribute of the relationship 
	 * @param destinationColumn the destination attribute of the relationship
	 * @return the EORelationship that joins the two given columns
	 */
	public EORelationship _newRelationship(ERXMigrationColumn sourceColumn, ERXMigrationColumn destinationColumn) {
		EOAttribute sourceAttribute = sourceColumn._newAttribute();
		EOEntity entity = sourceAttribute.entity();

		EOAttribute destinationAttribute = destinationColumn._newAttribute();
		EOEntity destinationEntity = destinationAttribute.entity();
		destinationEntity.setPrimaryKeyAttributes(new NSArray<EOAttribute>(destinationAttribute));

		EORelationship relationship = new EORelationship();
		relationship.setName(sourceAttribute.name() + "_" + destinationAttribute.name());
		relationship.setEntity(entity);
		
		EOJoin join = new EOJoin(sourceAttribute, destinationAttribute);
		relationship.addJoin(join);
		
		return relationship;
	}

	/**
	 * Returns a new ERXMigrationColumn with the given attributes.  This method is the
	 * most general-purpose of the .newXxx methods.  Calling this method will not
	 * actually create the column, rather it will only return a metadata wrapper of
	 * the attributes you specify.  Call .create() on the resulting column object
	 * to create the column (or create several columns and then call .create() on
	 * this table to create an entire table).
	 *  
	 * @param name the name of the column to create
	 * @param jdbcType the JDBC type of the column (see java.sql.Types)
	 * @param width the width of the column (or 0 for unspecified)
	 * @param precision the precision of the column (or 0 for unspecified)
	 * @param scale the scale of the column (or 0 for unspecified)
	 * @param allowsNull if true, the column will allow null values
	 * @param defaultValue the default value for the column
	 * @return the new ERXMigrationColumn
	 * @throws SQLException if the column cannot be created 
	 */
	public ERXMigrationColumn _newColumn(String name, int jdbcType, int width, int precision, int scale, boolean allowsNull, String overrideValueType, Object defaultValue, boolean autocreate) throws SQLException {
		ERXMigrationColumn newColumn = new ERXMigrationColumn(this, name, jdbcType, width, precision, scale, allowsNull, overrideValueType, defaultValue);
		_columns.addObject(newColumn);
		if (autocreate) {
			newColumn.create();
		}
		return newColumn;
	}

	/**
	 * Returns a new ERXMigrationColumn with the given attributes.  This method is the
	 * most general-purpose of the .newXxx methods.  If this table already exists, 
	 * calling the .newXxxColumn methods will immediate execute the SQL to add the
	 * columns to the table.  If this table is new, however, calling .newXxxColumn
	 * will only return a metadata object, and you must call .create() on
	 * the table.
	 *  
	 * @param name the name of the column to create
	 * @param jdbcType the JDBC type of the column (see java.sql.Types)
	 * @param width the width of the column (or 0 for unspecified)
	 * @param precision the precision of the column (or 0 for unspecified)
	 * @param scale the scale of the column (or 0 for unspecified)
	 * @param allowsNull if true, the column will allow null values
	 * @param defaultValue the default value for the column
	 * @return the new ERXMigrationColumn
	 * @throws SQLException if the column cannot be created 
	 */
	public ERXMigrationColumn newColumn(String name, int jdbcType, int width, int precision, int scale, boolean allowsNull, String overrideValueType, Object defaultValue) throws SQLException {
		return _newColumn(name, jdbcType, width, precision, scale, allowsNull, overrideValueType, defaultValue, !_new);
	}

	/**
	 * Returns a new ERXMigrationColumn with the given attributes.  This method is the
	 * most general-purpose of the .newXxx methods.  If this table already exists, 
	 * calling the .newXxxColumn methods will immediate execute the SQL to add the
	 * columns to the table.  If this table is new, however, calling .newXxxColumn
	 * will only return a metadata object, and you must call .create() on
	 * the table.
	 *  
	 * @param name the name of the column to create
	 * @param jdbcType the JDBC type of the column (see java.sql.Types)
	 * @param width the width of the column (or 0 for unspecified)
	 * @param precision the precision of the column (or 0 for unspecified)
	 * @param scale the scale of the column (or 0 for unspecified)
	 * @param allowsNull if true, the column will allow null values
	 * @return the new ERXMigrationColumn
	 * @throws SQLException if the column cannot be created 
	 */
	public ERXMigrationColumn newColumn(String name, int jdbcType, int width, int precision, int scale, boolean allowsNull, String overrideValueType) throws SQLException {
		return _newColumn(name, jdbcType, width, precision, scale, allowsNull, overrideValueType, null, !_new);
	}

	/**
	 * Returns a new String column (VARCHAR).  See newColumn(..) for the full docs.
	 * 
	 * @param name the name of the column
	 * @param width the max width of the varchar
	 * @param allowsNull if true, the column will allow null values
	 * @return the new ERXMigrationColumn
	 * @throws SQLException if the column cannot be created 
	 */
	public ERXMigrationColumn newStringColumn(String name, int width, boolean allowsNull) throws SQLException {
		return newColumn(name, Types.VARCHAR, width, 0, 0, allowsNull, null);
	}

	/**
	 * Returns a new String column (VARCHAR).  See newColumn(..) for the full docs.
	 * 
	 * @param name the name of the column
	 * @param width the max width of the varchar
	 * @param allowsNull if true, the column will allow null values
	 * @param defaultValue the default value of this column
	 * @return the new ERXMigrationColumn
	 * @throws SQLException if the column cannot be created 
	 */
	public ERXMigrationColumn newStringColumn(String name, int width, boolean allowsNull, String defaultValue) throws SQLException {
		return newColumn(name, Types.VARCHAR, width, 0, 0, allowsNull, null, defaultValue);
	}

	/**
	 * Returns a new localized String column (VARCHAR).  See newColumn(..) for the full docs.
	 * 
	 * @param name the name of the column
	 * @param width the max width of the varchar
	 * @param allowsNull if true, the column will allow null values
	 * @return the new ERXMigrationColumn
	 * @throws SQLException if the column cannot be created 
	 */
	public NSArray<ERXMigrationColumn> newLocalizedStringColumns(String name, int width, boolean allowsNull) throws SQLException {
		NSMutableArray<ERXMigrationColumn> result = new NSMutableArray<ERXMigrationColumn>();
		for (String language : languages()) {
			ERXMigrationColumn column = newColumn(localizedColumnName(name, language), Types.VARCHAR, width, 0, 0, allowsNull, null);
			result.addObject(column);
		}
		return result;
	}

	private String localizedColumnName(String name, String language) {
		return name + "_" + language;
	}

	/**
	 * Returns a new localized String column (VARCHAR).  See newColumn(..) for the full docs.
	 * 
	 * @param name the name of the column
	 * @param width the max width of the varchar
	 * @param allowsNull if true, the column will allow null values
	 * @param defaultValue the default value of this column
	 * @return the new ERXMigrationColumn
	 * @throws SQLException if the column cannot be created 
	 */
	public  NSArray<ERXMigrationColumn> newLocalizedStringColumns(String name, int width, boolean allowsNull, String defaultValue) throws SQLException {
		NSMutableArray<ERXMigrationColumn> result = new NSMutableArray<ERXMigrationColumn>();
		for (String language : languages()) {
			ERXMigrationColumn column = newColumn(localizedColumnName(name, language), Types.VARCHAR, width, 0, 0, allowsNull, null, defaultValue);
			result.addObject(column);
		}
		return result;
	}

	/**
	 * Returns a new localized string blob column.  See newColumn(..) for the full docs.
	 * 
	 * @param name the name of the column
	 * @param allowsNull if true, the column will allow null values
	 * @return the new ERXMigrationColumn
	 * @throws SQLException if the column cannot be created 
	 */
	public NSArray<ERXMigrationColumn> newLocalizedClobColumns(String name, boolean allowsNull) throws SQLException {
		NSMutableArray<ERXMigrationColumn> result = new NSMutableArray<ERXMigrationColumn>();
		for (String language : languages()) {
			ERXMigrationColumn column = newColumn(localizedColumnName(name, language), Types.CLOB, 0, 0, 0, allowsNull, null);
			result.addObject(column);
		}
		return result;
	}

	/**
	 * Returns a new integer column.  See newColumn(..) for the full docs.
	 * 
	 * @param name the name of the column
	 * @param allowsNull if true, the column will allow null values
	 * @return the new ERXMigrationColumn
	 * @throws SQLException if the column cannot be created 
	 */
	public ERXMigrationColumn newIntegerColumn(String name, boolean allowsNull) throws SQLException {
		return newColumn(name, Types.INTEGER, 0, 0, 0, allowsNull, null);
	}

	/**
	 * Returns a new integer column.  See newColumn(..) for the full docs.
	 * 
	 * @param name the name of the column
	 * @param allowsNull if true, the column will allow null values
	 * @param defaultValue the default value of this column
	 * @return the new ERXMigrationColumn
	 * @throws SQLException if the column cannot be created 
	 */
	public ERXMigrationColumn newIntegerColumn(String name, boolean allowsNull, Integer defaultValue) throws SQLException {
		return newColumn(name, Types.INTEGER, 0, 0, 0, allowsNull, null, defaultValue);
	}

	/**
	 * Returns a new integer column.  See newColumn(..) for the full docs.
	 * 
	 * @param name the name of the column
	 * @param scale the scale of the integer
	 * @param allowsNull if true, the column will allow null values
	 * @return the new ERXMigrationColumn
	 * @throws SQLException if the column cannot be created 
	 */
	public ERXMigrationColumn newIntegerColumn(String name, int scale, boolean allowsNull) throws SQLException {
		return newColumn(name, Types.INTEGER, 0, 0, scale, allowsNull, null);
	}

	/**
	 * Returns a new integer column.  See newColumn(..) for the full docs.
	 * 
	 * @param name the name of the column
	 * @param scale the scale of the integer
	 * @param allowsNull if true, the column will allow null values
	 * @param defaultValue the default value of this column
	 * @return the new ERXMigrationColumn
	 * @throws SQLException if the column cannot be created 
	 */
	public ERXMigrationColumn newIntegerColumn(String name, int scale, boolean allowsNull, Integer defaultValue) throws SQLException {
		return newColumn(name, Types.INTEGER, 0, 0, scale, allowsNull, null, defaultValue);
	}

	/**
	 * Returns a new integer column.  See newColumn(..) for the full docs.
	 * 
	 * @param name the name of the column
	 * @param scale the scale of the integer
	 * @param precision the precision of the integer
	 * @param allowsNull if true, the column will allow null values
	 * @return the new ERXMigrationColumn
	 * @throws SQLException if the column cannot be created 
	 */
	public ERXMigrationColumn newIntegerColumn(String name, int scale, int precision, boolean allowsNull) throws SQLException {
		return newColumn(name, Types.INTEGER, 0, scale, precision, allowsNull, null);
	}

	/**
	 * Returns a new integer column.  See newColumn(..) for the full docs.
	 * 
	 * @param name the name of the column
	 * @param scale the scale of the integer
	 * @param precision the precision of the integer
	 * @param allowsNull if true, the column will allow null values
	 * @param defaultValue the default value of this column
	 * @return the new ERXMigrationColumn
	 * @throws SQLException if the column cannot be created 
	 */
	public ERXMigrationColumn newIntegerColumn(String name, int scale, int precision, boolean allowsNull, Object defaultValue) throws SQLException {
		return newColumn(name, Types.INTEGER, 0, scale, precision, allowsNull, null, defaultValue);
	}
	
	/**
	 * Returns a new long column.  See newColumn(..) for the full docs.
	 * 
	 * @param name the name of the column
	 * @param allowsNull if true, the column will allow null values
	 * @return the new ERXMigrationColumn
	 * @throws SQLException if the column cannot be created 
	 */
	public ERXMigrationColumn newBigIntegerColumn(String name, boolean allowsNull) throws SQLException {
		return newColumn(name, Types.BIGINT, 0, 0, 0, allowsNull, null);
	}

	/**
	 * Returns a new long column.  See newColumn(..) for the full docs.
	 * 
	 * @param name the name of the column
	 * @param allowsNull if true, the column will allow null values
	 * @param defaultValue the default value of this column
	 * @return the new ERXMigrationColumn
	 * @throws SQLException if the column cannot be created 
	 */
	public ERXMigrationColumn newBigIntegerColumn(String name, boolean allowsNull, Long defaultValue) throws SQLException {
		return newColumn(name, Types.BIGINT, 0, 0, 0, allowsNull, null, defaultValue);
	}

	/**
	 * Returns a new float column.  See newColumn(..) for the full docs.
	 * 
	 * @param name the name of the column
	 * @param scale the scale of the float
	 * @param precision the precision of the float
	 * @param allowsNull if true, the column will allow null values
	 * @return the new ERXMigrationColumn
	 * @throws SQLException if the column cannot be created 
	 */
	public ERXMigrationColumn newFloatColumn(String name, int precision, int scale, boolean allowsNull) throws SQLException {
		return newColumn(name, Types.FLOAT, 0, precision, scale, allowsNull, null);
	}

	/**
	 * Returns a new float column.  See newColumn(..) for the full docs.
	 * 
	 * @param name the name of the column
	 * @param scale the scale of the float
	 * @param precision the precision of the float
	 * @param allowsNull if true, the column will allow null values
	 * @param defaultValue the default value of this column
	 * @return the new ERXMigrationColumn
	 * @throws SQLException if the column cannot be created 
	 */
	public ERXMigrationColumn newFloatColumn(String name, int precision, int scale, boolean allowsNull, Float defaultValue) throws SQLException {
		return newColumn(name, Types.FLOAT, 0, precision, scale, allowsNull, null, defaultValue);
	}

	/**
	 * Returns a new BigDecimal column.  See newColumn(..) for the full docs.
	 * 
	 * @param name the name of the column
	 * @param scale the scale of the BigDecimal
	 * @param precision the precision of the BigDecimal
	 * @param allowsNull if true, the column will allow null values
	 * @return the new ERXMigrationColumn
	 * @throws SQLException if the column cannot be created 
	 */
	public ERXMigrationColumn newBigDecimalColumn(String name, int precision, int scale, boolean allowsNull) throws SQLException {
		return newColumn(name, Types.DECIMAL, 0, precision, scale, allowsNull, null);
	}

	/**
	 * Returns a new BigDecimal column.  See newColumn(..) for the full docs.
	 * 
	 * @param name the name of the column
	 * @param scale the scale of the BigDecimal
	 * @param precision the precision of the BigDecimal
	 * @param allowsNull if true, the column will allow null values
	 * @param defaultValue the default value of this column
	 * @return the new ERXMigrationColumn
	 * @throws SQLException if the column cannot be created 
	 */
	public ERXMigrationColumn newBigDecimalColumn(String name, int precision, int scale, boolean allowsNull, BigDecimal defaultValue) throws SQLException {
		return newColumn(name, Types.DECIMAL, 0, precision, scale, allowsNull, null, defaultValue);
	}

	/**
	 * Returns a new varchar(5) boolean column.  See newColumn(..) for the full docs.
	 * 
	 * @param name the name of the column
	 * @param allowsNull if true, the column will allow null values
	 * @return the new ERXMigrationColumn
	 * @throws SQLException if the column cannot be created 
	 */
	public ERXMigrationColumn newBooleanColumn(String name, boolean allowsNull) throws SQLException {
		return newColumn(name, Types.VARCHAR, 5, 0, 0, allowsNull, null);
	}

	/**
	 * Returns a new varchar(5) boolean column.  See newColumn(..) for the full docs.
	 * 
	 * @param name the name of the column
	 * @param allowsNull if true, the column will allow null values
	 * @param defaultValue the default value of this column
	 * @return the new ERXMigrationColumn
	 * @throws SQLException if the column cannot be created 
	 */
	public ERXMigrationColumn newBooleanColumn(String name, boolean allowsNull, Boolean defaultValue) throws SQLException {
		return newColumn(name, Types.VARCHAR, 5, 0, 0, allowsNull, null, defaultValue == null ? null : defaultValue.toString());
	}

	/**
	 * Returns a new integer boolean column.  See newColumn(..) for the full docs.
	 * 
	 * @param name the name of the column
	 * @param allowsNull if true, the column will allow null values
	 * @return the new ERXMigrationColumn
	 * @throws SQLException if the column cannot be created 
	 */
	public ERXMigrationColumn newIntBooleanColumn(String name, boolean allowsNull) throws SQLException {
		return newColumn(name, Types.INTEGER, 0, 0, 0, allowsNull, null);
	}

	/**
	 * Returns a new integer boolean column.  See newColumn(..) for the full docs.
	 * 
	 * @param name the name of the column
	 * @param allowsNull if true, the column will allow null values
	 * @param defaultValue the default value of this column
	 * @return the new ERXMigrationColumn
	 * @throws SQLException if the column cannot be created 
	 */
	public ERXMigrationColumn newIntBooleanColumn(String name, boolean allowsNull, Boolean defaultValue) throws SQLException {
		return newColumn(name, Types.INTEGER, 0, 0, 0, allowsNull, null, defaultValue);
	}

	/**
	 * Returns a new flag boolean column.  See newColumn(..) for the full docs.
	 * This might or might not work with your database, it's only tested with PostgreSQL.
	 * 
	 * @param name the name of the column
	 * @param allowsNull if true, the column will allow null values
	 * @return the new ERXMigrationColumn
	 * @throws SQLException if the column cannot be created 
	 */
	public ERXMigrationColumn newFlagBooleanColumn(String name, boolean allowsNull) throws SQLException {
		return newColumn(name, Types.BOOLEAN, 0, 0, 0, allowsNull, null);
	}

	/**
	 * Returns a new string blob column.  See newColumn(..) for the full docs.
	 * 
	 * @param name the name of the column
	 * @param allowsNull if true, the column will allow null values
	 * @return the new ERXMigrationColumn
	 * @throws SQLException if the column cannot be created 
	 */
	public ERXMigrationColumn newClobColumn(String name, boolean allowsNull) throws SQLException {
		return newColumn(name, Types.CLOB, 0, 0, 0, allowsNull, null);
	}

	/**
	 * Returns a new Blob column.  See newColumn(..) for the full docs.
	 * 
	 * @param name the name of the column
	 * @param allowsNull if true, the column will allow null values
	 * @return the new ERXMigrationColumn
	 * @throws SQLException if the column cannot be created 
	 */
	public ERXMigrationColumn newBlobColumn(String name, boolean allowsNull) throws SQLException {
		return newColumn(name, Types.BLOB, 0, 0, 0, allowsNull, null);
	}

	/**
	 * Returns a new Blob column.  See newColumn(..) for the full docs.
	 * 
	 * @param name the name of the column
	 * @param width the width of the blob
	 * @param allowsNull if true, the column will allow null values
	 * @return the new ERXMigrationColumn
	 * @throws SQLException if the column cannot be created 
	 */
	public ERXMigrationColumn newBlobColumn(String name, int width, boolean allowsNull) throws SQLException {
		return newColumn(name, Types.BLOB, width, 0, 0, allowsNull, null);
	}

	/**
	 * Returns a new Blob column.  See newColumn(..) for the full docs.
	 * 
	 * @param name the name of the column
	 * @param width the width of the blob
	 * @param allowsNull if true, the column will allow null values
	 * @param defaultValue the default value of this column
	 * @return the new ERXMigrationColumn
	 * @throws SQLException if the column cannot be created 
	 */
	public ERXMigrationColumn newBlobColumn(String name, int width, boolean allowsNull, NSData defaultValue) throws SQLException {
		return newColumn(name, Types.BLOB, width, 0, 0, allowsNull, null, defaultValue);
	}

	/**
	 * Returns a new timestamp column.  See newColumn(..) for the full docs.
	 * 
	 * @param name the name of the column
	 * @param allowsNull if true, the column will allow null values
	 * @return the new ERXMigrationColumn
	 * @throws SQLException if the column cannot be created 
	 */
	public ERXMigrationColumn newTimestampColumn(String name, boolean allowsNull) throws SQLException {
		return newColumn(name, Types.TIMESTAMP, 0, 0, 0, allowsNull, ERXMigrationColumn.NULL_VALUE_TYPE);
	}

	/**
	 * Returns a new timestamp column.  See newColumn(..) for the full docs.
	 * 
	 * @param name the name of the column
	 * @param allowsNull if true, the column will allow null values
	 * @param defaultValue the default value of this column
	 * @return the new ERXMigrationColumn
	 * @throws SQLException if the column cannot be created 
	 */
	public ERXMigrationColumn newTimestampColumn(String name, boolean allowsNull, NSTimestamp defaultValue) throws SQLException {
		return newColumn(name, Types.TIMESTAMP, 0, 0, 0, allowsNull, ERXMigrationColumn.NULL_VALUE_TYPE, defaultValue);
	}

	/**
	 * Callback method for ERXMigrationColumn to notify the table that
	 * it has been deleted.
	 * 
	 * @param column the column that has been deleted
	 */
	public void _columnDeleted(ERXMigrationColumn column) {
		_columns.removeObject(column);
	}

	/**
	 * Returns an array of EOSQLExpressions for creating this table and all of its ERXMigrationColumns.
	 * 
	 * @return an array of EOSQLExpressions for creating this table
	 */
	@SuppressWarnings("unchecked")
	public NSArray<EOSQLExpression> _createExpressions() {
		EOSchemaGeneration schemaGeneration = _database.synchronizationFactory();
		NSArray<EOSQLExpression> expressions = schemaGeneration.createTableStatementsForEntityGroup(new NSArray<EOEntity>(_newEntity()));
		ERXMigrationDatabase._ensureNotEmpty(expressions, "create table", true);
		return expressions;
	}

	/**
	 * Executes the SQL operations to create this table.
	 * 
	 * @throws SQLException if the creation fails
	 */
	public void create() throws SQLException {
		if (_new) {
			ERXJDBCUtilities.executeUpdateScript(_database.adaptorChannel(), ERXMigrationDatabase._stringsForExpressions(_createExpressions()));
			NSMutableArray<ERXMigrationColumn> primaryKeys = new NSMutableArray<ERXMigrationColumn>();
			for (ERXMigrationColumn column : _columns) {
				if (column.isPrimaryKey()) {
					primaryKeys.addObject(column);
				}
				column._setNew(false);
			}
			if (primaryKeys.count() > 0) {
				setPrimaryKey(true, primaryKeys.toArray(new ERXMigrationColumn[primaryKeys.count()]));
			}
			for (ERXMigrationColumn column : _columns) {
				ERXMigrationColumn foreignKeyDestination = column.foreignKeyDestination();
				if (foreignKeyDestination != null) {
					addForeignKey(true, column, foreignKeyDestination);
				}
			}
			for (ERXMigrationIndex index : _indexes) {
				addIndex(true, index);
			}
			_new = false;
		}
		else {
			ERXMigrationDatabase.log.warn("You called .create() on the table '" + _name + "', but it was already created.");
		}
	}

	/**
	 * Returns an array of EOSQLExpressions for dropping this table.
	 * 
	 * @return an array of EOSQLExpressions for dropping this table
	 */
	@SuppressWarnings("unchecked")
	public NSArray<EOSQLExpression> _dropExpressions() {
		EOSchemaGeneration schemaGeneration = _database.synchronizationFactory();
		NSArray<EOSQLExpression> expressions = schemaGeneration.dropTableStatementsForEntityGroup(new NSArray<EOEntity>(_blankEntity()));
		ERXMigrationDatabase._ensureNotEmpty(expressions, "drop table", true);
		return expressions;
	}

	/**
	 * Executes the SQL operations to drop this table.
	 * 
	 * @throws SQLException if the drop fails
	 */
	public void drop() throws SQLException {
		ERXJDBCUtilities.executeUpdateScript(_database.adaptorChannel(), ERXMigrationDatabase._stringsForExpressions(_dropExpressions()));
		_database._tableDropped(this);
	}

	/**
	 * Returns an array of EOSQLExpressions for renaming this table.
	 * 
	 * @return an array of EOSQLExpressions for renaming this table
	 */
	@SuppressWarnings("unchecked")
	public NSArray<EOSQLExpression> _renameToExpressions(String newName) {
		EOSchemaSynchronization schemaSynchronization = _database.synchronizationFactory();
		NSArray<EOSQLExpression> expressions = schemaSynchronization.statementsToRenameTableNamed(name(), newName, NSDictionary.EmptyDictionary);
		ERXMigrationDatabase._ensureNotEmpty(expressions, "rename table", true);
		return expressions;
	}

	/**
	 * Executes the SQL operations to rename this table.
	 * 
	 * @throws SQLException if the rename fails
	 */
	public void renameTo(String newName) throws SQLException {
		ERXJDBCUtilities.executeUpdateScript(_database.adaptorChannel(), ERXMigrationDatabase._stringsForExpressions(_renameToExpressions(newName)));
		_setName(newName);
	}

	/**
	 * Returns an array of EOSQLExpressions for setting the primary key constraint of this table
	 * 
	 * @return an array of EOSQLExpressions for setting the primary key constraint of this table
	 */
	@SuppressWarnings("unchecked")
	public NSArray<EOSQLExpression> _setPrimaryKeyExpressions(ERXMigrationColumn... columns) {
		EOSchemaGeneration schemaGeneration = _database.synchronizationFactory();
		EOEntity entity = columns[0].table()._blankEntity();
		NSMutableArray<EOAttribute> attributes = new NSMutableArray<EOAttribute>();
		for (ERXMigrationColumn column : columns) {
			EOAttribute attribute = column._newAttribute(entity);
			attributes.addObject(attribute);
		}
		entity.setPrimaryKeyAttributes(attributes);
		NSArray<EOSQLExpression> expressions = schemaGeneration.primaryKeyConstraintStatementsForEntityGroup(new NSArray<EOEntity>(entity));
		ERXMigrationDatabase._ensureNotEmpty(expressions, "add primary key", true);
		NSArray<EOSQLExpression> supportExpressions = schemaGeneration.primaryKeySupportStatementsForEntityGroup(new NSArray<EOEntity>(entity));
		return expressions.arrayByAddingObjectsFromArray(supportExpressions);
	}
	
	/**
	 * Executes the SQL operations to add this unique index.
	 * 
	 * @param columnName the name of the column to add a unique index on
	 * @throws SQLException if the constraint fails
	 */
	public void addUniqueIndex(String columnName) throws SQLException {
		addUniqueIndex(null, columnName);
	}

	/**
	 * Executes the SQL operations to add this unique index.
	 * 
	 * @param indexName the name of the index
	 * @param columnName the name of the column to add a unique index on
	 * @throws SQLException if the constraint fails
	 */
	public void addUniqueIndex(String indexName, String columnName) throws SQLException {
		addUniqueIndex(indexName, existingColumnNamed(columnName));
	}
	
	/**
	 * Executes the SQL operations to add this unique index.
	 * 
	 * @param indexName the name of the index
	 * @param columnName the name of the column to add a unique index on
	 * @throws SQLException if the constraint fails
	 */
	public void addUniqueIndex(String indexName, String columnName, int width) throws SQLException {
		addUniqueIndex(indexName, new ERXSQLHelper.ColumnIndex(columnName, width));
	}
	
	/**
	 * Executes the SQL operations to add a unique index.
	 * 
	 * @param indexName the name of the index
	 * @param columns the columns to add a unique index on
	 * @throws SQLException if the constraint fails
	 */
	public void addUniqueIndex(String indexName, ERXMigrationColumn... columns) throws SQLException {
		ERXSQLHelper.ColumnIndex[] columnIndexes = _columnIndexesForMigrationColumns(columns);
		addUniqueIndex(indexName, columnIndexes);
	}
	
	/**
	 * Executes the SQL operations to add a unique index.
	 * 
	 * @param indexName the name of the index
	 * @param columnIndexes the column indexes to unique index on
	 * @throws SQLException if the constraint fails
	 */
	public void addUniqueIndex(String indexName, ERXSQLHelper.ColumnIndex... columnIndexes) throws SQLException {
		addUniqueIndex(!_new, indexName, columnIndexes);
	}
	
	/**
	 * Executes the SQL operations to add a unique index.
	 * 
	 * @param create if true, the index is created immediately
	 * @param indexName the name of the index
	 * @param columnIndexes the column indexes to unique index on
	 * @throws SQLException if the constraint fails
	 */
	public void addUniqueIndex(boolean create, String indexName, ERXSQLHelper.ColumnIndex... columnIndexes) throws SQLException {
		if (create) {
			ERXSQLHelper helper = ERXSQLHelper.newSQLHelper(_database.adaptorChannel());
			if(indexName == null) { 
				indexName = _defaultIndexName(true, _name, helper.columnNamesFromColumnIndexes(columnIndexes).toArray(new String[] {}));
			}
			String sql = helper.sqlForCreateUniqueIndex(indexName, _name, columnIndexes);
			ERXJDBCUtilities.executeUpdateScript(_database.adaptorChannel(), sql);
		}
		else {
			_indexes.addObject(new ERXMigrationIndex(indexName, true, columnIndexes));
		}
	}

	/**
	 * Executes the SQL operations to add this index.
	 * 
	 * @param columnName the name of the column to add a unique index on
	 * @throws SQLException if the constraint fails
	 */
	public void addIndex(String columnName) throws SQLException {
		addIndex(null, columnName);
	}
	
	/**
	 * Executes the SQL operations to add this index.
	 * 
	 * @param indexName the name of the index
	 * @param columnName the name of the column to add a unique index on
	 * @throws SQLException if the constraint fails
	 */
	public void addIndex(String indexName, String columnName) throws SQLException {
		addIndex(indexName, existingColumnNamed(columnName));
	}

	/**
	 * Executes the SQL operations to add this index.
	 * 
	 * @param indexName the name of the index
	 * @param columnName the name of the column to add a unique index on
	 * @throws SQLException if the constraint fails
	 */
	public void addIndex(String indexName, String columnName, int width) throws SQLException {
		addIndex(indexName, new ERXSQLHelper.ColumnIndex(columnName, width));
	}

	/**
	 * Executes the SQL operations to add an index.
	 * 
	 * @param indexName the name of the index
	 * @param columns the columns to add a unique index on
	 * @throws SQLException if the constraint fails
	 */
	public void addIndex(String indexName, ERXMigrationColumn... columns) throws SQLException {
		ERXSQLHelper.ColumnIndex[] columnIndexes = _columnIndexesForMigrationColumns(columns);
		addIndex(indexName, columnIndexes);
	}

	/**
	 * Executes the SQL operations to add an index.
	 * 
	 * @param indexName the name of the index
	 * @param columnIndexes the column indexes to unique index on
	 * @throws SQLException if the constraint fails
	 */
	public void addIndex(String indexName, ERXSQLHelper.ColumnIndex... columnIndexes) throws SQLException {
		addIndex(!_new, indexName, columnIndexes);
	}
	
	/**
	 * Executes the SQL operations to add an index.
	 * 
	 * @param create if true, the index is created immediately
	 * @param indexName the name of the index
	 * @param columnIndexes the column indexes to unique index on
	 * @throws SQLException if the constraint fails
	 */
	public void addIndex(boolean create, String indexName, ERXSQLHelper.ColumnIndex... columnIndexes) throws SQLException {
		if (create) {
			ERXSQLHelper helper = ERXSQLHelper.newSQLHelper(_database.adaptorChannel());
			if (indexName == null) {
				indexName = _defaultIndexName(false, _name, helper.columnNamesFromColumnIndexes(columnIndexes).toArray(new String[] {}));
			}
			String sql = helper.sqlForCreateIndex(indexName, _name, columnIndexes);
			ERXJDBCUtilities.executeUpdateScript(_database.adaptorChannel(), sql);
		}
		else {
			_indexes.addObject(new ERXMigrationIndex(indexName, false, columnIndexes));
		}
	}
	
	/**
	 * Executes the SQL operations to add an index.
	 * 
	 * @param index the index to add
	 * @throws SQLException if the constraint fails
	 */
	public void addIndex(ERXMigrationIndex index) throws SQLException {
		addIndex(!_new, index);
	}
	
	/**
	 * Executes the SQL operations to add an index.
	 * 
	 * @param create if true, the index is created immediately
	 * @param index the index to add
	 * @throws SQLException if the constraint fails
	 */
	public void addIndex(boolean create, ERXMigrationIndex index) throws SQLException {
		if (index.isUnique()) {
			addUniqueIndex(create, index.name(), index.columns());
		}
		else {
			addIndex(create, index.name(), index.columns());
		}
	}
	
	private ERXSQLHelper.ColumnIndex[] _columnIndexesForMigrationColumns(ERXMigrationColumn... columns) {
		ERXSQLHelper.ColumnIndex[] columnIndexes = new ERXSQLHelper.ColumnIndex[columns.length];
		for (int columnNum = 0; columnNum < columns.length; columnNum ++) {
			columnIndexes[columnNum] = new ERXSQLHelper.ColumnIndex(columns[columnNum].name(), columns[columnNum].width());
		}
		return columnIndexes;
	}
	
	private String _defaultIndexName(boolean unique, String tableName, String... columnNames) {
		String indexName = unique ? "UNIQUE_" : "INDEX_";
		indexName += tableName;
		indexName += "__";
		indexName += new NSArray<String>(columnNames).componentsJoinedByString("_");
		return indexName;
	}

	/**
	 * Executes the SQL operations to add this primary key constraint.
	 * 
	 * @param columnName the name of the column to set as the primary key
	 * @throws SQLException if the constraint fails
	 */
	public void setPrimaryKey(String columnName) throws SQLException {
		setPrimaryKey(existingColumnNamed(columnName));
	}

	/**
	 * Executes the SQL operations to add this primary key constraint.
	 * 
	 * @param columnNames the names of the columns to set as the primary key
	 * @throws SQLException if the constraint fails
	 */
	public void setPrimaryKey(String... columnNames) throws SQLException {
		ERXMigrationColumn[] columns = new ERXMigrationColumn[columnNames.length];
		for (int i = 0; i < columnNames.length; i ++) {
			columns[i] = existingColumnNamed(columnNames[i]);
		}
		setPrimaryKey(columns);
	}

	/**
	 * Executes the SQL operations to add this primary key constraint.
	 * 
	 * @param columns the primary key columns to designate as primary keys
	 * @throws SQLException if the constraint fails
	 */
	public void setPrimaryKey(ERXMigrationColumn... columns) throws SQLException {
		setPrimaryKey(!_new, columns);
	}
	
	/**
	 * Executes the SQL operations to add this primary key constraint.
	 * 
	 * @param create if create is true, execute this now (vs just flag the ERXMigrationColumn)
	 * @param columns the primary key columns to designate as primary keys
	 * @throws SQLException if the constraint fails
	 */
	public void setPrimaryKey(boolean create, ERXMigrationColumn... columns) throws SQLException {
		for (ERXMigrationColumn column : _columns) {
			column._setPrimaryKey(false);
		}
		for (ERXMigrationColumn column : columns) {
			column._setPrimaryKey(true);
		}
		if (create) {
			ERXJDBCUtilities.executeUpdateScript(_database.adaptorChannel(), ERXMigrationDatabase._stringsForExpressions(_setPrimaryKeyExpressions(columns)));
		}
	}

	/**
	 * Returns an array of EOSQLExpressions for adding a foreign key constraint to this table (only supports single attribute FK's right now).
	 * 
	 * @return an array of EOSQLExpressions for adding a foreign key constraint to this table
	 */
	@SuppressWarnings("unchecked")
	public NSArray<EOSQLExpression> _addForeignKeyExpressions(ERXMigrationColumn sourceColumn, ERXMigrationColumn destinationColumn) {
		EOSchemaGeneration schemaGeneration = _database.synchronizationFactory();
		NSArray<EOSQLExpression> expressions = schemaGeneration.foreignKeyConstraintStatementsForRelationship(_newRelationship(sourceColumn, destinationColumn));
		ERXMigrationDatabase._ensureNotEmpty(expressions, "add foreign key", false);
		return expressions;
	}

	/**
	 * Executes the SQL operations to add this foreign key constraint (only supports single attribute FK's right now).
	 * 
	 * @param sourceColumnName the source column name of the relationship
	 * @param destinationTableName the destination table of the relationship (should be the PK of the destination table)
	 * @param destinationColumnName the destination column of the relationship (should be the PK of the destination table)
	 * @throws SQLException if the add fails
	 */
	public void addForeignKey(String sourceColumnName, String destinationTableName, String destinationColumnName) throws SQLException {
		addForeignKey(existingColumnNamed(sourceColumnName), database().existingColumnNamed(destinationTableName, destinationColumnName));
	}

	/**
	 * Executes the SQL operations to add this foreign key constraint (only supports single attribute FK's right now).
	 * 
	 * @param sourceColumnName the source column name of the relationship
	 * @param destinationColumn the destination column of the relationship (should be the PK of the destination table)
	 * @throws SQLException if the add fails
	 */
	public void addForeignKey(String sourceColumnName, ERXMigrationColumn destinationColumn) throws SQLException {
		addForeignKey(existingColumnNamed(sourceColumnName), destinationColumn);
	}

	/**
	 * Executes the SQL operations to add this foreign key constraint (only supports single attribute FK's right now).
	 * 
	 * @param sourceColumn the source column of the relationship
	 * @param destinationColumn the destination column of the relationship (should be the PK of the destination table)
	 * @throws SQLException if the add fails
	 */
	public void addForeignKey(ERXMigrationColumn sourceColumn, ERXMigrationColumn destinationColumn) throws SQLException {
		addForeignKey(!_new, sourceColumn, destinationColumn);
	}

	/**
	 * Executes the SQL operations to add this foreign key constraint (only supports single attribute FK's right now).
	 * 
	 * @param sourceColumn the source column of the relationship
	 * @param destinationColumn the destination column of the relationship (should be the PK of the destination table)
	 * @throws SQLException if the add fails
	 */
	public void addForeignKey(boolean create, ERXMigrationColumn sourceColumn, ERXMigrationColumn destinationColumn) throws SQLException {
		sourceColumn._setForeignKeyDestination(destinationColumn);
		if (create) {
			NSArray<EOSQLExpression> expressions = _addForeignKeyExpressions(sourceColumn, destinationColumn);
			if (expressions != null) {
				ERXJDBCUtilities.executeUpdateScript(_database.adaptorChannel(), ERXMigrationDatabase._stringsForExpressions(expressions));
			}
		}
	}

	/**
	 * Returns an array of EOSQLExpressions for removing the primary key constraint of this table (only supports single attribute PK's right now).
	 *
	 * @param columns the primary key columns to drop
	 * @return an array of EOSQLExpressions for removing the primary key constraint of this table
	 */
	@SuppressWarnings("unchecked")
	public NSArray<EOSQLExpression> _dropPrimaryKeyExpressions(ERXMigrationColumn... columns) {
		EOSchemaGeneration schemaGeneration = _database.synchronizationFactory();
		EOEntity entity = columns[0].table()._blankEntity();
		NSMutableArray<EOAttribute> attributes = new NSMutableArray<EOAttribute>();
		for (ERXMigrationColumn column : columns) {
			EOAttribute attribute = column._newAttribute(entity);
			attributes.addObject(attribute);
		}
		entity.setPrimaryKeyAttributes(attributes);
		NSArray<EOSQLExpression> expressions = schemaGeneration.dropPrimaryKeySupportStatementsForEntityGroup(new NSArray<EOEntity>(entity));
		ERXMigrationDatabase._ensureNotEmpty(expressions, "drop primary key", true);
		return expressions;
	}

	/**
	 * Executes the SQL operations to drop this primary key constraint (only supports single attribute PK's right now).
	 * 
	 * @param columns the primary key columns
	 * @throws SQLException if the drop fails
	 */
	public void dropPrimaryKey(ERXMigrationColumn... columns) throws SQLException {
		ERXJDBCUtilities.executeUpdateScript(_database.adaptorChannel(), ERXMigrationDatabase._stringsForExpressions(_dropPrimaryKeyExpressions(columns)));
		for (ERXMigrationColumn column : columns) {
			column._setPrimaryKey(false);
		}
	}

	/**
	 * Returns a dictionary that represents the primary key for
	 * an entity described by this table.  Note that you must have specified
	 * the primary key columns for this table prior to calling this method via
	 * the setPrimaryKeys method (or calling _setPrimaryKey on the individual
	 * columns).
	 * 
	 * @return a dictionary of a primary key
	 */
	public NSDictionary<String, Object> newPrimaryKey() {
		return newPrimaryKeys(1).lastObject();
	}
	
	/**
	 * Returns an array of dictionaries that represent the primary keys for
	 * an entity described by this table.  Note that you must have specified
	 * the primary key columns for this table prior to calling this method via
	 * the setPrimaryKeys method (or calling _setPrimaryKey on the individual
	 * columns).
	 * 
	 * @param count the number of primary keys desired
	 * @return an array of dictionaries of primary keys
	 */
	@SuppressWarnings("unchecked")
	public NSArray<NSDictionary<String, Object>> newPrimaryKeys(int count) {
		NSArray<NSDictionary<String, Object>> primaryKeys = database().adaptorChannel().primaryKeysForNewRowsWithEntity(count, _newEntity());
		//NSMutableArray<NSMutableDictionary<String, Object>>
		return primaryKeys;
	}
}