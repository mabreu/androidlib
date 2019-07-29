package br.com.datumti.library.utils;

import android.util.DisplayMetrics;
import android.view.Display;

public class DisplayUtils {
	
	public static boolean isTablet(Display display) {
//	    Display display = getWindowManager().getDefaultDisplay();
	    DisplayMetrics displayMetrics = new DisplayMetrics();
	    display.getMetrics(displayMetrics);

	    int width = displayMetrics.widthPixels / displayMetrics.densityDpi;
	    int height = displayMetrics.heightPixels / displayMetrics.densityDpi;

	    double screenDiagonal = Math.sqrt( width * width + height * height );
	    return (screenDiagonal >= 6.0 );
	}
}
