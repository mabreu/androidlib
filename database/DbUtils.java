package br.com.datumti.library.database;

public class DbUtils {
	private String campos;
	private String tabela;

	public DbUtils() {
	}

	public String montaInsert( String dados ) {
		return "INSERT INTO " + tabela + "(" + campos + ") VALUES(" + dados + ")";
	}

	public String montaUpdate( String atribuicoes, String condicao ) {
		return "UPDATE " + tabela + " SET " + atribuicoes + " WHERE " + condicao;
	}

	public String montaDelete( String condicao ) {
		return "DELETE FROM " + tabela + " WHERE " + condicao;
	}

	public String getCampos() {
		return campos;
	}

	public void setCampos(String campos) {
		this.campos = campos;
	}

	public String getTabela() {
		return tabela;
	}

	public void setTabela(String tabela) {
		this.tabela = tabela;
	}
}
