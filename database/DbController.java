package br.com.datumti.library.database;

public class DbController extends DbObject {
	private DbConnection conn;

	public DbController() {
		super();
	}

	public DbController( DbConnection conn ) {
		this();
		this.conn = conn;
	}

	public DbConnection getDbConnection() {
		return conn;
	}

	public DbController setDbConnection( DbConnection conn ) {
		this.conn = conn;
		return this;
	}
}
