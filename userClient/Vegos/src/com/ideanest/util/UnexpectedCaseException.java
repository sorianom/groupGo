package com.ideanest.util;

/**
 * Thrown if a switch statement has unexpectedly reached the
 * default branch.
 */
public class UnexpectedCaseException extends RuntimeException {

	/**
	 * UnexpectedCaseException constructor comment.
	 */
	public UnexpectedCaseException() {
		super();
	}

	/**
	 * UnexpectedCaseException constructor comment.
	 * @param s java.lang.String
	 */
	public UnexpectedCaseException(String s) {
		super(s);
	}
}
