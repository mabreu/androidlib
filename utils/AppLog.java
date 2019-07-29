package br.com.datumti.library.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Environment;

public class AppLog {
	private static String filePrefix = null;
	private static String dirName = null;
	private static FileWriter fw = null; 
	private static SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss.SSS" );

	public static String getDirName() {
		return dirName;
	}

	public static File getFullDir() {
		return new File( getFullDirName() );
	}
	
	public static String getFullDirName() {
		String sDir;

		if( Environment.MEDIA_MOUNTED.equals( Environment.getExternalStorageState() ) ) {
//			dir = Environment.getExternalStoragePublicDirectory();
			sDir = Environment.getExternalStorageDirectory().getAbsolutePath();
		} else {
			sDir = Environment.getExternalStoragePublicDirectory( Environment.DIRECTORY_DOWNLOADS ).getAbsolutePath();
		}

		return sDir + '/' + getDirName();
	}
	
	public static void setDirName(String name) {
		dirName = name;
	}

	public static String getFilePrefix() {
		return filePrefix;
	}

	public static void setFilePrefix(String prefix) {
		filePrefix = prefix;
	}

	public static void setDateFormat( String dateFormat ) {
		sdf = new SimpleDateFormat( dateFormat );
	}
	
	public static String getDateFormated( Date date ) {
		return sdf.format( date );
	}
	
	public static void close() {
		if( fw != null ) {
			try {
				fw.close();
			} catch( IOException e ) {
				e.printStackTrace();
			}

			fw = null;
		}
	}

	public static void clearLogDir() {
		FilenameFilter filter = new FilenameFilter() {
	    	public boolean accept( File file, String fileName ) {
	    		return fileName.endsWith( ".log" );
	        }
		};

		File fDir = getFullDir();

		for( File file : fDir.listFiles( filter ) ) {
			if( file.exists() && file.isFile() ) {
				file.delete();
	        }
	    }		
	}
	
	public static void log( String texto ) {
		if( fw == null ) {
			String dir = getFullDirName();
			String data = new SimpleDateFormat( "yyyyMMdd_HHmmss" ).format( new Date() );
			int sequencial = 0;
			File file;

			while( true ) {
				String fileName = String.format( dir + "/%s_%s_%d.log", filePrefix, data, sequencial );
				file = new File( fileName );

				if( ! file.exists() ) {
					break;
				}

				sequencial++;
			}

			try {
				file.getParentFile().mkdirs();
				file.createNewFile();
				fw = new FileWriter( file.getAbsoluteFile() );
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		try {
//			fw.write( ";\"" + sdf.format( new Date() ) + texto + "\"\r\n" );
			fw.write( texto + "\r\n" );
			fw.flush();
		} catch( IOException e ) {
			e.printStackTrace();
		}
	}
}
