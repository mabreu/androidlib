package br.com.datumti.library.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtils {
	public static boolean deleteDir( File dir ) {
		try {
			File[] arqs = dir.listFiles();
			File arq;

			for( int i = 0; i < arqs.length; i++ ) {
				arq = arqs[ i ];

				if( arq.isDirectory() )
					deleteDir( arq );

				arq.delete();
			}

			dir.delete();
		} catch( Exception e ) {
			return false;
		}

		return true;
	}

	public static void copy(File src, File dst) throws IOException {
		InputStream in = new FileInputStream(src);
	    OutputStream out = new FileOutputStream(dst);

	    byte[] buf = new byte[1024];
	    int len;

	    while( ( len = in.read( buf ) ) > 0 ) {
	        out.write( buf, 0, len );
	    }

	    in.close();
	    out.close();
	}

	public static void copy(String src, String dst) throws IOException {
		copy( new File( src ), new File( dst ) );
	}
}
