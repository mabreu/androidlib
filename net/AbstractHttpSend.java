package br.com.datumti.library.net;

import android.content.Context;

public abstract class AbstractHttpSend {
	public static enum ErrorType { etNoError, etGenericError, etIOError, etClientProtError, etEncodingError,
									etNoResponseError, etSocketError, etParserError };
	
	protected Context context;
	protected ErrorType errorType = ErrorType.etNoError;
	protected String lastErrorMessage;
	protected int skt_timeout; // Socket timeout. time waiting for response

	public AbstractHttpSend( Context context ) {
		super();
		this.context = context;
		this.skt_timeout = 30000;
	}

	public void setSktTimeout( int timeout ) {
		skt_timeout = timeout;
	}

	public int getSktTimeout() {
		return skt_timeout;
	}

	public String getLastErrorMessage() {
		return this.lastErrorMessage;
	}

	public ErrorType getLastErrorType() {
		return errorType;
	}

	public abstract void setUrl(String url);
	public abstract void setUrl(int id_url);
	public abstract String getUrl();

	public abstract void addParam( String name, String value );
	public abstract void addParam( String name, long value );
	public abstract void addParam( String name, double value );
	public abstract void clearParams();

	public abstract String doSend();
}
