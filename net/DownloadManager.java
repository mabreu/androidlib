package br.com.datumti.library.net;
 
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
 
public class DownloadManager {
	private static String tag = "DownloadManager";
	public static final String UTF8 = "UTF8";
	public static final String ISO_8859_1 = "8859_1";

	public static String downloadString( URL url, String enconding ) {
		StringBuilder ret = new StringBuilder();

		try {
			// Abrindo conexao
			URLConnection conn = url.openConnection();
			BufferedReader read = new BufferedReader( new InputStreamReader(conn.getInputStream(), enconding) );

			conn.connect();
			String inputline;

			while( ( inputline = read.readLine() ) != null )
				ret.append( inputline );
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ret.toString();
	}
	
	public static String downloadFile( Context context, String urlString, String cacheFileName ) {
		return downloadFile( context, urlString, cacheFileName, "" );
	}

	public static String downloadFile( Context context, String urlString, String cacheFileName, HashMap<String, String> hmParams ) {
		String params = NetUtils.getQueryString( hmParams );
		return downloadFile( context, urlString, cacheFileName, params );
	}

	public static String downloadFile( Context context, String urlString, String cacheFileName, String params ) {
		String fileName = cacheFileName;
    	File file = new File( fileName );

        try {
        	if( ! file.exists() || file.length() == 0 ) {
            	if( file.exists() )
            		file.delete();

    			String dirName = fileName.substring( 0, fileName.lastIndexOf( "/" ) );
    			File dirHnd = new File( dirName );

    			if( ! dirHnd.exists() ) {
                	dirHnd.mkdirs();
    			}

            	HttpURLConnection conn;
            	
            	if( urlString.startsWith( "https://" ) )
            		conn = (HttpsURLConnection) new URL( urlString ).openConnection();
            	else
            		conn = (HttpURLConnection) new URL( urlString ).openConnection();

    			if( params != null && ! params.equals( "" ) ) {
    				conn.setRequestMethod( "POST" );
    				conn.setDoInput( true );
    				conn.setDoOutput( true );
    				conn.connect();
	    			OutputStream out = conn.getOutputStream();

	        		byte[] bytes = params.getBytes( UTF8 );
	        		out.write( bytes );
	        		out.flush();
	        		out.close();
    			}

				InputStream in = conn.getInputStream();
                FileOutputStream fos = new FileOutputStream( file );

                // Transferindo bytes para saida
                byte[] buffer = new byte[10240];
                int total, read, length, cont;
                total = 0;
                cont = 0;
                length = conn.getContentLength();
                DownloadInteface act = (DownloadInteface) context;

                while( ( read = in.read( buffer ) ) > 0 ) {
                	fos.write( buffer, 0, read );
                	total += read;
                	cont++;

                	if( cont == 100 ) {
                		act.updateProgress( total, length );
                		cont = 0;
                	}
                }

        		act.updateProgress( total, length );
                fos.flush();
                fos.close();
                in.close();
                conn.disconnect();
        	}
        } catch( IOException e ) {
//        	Log.e( tag, "Erro baixando arquivo. " + e );
        	if( file.exists() )
        		file.delete();

        	e.printStackTrace();
            return null;
        } catch( Exception e ) {
//        	Log.e( tag, "Erro baixando arquivo. " + e );
        	if( file.exists() )
        		file.delete();

            e.printStackTrace();
            return null;
        }

        return file.getPath();
    }

	public static Bitmap downloadImage(Context context, String urlString, String cacheFileName) {        
        Bitmap bitmap = null;

        try {
			String fileName = context.getCacheDir() + "/" + cacheFileName;
        	File file = new File( fileName );

        	if( ( cacheFileName != null ) && ( file.exists() ) ) {
    			FileInputStream infile = new FileInputStream(file);
    			bitmap = BitmapFactory.decodeStream(infile);
    			infile.close();
    		} else {
    			String dirName = fileName.substring( 0, fileName.lastIndexOf( "/" ) );
    			File dirHnd = new File( dirName );

    			if( ! dirHnd.exists() ) {
                	dirHnd.mkdirs();
    			}

    			URLConnection conn = new URL( urlString ).openConnection();
    	        InputStream in = conn.getInputStream();
                bitmap = BitmapFactory.decodeStream( in );

                FileOutputStream fileOutputStream = new FileOutputStream( file );
                BufferedOutputStream bos = new BufferedOutputStream( fileOutputStream );
                bitmap.compress( CompressFormat.PNG, 100, bos );

                bos.flush();
                bos.close();
                in.close();
        	}
        } catch( Exception e ) {
//        	Log.e(tag, "Erro baixando imagem. " + e );
            e.printStackTrace();
        }

        return bitmap;                
    }

	public static Bitmap downloadImage(String urlString) {        
        Bitmap bitmap = null;

        try {
			URLConnection conn = new URL( urlString ).openConnection();
	        InputStream in = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream( in );
            in.close();
        } catch( Exception e ) {
//        	Log.e(tag, "Erro baixando imagem. " + e );
            e.printStackTrace();
        }

        return bitmap;                
    }
}
