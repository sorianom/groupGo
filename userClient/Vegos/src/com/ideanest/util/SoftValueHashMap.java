package com.ideanest.util;

import java.util.*;
import java.lang.ref.*;

public class SoftValueHashMap extends ReferenceValueHashMap {
	private static class SoftValue extends SoftReference implements ReferenceValue {
		private final Object key;
		private final int hash;	// hashcode of value, in case it gets GC'd
		private SoftValue(Object key, Object value, ReferenceQueue q) {
			super(value, q);
			this.key = key;
			this.hash = value.hashCode();
		}
		private SoftValue(Object value) {
			super(value);
			this.key = null;
			this.hash = value.hashCode();
		}
		public Object getKey() {return key;}
		public String toString() {
			Object o = get();
			if (o == null) return "**CLEARED**";
			else return o.toString();
		}
		public int hashCode() {return hash;}
		public boolean equals(Object o) {
			if (this == o) return true;
			Object r = get();
			if (r == null) return false;
			try {
				Object or = ((SoftValue) o).get();
				if (or == null) return false;
				return r == or || r.equals(or);
			} catch (ClassCastException e) {
				return false;
			}
		}
	}

	/**
	 * SoftValueHashMap constructor comment.
	 */
	public SoftValueHashMap() {
		super();
	}

	/**
	 * SoftValueHashMap constructor comment.
	 * @param initialCapacity int
	 */
	public SoftValueHashMap(int initialCapacity) {
		super(initialCapacity);
	}

	/**
	 * SoftValueHashMap constructor comment.
	 * @param initialCapacity int
	 * @param loadFactor float
	 */
	public SoftValueHashMap(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}

	protected ReferenceValue createReferenceValue(Object value) {
		if (value == null) return null;
		else return new SoftValue(value);
	}

	protected ReferenceValue createReferenceValue(Object key, Object value, ReferenceQueue q) {
		if (value == null) return null;
		else return new SoftValue(key, value, q);
	}
}
