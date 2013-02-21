package com.ideanest.util;

/**
 * A read write lock that can be checked to see if it's been acquired
 * by the current thread.
 */
public interface CheckableReadWriteLock  {

	/**
	 * Return whether the current thread has acquired the read lock.
	 */
	boolean isReadAcquired();

	/**
	 * Return whether the current thread has acquired the write lock.
	 */
	boolean isWriteAcquired();
}
