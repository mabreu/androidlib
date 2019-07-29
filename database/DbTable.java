package br.com.datumti.library.database;

import java.util.HashMap;
import java.util.Map;

import android.text.TextUtils;
import br.com.datumti.library.database.DbField.FieldType;

public class DbTable extends DbObject {
	private DbConnection conn;
	private DbFieldList fields;
	private String tableName;

	public DbTable() {
		super();
	}

	public DbTable( DbConnection conn ) {
		this();
	}

	public DbTable( DbConnection conn, String tableName ) {
		this( conn );
		this.tableName = tableName;
		this.fields = new DbFieldList();
	}

	public DbConnection getDbConnection() {
		return conn;
	}

	public DbTable setDbConnection( DbConnection conn ) {
		this.conn = conn;
		return this;
	}

	public String getTableName() {
		return tableName;
	}

	public DbTable setTableName( String tableName ) {
		this.tableName = tableName;
		return this;
	}

	public DbFieldList getFields() {
		return fields;
	}

	public DbTable setFields( DbFieldList fields ) {
		this.fields = fields;
		return this;
	}

	public DbField addField( String fieldName, FieldType fieldType ) {
		return addField( fieldName, fieldType, fieldName, false, false, 0, false, true, false, -1, false, false );
	}

	public DbField addField( String fieldName, FieldType fieldType, int size ) {
		return addField( fieldName, fieldType, fieldName, false, false, size, false, true, false, -1, false, false );
	}

	public DbField addField( String fieldName, FieldType fieldType, DbDomain domain ) {
		return addField( fieldName, fieldType, fieldName, DbDomain.DDOrDef(domain.getIsKey(), false), DbDomain.DDOrDef(domain.getNotNull(), false), domain.getSize(),
							DbDomain.DDOrDef(domain.getAutoInc(), true), DbDomain.DDOrDef(domain.getNullIfEmpty(), true), DbDomain.DDOrDef(domain.getReadOnly(), false),
							domain.getPrecision(), DbDomain.DDOrDef(domain.getRoundValue(), false), DbDomain.DDOrDef(domain.getIsUnique(), false) );
	}

	public DbField addField( String fieldName, FieldType fieldType, String displayName, boolean isKey, boolean notNull, int size,
							boolean autoInc, boolean nullIfEmpty, boolean readOnly, int precision, boolean roundValue, boolean isUnique ) {
		return fields.add( fieldName, new DbField( fieldName, fieldType, displayName, isKey, notNull, size, autoInc,
													nullIfEmpty, readOnly, precision, roundValue, isUnique ) );
	}

	private String getInsertSQL() {
		HashMap<String, DbField> keyFields = fields.getKeyFieldsOrFields();
		String fieldNames = "";
		String fieldValues = "";
		DbField dbField;

		for( Map.Entry<String, DbField> field : keyFields.entrySet() ) {
			dbField = field.getValue();
			fieldNames += dbField.getFieldName() + ", ";
			fieldValues += dbField.getValueScaped() + ", ";
		}

		if( fieldNames.equals( "" ) )
			return "";

		fieldNames = fieldNames.substring( 0, fieldNames.length() - 2 );
		fieldValues = fieldNames.substring( 0, fieldValues.length() - 2 );
		return "INSERT INTO " + tableName + "( " + fieldNames + " ) VALUES( " + fieldValues + " )";
	}

	private String getUpdateSQL() {
		HashMap<String, DbField> keyFields = fields.getFields();
		String fieldSets = "";
		DbField dbField;

		for( Map.Entry<String, DbField> field : keyFields.entrySet() ) {
			dbField = field.getValue();

			if( dbField.isChanged() )
				fieldSets += dbField.getFieldName() + " = " + dbField.getValueScaped() + ", ";
		}

		if( fieldSets.equals( "" ) )
			return "";

		fieldSets = fieldSets.substring( 0, fieldSets.length() - 2 );

		HashMap<String, DbField> flds = fields.getKeyFieldsOrFields();
		String conds = "";

		for( Map.Entry<String, DbField> field : flds.entrySet() ) {
			dbField = field.getValue();
			conds += dbField.getFieldName() +
						( dbField.getOldValue() == null ? " IS " : " = " ) +
						dbField.getOldValueEscaped() + " AND ";
		}

		if( ! conds.equals( "" ) )
			conds = conds.substring( 0, conds.length() - 5 );

		return "UPDATE " + tableName + " SET " + fieldSets + " WHERE " + conds;
	}

	private String getDeleteSQL() {
		HashMap<String, DbField> flds = fields.getKeyFieldsOrFields();
		String conds = "";
		DbField dbField;

		for( Map.Entry<String, DbField> field : flds.entrySet() ) {
			dbField = field.getValue();
			conds += dbField.getFieldName() +
						( dbField.getOldValue() == null ? " IS " : " = " ) +
						dbField.getValueScaped() + " AND ";
		}

		if( conds.equals( "" ) )
			return "";

		return "DELETE FROM " + tableName + " WHERE " + conds.substring( 0, conds.length() - 5 );
	}

	public int insert() {
		String sql = getInsertSQL();

		if( TextUtils.isEmpty( sql ) )
			return 0;

		return conn.execSQL( sql );
	}

	public int update() {
		String sql = getUpdateSQL();

		if( TextUtils.isEmpty( sql ) )
			return 0;

		return conn.execSQL( sql );
	}

	public int delete() {
		String sql = getDeleteSQL();

		if( TextUtils.isEmpty( sql ) )
			return 0;

		return conn.execSQL( sql );
	}
}
