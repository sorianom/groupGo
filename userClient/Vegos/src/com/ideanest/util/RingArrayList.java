package com.ideanest.util;

import java.util.*;
import java.io.*;

/**
 * A circular array list that expands automatically when needed.
 *
 * @invariant items != null
 * @invariant capacity >= 0
 * @invariant capacity == items.length
 * @invariant 0 <= first < capacity
 * @invariant 0 <= last < capacity
 * @invariant 0 <= size <= capacity
 * @invariant (size == 0 && first == last) || (first <= last && size == last - first + 1) || (size == capacity - first + last + 1)
 */
public class RingArrayList extends AbstractList implements Cloneable, Serializable {
	private int capacity;
	private int size;
	private transient Object[] items;
	private transient int first, last;

	public RingArrayList() {
		this(10);
	}

	public RingArrayList(int capacity) {
		this.capacity = capacity;
		items = new Object[capacity];
	}

	public RingArrayList(Collection c) {
		this(c.size());
		addAll(c);
	}

	public void add(int index, Object o) {
		int i = makeSpace(index, 1);
		items[i] = o;
	}

	public boolean add(Object o) {
		add(size, o);
		return true;
	}

	public boolean addAll(int index, Collection c) {
		int csize = c.size();
		if (csize == 0) return false;
		int i = makeSpace(index, csize);
		for (Iterator it = c.iterator(); it.hasNext(); i = (i+1) % capacity) {
			items[i] = it.next();
		}
		return true;
	}

	public boolean addAll(Collection c) {
		return addAll(size, c);
	}

	public void clear() {
		for (int i=size, j=first; i > 0; i--, j = (j+1) % capacity) {
			items[j] = null;
		}
		size = first = last = 0;
	}

	/**
	 * Returns a shallow copy of this <code>WraparoundArrayList</code> instance.  (The
	 * elements themselves are not copied.)
	 *
	 * @return  a clone of this <code>WraparoundArrayList</code> instance.
	 */
	public Object clone() {
		try {
			RingArrayList v = (RingArrayList) super.clone();
			v.items = toArray();
			v.capacity = items.length;
			v.modCount = 0;
			v.first = 0;
			v.last = size - 1;
			// size was copied already, no change
			return v;
		} catch (CloneNotSupportedException e) {
			// this shouldn't happen, since we are Cloneable
			throw new InternalError();
		}
	}

	public boolean contains(Object o) {
		for (int i=0, j=first; i < size; i++, j = (j+1) % capacity) {
			if (Safe.equals(o, items[j])) return true;
		}
		return false;
	}

	public boolean containsAll(Collection c) {
		for (Iterator it = c.iterator(); it.hasNext(); ) {
			if (!contains(it.next())) return false;
		}
		return true;
	}

	public void ensureCapacity(int minCapacity) {
		if (minCapacity < 0) throw new IllegalArgumentException("capacity = " + capacity + " < 0");
		if (minCapacity <= capacity) return;

		modCount++;
		int newCapacity = (capacity * 3) / 2 + 1;
		if (newCapacity < minCapacity) newCapacity = minCapacity;
		Object[] newItems = new Object[newCapacity];

		if (first <= last) {
			System.arraycopy(items, first, newItems, 0, size);
		} else  {
			int k = capacity - first;
			System.arraycopy(items, first, newItems, 0, k);
			System.arraycopy(items, 0, newItems, k, last + 1);
		}
		first = 0;
		last = size - 1;

		capacity = newCapacity;
		items = newItems;
	}

	public Object get(int index) {
		checkIndex(index);
		return items[ground(index)];
	}

	public int indexOf(Object o) {
		for (int i=0, j=first; i < size; i++, j = (j+1) % capacity) {
			if (Safe.equals(o, items[j])) return i;
		}
		return -1;
	}

	public boolean isEmpty() {
		return size == 0;
	}

	public int lastIndexOf(Object o) {
		for (int i=size, j=last; i > 0; i--, j = j-1 < 0 ? capacity-1 : j-1) {
			if (Safe.equals(o, items[j])) return i;
		}
		return -1;
	}

	public Object remove(int index) {
		checkIndex(index);
		final int i = (index + first) % capacity;
		Object old = items[i];
		if (index == 0) {
			first = (first + 1) % capacity;
			items[i] = null;
		} else if (index == size - 1) {
			if (--last < 0) last = capacity - 1;
			items[i] = null;
		} else {
			// TODO: optimize if (first < i < last) to shrink shorter side
			if (i > first) {
				System.arraycopy(items, first, items, first + 1, i - first);
				items[first] = null;
				first = (first + 1) % capacity;
			} else if (i < last) {
				System.arraycopy(items, i + 1, items, i, last - index);
				items[last] = null;
				last--;
				assert last >= 0;
			} else {
				assert false;
			}
		}
		if (--size == 0) first = last = 0;
		modCount++;
		return old;
	}

	public boolean remove(Object o) {
		int i = indexOf(o);
		if (i == -1) return false;
		remove(i);
		return true;
	}

	public Object set(int index, Object o) {
		checkIndex(index);
		index = (index + first) % capacity;
		Object old = items[index];
		items[index] = o;
		return old;
	}

	public int size() {
		return size;
	}

	public Object[] toArray() {
		Object[] a = new Object[size];
		if (first <= last) {
			System.arraycopy(items, first, a, 0, size);
		} else {
			int k = capacity - first;
			System.arraycopy(items, first, a, 0, k);
			System.arraycopy(items, 0, a, k, last);
		}
		return a;
	}

	public Object[] toArray(Object[] a) {
		if (a.length < size) {
			a = (Object[]) java.lang.reflect.Array.newInstance(
				a.getClass().getComponentType(), size);
		}
		if (first <= last) {
			System.arraycopy(items, first, a, 0, size);
		} else {
			int k = capacity - first;
			System.arraycopy(items, first, a, 0, k);
			System.arraycopy(items, 0, a, k, last);
		}
		if (a.length > size) a[size] = null;
		return a;
	}

	public void trimToSize() {
		if (size == capacity) return;
		items = toArray();
		capacity = items.length;
		first = 0;
		last = capacity - 1;
		modCount++;
	}

	/**
	 * Check if the index is within bounds.
	 */
	protected final void checkIndex(int index) {
		if (index < 0 || index >= size) throw new IndexOutOfBoundsException("Index: " + index + "  Size: " + size);
	}

	protected final int ground(int index) {
		return (first + index) % capacity;
	}

	/**
	 * Shift items around to make space for more.  We need a gap at the
	 * given logical index.
	 *
	 * @param index the logical index at which we need a gap
	 * @param gap the size of the gap required
	 * @return the real index at which the gap is now located
	 */
	protected final int makeSpace(int index, int gap) {
		if (index < 0 || index > size) throw new IndexOutOfBoundsException("Index: " + index + "  Size: " + size);

		if (size + gap > capacity) {
			// grow array; this could be more efficient if we made space for
			// the gap as we grow, but would lead to very twisted code; we
			// don't need to grow that often anyway
			ensureCapacity(size + gap);  // does modCount++
		} else {
			modCount++;
		}

		// take care of common (cheap) cases

		if (index == 0) { // insert at front
			first -= gap;
			if (size == 0) first++;		// special case if array is empty
			if (first < 0) first += capacity;
			size += gap;
			return first;
		}

		if (index == size) { // append at back
			int i = (last + 1) % capacity;
			last = (last + gap) % capacity;
			size += gap;
			return i;
		}

		// insertion in middle

		size += gap;
		int i = ground(index);

		int prefixSize = i - first;
		if (prefixSize < 0) prefixSize += capacity;
		int suffixSize = last - i;
		if (suffixSize < 0) suffixSize += capacity;
		if (prefixSize < suffixSize) {  // choose smaller shift
			assert i != first;
			i = shift(first, i, -gap);
			first -= gap;
			if (first < 0) first += capacity;
		} else {
			i = shift(i, (last+1) % capacity, gap);
			last = (last + gap) % capacity;
		}

		return i;
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		items = new Object[capacity];
		for (int i=0; i<size; i++) {
			items[i] = in.readObject();
		}
		first = 0;
		last = size - 1;
	}

	protected void removeRange(int fromIndex, int toIndex) {
		assert fromIndex <= toIndex;
		assert fromIndex >= 0 && fromIndex < size;
		assert toIndex >= 0 && toIndex <= size;
		if (fromIndex == toIndex) return;

		modCount++;
		int from = ground(fromIndex);
		int to = ground(toIndex);
		int prefixSize = from - first;
		if (prefixSize < 0) prefixSize += capacity;
		int suffixSize = last - to + 1;
		if (suffixSize < 0) suffixSize += capacity;

		int gap = toIndex - fromIndex;
		int clearFrom;
		if (prefixSize < suffixSize) {
			// shift prefix right to cover gap
			clearFrom = shift(first, (from+1)%capacity, gap);
			first = (first + gap) % capacity;
		} else {
			// shift suffix left to cover gap
			clearFrom = shift(to, (last+1)%capacity, -gap);
			last -= gap;
			if (last < 0) last += capacity;
		}

		size -= gap;
		if (size == 0) first = last = 0;
		while(gap-- > 0) {
			items[clearFrom] = null;
			clearFrom = (clearFrom + 1) % capacity;
		}
	}

	/**
	 * Shift items in the array.  The range specified must not include
	 * any gap elements, but can wrap around the end of the array.  The
	 * amount to shift by must be no greater than the capacity minus the
	 * range size; in other words, it's illegal to make the range wrap
	 * around back onto itself.
	 *
	 * @param from starting range index (inclusive)
	 * @param to ending range index (exclusive)
	 * @param by amount to shift by (positive or negative)
	 * @return the index of the newly created gap
	 */
	protected final int shift(int from, int to, int by) {
		if (by == 0) throw new IllegalArgumentException();
		if (from == to) return by > 0 ? from : from + by;

		// calculate number of elements to shift
		int len = to - from;
		if (len < 0) len += capacity;

		// change "to" to be inclusive for easier bounds checking below
		if (--to < 0) to += capacity;

		// calculate destination from and to
		int dfrom = (from + by) % capacity;
		if (dfrom < 0) dfrom += capacity;
		int dto = (to + by) % capacity;
		if (dto < 0) dto += capacity;

		assert len != 0;

		if (by > 0) {
			if (from <= to) {
				if (dfrom <= dto) {
					// contiguous range, contiguous dest
					System.arraycopy(items, from, items, dfrom, len);
				} else { // dfrom > dto
					// contiguous range, split dest
					System.arraycopy(items, to-dto, items, 0, dto+1);
					System.arraycopy(items, from, items, dfrom, capacity - dfrom);
				}
			} else { // from > to
				if (dfrom <= dto) {
					// split range, contiguous dest
					System.arraycopy(items, 0, items, dto-to, to+1);
					System.arraycopy(items, from, items, dfrom, capacity - from);
				} else { // dfrom > dto
					// split range, split dest
					assert to < dto;
					System.arraycopy(items, 0, items, dto-to, to+1);
					System.arraycopy(items, capacity - (dto-to), items, 0, dto-to);
					System.arraycopy(items, from, items, dfrom, capacity - dfrom);
				}
			}
			return from;
		} else { // by < 0
			if (from <= to) {
				if (dfrom <= dto) {
					// contiguous range, contiguous dest
					System.arraycopy(items, from, items, dfrom, len);
				} else { // dfrom > dto
					// contiguous range, split dest
					System.arraycopy(items, from, items, dfrom, capacity - dfrom);
					System.arraycopy(items, to-dto, items, 0, dto+1);
				}
			} else { // from > to
				if (dfrom <= dto) {
					// split range, contiguous dest
					System.arraycopy(items, from, items, dfrom, capacity - from);
					System.arraycopy(items, 0, items, dto-to, to+1);
				} else { // dfrom > dto
					// split range, split dest
					assert to < dto;
					System.arraycopy(items, from, items, dfrom, capacity - dfrom);
					System.arraycopy(items, capacity - (dto-to), items, 0, dto-to);
					System.arraycopy(items, 0, items, dto-to, to+1);
				}
			}
			return (dto + 1) % capacity;
		}
	}

	private void writeObject(ObjectOutputStream out) throws IOException {
		out.defaultWriteObject();
		for (int i=0, j=first; i < size; i++, j = (j+1)%capacity)
			out.writeObject(items[j]);
	}
}
