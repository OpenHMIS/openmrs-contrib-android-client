package org.openmrs.mobile.data.rest.impl;

import org.openmrs.mobile.data.rest.BaseRestService;
import org.openmrs.mobile.data.rest.retrofit.ConceptClassRestService;
import org.openmrs.mobile.models.ConceptClass;
import org.openmrs.mobile.utilities.ApplicationConstants;

import javax.inject.Inject;

public class ConceptClassRestServiceImpl extends BaseRestService<ConceptClass, ConceptClassRestService> {
	@Inject
	public ConceptClassRestServiceImpl() { }

	@Override
	protected String getRestPath() {
		return ApplicationConstants.API.REST_ENDPOINT_V1;
	}

	@Override
	protected String getEntityName() {
		return "conceptclass";
	}
}
