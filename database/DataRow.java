package br.com.datumti.library.database;

import java.util.HashMap;

import android.annotation.TargetApi;
import android.os.Build;

public class DataRow {
	private HashMap<String, Object> fields; 

	public DataRow() {
		super();
		fields = new HashMap<String, Object>();
	}

	public DataRow( DbCursor cur ) {
		this();
		loadData( cur );
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void loadData( DbCursor cur ) {
		int tipo;

		for( int i = 0; i < cur.getColumnCount(); i++ ) {
			tipo = cur.getType( i );
			
			if( tipo == cur.FIELD_TYPE_INTEGER )
				setValue( cur.getColumnName( i ), cur.getInt( i ) );
			else if( tipo == cur.FIELD_TYPE_FLOAT )
				setValue( cur.getColumnName( i ), cur.getDouble( i ) );
			else if( tipo == cur.FIELD_TYPE_STRING )
				setValue( cur.getColumnName( i ), cur.getString( i ) );
			else if( tipo == cur.FIELD_TYPE_BLOB )
				setValue( cur.getColumnName( i ), cur.getBlob( i ) );
			else if( tipo == cur.FIELD_TYPE_NULL )
				setValue( cur.getColumnName( i ), null );
		}
	}

	public boolean hasData() {
		return (fields.size() > 0);
	}

	public Object getValue( String name ) {
		return fields.get( name );
	}

	public Object setValue( String name, Object value ) {
		return fields.put( name, value );
	}

	public int getInt( String name ) {
		Object v = fields.get( name );
		
		if( v == null )
			return 0;

		return ((Integer) v).intValue();
	}

	public Object setInt( String name, int value ) {
		return fields.put( name, new Integer( value ) );
	}

	public double getFloat( String name ) {
		Object v = fields.get( name );
		
		if( v == null )
			return 0;

		return ((Double) v).doubleValue();
	}

	public Object setFloat( String name, double value ) {
		return fields.put( name, new Double( value ) );
	}

	public String getString( String name ) {
		return (String) fields.get( name );
	}

	public Object setString( String name, String value ) {
		return fields.put( name, value );
	}
}
