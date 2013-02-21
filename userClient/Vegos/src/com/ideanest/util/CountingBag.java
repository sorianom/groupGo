package com.ideanest.util;

import java.util.*;

/**
 * A bag implemented using a map of elements to quantities.  If multiple
 * equal objects are added, any one instance will be retained; the rest
 * will be merely counted.  When iterating, the instance retained will be
 * returned multiple times, according to the count.
 *
 * @author Piotr Kaminski (piotr@ideanest.com)
 * @version 1.1, 2002-03-10
 */
public class CountingBag extends AbstractCollection implements Cloneable, java.io.Serializable {
	private final Map base;
	private int size;

	public CountingBag() {
		this(new HashMap());
	}

	public CountingBag(Collection c) {
		this();
		addAll(c);
	}

	public CountingBag(Map base) {
		this.base = base;
	}

	public boolean add(Object o) {
		return add(o, 1);
	}

	public boolean add(Object o, int n) {
		Integer countObject = (Integer) base.get(o);
		int count = countObject == null ? n : countObject.intValue() + n;
		base.put(o, new Integer(count));
		size += n;
		return true;
	}

	public void clear() {
		base.clear();
		size = 0;
	}

	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			throw new UnexpectedException(e);
		}
	}

	public boolean contains(Object o) {
		return base.containsKey(o);
	}

	public int count(Object o) {
		Integer countObject = (Integer) base.get(o);
		if (countObject == null) return 0;
		else return countObject.intValue();
	}

	public Iterator iterator() {
		return new Iterator() {
			private final Iterator baseIterator = base.entrySet().iterator();
			private Map.Entry entry;
			private Object key;
			private int totalCount, remainingCount;
			private boolean canRemove = false;

			public boolean hasNext() {
				return remainingCount > 0 || baseIterator.hasNext();
			}
			public Object next() {
				if (remainingCount > 0) {
					remainingCount--;
					canRemove = true;
				} else {
					entry = (Map.Entry) baseIterator.next();
					key = entry.getKey();
					totalCount = ((Integer) entry.getValue()).intValue();
					assert totalCount > 0;
					remainingCount = totalCount - 1;
					canRemove = true;
				}
				return key;
			}
			public void remove() {
				if (!canRemove) throw new IllegalStateException();
				canRemove = false;
				if (--totalCount == 0) {
					assert remainingCount == 0;
					baseIterator.remove();
				} else {
					entry.setValue(new Integer(totalCount));
				}
			}
		};
	}

	public boolean remove(Object o) {
		Integer countObject = (Integer) base.get(o);
		if (countObject == null) return false;
		int count = countObject.intValue() - 1;
		size--;
		if (count == 0) base.remove(o);
		else base.put(o, new Integer(count));
		return true;
	}

	public int remove(Object o, int n) {
		Integer countObject = (Integer) base.get(o);
		if (countObject == null) return 0;
		int count = countObject.intValue();
		if (count < n) n = count;
		count -= n;
		size -= n;
		if (count == 0) base.remove(o);
		else base.put(o, new Integer(count));
		return n;
	}

	public boolean removeAll(Collection c) {
		boolean flag = false;
		for (Iterator it = c.iterator(); it.hasNext(); ) {
			Object key = it.next();
			Integer countObject = (Integer) base.get(key);
			if (countObject == null) continue;
			size -= countObject.intValue();
			base.remove(key);
			flag = true;
		}
		return flag;
	}

	public boolean retainAll(Collection c) {
		boolean modified = false;
		for (Iterator it = base.entrySet().iterator(); it.hasNext(); ) {
			Map.Entry entry = (Map.Entry) it.next();
			if (!c.contains(entry.getKey())) {
				size -= ((Integer) entry.getValue()).intValue();
				it.remove();
				modified = true;
			}
		}
		return modified;
	}

	public int size() {
		return size;
	}

	public Set toSet() {
		return Collections.unmodifiableSet(base.keySet());
	}
}
