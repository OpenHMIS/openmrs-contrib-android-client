package org.openmrs.mobile.data.impl;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.openmrs.mobile.data.BaseDataService;
import org.openmrs.mobile.data.PagingInfo;
import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.data.db.impl.EncounterDbService;
import org.openmrs.mobile.data.rest.impl.EncounterRestServiceImpl;
import org.openmrs.mobile.models.Encounter;

import java.util.List;

import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

public class EncounterDataService extends BaseDataService<Encounter, EncounterDbService, EncounterRestServiceImpl> {
	@Inject
	public EncounterDataService() { }

	public void getByVisit(@NonNull String visitUuid, @Nullable QueryOptions options, @Nullable PagingInfo pagingInfo,
			@NonNull GetCallback<List<Encounter>> callback) {
		checkNotNull(visitUuid);
		checkNotNull(callback);

		executeMultipleCallback(callback, options, pagingInfo,
				() -> dbService.getByVisit(visitUuid, options, pagingInfo),
				() -> restService.getByVisit(visitUuid, options, pagingInfo));
	}
}
