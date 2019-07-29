package br.com.datumti.library.database;

import java.util.ArrayList;

public class DataPacket {
	private ArrayList<DataRow> data;
	private DataRow dr;
	private int position;

	public DataPacket() {
		super();
		data = new ArrayList<DataRow>();
		this.position = -1;
	}

	public boolean moveTo( int position ) {
		if( position < 0 || position > data.size() || data.size() == 0 )
			return false;

		this.position = position;
		dr = getRow();
		return true;
	}

	public boolean moveToFirst() {
		if( data.size() == 0 )
			return false;

		this.position = 0;
		dr = getRow();
		return true;
	}

	public boolean moveToLast() {
		if( data.size() == 0 )
			return false;

		this.position = data.size() - 1;
		dr = getRow();
		return true;
	}

	public boolean moveToNext() {
		if( this.position >= ( data.size() - 1 ) )
			return false;

		this.position++;
		dr = getRow();
		return true;
	}

	public boolean moveToPrevious() {
		if( this.position <= 0 )
			return false;

		this.position--;
		dr = getRow(); 
		return true;
	}

	public boolean insert( DataRow row ) {
		return data.add( row );
	}

	public int getCount() {
		return data.size();
	}

	public int getPosition() {
		return this.position;
	}

	public DataRow getRow() {
		return data.get( this.position );
	}

	public Object getValue( String name ) {
		return dr.getValue( name );
	}

	public Object setValue( String name, Object value ) {
		return dr.setValue( name, value );
	}

	public int getInt( String name ) {
		return dr.getInt( name );
	}

	public Object setInt( String name, int value ) {
		return dr.setInt( name, value );
	}

	public double getFloat( String name ) {
		return dr.getFloat( name );
	}

	public Object setFloat( String name, double value ) {
		return dr.setFloat( name, value );
	}

	public String getString( String name ) {
		return dr.getString( name );
	}

	public Object setString( String name, String value ) {
		return dr.setString( name, value );
	}
}
