package br.com.datumti.library.utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.text.TextUtils;

public class StringUtils {
	
	final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

	public static String bytesToHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];

		for( int j = 0; j < bytes.length; j++ ) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}

		return new String( hexChars );
	}

	public static String emptyOr(String s) {
		return ( ( ! TextUtils.isEmpty( s ) ) ? s : "" );
	}

	public static String fromCharCode(int... codePoints) {
		return new String( codePoints, 0, codePoints.length );
	}

	public static String getBasename(String s) {
		int sep = s.lastIndexOf( "/" );
		return s.substring( sep + 1, s.length() );
	}

	public static String getBasename(String s, String ext) {
		int sep = s.lastIndexOf( "/" );
		String str = s.substring( sep + 1, s.length() );
		return str.replaceAll( '.' + ext, "" );
	}

	public static String getExtension(String s) {
		int dot = s.lastIndexOf( "." );
		return s.substring( dot + 1 );
	}

	public static String left( String s, int n ) {
		if( n < 0 )
			return right( s, -n );

		return s.substring( 0, n );
	}

	public static String removeMask( String valor ) {
		return valor.replace( ".", "" ).replace( "-", "" ).replace( "/", "" ).replace( "(", "" ).replace( ")", "" );
	}

	public static String repeat( String str, int times ) {
		return new String( new char[times] ).replace( "\0", str );
	}

	public static String right( String s, int n ) {
		if( n < 0 )
			return left( s, -n );

		return s.substring( s.length() - n );
	}

	public String substr( String str, int ini, int qtd ) {
		return str.substring( ini, ini + qtd );
	}

	public static String StreamToString(InputStream is) throws IOException {
		if( is == null )
			return "";

		Writer writer = new StringWriter();
		char[] buffer = new char[1024];

		try {
			Reader reader = new BufferedReader( new InputStreamReader( is, "UTF-8" ) );
			int n;

			while( ( n = reader.read( buffer ) ) != -1 ) {
				writer.write( buffer, 0, n );
			}
		} finally {
			is.close();
		}

		return writer.toString();
	}

	public static InputStream StringToStream(String str) throws UnsupportedEncodingException {
		if( str == null )
			return null;

		return new ByteArrayInputStream( str.getBytes( "UTF-8" ) );
	}

	public static String transform( String str, String mask ) {
		char[] cstr = str.toCharArray();
		char[] cmask = mask.toCharArray();
		StringBuilder res = new StringBuilder();
		int m = 0, s = 0;

		while( m < cmask.length && s < cstr.length ) {
			if( cmask[m] == 'X' ) {
				res.append( cstr[s] );
				m++;
				s++;
			} else {
				res.append( cmask[m] );
				m++;
			}
		}

		return res.toString();
	}

	public static String unescapeHTML(String str) {
		Pattern pattern = Pattern.compile( "&#([^;]+);" );
		StringBuffer b = new StringBuffer();
		Matcher matcher = pattern.matcher( str );

		while( matcher.find() ) {
			String c = matcher.group( 1 );
			matcher.appendReplacement( b, StringUtils.fromCharCode( Integer.parseInt( c ) ) );
		}

		matcher.appendTail( b );
		return b.toString();
	}

	public static boolean validateEmail( String email ) {
		return email.matches( "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$" );
	}
}
