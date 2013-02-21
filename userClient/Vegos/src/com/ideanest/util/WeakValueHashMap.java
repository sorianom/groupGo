package com.ideanest.util;

import java.util.*;
import java.lang.ref.*;

public class WeakValueHashMap extends ReferenceValueHashMap {
	private static class WeakValue extends WeakReference implements ReferenceValue {
		private final Object key;
		private final int hash;	// hashcode of value, in case it gets GC'd
		private WeakValue(Object key, Object value, ReferenceQueue q) {
			super(value, q);
			this.key = key;
			this.hash = value.hashCode();
		}
		private WeakValue(Object value) {
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
				Object or = ((WeakValue) o).get();
				if (or == null) return false;
				return r == or || r.equals(or);
			} catch (ClassCastException e) {
				return false;
			}
		}
	}

	public WeakValueHashMap() {
		super();
	}

	public WeakValueHashMap(int initialCapacity) {
		super(initialCapacity);
	}

	public WeakValueHashMap(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}

	protected ReferenceValue createReferenceValue(Object value) {
		if (value == null) return null;
		else return new WeakValue(value);
	}

	protected ReferenceValue createReferenceValue(Object key, Object value, ReferenceQueue q) {
		if (value == null) return null;
		else return new WeakValue(key, value, q);
	}
}
