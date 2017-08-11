package org.openmrs.mobile.data.rest.impl;

import org.openmrs.mobile.data.rest.BaseMetadataRestService;
import org.openmrs.mobile.data.rest.retrofit.EncounterTypeRestService;
import org.openmrs.mobile.models.EncounterType;
import org.openmrs.mobile.utilities.ApplicationConstants;

import javax.inject.Inject;

public class EncounterTypeRestServiceImpl extends BaseMetadataRestService<EncounterType, EncounterTypeRestService> {
	@Inject
	public EncounterTypeRestServiceImpl() { }

	@Override
	protected String getRestPath() {
		return ApplicationConstants.API.REST_ENDPOINT_V1;
	}

	@Override
	protected String getEntityName() {
		return "encountertype";
	}
}
