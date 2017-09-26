package org.openmrs.mobile.data.sync.impl;

import android.support.annotation.NonNull;

import org.openmrs.mobile.data.db.impl.SyncLogDbService;
import org.openmrs.mobile.data.sync.SyncLogService;
import org.openmrs.mobile.models.BaseOpenmrsObject;
import org.openmrs.mobile.models.SyncAction;
import org.openmrs.mobile.models.SyncLog;

import java.util.Random;

import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

public class SyncLogServiceImpl implements SyncLogService {

	private SyncLogDbService syncLogDbService;

	@Inject
	public SyncLogServiceImpl(SyncLogDbService syncLogDbService) {
		this.syncLogDbService = syncLogDbService;
	}

	@Override
	public <E extends BaseOpenmrsObject> void save(@NonNull E entity, @NonNull SyncAction action) {
		checkNotNull(entity);
		checkNotNull(action);

		// check if entity exists
		SyncLog syncLog = syncLogDbService.getByKey(entity.getUuid());
		if (syncLog == null) {
			syncLogDbService.save(createSyncLog(entity, action));
			return;
		}

		// remove record just so it doesn't get sent to the server multiple times
		if (action.equals(SyncAction.DELETED)) {
			syncLogDbService.delete(syncLog.getUuid());
			return;
		}


		// No need to create another SyncLog for a local record that it being updated
	}

	private <E extends BaseOpenmrsObject> SyncLog createSyncLog(E entity, SyncAction action) {
		SyncLog syncLog = new SyncLog();
		syncLog.setAction(action);
		syncLog.setKey(entity.getUuid());
		syncLog.setType(entity.getClass().getSimpleName());

		if(entity.getClass().getSimpleName().equalsIgnoreCase("Patient")) {
			syncLog.setOrder(0);
		} else if(entity.getClass().getSimpleName().equalsIgnoreCase("Visit")) {
			syncLog.setOrder(1);
		} else {
			syncLog.setOrder(new Random().nextInt(10) + 2);
		}

		return syncLog;
	}
}
