package br.com.datumti.library.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

public class ResourceUtils {
	public static String loadRawToString(Context context, int resId ) {
		try {
			StringBuilder data = new StringBuilder();
			InputStream is = context.getResources().openRawResource( resId );

			try {
				BufferedReader in = new BufferedReader( new InputStreamReader( is ) );
				String str;

				while( ( str = in.readLine() ) != null )
					data.append( str );

				in.close();
			} catch( IOException e ) {
				Log.e( "ResouceUtils", "Não foi possível ler o arquivo.", e );
			}

			return data.toString();
		} catch( Exception e ) {
			Log.e( "ResourceUtils", "Não foi possível abrir o raw arquivo", e );
		}

		return null;
	}

	public static boolean isAppInstalled(Context context, String packageName) {
		PackageManager pm = context.getPackageManager();
		boolean app_installed = false;

		try {
			pm.getPackageInfo( packageName, PackageManager.GET_ACTIVITIES );
			app_installed = true;
		} catch( PackageManager.NameNotFoundException e ) {
//			app_installed = false;
		}

		return app_installed;
	}
}
