package com.ideanest.swing;

import java.text.*;

/**
 * Adapts a <code>MessageFormat</code> that requires an array of
 * arguments into a <code>Format</code> that takes only one argument
 * and places it in the first location of the array.
 */
public class OneArgMessageFormatAdapter extends Format {
	private final MessageFormat format;

	public OneArgMessageFormatAdapter(MessageFormat format) {
		this.format = format;
	}

	public StringBuffer format(Object o, StringBuffer buf, FieldPosition pos) {
		return format.format(new Object[]{o}, buf, pos);
	}

	public Object parseObject(String string, ParsePosition pos) {
		return ((Object[]) format.parseObject(string, pos))[0];
	}
}
