package org.openmrs.mobile.data.rest.impl;

import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.data.rest.BaseEntityRestService;
import org.openmrs.mobile.data.rest.retrofit.VisitRestService;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.DateUtils;

import javax.inject.Inject;

import retrofit2.Call;

public class VisitRestServiceImpl extends BaseEntityRestService<Visit, VisitRestService> {
	@Inject
	public VisitRestServiceImpl() {
	}

	@Override
	protected String getRestPath() {
		return ApplicationConstants.API.REST_ENDPOINT_V1;
	}

	@Override
	protected String getEntityName() {
		return "visit";
	}

	public Call<Visit> endVisit(String uuid, Visit visit, QueryOptions options) {
		return restService.endVisit(buildRestRequestPath(), uuid, visit);
	}

	public Call<Visit> updateVisit(String visitUuid, Visit updatedVisit) {
		final String stopDateTime;
		if (null != updatedVisit.getStopDatetime()) {
			stopDateTime = DateUtils.convertTime(
					updatedVisit.getStopDatetime().getTime(), DateUtils.OPEN_MRS_REQUEST_PATIENT_FORMAT);
		} else
			stopDateTime = null;

		return restService.updateVisit(ApplicationConstants.API.REST_ENDPOINT_V2 + "/custom/visitedit",
				visitUuid, updatedVisit.getVisitType().getUuid(),
				DateUtils.convertTime(updatedVisit.getStartDatetime().getTime(), DateUtils.OPEN_MRS_REQUEST_PATIENT_FORMAT),
				stopDateTime,
				updatedVisit.getAttributes());
	}
}
