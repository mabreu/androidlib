package br.com.datumti.library.net;

import br.com.datumti.library.net.AbstractHttpSend.ErrorType;

public interface ThreadSendInterface {
	public void onDownloadError( ErrorType errorType, String lastErrorMessage );
	public void onDownloadFinished( String result );
}
