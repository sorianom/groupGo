package com.ideanest.swing.status;

/**
 * This type was created in VisualAge.
 *
 * @author Piotr Kaminski
 */
public interface StatusModel {
	public static final int PRIORITY_NORMAL = 0;

	int getMaxProgress();

	String getMessage();

	int getPriority();

	int getProgress();

	void setMaxProgress(int maxProgress);

	void setMessage(String message);

	void setPriority(int priority);

	void setProgress(int progress);
}
