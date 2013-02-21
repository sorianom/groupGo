package com.ideanest.util;

public class CheckableReentrantWriterPreferenceReadWriteLock {

	public boolean isReadAcquired() {
		return true;//readers_.containsKey(Thread.currentThread());
	}

	public boolean isWriteAcquired() {
		return true;//activeWriter_ == Thread.currentThread();
	}
}
