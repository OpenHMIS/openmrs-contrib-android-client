package org.openmrs.mobile.data.rest.impl;

import static org.openmrs.mobile.data.rest.RestConstants.Representations.PATIENT_LIST_PATIENTS;

import org.openmrs.mobile.data.PagingInfo;
import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.data.rest.BaseRestService;
import org.openmrs.mobile.data.rest.retrofit.PatientListContextRestService;
import org.openmrs.mobile.models.PatientListContext;
import org.openmrs.mobile.models.Results;
import org.openmrs.mobile.utilities.ApplicationConstants;

import javax.inject.Inject;

import retrofit2.Call;

public class PatientListContextRestServiceImpl extends BaseRestService<PatientListContext, PatientListContextRestService> {
	@Inject
	public PatientListContextRestServiceImpl() { }

	@Override
	protected String getRestPath() {
		return ApplicationConstants.API.REST_ENDPOINT_V2;
	}

	@Override
	protected String getEntityName() {
		return "patientlist/data";
	}

	public Call<Results<PatientListContext>> getListPatients(String patientListUuid, QueryOptions options, PagingInfo pagingInfo) {
		// If no paging is specified then override the server defaults to return all records
		if (pagingInfo == null) {
			pagingInfo = PagingInfo.ALL;
		}
		String representation = QueryOptions.getRepresentation(options);
		if (options == null) {
			representation = PATIENT_LIST_PATIENTS;
		}

		return restService.getAll(buildRestRequestPath(), patientListUuid,
				representation, QueryOptions.getIncludeInactive(options),
				PagingInfo.getLimit(pagingInfo), PagingInfo.getStartIndex(pagingInfo));
	}
}
