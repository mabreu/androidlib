package br.com.datumti.library.utils;

import android.location.Location;
import android.util.FloatMath;

public class Matemat {
	public static double arrange( int n, int p ) {
		/*
		 * Calcula o Arranjo de "n" "p" a "p"
		 */
		return fatDivFat( n, n - p );
	}

	public static double combine( int n, int p ) {
		/*
		 * Calcula a combinação de "n" "p" a "p"
		 */
		return fatDivFat( n, n - p ) / factorial( p );
	}

	public static double fatDivFat( int n, int d ) {
		/*
		 * Calcula o fatorial de um numero dividido pelo fatorial de outro
		 */

		if( n == d )
			return 1;

		double res;

		if( n > d ) {
			res = n;

			for( int nind = d + 1; nind < n; nind++ )
				res *= nind;
		} else {
			res = d;

			for( int nind = n + 1; nind < d; nind++ )
				res *= nind;

			res = 1 / res;
		}

		return res;
	}

	public static double factorial( long n ) {
		/*
		 * Objetivo: Calcula o fatorial de um numero
		 */

		if( n < 0 )
			return 0;

		double res = n;

		while( --n > 1 )
			res *= n;

		return res;
	}

	public static double gps2m( Location loc_a, Location loc_b ) {
		return gps2m( loc_a.getLatitude(), loc_a.getLongitude(), loc_b.getLatitude(), loc_b.getLongitude() );
	}

	public static double gps2m( double lat_a, double lng_a, double lat_b, double lng_b ) {
		double pk = (180 / Math.PI);

	    double a1 = lat_a / pk;
	    double a2 = lng_a / pk;
	    double b1 = lat_b / pk;
	    double b2 = lng_b / pk;

	    double t1 = Math.cos(a1) * Math.cos(a2) * Math.cos(b1) * Math.cos(b2);
	    double t2 = Math.cos(a1) * Math.sin(a2) * Math.cos(b1) * Math.sin(b2);
	    double t3 = Math.sin(a1) * Math.sin(b1);
	    double tt = Math.acos(t1 + t2 + t3);

	    return 6366000 * tt;
	}

	public static long mdc( long n1, long n2 ) {
		/*
		 * Calcula o M.D.C. entre dois numeros
		 */
		long res;

		if( n1 > n2 ) {
			res = n1;
			n1 = n2;
			n2 = res;
		}

		if( ( n2 % n1 ) == 0 )
			return n1;

		res = n1 / 2;

		while( ( ( n1 % res ) == 0 || ( n1 % res ) == 0 ) && res > 1 )
			res--;

		return res;
	}

	public static long mmc( long n1, long n2 ) {
		/*
		 * Calcula o M.M.C. entre dois numeros
		 */
		if( n1 == n2 )
			return n1;

		long nind1, nind2;

		if( n1 > n2 ) {
			nind1 = n1;
			n1 = n2;
			n2 = nind1;
		}

		nind1 = 1;
		nind2 = 1;

		while( true ) {
			while( ( n1 * nind1 ) < ( n2 * nind2 ) )
				nind1++;

			if( ( n1 * nind1 ) == ( n2 * nind2 ) )
				return n1 * nind1;

			nind2++;
		}
	}

	public static boolean isPrime( long num ) {
		/*
		 * Verifica se o número é primo
		 */
		double limite = (long) Math.sqrt( num );

		if( Math.sqrt( num ) == limite )
			return false;

		for( int divisor = 2; divisor <= limite; divisor++ ) {
			if( ( num % divisor ) == 0 )
				return false;
	    }

		return true;
	}

	public static double root( long i, long n ) {
		/*
		 * Calcula a raiz i do número n
		 */
		return ( i == 0 ? -1 : n ^ ( 1 / i ) );
	}
}
