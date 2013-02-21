package com.ideanest.util;

/**
 * Indicates that the operation was unexpectedly interrupted.
 * Used in preference to <code>InterruptedException</code> to
 * avoid having to declare it just about everywhere.
 */
public class InterruptedRuntimeException extends RuntimeException {

	public InterruptedRuntimeException() {
		super();
	}

	public InterruptedRuntimeException(String s) {
		super(s);
	}

	public InterruptedRuntimeException(String s, Throwable e) {
		super(s, e);
	}

	public InterruptedRuntimeException(Throwable e) {
		super(e);
	}
}
