package br.com.datumti.library.utils;

public class Extenso {
	private static final String[] aCentoM = { "Cem", "Duzentos", "Trezentos", "Quatrocentos", "Quinhentos", "Seissentos", "Setecentos", "Oitocentos", "Novecentos" };
	private static final String[] aCentoF = { "Cem", "Duzentas", "Trezentas", "Quatrocentas", "Quinhentas", "Seissentas", "Setecentas", "Oitocentas", "Novecentas" };

	private static final String[] aDezena = { "Dez", "Vinte", "Trinta", "Quarenta", "Cinquenta", "Sessenta", "Setenta", "Oitenta", "Noventa" };

	private static final String[] aTeen = { "Onze", "Doze", "Treze", "Catorze", "Quinze", "Dezesseis", "Dezessete", "Dezoito", "Dezenove" };

	private static final String[] aUnidadeM = { "Um", "Dois", "Tres", "Quatro", "Cinco", "Seis", "Sete", "Oito", "Nove" };
	private static final String[] aUnidadeF = { "Uma", "Duas", "Tres", "Quatro", "Cinco", "Seis", "Sete", "Oito", "Nove" };

	private static final String[] aGrupos = { "Quadrilhoes", "Trilhoes", "Bilhoes", "Milhoes", "Mil" };
	private static final String[] aGrupo = { "Quadrilhao", "Trilhao", "Bilhao", "Milhao", "Mil" };

	private static final int TAMANHO = 18;
	private static final int PASSO = 3;
	private static final int LIMITE = 6;

	private String[] aInteiro = { "Real", "Reais" };
	private String[] aDecimal = { "Centavo", "Centavos" };
	private boolean bGeneroMasc = true;

	public Extenso() {

	}

	public Extenso setGenero( boolean bGeneroMasc ) {
		this.bGeneroMasc = bGeneroMasc;
		return this;
	}

	public Extenso setStrInteiros( String singular, String plural ) {
		aInteiro[ 0 ] = singular;
		aInteiro[ 1 ] = plural;
		return this;
	}

	public Extenso setStrDecimais( String singular, String plural ) {
		aDecimal[ 0 ] = singular;
		aDecimal[ 1 ] = plural;
		return this;
	}

	public String getText( double nnum ) {
		/*
		 * Converter um numero para o seu valor por extenso.
		 */
		if( nnum == 0.00 ) {
			return "Zero " + aInteiro[ 1 ];
		}

		boolean lnegativo;

		if( nnum < 0.00 ) {
			lnegativo = true;
			nnum = nnum * -1;
		} else
	         lnegativo = false;

		boolean lvirgula = false;
		String cinteiro = String.format( "%.4f",  nnum );
		cinteiro = StringUtils.left( cinteiro, cinteiro.length() - 5 );
		String cdecimal = StringUtils.right( String.format( "%.2f", nnum - Double.parseDouble( cinteiro ) ), 2 );
		cinteiro = StringUtils.repeat( "0", TAMANHO ) + cinteiro.trim();
		cinteiro = StringUtils.right( cinteiro, TAMANHO );
		String ZEROS = StringUtils.repeat( "0", PASSO );
		String Result = "";
		String cgrupo;
		int pos;

		for( int ngr = 0; ngr < LIMITE; ngr++ ) {
			if( lvirgula ) {
				Result   = Result.trim() + ", ";
				lvirgula = false;
			}

			pos = ngr * PASSO;
			cgrupo = cinteiro.substring( pos, pos + PASSO );

			if( cgrupo.equals( "000" ) )
				continue;

			Result += miniExtenso( cgrupo );
			pos = ( ngr + 1 ) * PASSO;

			if( ngr < LIMITE && ! cinteiro.substring( pos, pos + PASSO ).equals( ZEROS ) )
				lvirgula = true;

			if( ngr < LIMITE )
				if( Integer.parseInt( cgrupo ) >= 2 )
					Result += aGrupos[ ngr ] + ' ';
				else
					if( Integer.parseInt( cgrupo ) == 1 )
						Result += aGrupo[ ngr ] + ' ';
		}

		if( StringUtils.right( cinteiro, 6 ).equals( StringUtils.repeat( "0", 6 ) ) && Double.parseDouble( cinteiro ) != 0.00 )
	        Result += "De ";

		if( nnum >= 2.00 )
			Result += aInteiro[ 2 ];
		else
			if( nnum >= 1.00 )
				Result += aInteiro[ 1 ];

		if( Double.parseDouble( cdecimal ) != 0.00 && Double.parseDouble( cinteiro ) != 0.00 )
	        Result += " e ";

		Result += miniExtenso( '0' + cdecimal );

		if( Integer.parseInt( cdecimal ) >= 2 )
	        Result += aDecimal[ 2 ];
		else
			if( Integer.parseInt( cdecimal ) == 1 )
				Result += aDecimal[ 1 ];

		if( lnegativo )
			Result += "(Negativo)";

		return Result.trim();
	}

	private String miniExtenso( String cnum ) {
		/*
		 * Retornar um número por extenso
		 */
		int nnum_aux;
		String res = "";

		for( int npos = 0; npos < PASSO; npos++ ) {
			nnum_aux = Integer.parseInt( cnum.substring( npos, npos + 1 ) );

			if( nnum_aux != 0 ) {
				switch( npos ) {
					case 0:
						if( nnum_aux == 1 && !cnum.substring( 1, 3 ).equals( "00" ) ) {
							res += "Cento ";
						} else {
							if( this.bGeneroMasc ) {
								res += aCentoM[nnum_aux] + ' ';
							} else {
								res += aCentoF[nnum_aux] + ' ';
							}
						}

						if( !StringUtils.right( cnum, 2 ).equals( "00" ) ) {
							res += "e ";
						}

						break;

					case 1:
						if( nnum_aux == 1 ) {
							nnum_aux = Integer.parseInt( cnum.substring( PASSO, PASSO + 1 ) );

							if( nnum_aux != 0 ) {
								res += aTeen[ nnum_aux ] + ' ';
								return res;
							}

							res += aDezena[ 1 ] + ' ';
						} else {
							res += aDezena[ nnum_aux ] + ' ';

							if( ! StringUtils.right( cnum, 1 ).equals( '0' ) ) {
								res += "e ";
							}
						}

						break;

					case 2:
						if( this.bGeneroMasc ) {
							res += aUnidadeM[nnum_aux] + ' ';
						} else {
							res += aUnidadeF[nnum_aux] + ' ';
						}
				}
			}
		}

		return res;
	}
}
