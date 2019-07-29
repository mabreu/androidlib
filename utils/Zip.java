package br.com.datumti.library.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.util.Log;

public class Zip {
	public static String extractZip(String zipFileName, String destinationDir) {
		try {
            byte[] buf = new byte[1024];
            ZipInputStream zipinputstream = new ZipInputStream( new FileInputStream( zipFileName ) );
            ZipEntry zipentry = zipinputstream.getNextEntry();

            while( zipentry != null ) { 
                //for each entry to be extracted
                String entryName = zipentry.getName();

                // Cria o diretorio
				String targetDir = destinationDir + entryName;
				targetDir = targetDir.substring( 0, targetDir.lastIndexOf( "/" ) );

				// Se for diretorio
				if( entryName.endsWith( "/" ) ) {
					zipentry = zipinputstream.getNextEntry();
					continue;
				}

				Log.i("ZIP", "entryName = " + entryName);
				File targetDirF = new File( targetDir );
				targetDirF.mkdirs();

                int n;
                FileOutputStream fileoutputstream = new FileOutputStream( destinationDir + entryName );             

                while( ( n = zipinputstream.read( buf, 0, 1024 ) ) > -1 )
                    fileoutputstream.write( buf, 0, n );

                fileoutputstream.close(); 
                zipinputstream.closeEntry();
                zipentry = zipinputstream.getNextEntry();
            } // while

            zipinputstream.close();
        } catch( Exception e ) {
            e.printStackTrace();
        }

		return "";
	}
}
