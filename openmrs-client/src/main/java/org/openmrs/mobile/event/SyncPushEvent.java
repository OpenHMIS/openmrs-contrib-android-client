package org.openmrs.mobile.event;

import androidx.annotation.Nullable;

public class SyncPushEvent extends SyncEvent {

	public SyncPushEvent(String message, @Nullable String entity, @Nullable Integer totalItemsToPush) {
		super(message, entity, totalItemsToPush);
	}
}
