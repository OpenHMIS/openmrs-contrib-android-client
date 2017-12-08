package org.openmrs.mobile.data.impl;

import org.openmrs.mobile.data.BaseDataService;
import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.data.db.impl.ObsDbService;
import org.openmrs.mobile.data.rest.impl.ObsRestServiceImpl;
import org.openmrs.mobile.models.Observation;

import java.util.List;

import javax.inject.Inject;

public class ObsDataService extends BaseDataService<Observation, ObsDbService, ObsRestServiceImpl> {
	@Inject
	public ObsDataService() {
	}

	public void getVisitPhotoObservations(String visitUuid, QueryOptions options,
			GetCallback<List<Observation>> callback) {
		executeMultipleCallback(callback, options, null,
				() -> dbService.getVisitPhotoObservations(visitUuid, options),
				() -> restService.getVisitPhotoObservations(visitUuid));
	}
}
