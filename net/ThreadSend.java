package br.com.datumti.library.net;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import br.com.datumti.library.net.AbstractHttpSend.ErrorType;

public class ThreadSend extends AsyncTask<AbstractHttpSend, String, Object> {
	private static final String tag = "ThreadSend";

	public static enum ShowMessageBy { smbNone, smbToast, smbAlert };
	public static enum ThreadSendError { tseNone, tseNoNet, tseError, tseCancelled, tseParser };

	private boolean showProgress = true;
	private boolean showProgressBar = false;

	private ShowMessageBy showMessageBy = ShowMessageBy.smbNone;
	private ShowMessageBy showErrorBy = ShowMessageBy.smbAlert;

	private ThreadSendError error = ThreadSendError.tseNone;

	private String progressMsg = "Baixando...";
	private String endMsg = "Concluído.";
	private String noNetMsg = "Sem Internet.";
	private String errorMsg = "Erro durante download.";
	private String cancelledMsg = "O download foi cancelado.";

	private AbstractHttpSend hs;
	private Context context;
	private ThreadSendInterface tsi;
	private ThreadSendBackgroundParser tsbp;
	private ProgressDialog dialog = null;

	private ProgressBar pb;
	private int old_visibility;

	public ThreadSend( Context context ) {
		this.context = context;

		if( context instanceof ThreadSendBackgroundParser )
			setThreadSendBackgroundParser( (ThreadSendBackgroundParser) context );
		else
			setThreadSendInterface((ThreadSendInterface) context );
	}

	public ThreadSend( Context context, ThreadSendInterface tsi ) {
		this.context = context;
		setThreadSendInterface( tsi );
	}

	public ThreadSend( Context context, ThreadSendBackgroundParser tsbp ) {
		this.context = context;
		setThreadSendBackgroundParser( tsbp );
	}

	public void setThreadSendInterface( ThreadSendInterface tsi ) {
		this.tsi = tsi;
	}

	public void setThreadSendBackgroundParser( ThreadSendBackgroundParser tsbp ) {
		this.tsbp = tsbp;
	}

	public void setErrorMessage( int idErrorMsg ) {
		setErrorMessage( context.getString( idErrorMsg ) );
	}

	public void setErrorMessage( String errorMsg ) {
		this.errorMsg = errorMsg;
	}

	public void setNoNetMessage( int idNoNetMsg ) {
		setErrorMessage( context.getString( idNoNetMsg ) );
	}

	public void setNoNetMessage( String noNetMsg ) {
		this.noNetMsg = noNetMsg;
	}

	public String getCancelledMessage() {
		return cancelledMsg;
	}

	public void setCancelledMessage( int idCancelledMsg ) {
		setCancelledMessage( idCancelledMsg );
	}

	public void setCancelledMessage( String cancelledMsg ) {
		this.cancelledMsg = cancelledMsg;
	}

	public void setEndMessage( int idEndMsg ) {
		setEndMessage( context.getString( idEndMsg ) );
	}

	public void setEndMessage( String endMsg ) {
		this.endMsg = endMsg;
	}

	public void setProgressMessage( int idprogressMsg ) {
		setProgressMessage( context.getString( idprogressMsg ) );
	}

	public void setProgressMessage( String progressMsg ) {
		this.progressMsg = progressMsg;
	}

	public void setShowProgress( boolean showProgress ) {
		this.showProgress = showProgress;
	}

	public boolean getShowProgress() {
		return this.showProgress;
	}

	public void setShowProgressBar( boolean showProgressBar ) {
		this.showProgressBar = showProgressBar;
	}

	public boolean getShowProgressBar() {
		return this.showProgressBar;
	}

	public void setProgressBar( ProgressBar pb ) {
		this.pb = pb;
	}

	public ProgressBar getProgressBar() {
		return this.pb;
	}

	public ShowMessageBy getShowMessageBy() {
		return showMessageBy;
	}

	public void setShowMessageBy(ShowMessageBy showMessageBy) {
		this.showMessageBy = showMessageBy;
	}

	public ShowMessageBy getShowErrorBy() {
		return showErrorBy;
	}

	public void setShowErrorBy(ShowMessageBy showErrorBy) {
		this.showErrorBy = showErrorBy;
	}

	public void setShowNothing() {
		setShowProgress( false );
		setShowProgressBar( false );
		setShowErrorBy( ShowMessageBy.smbNone );
		setShowMessageBy( ShowMessageBy.smbNone );
	}

	public ThreadSendError getError() {
		return error;
	}

	public void setError(ThreadSendError tsError) {
		this.error = tsError;
	}

	private String getMessage() {
		switch( error ) {
		case tseNone:
			return endMsg;

		case tseNoNet:
			return noNetMsg;

		case tseCancelled:
			return cancelledMsg;

		case tseError:
			return errorMsg;
		}

		return "";
	}

	private void showToast() {
		Toast.makeText( context, getMessage(), Toast.LENGTH_LONG ).show();
	}

	private void showAlert() {
		AlertDialog.Builder builder = new AlertDialog.Builder( context );
		builder.setTitle( context.getApplicationInfo().name );
		builder.setIcon( android.R.drawable.ic_dialog_info );
		builder.setNeutralButton( android.R.string.ok, null );
		builder.setMessage( getMessage() );
		builder.show();
	}

	private void cancelProcess() {
		if( error == ThreadSendError.tseNone ) {
			error = ThreadSendError.tseCancelled;

			if( hs != null && hs instanceof HttpSend ) {
				((HttpSend) hs).abort();
			}
		}
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		Log.d( tag, "onPreExecute" );

		if( ! NetUtils.isNetworkAvailable( context ) ) {
   			error = ThreadSendError.tseNoNet;
   			return;
		}

		error = ThreadSendError.tseNone;

		if( showProgressBar && pb != null ) {
			old_visibility = pb.getVisibility();
			pb.setVisibility( View.VISIBLE );
		}

		if( showProgress )
			dialog = ProgressDialog.show( context, context.getApplicationInfo().name, progressMsg, true, true,
						new OnCancelListener() {
							@Override
							public void onCancel(DialogInterface dialog) {
								cancelProcess();
							}
						} );
	}

	@Override
	protected Object doInBackground(AbstractHttpSend... params) {
		Log.d( tag, "doInBackground" );

		if( error != ThreadSendError.tseNone )
			return null;

		hs = params[0];
		String res = hs.doSend();

		if( res == null || hs.getLastErrorType() != ErrorType.etNoError || ! TextUtils.isEmpty( hs.getLastErrorMessage() ) )
			error = ThreadSendError.tseError;

		if( tsbp == null )
			return res;

		Object objRes;

		try {
			objRes = tsbp.onBackgroungParser( res, this );
		} catch( Exception e ) {
			// apenas para não propagar um erro no método externo
			objRes = res;
			setError( ThreadSendError.tseParser );
			setErrorMessage( e.getMessage() );
			e.printStackTrace();
		}

		return objRes;
	}

	@Override
	protected void onProgressUpdate(String... progress) {
	}

	@Override
	protected void onPostExecute(Object result) {
		Log.d( tag, "onPostExecute" );

		try {
			if( error != ThreadSendError.tseNone )
				tsbp.onDownloadError( hs.getLastErrorType(), hs.getLastErrorMessage() );
			else
				if( tsbp != null )
					tsbp.onDownloadFinished( result );
				else
					tsi.onDownloadFinished( (String) result );
		} catch( Exception e ) {
			// apenas para não propagar um erro no método externo
			e.printStackTrace();
		}

		if( dialog != null ) {
			dialog.dismiss();
			dialog = null;
		}

		if( showProgressBar && pb != null )
			pb.setVisibility( old_visibility );

		// Se for uma Activity e ela estiver fechada, não exibe mensagem
		if( ( context instanceof Activity ) && ((Activity) context).isFinishing() )
			return;

		if( error == ThreadSendError.tseNone ) { // Se não teve erro, verifica se precisa exibir mensagem
			if( showMessageBy == ShowMessageBy.smbToast )
				showToast();
			else
				if( showMessageBy == ShowMessageBy.smbAlert )
					showAlert();
		} else { // Se teve erro, verifica se precisa exibir mensagem
			if( showErrorBy == ShowMessageBy.smbToast )
				showToast();
			else
				if( showErrorBy == ShowMessageBy.smbAlert )
					showAlert();
		}
	}
}
