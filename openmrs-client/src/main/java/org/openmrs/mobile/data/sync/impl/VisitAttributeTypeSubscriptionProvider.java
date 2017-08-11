package org.openmrs.mobile.data.sync.impl;

import org.openmrs.mobile.data.db.Repository;
import org.openmrs.mobile.data.db.impl.RecordInfoDbService;
import org.openmrs.mobile.data.db.impl.VisitAttributeTypeDbServie;
import org.openmrs.mobile.data.rest.impl.VisitAttributeTypeRestServiceImpl;
import org.openmrs.mobile.data.sync.AdaptiveSubscriptionProvider;
import org.openmrs.mobile.models.VisitAttributeType;

import javax.inject.Inject;

public class VisitAttributeTypeSubscriptionProvider extends AdaptiveSubscriptionProvider<VisitAttributeType,
		VisitAttributeTypeDbServie, VisitAttributeTypeRestServiceImpl> {
	@Inject
	public VisitAttributeTypeSubscriptionProvider(VisitAttributeTypeDbServie dbService,
			RecordInfoDbService recordInfoDbService,
			VisitAttributeTypeRestServiceImpl restService, Repository repository) {
		super(dbService, recordInfoDbService, restService, repository);
	}
}
