package org.openmrs.mobile.data.rest.impl;

import org.openmrs.mobile.data.rest.BaseMetadataRestService;
import org.openmrs.mobile.data.rest.retrofit.RestServiceBuilder;
import org.openmrs.mobile.data.rest.retrofit.SessionRestService;
import org.openmrs.mobile.models.Session;
import org.openmrs.mobile.utilities.ApplicationConstants;

import javax.inject.Inject;

import retrofit2.Call;

public class SessionRestServiceImpl extends BaseMetadataRestService<Session, SessionRestService> {
	@Inject
	public SessionRestServiceImpl() { }

	@Override
	protected String getRestPath() {
		return ApplicationConstants.API.REST_ENDPOINT_V1;
	}

	@Override
	protected String getEntityName() {
		return "session";
	}

	public Call<Session> getSession(String username, String password) {
		restService = RestServiceBuilder.createService(SessionRestService.class, username, password);

		return restService.getSession();
	}
}
