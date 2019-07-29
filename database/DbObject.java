package br.com.datumti.library.database;

public class DbObject {
	public enum DbOperation { dboInsert, dboUpdate, dboDelete };

	private String message;

	protected OnValidate validateEvent;

	public DbObject() {
		this.message = null;
		this.validateEvent = null;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setOnValidate( OnValidate validateEvent ) {
		this.validateEvent = validateEvent;
	}

	public boolean validate( DbOperation operation ) {
		if( validateEvent != null )
			return validateEvent.validate( this, operation );

		return true;
	}
}
