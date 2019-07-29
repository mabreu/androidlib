package br.com.datumti.library.database;

import java.util.HashMap;
import java.util.Map;

public class DbFieldList extends DbObject {
	private final HashMap<String, DbField> fields;
	private final HashMap<String, DbField> keyFields;

	public DbFieldList() {
		fields = new HashMap<String, DbField>();
		keyFields = new HashMap<String, DbField>();
	}

	public DbField add( String name, DbField dbf ) {
		if( dbf.isKey() )
			return keyFields.put( name, dbf );
		else
			return fields.put( name, dbf );
	}

	public DbField get( String name ) {
		DbField res = fields.get( name );

		if( res == null )
			res = keyFields.get( name );

		return res;
	}

	public HashMap<String, DbField> getKeyFieldsOrFields() {
		return (keyFields.size() != 0 ? keyFields : fields);
	}

	public HashMap<String, DbField> getKeyFields() {
		return keyFields;
	}

	public HashMap<String, DbField> getFields() {
		return fields;
	}
}
