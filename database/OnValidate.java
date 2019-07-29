package br.com.datumti.library.database;

import br.com.datumti.library.database.DbObject.DbOperation;

public interface OnValidate {
	public boolean validate( DbObject obj, DbOperation operation );
}
