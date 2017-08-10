package org.openmrs.mobile.data.sync.impl;

import org.openmrs.mobile.data.db.Repository;
import org.openmrs.mobile.data.db.impl.RecordInfoDbService;
import org.openmrs.mobile.data.db.impl.VisitTypeDbService;
import org.openmrs.mobile.data.rest.impl.VisitTypeRestServiceImpl;
import org.openmrs.mobile.data.sync.AdaptiveSubscriptionProvider;
import org.openmrs.mobile.models.VisitType;

import javax.inject.Inject;

public class VisitTypeSubscriptionProvider extends AdaptiveSubscriptionProvider<VisitType, VisitTypeDbService,
		VisitTypeRestServiceImpl> {
	@Inject
	public VisitTypeSubscriptionProvider(VisitTypeDbService dbService,
			RecordInfoDbService recordInfoDbService,
			VisitTypeRestServiceImpl restService, Repository repository) {
		super(dbService, recordInfoDbService, restService, repository);
	}
}
