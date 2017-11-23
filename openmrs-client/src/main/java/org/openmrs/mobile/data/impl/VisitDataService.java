package org.openmrs.mobile.data.impl;

import android.support.annotation.NonNull;

import org.openmrs.mobile.data.BaseEntityDataService;
import org.openmrs.mobile.data.EntityDataService;
import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.data.db.impl.VisitDbService;
import org.openmrs.mobile.data.rest.impl.VisitRestServiceImpl;
import org.openmrs.mobile.models.SyncAction;
import org.openmrs.mobile.models.Visit;

import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

public class VisitDataService extends BaseEntityDataService<Visit, VisitDbService, VisitRestServiceImpl>
		implements EntityDataService<Visit> {
	@Inject
	public VisitDataService() {
	}

	public void endVisit(@NonNull String uuid, @NonNull Visit visit, @NonNull GetCallback<Visit> callback) {
		checkNotNull(uuid);
		checkNotNull(visit);
		checkNotNull(callback);

		executeSingleCallback(callback, QueryOptions.REMOTE,
				() -> {
					Visit result = dbService.endVisit(visit);
					syncLogService.save(visit, SyncAction.DELETED);
					return result;
				},
				() -> restService.endVisit(uuid, visit));
	}

	public void updateVisit(Visit existingVisit, Visit updatedVisit, GetCallback<Visit> callback) {
		executeSingleCallback(callback, QueryOptions.REMOTE,
				() -> {
					existingVisit.processRelationships();
					Visit result = dbService.save(existingVisit);
					syncLogService.save(existingVisit, SyncAction.UPDATED);
					return result;
				},
				() -> restService.updateVisit(updatedVisit),
				(e) -> {
					// void existing attributes
					dbService.voidExistingVisitAttributes(existingVisit);

					// update visit
					dbService.save(e);
				});
	}
}
