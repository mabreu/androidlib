package br.com.datumti.library.database;

import android.util.Log;

public class DbCtrl {
	protected DbConnection conn;

	public DbCtrl( DbConnection conn ) {
		super();
		this.conn = conn;
	}

	protected int write( String sql ) {
		try {
			return conn.execSQL( sql );
		} catch( Exception e ) {
			e.printStackTrace();
			Log.e( "DbCtrl:Gravar", e.getMessage() );
			Log.e( "DbCtrl:SQL", sql );
		}

		return -1;
	}

	protected int writeKey( String sql ) {
		try {
			int res = conn.execSQL( sql );

			if( res >= 0 )
				return conn.getLastKey();
		} catch( Exception e ) {
			e.printStackTrace();
			Log.e( "DbCtrl:GravarKey", e.getMessage() );
			Log.e( "DbCtrl:SQL", sql );
		}

		return -1;
	}

	protected int delete( String sql ) {
		try {
			return conn.execSQL( sql );
		} catch( Exception e ) {
			e.printStackTrace();
			Log.e( "DbCtrl:Excluir", e.getMessage() );
			Log.e( "DbCtrl:SQL", sql );
		}

		return -1;
	}

}
