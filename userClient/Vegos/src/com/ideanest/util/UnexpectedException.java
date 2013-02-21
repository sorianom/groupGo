package com.ideanest.util;

public class UnexpectedException extends RuntimeException {

	public UnexpectedException(String s, Throwable e) {
		super(s, e);
	}

	public UnexpectedException(Throwable e) {
		super(e);
	}
}
