package org.openmrs.mobile.data.sync.impl;

import org.greenrobot.eventbus.EventBus;
import org.openmrs.mobile.data.db.Repository;
import org.openmrs.mobile.data.db.impl.RecordInfoDbService;
import org.openmrs.mobile.data.db.impl.VisitAttributeTypeDbService;
import org.openmrs.mobile.data.rest.impl.VisitAttributeTypeRestServiceImpl;
import org.openmrs.mobile.data.sync.AdaptiveSubscriptionProvider;
import org.openmrs.mobile.models.VisitAttributeType;

import javax.inject.Inject;

public class VisitAttributeTypeSubscriptionProvider extends AdaptiveSubscriptionProvider<VisitAttributeType,
		VisitAttributeTypeDbService, VisitAttributeTypeRestServiceImpl> {
	@Inject
	public VisitAttributeTypeSubscriptionProvider(VisitAttributeTypeDbService dbService,
			RecordInfoDbService recordInfoDbService,
			VisitAttributeTypeRestServiceImpl restService, Repository repository, EventBus eventBus) {
		super(dbService, recordInfoDbService, restService, repository, eventBus);
	}
}
