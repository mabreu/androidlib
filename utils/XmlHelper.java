package br.com.datumti.library.utils;

import java.util.HashMap;
import java.util.Map;

public class XmlHelper {
	private HashMap<String, String> attributes;
	private String tagName;

	public XmlHelper( String tagName ) {
		super();
		this.tagName = tagName;
		attributes = new HashMap<String, String>();
	}

	public XmlHelper addAttr( String name, long value ) {
		attributes.put( name, "" + value );
		return this;
	}

	public XmlHelper addAttr( String name, double value ) {
		attributes.put( name, "" + value );
		return this;
	}

	public XmlHelper addAttr( String name, String value ) {
		attributes.put( name, value );
		return this;
	}

	public String toString() {
		String res = "<" + tagName;

		for( Map.Entry<String, String> attr : attributes.entrySet() ) {
			res += " " + attr.getKey() + "=\"" + attr.getValue() + "\""; 
		}

		return res + " />";
	}
}
