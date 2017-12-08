package org.openmrs.mobile.data.rest.impl;

import org.openmrs.mobile.data.rest.BaseMetadataRestService;
import org.openmrs.mobile.data.rest.retrofit.VisitPredefinedTaskRestService;
import org.openmrs.mobile.models.VisitPredefinedTask;
import org.openmrs.mobile.utilities.ApplicationConstants;

import javax.inject.Inject;

public class VisitPredefinedTaskRestServiceImpl
		extends BaseMetadataRestService<VisitPredefinedTask, VisitPredefinedTaskRestService> {
	@Inject
	public VisitPredefinedTaskRestServiceImpl() {
	}

	@Override
	protected String getRestPath() {
		return ApplicationConstants.API.REST_ENDPOINT_V2;
	}

	@Override
	protected String getEntityName() {
		return "visittasks/predefinedTask";
	}
}
