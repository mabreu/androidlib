package br.com.datumti.library.database;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteCursorDriver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQuery;
import android.text.TextUtils;

public class DbCursor extends SQLiteCursor {
	private SimpleDateFormat sdf = null;

	public DbCursor( SQLiteDatabase db, SQLiteCursorDriver driver,
			String editTable, SQLiteQuery query ) {
		super(db, driver, editTable, query);
	}

	public static class Factory implements SQLiteDatabase.CursorFactory {
		public Cursor newCursor( SQLiteDatabase db, SQLiteCursorDriver driver, String editTable, SQLiteQuery query ) {
			return new DbCursor( db, driver, editTable, query );
		}
	}

	public boolean isNull( String fieldName ) {
		return isNull( getColumnIndex( fieldName ) );
	}

	public int getInt( String fieldName ) {
		return getInt( getColumnIndex( fieldName ) );
	}

	public long getLong( String fieldName ) {
		return getLong( getColumnIndex( fieldName ) );
	}

	public float getFloat( String fieldName ) {
		return getFloat( getColumnIndex( fieldName ) );
	}

	public double getDouble( String fieldName ) {
		return getDouble( getColumnIndex( fieldName ) );
	}

	public short getShort( String fieldName ) {
		return getShort( getColumnIndex( fieldName ) );
	}

	public String getString( String fieldName ) {
		return getString( getColumnIndex( fieldName ) );
	}

	public Date getDate( int field ) {
		Date res = null;

		try {
			String sDate = getString( field );

			if( ! TextUtils.isEmpty( sDate ) ) { 
				if( sdf == null )
					sdf = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );

				res = sdf.parse( sDate );
			}
		} catch( ParseException e ) {
			e.printStackTrace();
		}

		return res;
	}

	public Date getDate( String fieldName ) {
		return getDate( getColumnIndex( fieldName ) );
	}
}
