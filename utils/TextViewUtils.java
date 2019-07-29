package br.com.datumti.library.utils;

import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.widget.TextView;

public class TextViewUtils {
	/**
	 * Adiciona o estilo ao texto do TextView e adiciona o texto ao TextView
	 * 
	 * @param tv 	= TextView alvo
	 * @param style	= Style que será usado
	 * @param value = Valor do texto que será adicionado ao TextView
	 * @param start = Indice de início do "value" que irá receber o style
	 * @param end   = Indice de fim do "value" que irá receber o style
	 */
	public static void applyStyle(TextView tv, int style, String value, int start, int end) {
		tv.setText( value, TextView.BufferType.SPANNABLE );
        Spannable tvSpan = (Spannable) tv.getText();
        tvSpan.setSpan( new StyleSpan( style ), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE );
	}
	
	/**
	 * Adiciona o estilo ao TextView
	 * 
	 * @param tv 	= TextView alvo
	 * @param style	= Style que será usado
	 * @param start = Indice de início do texto que irá receber o style
	 * @param end   = Indice de fim do texto que irá receber o style
	 */
	public static void applyStyle(TextView tv, int style, int start, int end) {
        Spannable tvSpan = (Spannable) tv.getText();
        tvSpan.setSpan( new StyleSpan( style ), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE );
	}
	
	/**
	 * Adiciona o estilo ao TextView em uma determinada palavra que deve existir no TextView
	 * 
	 * @param tv	= TextView alvo
	 * @param style	= Style que será usado
	 * @param target= Palavra que irá receber o estilo
	 */
	public static void applyStyle(TextView tv, int style, String target) {
		StyleSpan styleSpan = new StyleSpan(style);
        Spannable tvSpan = (Spannable) tv.getText();
        int init = 0;
        int start = tvSpan.toString().indexOf( target, init );

        while( start >= 0 ) {
        	int end = start + target.length();
        	tvSpan.setSpan( styleSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE );
        	init = end;
        	start = tvSpan.toString().indexOf( target, init );
        }
	}
	
	public static void applyForegroundColor(TextView tv, int color, String target) {
		ForegroundColorSpan fgColorSpan = new ForegroundColorSpan( color );
        Spannable tvSpan = (Spannable) tv.getText();
        int init = 0;
        int start = tvSpan.toString().indexOf( target, init );

        while( start >= 0 ) {
        	int end = start + target.length();
        	tvSpan.setSpan( fgColorSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE );
        	init = end;
        	start = tvSpan.toString().indexOf( target, init );
        }
	}

	public static void applyForegroundColor(TextView tv, int color, String value, int start, int end) {
		tv.setText( value, TextView.BufferType.SPANNABLE );
        Spannable tvSpan = (Spannable) tv.getText();
        tvSpan.setSpan( new ForegroundColorSpan( color ), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE );
	}
}
