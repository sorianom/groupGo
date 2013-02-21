package com.ideanest.util;

import java.io.IOException;
/**
 * Signals that two I/O exceptions occurred.  After the primary exception
 * occurred, the secondary one was encountered while trying to clean up
 * resources before propagating the primary one.  This can happen when
 * trying to close a stream after some read/write error occurred, since
 * the <code>close</code> method can throw an IOException itself.
 *
 * @author Piotr Kaminski (piotr@ideanest.com)
 * @version 1.0, 2002-03-10
 */
public class DualIOException extends IOException {
	private final IOException primary, secondary;

	public DualIOException(IOException e1, IOException e2) {
		this.primary = e1;
		this.secondary = e2;
	}

	/**
	 * Propagate any exceptions that may have occurred.  If both arguments are
	 * <code>null</code>, do nothing.  If only one is non-<code>null</code>,
	 * rethrow that exception.  Otherwise create a new <code>DualIOException</code>
	 * containing the two and throw it.
	 */
	public static void rethrow(IOException e1, IOException e2) throws IOException {
		if (e1 == null && e2 == null) return;
	// TODO: figure out if we need to fill in the stack trace here -- no effect, or too late?
	//	if (e1 != null) e1.fillInStackTrace();
	//	if (e2 != null) e2.fillInStackTrace();
		if (e1 == null) throw e2;
		if (e2 == null) throw e1;
		throw new DualIOException(e1, e2);
	}

	public String getMessage() {
		return primary.getMessage() + ", and then " + secondary.getMessage();
	}

	public IOException getPrimaryException() {return primary;}

	public IOException getSecondaryException() {return secondary;}
}
