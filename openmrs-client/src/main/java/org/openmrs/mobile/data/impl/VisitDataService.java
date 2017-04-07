package org.openmrs.mobile.data.impl;

import org.openmrs.mobile.data.BaseEntityDataService;
import org.openmrs.mobile.data.EntityDataService;
import org.openmrs.mobile.data.rest.VisitRestService;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.utilities.ApplicationConstants;

public class VisitDataService extends BaseEntityDataService<Visit>
        implements EntityDataService<Visit> {
    @Override
    protected Class<VisitRestService> getRestServiceClass() {
        return VisitRestService.class;
    }

    @Override
    protected String getRestPath() {
        return ApplicationConstants.API.REST_ENDPOINT_V1;
    }

    @Override
    protected String getEntityName() {
        return "visit";
    }
}
