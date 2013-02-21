package com.ideanest.util;

/**
 * Used to signal that a method has not yet been implemented.
 * Should only occur during development.
 */
public class NotYetImplementedException extends RuntimeException {

	public NotYetImplementedException() {
		super();
	}

	public NotYetImplementedException(String s) {
		super(s);
	}
}
