package org.openmrs.mobile.event;

import androidx.annotation.Nullable;

public class SyncEvent extends OpenMRSEvent {

	protected final String entity;
	protected final Integer totalItems;

	public SyncEvent(String message, @Nullable String entity, @Nullable Integer totalItems) {
		super(message);

		this.entity = entity;
		this.totalItems = totalItems;
	}

	public String getEntity() {
		return entity;
	}

	public Integer getTotalItems() {
		return totalItems;
	}
}
