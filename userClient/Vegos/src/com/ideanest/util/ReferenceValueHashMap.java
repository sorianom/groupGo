package com.ideanest.util;

import java.util.*;
import java.lang.ref.*;

/**
 *
 */

public abstract class ReferenceValueHashMap extends AbstractMap {
	private final HashMap map;
	private EntrySet entrySet;
	private final ReferenceQueue refQueue = new ReferenceQueue();

	protected interface ReferenceValue {
		Object getKey();
		Object get();
		void clear();
		boolean enqueue();
	}

	private class Entry implements Map.Entry {
		private Map.Entry ent;
		private Object value;
		/* Strong reference to value, so that the GC will leave it alone
			as long as this Entry exists */

		Entry(Map.Entry ent, Object value) {
			this.ent = ent;
			this.value = value;
		}

		public Object getKey() {
			return ent.getKey();
		}
		public Object getValue() {
			return value;
		}

		public Object setValue(Object value) {
			ent.setValue(createReferenceValue(getKey(), value, refQueue));
			Object oldValue = this.value;
			this.value = value;
			return oldValue;
		}

		public boolean equals(Object o) {
			try {
				Map.Entry e = (Map.Entry) o;
				return Safe.equals(getKey(), e.getKey())
					&& Safe.equals(getValue(), e.getValue());
			} catch (ClassCastException e) {
				return false;
			}
		}

		public int hashCode() {
			Object k;
			return ((k = getKey()) == null ? 0 : k.hashCode())
				^ ((value == null ? 0 : value.hashCode()));
		}

		public String toString() {
			return ent.getKey() + "=" + value;
		}

	}

	private class EntrySet extends AbstractSet {
		Set baseEntrySet = map.entrySet();

		public Iterator iterator() {
			return new Iterator() {
				Iterator baseIterator = baseEntrySet.iterator();
				Entry next = null;
				ReferenceValue last = null;

				public boolean hasNext() {
					if (next != null) return true;
					while (baseIterator.hasNext()) {
						Map.Entry ent = (Map.Entry) baseIterator.next();
						ReferenceValue wv = (ReferenceValue) ent.getValue();
						Object v = null;
						if (wv != null && (v = wv.get()) == null) continue;
						next = new Entry(ent, v);
						return true;
					}
					return false;
				}

				public Object next() {
					if (next == null && !hasNext()) throw new NoSuchElementException();
					Entry e = next;
					next = null;
					last = (ReferenceValue) e.ent.getValue();
					return e;
				}

				public void remove() {
					if (last == null) throw new NoSuchElementException();
					last.clear();
					last.enqueue();
					last = null;
				}

			};
		}

		public boolean isEmpty() {
			return !iterator().hasNext();
		}

		public int size() {
			int j = 0;
			for (Iterator i = iterator(); i.hasNext(); i.next()) j++;
			return j;
		}

		public boolean remove(Object o) {
			processQueue();
			return baseEntrySet.remove(o);
		}

		public void clear() {
			processQueue();
			baseEntrySet.clear();
		}

		public Object[] toArray() {
			// take a quick estimated upper-limit size
			Object[] result = new Object[baseEntrySet.size()];
			int i=0;
			for (Iterator it = iterator(); it.hasNext(); i++) result[i] = it.next();
			if (i != result.length) {
				// values disappeared while we were iterating
				Object[] temp = new Object[i];
				System.arraycopy(result, 0, temp, 0, i);
				result = temp;
			}
			return result;
		}

		public Object[] toArray(Object a[]) {
			Iterator it = iterator();
			int i = 0;
			while (it.hasNext() && i < a.length) a[i++] = it.next();
			if (!it.hasNext()) {
				// everything fit in the given array
				if (i < a.length) a[i] = null;
				return a;
			}
			// didn't fit, reallocate new larger array based on estimated max
			// needed size and continue
			{
				Object[] b = (Object[]) java.lang.reflect.Array.newInstance(
					a.getClass().getComponentType(), baseEntrySet.size());
				System.arraycopy(a, 0, b, 0, i);
				a = b;
			}
			while (it.hasNext()) a[i++] = it.next();
			if (i < a.length) {
				// array was too large, reallocate smaller
				Object[] b = (Object[]) java.lang.reflect.Array.newInstance(
					a.getClass().getComponentType(), i);
				System.arraycopy(a, 0, b, 0, i);
				a = b;
			}
			return a;
		}

		public String toString() {
			StringBuffer buf = new StringBuffer();
			buf.append("[");
			for (Iterator it = iterator(); it.hasNext(); ) {
				buf.append(String.valueOf(it.next()));
				if (it.hasNext()) buf.append(", ");
			}
			buf.append("]");
			return buf.toString();
		}

	}

	/**
	 * Constructs a new, empty <code>WeakValuesHashMap</code> with the default
	 * capacity and the default load factor, which is <code>0.75</code>.
	 */
	public ReferenceValueHashMap() {
		map = new HashMap();
	}

	/**
	 * Constructs a new, empty <code>WeakValuesHashMap</code> with the given
	 * initial capacity and the default load factor, which is
	 * <code>0.75</code>.
	 *
	 * @param  initialCapacity  The initial capacity of the
	 *                          <code>WeakValuesHashMap</code>
	 *
	 * @throws IllegalArgumentException  If the initial capacity is less than
	 *                                   zero
	 */
	public ReferenceValueHashMap(int initialCapacity) {
		map = new HashMap(initialCapacity);
	}

	/**
	 * Constructs a new, empty <code>WeakValuesHashMap</code> with the given
	 * initial capacity and the given load factor.
	 *
	 * @param  initialCapacity  The initial capacity of the
	 *                          <code>WeakValuesHashMap</code>
	 *
	 * @param  loadFactor       The load factor of the <code>WeakValuesHashMap</code>
	 *
	 * @throws IllegalArgumentException  If the initial capacity is less than
	 *                                   zero, or if the load factor is
	 *                                   nonpositive
	 */
	public ReferenceValueHashMap(int initialCapacity, float loadFactor) {
		map = new HashMap(initialCapacity, loadFactor);
	}

	public void clear() {
		processQueue();
		map.clear();
	}

	public boolean containsKey(Object key) {
		if (!map.containsKey(key)) return false;
		ReferenceValue wv = (ReferenceValue) map.get(key);
		if (wv == null) return true;
		return wv.get() != null;
	}

	public boolean containsValue(Object value) {
		return map.containsValue(createReferenceValue(value));
	}

	public Set entrySet() {
		if (entrySet == null) entrySet = new EntrySet();
		return entrySet;
	}

	public Object get(Object key) {
		ReferenceValue wv = (ReferenceValue) map.get(key);
		return wv == null ? null : wv.get();
	}

	public boolean isEmpty() {
		return entrySet().isEmpty();
	}

	public Object put(Object key, Object value) {
		processQueue();
		ReferenceValue wv = (ReferenceValue) map.put(key, createReferenceValue(key, value, refQueue));
		if (wv == null) return null;
		Object o = wv.get();
		wv.clear();
		return o;
	}

	public Object remove(Object key) {
		processQueue();
		ReferenceValue wv = (ReferenceValue) map.remove(key);
		if (wv == null) return null;
		Object o = wv.get();
		wv.clear();
		return o;
	}

	public int size() {
		return entrySet().size();
	}

	protected abstract ReferenceValue createReferenceValue(Object value);

	protected abstract ReferenceValue createReferenceValue(Object key, Object value, ReferenceQueue q);

	private void processQueue() {
		ReferenceValue wv;
		while ((wv = (ReferenceValue) refQueue.poll()) != null) {
			// must check whether this value is still at the key
			// -- it might have gotten replaced by another one
			if (map.get(wv.getKey()) == wv) map.remove(wv.getKey());
		}
	}
}
