package org.openmrs.mobile.data.sync.impl;

import org.openmrs.mobile.data.db.Repository;
import org.openmrs.mobile.data.db.impl.RecordInfoDbService;
import org.openmrs.mobile.data.db.impl.VisitPredefinedTaskDbService;
import org.openmrs.mobile.data.rest.impl.VisitPredefinedTaskRestServiceImpl;
import org.openmrs.mobile.data.sync.AdaptiveSubscriptionProvider;
import org.openmrs.mobile.models.VisitPredefinedTask;

import javax.inject.Inject;

public class VisitPredefinedTaskSubscriptionProvider extends AdaptiveSubscriptionProvider<VisitPredefinedTask,
		VisitPredefinedTaskDbService, VisitPredefinedTaskRestServiceImpl> {
	@Inject
	public VisitPredefinedTaskSubscriptionProvider(VisitPredefinedTaskDbService dbService,
			RecordInfoDbService recordInfoDbService,
			VisitPredefinedTaskRestServiceImpl restService, Repository repository) {
		super(dbService, recordInfoDbService, restService, repository);
	}
}
