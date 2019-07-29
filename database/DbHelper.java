package br.com.datumti.library.database;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {
//	private static String tag = "DbHelper";
	@SuppressWarnings("unused")
	private Context context;
	private int version;
	private ArrayList<String> createCommands;
	private HashMap<Integer, ArrayList<String>> upgradeCommands;

	public DbHelper( Context context, String dbName, int version ) {
		super(context, dbName, new DbCursor.Factory(), version);
		this.context = context;
		this.version = version;
		createCommands = new ArrayList<String>();
		upgradeCommands = new HashMap<Integer, ArrayList<String>>();
	}

	@Override
	public void onCreate( SQLiteDatabase db ) {
		for( int i = 0; i < createCommands.size(); i++ )
			db.execSQL( createCommands.get( i ) );

		afterCreateOrUpgrade();
	}

	@Override
	public void onUpgrade( SQLiteDatabase db, int oldVersion, int newVersion ) {
		ArrayList<String> cmds;

		for( int ver = oldVersion + 1; ver <= newVersion; ver++ ) {
			cmds = upgradeCommands.get( ver );

			for( int i = 0; i < cmds.size(); i++ )
				db.execSQL( cmds.get( i ) );
		}

		afterCreateOrUpgrade();
	}

	public void addCreate( ArrayList<String> cmds ) {
		for( int i = 0; i < cmds.size(); i++ )
			createCommands.add( cmds.get( i ) );
	}

	public void addCreate( String[] cmds ) {
		for( int i = 0; i < cmds.length; i++ )
			createCommands.add( cmds[ i ] );
	}

	public void addCreate( String cmd ) {
		createCommands.add( cmd );
	}

	public void addCreateFile( String fileName ) {
		try {
			File f = new File( fileName );
			InputStream is = new FileInputStream( f );
			addCreateInputStream( is );
			is.close();
		} catch( Exception e ) {
			e.printStackTrace();
		}
	}

	public void addCreateAsset( String fileName ) {
		try {
			AssetManager assets = context.getAssets();
//	    	boolean existe = false;
//
//		    try {
//		    	String arqs[] = assets.list( "" );
//
//		    	for( int i = 0; i < arqs.length; i++ ) {
//			    	if( arqs[i].equals( fileName ) ) {
//			    		existe = true;
//			    		break;
//			    	}
//			    }
//		    } catch (IOException e) {
//		    	e.printStackTrace();
//		    }
//
//			if( ! existe )
//		        throw( new Exception( "File not found." ) );

			InputStream is = assets.open( fileName );
			addCreateInputStream( is );
			is.close();
		} catch( Exception e ) {
			e.printStackTrace();
		}
	}

	public void addCreateInputStream( InputStream is ) {
		try {
			BufferedReader bufferedReader = new BufferedReader( new InputStreamReader( is, "UTF-8" ) );
	    	String sComando = "";
	    	String sLinha;

	    	while( ( sLinha = bufferedReader.readLine() ) != null ) {
	    		sLinha = sLinha.trim();

	    		if( ! sLinha.equals( "" ) && ! sLinha.startsWith( "--" ) ) {
	    			sComando = sComando + " " + sLinha;

	    			if( sLinha.endsWith( ";" ) ) {
	    				addCreate( sComando );
	    				sComando = "";
	    			}
	    		}
	    	}

	    	bufferedReader.close();

	    	if( ! sComando.equals( "" ) )
				addCreate( sComando );
		} catch( IOException e ) {
			e.printStackTrace();

		} catch( Exception e ) {
			e.printStackTrace();
		}
	}
	
	public void addUpgrade( int ver, ArrayList<String> cmds ) {
		if( ! upgradeCommands.containsKey( ver ) )
			upgradeCommands.put( ver, cmds );
		else {
			ArrayList<String> alCmds = upgradeCommands.get( ver );
			upgradeCommands.remove( ver );

			for( int i = 0; i < cmds.size(); i++ )
				alCmds.add( cmds.get( i ) );

			upgradeCommands.put( ver, alCmds );
		}
	}

	public void addUpgrade( int ver, String[] cmds ) {
		ArrayList<String> alCmds;

		if( upgradeCommands.containsKey( ver ) ) {
			alCmds = upgradeCommands.get( ver );
			upgradeCommands.remove( ver );
		} else
			alCmds = new ArrayList<String>();

		for( int i = 0; i < cmds.length; i++ )
			alCmds.add( cmds[ i ] );

		upgradeCommands.put( ver, alCmds );
	}

	public void addUpgrade( int ver, String cmd ) {
		ArrayList<String> alCmds;

		if( upgradeCommands.containsKey( ver ) ) {
			alCmds = upgradeCommands.get( ver );
			upgradeCommands.remove( ver );
		} else
			alCmds = new ArrayList<String>();

		alCmds.add( cmd );
		upgradeCommands.put( ver, alCmds );
	}

	public void addUpgradeFile( int ver, String fileName ) {
		try {
			File arq = new File( fileName );

			if( ! arq.exists() )
		        throw( new Exception( "File not found." ) );

			FileReader fileReader = new FileReader( arq );
	    	BufferedReader bufferedReader = new BufferedReader( fileReader );
	    	String sComando = "";
	    	String sLinha;

	    	while( ( sLinha = bufferedReader.readLine() ) != null ) {
	    		sLinha = sLinha.trim();

	    		if( ! sLinha.equals( "" ) && ! sLinha.startsWith( "--" ) ) {
	    			sComando = sComando + " " + sLinha;

	    			if( sLinha.endsWith( ";") ) {
	    				addUpgrade( ver, sComando );
	    				sComando = "";
	    			}
	    		}
	    	}

	    	bufferedReader.close();
	    	fileReader.close();

	    	if( ! sComando.equals( "" ) )
				addUpgrade( ver, sComando );
		} catch( IOException e ) {
			e.printStackTrace();

		} catch( Exception e ) {
			e.printStackTrace();
		}
	}

	private void afterCreateOrUpgrade() {
		createCommands.clear();
		upgradeCommands.clear();
	}
}
