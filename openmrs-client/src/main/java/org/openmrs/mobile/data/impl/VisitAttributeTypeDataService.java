package org.openmrs.mobile.data.impl;

import org.openmrs.mobile.data.BaseDataService;
import org.openmrs.mobile.data.DataService;
import org.openmrs.mobile.data.db.impl.VisitAttributeTypeDbServie;
import org.openmrs.mobile.data.rest.impl.VisitAttributeTypeRestServiceImpl;
import org.openmrs.mobile.models.VisitAttributeType;

public class VisitAttributeTypeDataService
		extends BaseDataService<VisitAttributeType, VisitAttributeTypeDbServie, VisitAttributeTypeRestServiceImpl>
		implements DataService<VisitAttributeType> {

}
