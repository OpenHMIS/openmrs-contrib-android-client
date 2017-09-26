package org.openmrs.mobile.data.sync;

import org.openmrs.mobile.models.BaseOpenmrsObject;
import org.openmrs.mobile.models.SyncAction;

public interface SyncLogService{
	<E extends BaseOpenmrsObject> void save(E entity, SyncAction action);
}
