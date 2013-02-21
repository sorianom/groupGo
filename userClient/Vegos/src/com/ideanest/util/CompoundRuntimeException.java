package com.ideanest.util;

import java.util.*;

/**
 * A collection of runtime exceptions that needed to be ignored
 * for some reason.
 *
 * @author Piotr Kaminski (piotr@ideanest.com)
 * @version 1.0, 2002-09-13
 */
public class CompoundRuntimeException extends RuntimeException {
	private final List exceptions;

	public CompoundRuntimeException(String msg, List exceptions) {
		super(msg);
		this.exceptions = Collections.unmodifiableList(new ArrayList(exceptions));
	}

	public CompoundRuntimeException(List exceptions) {
		this(String.valueOf(exceptions.size()) + " exceptions", exceptions);
	}

	public List getExceptions() {
		return exceptions;
	}
}
