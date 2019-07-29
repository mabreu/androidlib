package br.com.datumti.library.net;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.SocketException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.NoHttpResponseException;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpTrace;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.text.TextUtils;

public class HttpSend extends AbstractHttpSend {
	public static enum Method { None, Get, Post, Put, Delete, Options, Trace, Head };

	private Method method;
	private int con_timeout; // Connection timeout. time waiting to establish connection
	private int mgr_timeout; // Con Manager timeout

	private ArrayList<String> trustedDomains;
	private URI uri;

	private HashMap<String, String> files;
	private ArrayList<NameValuePair> params;
	private HttpUriRequest hreq;

	public HttpSend( Context context ) {
		super( context );
		method = Method.Get;
		mgr_timeout = 10000;
		con_timeout = 30000;
		trustedDomains = null;
	}

	public HttpSend( Context context, String url ) {
		this( context );
		setUrl( url );
	}

	public HttpSend( Context context, URI uri ) {
		this( context );
		setURI( uri );
	}

	public HttpSend( Context context, int id_url ) {
		this( context, context.getString( id_url ) );
	}

	public HttpSend( Context context, Method method ) {
		this( context );
		setMethod( method );
	}

	public HttpSend( Context context, Method method, String url ) {
		this( context, url );
		setMethod( method );
	}

	public HttpSend( Context context, Method method, URI uri ) {
		this( context, uri );
		setMethod( method );
	}

	public HttpSend( Context context, Method method, int id_url ) {
		this( context, id_url );
		setMethod( method );
	}

	public void setMethod( Method method ) {
		this.method = method;
	}

	public Method getMethod() {
		return this.method;
	}

	public void setUrl(String url) {
		try {
			setURI( new URI( url ) );
/*
			int pos = url.indexOf( "/" );

			if( pos == -1 ) {
				setURI( new URI( url ) );
			} else {
				if( url.startsWith( "http://" ) ) {
					url = url.substring( 7 );
				} else
					if( url.startsWith( "https://" ) ) {
						url = url.substring( 8 );
					}

				String _host = url.substring( 0, pos );
				String _path = url.substring( pos );
				URI _uri = new URI( "http", _host, _path, "" );
				setURI( _uri );
			}
*/
		} catch( URISyntaxException e ) {
			lastErrorMessage = e.getMessage();
			e.printStackTrace();
		}
	}

	public void setUrl(int id_url) {
		setUrl( context.getString( id_url ) );
	}

	public String getUrl() {
		return ( uri == null ? null : uri.toString() );
	}

	public void setURI( URI uri ) {
		this.uri = uri;
	}

	public URI getURI() {
		return uri;
	}

    public void addTrustedDomain( String trustedDomain ) {
    	if( trustedDomains == null )
    		trustedDomains = new ArrayList<String>();

		trustedDomains.add( trustedDomain );
	}

	public ArrayList<String> getTrustedDomains() {
		return this.trustedDomains;
	}

	public void clearTrustedDomains() {
    	if( trustedDomains != null )
    		trustedDomains.clear();
	}

	public void setMgrTimeout( int timeout ) {
		mgr_timeout = timeout;
	}

	public int getMgrTimeout() {
		return mgr_timeout;
	}

	public void setConTimeout( int timeout ) {
		con_timeout = timeout;
	}

	public int getConTimeout() {
		return con_timeout;
	}

	public void addParam( String name, String value ) {
		if( value == null )
			return;

		if( params == null )
			params = new ArrayList<NameValuePair>();

		params.add( new BasicNameValuePair( name, value ) );
	}

	public void addParam( String name, long value ) {
		addParam( name, "" + value );
	}

	public void addParam( String name, double value ) {
		addParam( name, "" + value );
	}

	public void setParams( ArrayList<NameValuePair> params ) {
		this.params = params;
	}

	public void removeParam( String name ) {
		if( params != null ) {
			for( int i = 0; i < params.size(); i++ ) {
				if( ( (BasicNameValuePair) params.get( i ) ).getName().equals( name ) )
					params.remove( i );
			}
		}
	}

	public void clearParams() {
		params = null;
	}

	public void addFile( String paramName, String fileName ) {
		if( TextUtils.isEmpty( fileName ) )
			return;

		if( files == null )
			files = new HashMap<String, String>();

		files.put( paramName, fileName );
	}

	public void removeFile( String paramName ) {
		if( files != null )
			files.remove( paramName );
	}

	public void clearFiles() {
		files = null;
	}

	public void abort() {
		if( hreq != null && ! hreq.isAborted() ) {
			hreq.abort();
		}
	}

	public String doSend() {
		switch( method ) {
		case Post:
			hreq = new HttpPost( uri );
			break;

		case Put:
			hreq = new HttpPut( uri );
			break;

		case Delete:
			hreq = new HttpDelete( uri );
			break;

		case Head:
			hreq = new HttpHead( uri );
			break;

		case Options:
			hreq = new HttpOptions( uri );
			break;

		case Trace:
			hreq = new HttpTrace( uri );
			break;

		case Get:
		default:
			hreq = new HttpGet( uri );
			break;
		}

		lastErrorMessage = null;
		errorType = ErrorType.etNoError;
		String res;

		try {
            HttpParams conParams = new BasicHttpParams();
            HttpProtocolParams.setVersion( conParams, HttpVersion.HTTP_1_1 );
            HttpProtocolParams.setContentCharset( conParams, HTTP.UTF_8 );

            ConnManagerParams.setTimeout( conParams, mgr_timeout );
            HttpConnectionParams.setConnectionTimeout( conParams, con_timeout );
            HttpConnectionParams.setSoTimeout( conParams, skt_timeout );
            MultipartEntity entity = new MultipartEntity( HttpMultipartMode.BROWSER_COMPATIBLE );

        	if( params != null && params.size() > 0 ) {
//        		((HttpEntityEnclosingRequestBase) hreq).setEntity( new UrlEncodedFormEntity( params ) );

                for( NameValuePair nvp : params ) {
//                    if( params.get(i).getName().equalsIgnoreCase( "image" ) ) {
                        // If the key equals to "image", we use FileBody to transfer the data
//                        entity.addPart( params.get(i).getName(), new FileBody( new File ( params.get(i).getValue() ) ) );
//                    } else {
                        // Normal string data
                        entity.addPart( nvp.getName(), new StringBody( nvp.getValue() ) );
//                    }
                }
        	}

        	// If have files to send. Only for POST and PUT

        	if( files != null && files.size() > 0 && ( method == Method.Post || method == Method.Put ) ) {
//        		MultipartEntity entity = new MultipartEntity();

        		for( Entry<String, String> entry : files.entrySet() ) {
	        		entity.addPart( entry.getKey(), new FileBody( new File( entry.getValue() ) ) );

//	        		File file = new File( fileName );
//	        	    InputStreamEntity reqEntity = new InputStreamEntity( new FileInputStream( file ), -1 );
//	        	    reqEntity.setContentType( "binary/octet-stream" );
//	        	    reqEntity.setChunked( true );  // Send in multiple parts if needed
        		}

//        		if( method == Method.Post )
//            	    ((HttpPost) hreq).setEntity( entity );
//        		else
//            	    ((HttpPut) hreq).setEntity( entity );
        	}

        	if( method != Method.Get )
        		((HttpEntityEnclosingRequestBase) hreq).setEntity( entity );

    		HttpClient hc;

        	if( ! getUrl().startsWith( "https://" ) || trustedDomains == null || trustedDomains.isEmpty() )
        		hc = new DefaultHttpClient();
        	else {
                KeyStore trustStore = KeyStore.getInstance( KeyStore.getDefaultType() );
                trustStore.load( null, null );

        		SSLSocketFactory sf = new MySSLSocketFactory( trustStore );
//                sf.setHostnameVerifier( SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER );
                sf.setHostnameVerifier( new X509HostnameVerifier() {
					@Override
					public void verify(String host, String[] cns, String[] subjectAlts) throws SSLException {
					}

					@Override
					public void verify(String host, X509Certificate cert) throws SSLException {
					}

					@Override
					public void verify(String host, SSLSocket ssl) throws IOException {
					}

					@Override
					public boolean verify(String host, SSLSession session) {
						if( trustedDomains == null )
							return false;

						return trustedDomains.contains( host );
					}
				} );

                SchemeRegistry sr = new SchemeRegistry();
                sr.register( new Scheme( "http", PlainSocketFactory.getSocketFactory(), 80 ) );
                sr.register( new Scheme( "https", sf, 443 ) );

	            ClientConnectionManager ccm = new ThreadSafeClientConnManager( conParams, sr );
	    		hc = new DefaultHttpClient( ccm, conParams );
        	}

        	HttpResponse response = hc.execute( hreq );
        	res = ( response == null ? null : EntityUtils.toString( response.getEntity(), "UTF8" ) );

//            BufferedReader bufferedReader = new BufferedReader( new InputStreamReader( response.getEntity().getContent() ) );
//            StringBuffer sb = new StringBuffer();
//            String LineSeparator = System.getProperty( "line.separator" );
//            String line;
//
//            while( ( line = bufferedReader.readLine() ) != null ) {
//            	sb.append( line + LineSeparator );
//            }
//
//            bufferedReader.close();
//            res = sb.toString();

        } catch( SocketException e ) {
			errorType = ErrorType.etSocketError;
			lastErrorMessage = e.getMessage();
			e.printStackTrace();
			res = null;

        } catch( NoHttpResponseException e ) {
			errorType = ErrorType.etNoResponseError;
			lastErrorMessage = e.getMessage();
			e.printStackTrace();
			res = null;

        } catch( UnsupportedEncodingException e ) {
			errorType = ErrorType.etEncodingError;
			lastErrorMessage = e.getMessage();
			e.printStackTrace();
			res = null;

		} catch( ClientProtocolException e ) {
			errorType = ErrorType.etClientProtError;
			lastErrorMessage = e.getMessage();
			e.printStackTrace();
			res = null;

		} catch( IOException e) {
			errorType = ErrorType.etIOError;
			lastErrorMessage = e.getMessage();
			e.printStackTrace();
			res = null;

		} catch( ParseException e) {
			errorType = ErrorType.etParserError;
			lastErrorMessage = e.getMessage();
			e.printStackTrace();
			res = null;

		} catch( Exception e) {
			errorType = ErrorType.etGenericError;
			lastErrorMessage = e.getMessage();
			e.printStackTrace();
			res = null;
		}

        return res;
	}

	private class MySSLSocketFactory extends SSLSocketFactory {
	    SSLContext sslContext = SSLContext.getInstance( "TLS" );

	    public MySSLSocketFactory(KeyStore truststore) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
	        super(truststore);

	        TrustManager tm = new X509TrustManager() {
	            @Override
				public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
	            }

	            @Override
				public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
	            }

	            @Override
				public X509Certificate[] getAcceptedIssuers() {
	                return null;
	            }
	        };

	        sslContext.init(null, new TrustManager[] { tm }, null);
	    }

	    @Override
	    public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException {
	        return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
	    }

	    @Override
	    public Socket createSocket() throws IOException {
	        return sslContext.getSocketFactory().createSocket();
	    }
	}
}
