package com.ideanest.util;

/**
 * A simple enumerated type superclass.
 * <p>
 * Instances are serializable, and will assume their correct unique
 * identity upon deserialization.  All fields in all subclasses
 * should be declared <code>transient</code>.
 */

import java.io.*;

public abstract class Enum implements Serializable {
	private int value;
	private transient String name;

	protected Enum(int value, String name) {
		this.value = value;
		this.name = name;
	}

	public int intValue() {return value;}

	public String toString() {return name;}

	protected abstract Enum[] getInstanceArray();

	protected Object readResolve() {
		return getInstanceArray()[value];
	}
}
