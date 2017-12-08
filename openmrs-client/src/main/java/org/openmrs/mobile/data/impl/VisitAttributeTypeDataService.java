package org.openmrs.mobile.data.impl;

import org.openmrs.mobile.data.BaseDataService;
import org.openmrs.mobile.data.DataService;
import org.openmrs.mobile.data.db.impl.VisitAttributeTypeDbService;
import org.openmrs.mobile.data.rest.impl.VisitAttributeTypeRestServiceImpl;
import org.openmrs.mobile.models.VisitAttributeType;

import javax.inject.Inject;

public class VisitAttributeTypeDataService
		extends BaseDataService<VisitAttributeType, VisitAttributeTypeDbService, VisitAttributeTypeRestServiceImpl>
		implements DataService<VisitAttributeType> {
	@Inject
	public VisitAttributeTypeDataService() { }
}
