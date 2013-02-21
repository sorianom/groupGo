package com.ideanest.util;

/**
 * A marker used to indicate lockable objects.  Lockable objects can be
 * locked for exclusive access by synchronizing on their lock object.
 * While often the lock will be the object itself, it's necessary to
 * separate the two concepts since composite object assemblies that
 * conceptually represent one coordinated object must have one lock for
 * the whole composite.
 */
public interface Lockable {

	/**
	 * Return the lock for this object.  As long as this lock is acquired
	 * using the <code>synchronized</code> statement, the object guarantees
	 * that no conflicting concurrent access will be allowed.  Concurrent
	 * access which is not conflicting or does not rely on the mutable state
	 * of an object may be allowed.
	 *
	 * @return this object's lock
	 */
	Object getLock();
}
