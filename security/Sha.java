package br.com.datumti.library.security;

public class Sha {
	static public native String stringToSha(String p1, String p2, String p3, String p4, String p5, String p6, String p7, String p8, String p9, 
				String p10, String p11, String p12, String p13, String p14, String p15, String p16, String p17, String p18, String p19, String p20);

	static { 
	    System.loadLibrary("sha");
	}
}
