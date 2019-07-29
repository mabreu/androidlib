package br.com.datumti.library.database;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DbConnection {
//	private static String tag = "DbConnection";

	private SQLiteDatabase db;
	private DbHelper dbHelper;
	private Context context;
	private String DATABASE_NAME;
	private String lastErrorMsg = "";

	private static String[] SCRIPT_CREATE = { "CREATE TABLE variaveis( chave text not null primary key, valor text );" }; 


	public DbConnection( Context ctx, String dbName, int dbVersion ) {
		context = ctx;
		DATABASE_NAME = ctx.getDatabasePath( dbName ).getAbsolutePath();
		dbHelper = new DbHelper( ctx, dbName, dbVersion );
		addCreate( SCRIPT_CREATE );
	}

	public boolean open() {
		db = dbHelper.getWritableDatabase();
		return ( db != null );
	}

	public void close() {
		if( db != null )
			db.close();
	}

	public boolean exists() {
		File f = new File( DATABASE_NAME );
		return f.exists();
	}

	public boolean delete() {
		File f = new File( DATABASE_NAME );
		
		if( ! f.exists() )
			return false;

		return f.delete();
	}

	public boolean copyFromAssets( String origin ) {
		try {
			InputStream entrada = context.getAssets().open( origin );
	        OutputStream saida = new FileOutputStream( DATABASE_NAME );

	        byte[] buffer = new byte[1024];
	        int length;

	        while( ( length = entrada.read( buffer ) ) > 0 ) {
	        	saida.write( buffer, 0, length );
	        }

	        saida.flush();
	        saida.close();
	        entrada.close();
		} catch( Exception e ) {
			lastErrorMsg = e.getMessage();
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public DbCursor getCursor( String sql ) {
		DbCursor result;

		try {
			result = (DbCursor) db.rawQuery( sql, null );
		} catch( Exception e ) {
			result = null;
			lastErrorMsg = e.getMessage();
		}

		return result;
	}

	public int execSQL( String sql ) {
		int result;

		try {
			db.execSQL( sql );
			result = rowsAffected();
		} catch( Exception e ) {
			result = -1;
			lastErrorMsg = e.getMessage();
		}

		return result;
	}

	public void beginTrans() {
		db.beginTransaction();
	}

	public void commit() {
		db.setTransactionSuccessful();
		db.endTransaction();
	}

	public void rollback() {
		db.endTransaction();
	}

	public boolean inTransaction() {
		return db.inTransaction();
	}

	public String getLastErrorMessage() {
		return this.lastErrorMsg;
	}
	
	public int getIntField( String sql ) {
		int res = 0;
		Cursor cur = getCursor( sql );
		
		if( cur != null ) {
			if( cur.moveToFirst() ) {
				res = cur.getInt( 0 );
			}

			cur.close();
		}

		return res;
	}

	public double getFloatField( String sql ) {
		double res = 0;
		Cursor cur = getCursor( sql );

		if( cur != null ) {
			if( cur.moveToFirst() ) {
				res = cur.getFloat( 0 );
			}

			cur.close();
		}

		return res;
	}

	public String getStringField( String sql ) {
		String res = null;
		Cursor cur = getCursor( sql );

		if( cur != null ) {
			if( cur.moveToFirst() ) {
				res = cur.getString( 0 );
			}

			cur.close();
		}

		return res;
	}

	public Date getDateField( String sql ) {
		Date res = null;
		DbCursor cur = getCursor( sql );

		if( cur != null ) {
			if( cur.moveToFirst() ) {
				res = cur.getDate( 0 );
			}

			cur.close();
		}

		return res;
	}

	public int getLastKey() throws Exception {
		// LAST_INSERT_ID -> MySql
		return getIntField( "SELECT last_insert_rowid()" );
	}

	public int rowsAffected() throws Exception {
		return getIntField( "SELECT changes()" );
	}

	public String boolToStr( boolean value ) {
		return "'" + (value ? "S" : "N") + "'";
	}

	public String escapeStr( String str ) {
		if( str == null || str.toUpperCase().equals( "NULL" ) )
			return "NULL";

		return "'" + str.replaceAll( "'", "''" ) + "'";
	}

	public String nullIfZero( long val ) {
		if( val == 0 )
			return "NULL";

		return "" + val;
	}

	public String nullIfZero( double val ) {
		if( val == 0.0 )
			return "NULL";

		return "" + val;
	}

	public String nullIfEmpty( String val ) {
		if( val == null || val.equals( "" ) )
			return "NULL";

		return escapeStr( val );
	}

	public void addCreate( ArrayList<String> cmds ) {
		dbHelper.addCreate( cmds );
	}

	public void addCreate( String[] cmds ) {
		dbHelper.addCreate( cmds );
	}

	public void addCreate( String cmd ) {
		dbHelper.addCreate( cmd );
	}

	public void addCreateFile( String fileName ) {
		dbHelper.addCreateFile( fileName );
	}

	public void addCreateAsset( String fileName ) {
		dbHelper.addCreateAsset( fileName );
	}

	public void addCreateInputStream( InputStream is) {
		dbHelper.addCreateInputStream( is );
	}

	public void addUpgrade( int ver, ArrayList<String> cmds ) {
		dbHelper.addUpgrade( ver, cmds );
	}

	public void addUpgrade( int ver, String[] cmds ) {
		dbHelper.addUpgrade( ver, cmds );
	}

	public void addUpgrade( int ver, String cmd ) {
		dbHelper.addUpgrade( ver, cmd );
	}

	public void addUpgradeFile( int ver, String fileName ) {
		dbHelper.addUpgradeFile( ver, fileName );
	}

	public int setVar( String chave, String valor ) {
		if( chave == null ) 
			return -1;

		String sql = "INSERT OR REPLACE INTO variaveis( chave, valor ) " +
						"VALUES( " + escapeStr( chave ) + ", " + ( valor == null ? "NULL" : escapeStr( valor ) ) + " )";

		return execSQL( sql );
	}

	public String getVar( String chave ) {
		return getStringField( "SELECT valor FROM variaveis WHERE chave = " +  escapeStr( chave ) );
	}
	
	public boolean execSQLFile( String sSqlFile ) {
		return execSQLFile( sSqlFile, true, true );
	}

	public boolean execSQLFile( String sSqlFile, boolean bAbortOnError ) {
		return execSQLFile( sSqlFile, bAbortOnError, true );
	}

	public boolean execSQLFile( String sSqlFile, boolean bAbortOnError, boolean bNewTransaction ) {
		try {
			File arq = new File( sSqlFile );

			if( ! arq.exists() )
		        throw( new Exception( "File not found." ) );

			if( bNewTransaction )
	           beginTrans();

			FileReader fileReader = new FileReader( arq );
	    	BufferedReader bufferedReader = new BufferedReader( fileReader );
	    	String sComando = "";
	    	String sLinha;

	    	while( ( sLinha = bufferedReader.readLine() ) != null ) {
	    		sLinha = sLinha.trim();

	    		if( ! sLinha.equals( "" ) && ! sLinha.startsWith( "--" ) ) {
	    			sComando = sComando + " " + sLinha;

	    			if( sLinha.endsWith( ";") ) {
	    				if( execSQL( sComando ) < 0 && bAbortOnError )
	    					throw new Exception( "Error executing SQL command." );

	    				sComando = "";
	    			}
	    		}
	    	}

	    	bufferedReader.close();
	    	fileReader.close();

	    	if( ! sComando.equals( "" ) )
				if( execSQL( sComando ) < 0 )
					throw new Exception( "Error executing SQL command." );
	    	
			if( bNewTransaction )
				commit();

			return true;
		} catch( IOException e ) {
			lastErrorMsg = e.getMessage();
			e.printStackTrace();

		} catch( Exception e ) {
			lastErrorMsg = e.getMessage();
			e.printStackTrace();

		} finally {
			if( bNewTransaction && inTransaction() )
				rollback();
		}

		return false;
	}

	public boolean execSQL( ArrayList<String> commands ) {
		return execSQL( commands, true, true );
	}

	public boolean execSQL( ArrayList<String> commands, boolean bAbortOnError ) {
		return execSQL( commands, bAbortOnError, true );
	}

	public boolean execSQL( ArrayList<String> commands, boolean bAbortOnError, boolean bNewTransaction ) {
		try {
			if( bNewTransaction )
				beginTrans();

			String sLinha;

			for( int i = 0; i < commands.size(); i++ ) {
				sLinha = commands.get( i ).trim();

				if( ! sLinha.equals( "" ) && ! sLinha.startsWith( "--" ) ) {
    				if( execSQL( sLinha ) < 0 && bAbortOnError )
    					throw new Exception( "Error executing SQL command." );
	    		}
			}

			if( bNewTransaction )
				commit();

			return true;
		} catch( Exception e ) {
			lastErrorMsg = e.getMessage();
			e.printStackTrace();
		}

		if( bNewTransaction && inTransaction() )
			rollback();

		return false;
	}

	public DataPacket getDataPacket( DbCursor cur ) {
		if( cur == null ) 
			return null;

		DataPacket data = new DataPacket();

		if( cur.getCount() > 0 && cur.moveToFirst() ) {
			do {
				data.insert( new DataRow( cur ) );
			} while( cur.moveToNext() );
		}

		return data;
	}

	public DataPacket getDataPacket( String sql ) {
		try {
			DbCursor cur = getCursor( sql );
			DataPacket data = getDataPacket( cur );
			cur.close();
			return data;
		} catch( Exception e ) {
			lastErrorMsg = e.getMessage();
			e.printStackTrace();
			return null;
		}
	}

	public DataRow getDataRow( DbCursor cur ) {
		try {
			if( cur == null ) 
				return null;

			DataRow dr = new DataRow();

			if( cur.getCount() != 0 ) {
				if( cur.isBeforeFirst() )
					cur.moveToFirst();
				else
					if( cur.isAfterLast() )
						cur.moveToLast();

				dr.loadData( cur );
			}

			return dr;
		} catch( Exception e ) {
			lastErrorMsg = e.getMessage();
			e.printStackTrace();
			return null;
		}
	}

	public DataRow getDataRow( String sql ) {
		try {
			DataRow dr = null;
			DbCursor cur = getCursor( sql );

			if( cur != null ) {
				dr = getDataRow( cur );
				cur.close();
			}

			return dr;
		} catch( Exception e ) {
			lastErrorMsg = e.getMessage();
			e.printStackTrace();
			return null;
		}
	}
}
