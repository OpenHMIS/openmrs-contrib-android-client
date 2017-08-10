package org.openmrs.mobile.data.rest.impl;

import org.openmrs.mobile.data.PagingInfo;
import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.data.rest.BaseEntityRestService;
import org.openmrs.mobile.data.rest.retrofit.VisitTaskRestService;
import org.openmrs.mobile.models.Results;
import org.openmrs.mobile.models.VisitTask;
import org.openmrs.mobile.utilities.ApplicationConstants;

import java.util.Date;

import javax.inject.Inject;

import retrofit2.Call;

public class VisitTaskRestServiceImpl extends BaseEntityRestService<VisitTask, VisitTaskRestService> {
	@Inject
	public VisitTaskRestServiceImpl() { }

	@Override
	protected String getRestPath() {
		return ApplicationConstants.API.REST_ENDPOINT_V2;
	}

	@Override
	protected String getEntityName() {
		return "visittasks/task";
	}

	public Call<Results<VisitTask>> getAll(String status, String patient_uuid, String visit_uuid, QueryOptions options,
			PagingInfo pagingInfo) {
		return restService.getAll(buildRestRequestPath(), status, patient_uuid, visit_uuid,
				QueryOptions.getRepresentation(options), QueryOptions.getIncludeInactive(options),
				PagingInfo.getLimit(pagingInfo), PagingInfo.getStartIndex(pagingInfo));
	}

	public Call<Results<VisitTask>> getAllSince(String visitUuid, Date since, QueryOptions options, PagingInfo pagingInfo) {
		return null;
	}
}
