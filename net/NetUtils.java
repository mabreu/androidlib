package br.com.datumti.library.net;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetUtils {
	private static final String tag = "NetUtils";
	
	public static boolean isNetworkAvailable( Context context ) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService( Context.CONNECTIVITY_SERVICE );

		if( cm != null ) {
			NetworkInfo[] info = cm.getAllNetworkInfo();
			
			if( info != null )
				for( int i = 0; i < info.length; i++ )
					if( info[i].getState() == NetworkInfo.State.CONNECTED )
						return true;
		}

		return false;
	}

	@SuppressWarnings("unchecked")
	public static String getQueryString( Map params ) {
		String result = null;

		try {
			if( params == null || params.size() == 0 )
				return null;

			Iterator i = (Iterator) params.keySet().iterator();
			String chave, valor;
			Object objValor;

			while( i.hasNext() ) {
				chave = (String) i.next();
				objValor = params.get( chave );

				if( objValor != null ) {
					valor = URLEncoder.encode( objValor.toString(), "UTF8" );
					result = ( result == null ? "" : result + "&" ) + chave + "=" + valor;
				}
			}
        } catch( IOException e ) {
//			Log.e( tag, "Erro executando Post: " + e );
        	e.printStackTrace();
			result = null;
        }

		return result;
	}
	
	public static String getQueryString( String str ) {
		String result;

		try {
			result = URLEncoder.encode( str, "UTF8" );
		} catch (UnsupportedEncodingException e) {
        	e.printStackTrace();
			result = "";
		}

		return result;
	}
}
