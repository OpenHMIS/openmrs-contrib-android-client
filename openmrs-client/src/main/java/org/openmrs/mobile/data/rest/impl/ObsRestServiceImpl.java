package org.openmrs.mobile.data.rest.impl;

import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.data.rest.BaseRestService;
import org.openmrs.mobile.data.rest.RestConstants;
import org.openmrs.mobile.data.rest.retrofit.ObsRestService;
import org.openmrs.mobile.models.Observation;
import org.openmrs.mobile.models.RecordInfo;
import org.openmrs.mobile.models.Results;
import org.openmrs.mobile.utilities.ApplicationConstants;

import javax.inject.Inject;

import retrofit2.Call;

public class ObsRestServiceImpl extends BaseRestService<Observation, ObsRestService> {
	@Inject
	public ObsRestServiceImpl() {
	}

	@Override
	protected String getRestPath() {
		return ApplicationConstants.API.REST_ENDPOINT_V1;
	}

	@Override
	protected String getEntityName() {
		return "obs";
	}

	public Call<Results<Observation>> getVisitPhotoObservations(String visitUuid) {
		return restService.getVisitPhotoObservations(buildRestRequestPath(), visitUuid,
				ApplicationConstants.ObservationLocators.VISIT_DOCUMENT_UUID, RestConstants.Representations.OBSERVATION);
	}

	public Call<Results<RecordInfo>> getRecordInfoObservationsByVisit(String visitUuid) {
		return restService.getRecordInfoObservationsByVisit(buildRestRequestPath(), visitUuid,
				ApplicationConstants.ObservationLocators.VISIT_DOCUMENT_UUID, RestConstants.Representations.OBSERVATION);
	}
}
