package com.ideanest.util;

import java.util.*;
/**
 *
 *
 * @author Piotr Kaminski (piotr@ideanest.com)
 * @version 1.1, 2002-03-10
 */
public abstract class AbstractMultiMap implements MultiMap, java.io.Serializable {
	private transient Collection values;

	/**
	 * AbstractMultiMap constructor comment.
	 */
	public AbstractMultiMap() {
		super();
	}

	public boolean add(Object key, Object value) {
		throw new UnsupportedOperationException();
	}

	public boolean addAll(MultiMap map) {
		boolean modified = false;
		for (Iterator it = map.entrySet().iterator(); it.hasNext(); ) {
			Map.Entry entry = (Map.Entry) it.next();
			modified |= add(entry.getKey(), entry.getValue());
		}
		return modified;
	}

	public boolean addAll(Map map) {
		boolean modified = false;
		for (Iterator it = map.entrySet().iterator(); it.hasNext(); ) {
			Map.Entry entry = (Map.Entry) it.next();
			modified |= add(entry.getKey(), entry.getValue());
		}
		return modified;
	}

	public void clear() {
		entrySet().clear();
	}

	public boolean contains(Object key, Object value) {
		Collection c = get_(key);
		return c != null && c.contains(value);
	}

	public boolean containsKey(Object key) {
		return get_(key) != null;
	}

	public boolean containsValue(Object value) {
		for (Iterator it = entrySet().iterator(); it.hasNext(); ) {
			MultiMap.Entry entry = (MultiMap.Entry) it.next();
			if (Safe.equals(value, entry.getValue())) return true;
		}
		return false;
	}

	public boolean equals(Object obj) {
		if (obj == this) return true;
		try {
			MultiMap that = (MultiMap) obj;
			if (size() != that.size()) return false;
			for (Iterator it = keySet().iterator(); it.hasNext(); ) {
				Object key = it.next();
				if (!get_(key).equals(that.get(key))) return false;
			}
			return true;
		} catch (ClassCastException e) {
			return false;
		}
	}

	public int hashCode() {
		int code = 0;
		for (Iterator it = entrySet().iterator(); it.hasNext(); ) {
			code += it.next().hashCode();
		}
		return code;
	}

	public boolean isEmpty() {
		return size() == 0;
	}

	public boolean remove(Object key) {
		boolean modified = false;
		for (Iterator it = entrySet().iterator(); it.hasNext(); ) {
			MultiMap.Entry entry = (MultiMap.Entry) it.next();
			if (Safe.equals(key, entry.getKey())) {
				it.remove();
				modified = true;
			}
		}
		return modified;
	}

	public boolean remove(Object key, Object value) {
		for (Iterator it = entrySet().iterator(); it.hasNext(); ) {
			MultiMap.Entry entry = (MultiMap.Entry) it.next();
			if (Safe.equals(key, entry.getKey()) && Safe.equals(value, entry.getValue())) {
				it.remove();
				return true;
			}
		}
		return false;
	}

	public int size() {
		return entrySet().size();
	}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append('{');
		for (Iterator it = keySet().iterator(); it.hasNext(); ) {
			Object key = it.next();
			buf.append(String.valueOf(key));
			buf.append('=');
			buf.append(String.valueOf(get_(key)));
			if (it.hasNext()) buf.append(", ");
		}
		buf.append('}');
		return buf.toString();
	}

	public Collection values() {
		if (values == null) values = new AbstractCollection() {
			public int size() {return AbstractMultiMap.this.size();}
			public void clear() {AbstractMultiMap.this.clear();}
			public Iterator iterator() {
				return new Iterator() {
					private final Iterator it = AbstractMultiMap.this.entrySet().iterator();
					public boolean hasNext() {return it.hasNext();}
					public Object next() {return ((MultiMap.Entry) it.next()).getValue();}
					public void remove() {it.remove();}
				};
			}
		};
		return values;
	}

	protected Object clone() throws CloneNotSupportedException {
		AbstractMultiMap copy = (AbstractMultiMap) super.clone();
		copy.values = null;
		return copy;
	}

	protected abstract Collection get_(Object key);
}
