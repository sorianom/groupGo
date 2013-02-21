package com.ideanest.util;

import java.util.*;
/**
 * Insert the type's description here.
 *
 * @author Piotr Kaminski (piotr@ideanest.com)
 * @version 1.1, 2002-03-10
 */
public class WrapperMultiMap extends CollectionValuedMultiMap {
	protected final Map base;
	protected int size;
	private boolean deleteEmptyKeys;

	public WrapperMultiMap() {
		this(new HashMap());
	}

	public WrapperMultiMap(CollectionFactory collectionFactory) {
		this(new HashMap(), collectionFactory);
	}

	public WrapperMultiMap(Collection valuesPrototype) {
		this(new HashMap(), valuesPrototype);
	}

	public WrapperMultiMap(Map base) {
		this(base, new CollectionFactory() {
			public Collection createValueCollection() {
				return new LinkedList();
			}
		});
	}

	public WrapperMultiMap(Map base, CollectionFactory collectionFactory) {
		super(collectionFactory);
		this.base = base;
	}

	public WrapperMultiMap(Map base, Collection valuesPrototype) {
		this(base, new ClonedCollectionFactory(valuesPrototype));
	}

	public boolean add(Object key, Object value) {
		Collection c = (Collection) base.get(key);
		if (c == null) {
			c = createValueCollection();
			base.put(key, c);
		}
		int s = c.size();
		c.add(value);
		s -= c.size();
		if (s == 0) return false;
		size -= s;
		return true;
	}

	public void clear() {
		base.clear();
		size = 0;
	}

	public Set entrySet() {
		throw new NotYetImplementedException();
	}

	public Collection get(Object key) {
		Collection c = (Collection) base.get(key);
		return (c == null || c.isEmpty()) ? null : Collections.unmodifiableCollection(c);
	}

	public boolean getDeleteEmptyKeys() {
		return deleteEmptyKeys;
	}

	public Set keySet() {
		throw new NotYetImplementedException();
	}

	public boolean remove(Object key) {
		Collection c = get_(key);
		base.remove(key);
		if (c != null) {
			size -= c.size();
			return true;
		} else {
			return false;
		}
	}

	public boolean remove(Object key, Object value) {
		Collection c = get_(key);
		if (c == null) return false;
		int s = c.size();
		c.remove(value);
		s -= c.size();
		if (s == 0) return false;
		size -= s;
		if (deleteEmptyKeys && c.isEmpty()) base.remove(key);
		return true;
	}

	public void setDeleteEmptyKeys(boolean flag) {
		deleteEmptyKeys = flag;
	}

	public int size() {
		return size;
	}

	public String toString() {
		return base.toString();
	}

	protected Collection get_(Object key) {
		Collection c = (Collection) base.get(key);
		return (c == null || c.isEmpty()) ? null : c;
	}
}
