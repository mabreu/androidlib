package br.com.datumti.library.security;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hash {
	public static String md5(String in) {
        MessageDigest digest;

        try {
            digest = MessageDigest.getInstance( "MD5" );
            digest.reset();
            digest.update( in.getBytes() );
            byte[] a = digest.digest();
            int len = a.length;
            StringBuilder sb = new StringBuilder( len << 1 );

            for( int i = 0; i < len; i++ ) {
                sb.append( Character.forDigit( ( a[i] & 0xf0 ) >> 4, 16 ) );
                sb.append( Character.forDigit( a[i] & 0x0f, 16 ) );
            }

            return sb.toString();
        } catch( NoSuchAlgorithmException e ) {
        	e.printStackTrace();
        }

        return null;
    }

    public static String sha1(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException  { 
	    MessageDigest md = MessageDigest.getInstance( "SHA-1" );        
	    md.update( text.getBytes( "UTF-8" ), 0, text.length() );
	    byte[] sha1hash = md.digest();
	    return Crypto.bytesToHex( sha1hash );
    } 
}
