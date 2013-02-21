package com.ideanest.util;

/**
 * This type was created in VisualAge.
 *
 * @author Piotr Kaminski
 */
public final class URLDecoder {

	private URLDecoder() {
	}

	public static String decode(String s) {
		s = s.replace('+', ' ');
		int i = s.indexOf('%');
		if (i >= 0) {
			int j = 0;
			StringBuffer buf = new StringBuffer(s.length());
			while (i >= 0) {
				buf.append(s.substring(j, i));
				// check for malformed encoding
				if (s.length() < i+3) break;
				buf.append((char) Integer.parseInt(s.substring(i+1, i+3), 16));
				j = i+3;
				i = s.indexOf('%', j);
			}
			// append tail
			buf.append(s.substring(j));
			s = buf.toString();
		}
		return s;
	}
}
