package com.ideanest.util;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.*;
import java.io.*;
import java.net.*;

/**
 * This type was created in VisualAge.
 *
 * @author Piotr Kaminski
 */
public final class Safe {
	public static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];
	public static final Class[] EMPTY_CLASS_ARRAY = new Class[0];
	public static final String[] EMPTY_STRING_ARRAY = new String[0];
	
	private Safe() {}

	/**
	 * Attempt to clone an object, if possible.  If the given object
	 * implements <code>Cloneable</code> and has a public <code>clone()</code>
	 * method, it will be invoked and the result returned.  Otherwise, or
	 * if a <code>CloneNotSupportedException</code> is thrown, <code>null</code>
	 * is returned.
	 *
	 * @param o the object to clone
	 * @return a clone of the object or <code>null</code>
	 */
	public static Object clone(Object o) {
		if (!(o instanceof Cloneable)) return null;
		try {
			return o.getClass().getMethod("clone", EMPTY_CLASS_ARRAY).invoke(o, EMPTY_OBJECT_ARRAY);
		} catch (NoSuchMethodException e) {
			return null;
		} catch (IllegalAccessException e) {
			return null;
		} catch (InvocationTargetException e) {
			Throwable te = e.getTargetException();
			if (te instanceof CloneNotSupportedException) {
				return null;
			} else if (te instanceof Error) {
				throw (Error) te;
			} else if (te instanceof RuntimeException) {
				throw (RuntimeException) te;
			} else {
				Logger.getLogger("com.ideanest.util.Safe.clone").log(Level.WARNING, "Invocation of clone failed", te);
				return null;
			}
		}
	}

	public static boolean equals(Object a, Object b) {
		return a == null ? a == b : a.equals(b);
	}

	public static OutputStream getOutputStream(URLConnection conn) throws IOException {
		URL url = conn.getURL();
		if ("file".equals(url.getProtocol())) {
			// default file protocol handler doesn't support output!
			return new FileOutputStream(url.getFile());
		} else {
			return conn.getOutputStream();
		}
	}
}
