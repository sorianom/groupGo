package com.ideanest.util;

import java.util.*;
/**
 * Insert the type's description here.
 *
 * @author Piotr Kaminski (piotr@ideanest.com)
 * @version 1.1, 2002-06-02
 */
public class CountingBagMultiMap extends WrapperMultiMap {

	protected static class CountingBagCollectionFactory implements CollectionFactory, java.io.Serializable {
		public Collection createValueCollection() {
			return new CountingBag();
		}
	}

	public CountingBagMultiMap() {
		super(new CountingBagCollectionFactory());
	}

	public CountingBagMultiMap(Map base) {
		super(base, new CountingBagCollectionFactory());
	}

	public boolean add(Object key, Object value, int n) {
		CountingBag c = (CountingBag) base.get(key);
		if (c == null) {
			c = (CountingBag) createValueCollection();
			base.put(key, c);
		}
		c.add(value, n);
		size += n;
		return true;
	}

	public Set getSet(Object key) {
		CountingBag c = (CountingBag) base.get(key);
		return (c == null || c.isEmpty()) ? null : c.toSet();
	}

	public int remove(Object key, Object value, int n) {
		CountingBag c = (CountingBag) get_(key);
		if (c == null) return 0;
		n = c.remove(value, n);
		size -= n;
		if (getDeleteEmptyKeys() && c.isEmpty()) base.remove(key);
		return n;
	}
}
