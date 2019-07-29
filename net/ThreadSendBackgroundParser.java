package br.com.datumti.library.net;

import br.com.datumti.library.net.AbstractHttpSend.ErrorType;

public interface ThreadSendBackgroundParser {
	public Object onBackgroungParser( String result, ThreadSend ts );
	public void onDownloadError( ErrorType errorType, String lastErrorMessage );
	public void onDownloadFinished( Object result );
}
