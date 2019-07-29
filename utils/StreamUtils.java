package br.com.datumti.library.utils;

import java.io.InputStream;
import java.io.OutputStream;

public class StreamUtils {
    private static final int buffer_size = 1024;

    public static void CopyStream(InputStream is, OutputStream os) {
        try {
            byte[] bytes = new byte[ buffer_size ];

            for(;;) {
				int count = is.read( bytes, 0, buffer_size );

				if( count == -1 )
					break;

				os.write( bytes, 0, count );
            }
        } catch( Exception e ) {
        	e.printStackTrace();
        }
    }
}
