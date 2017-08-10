package org.openmrs.mobile.data.rest.impl;

import org.openmrs.mobile.data.PagingInfo;
import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.data.rest.BaseRestService;
import org.openmrs.mobile.data.rest.RestConstants;
import org.openmrs.mobile.data.rest.retrofit.EncounterRestService;
import org.openmrs.mobile.models.Encounter;
import org.openmrs.mobile.models.RecordInfo;
import org.openmrs.mobile.models.Results;
import org.openmrs.mobile.utilities.ApplicationConstants;

import javax.inject.Inject;

import retrofit2.Call;

public class EncounterRestServiceImpl extends BaseRestService<Encounter, EncounterRestService> {
	@Inject
	public EncounterRestServiceImpl() { }

	@Override
	protected String getRestPath() {
		return ApplicationConstants.API.REST_ENDPOINT_V1;
	}

	@Override
	protected String getEntityName() {
		return "encounter";
	}

	public Call<Results<Encounter>> getByVisit(String visitUuid, QueryOptions options, PagingInfo pagingInfo) {
		return restService.getByVisit(buildRestRequestPath(), visitUuid,
				QueryOptions.getRepresentation(options), QueryOptions.getIncludeInactive(options),
				PagingInfo.getLimit(pagingInfo), PagingInfo.getStartIndex(pagingInfo));
	}

	public Call<Results<RecordInfo>> getRecordInfoByVisit(String visitUuid, QueryOptions options, PagingInfo pagingInfo) {
		return restService.getRecordInfoByVisit(buildRestRequestPath(), visitUuid,
				RestConstants.Representations.RECORD_INFO, QueryOptions.getIncludeInactive(options),
				PagingInfo.getLimit(pagingInfo), PagingInfo.getStartIndex(pagingInfo));
	}
}
