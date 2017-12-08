package org.openmrs.mobile.data.sync;

import org.openmrs.mobile.models.SyncLog;

public interface PushProvider {
	void push(SyncLog record);
}
