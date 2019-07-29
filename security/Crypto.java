package br.com.datumti.library.security;

public class Crypto {
	public static byte[] hexToBytes(String str) {
		if( str == null || str.length() < 2 ) {
			return null;
		} else {
			int len = str.length() / 2;
			byte[] buffer = new byte[ len ];

			for( int i = 0; i < len; i++ ) {
				buffer[i] = (byte) Integer.parseInt( str.substring( i * 2, i * 2 + 2 ), 16 );
			}

			return buffer;
		}
	}

	public static String bytesToHex(byte[] data) {
		if( data == null ) {
			return null;
		} else {
			int len = data.length;
			int b;
			String str = "";

			for( int i = 0; i < len; i++ ) {
				b = data[i] & 0xFF;

				if( b < 16 )
					str = str + "0" + java.lang.Integer.toHexString( b );
				else
					str = str + java.lang.Integer.toHexString( b );
			}

			return str.toUpperCase();
		}
	}
}
