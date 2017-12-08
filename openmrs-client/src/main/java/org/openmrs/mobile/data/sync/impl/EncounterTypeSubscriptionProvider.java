package org.openmrs.mobile.data.sync.impl;

import org.greenrobot.eventbus.EventBus;
import org.openmrs.mobile.data.db.Repository;
import org.openmrs.mobile.data.db.impl.EncounterTypeDbService;
import org.openmrs.mobile.data.db.impl.RecordInfoDbService;
import org.openmrs.mobile.data.rest.impl.EncounterTypeRestServiceImpl;
import org.openmrs.mobile.data.sync.AdaptiveSubscriptionProvider;
import org.openmrs.mobile.models.EncounterType;

import javax.inject.Inject;

public class EncounterTypeSubscriptionProvider extends AdaptiveSubscriptionProvider<EncounterType, EncounterTypeDbService,
		EncounterTypeRestServiceImpl> {
	@Inject
	public EncounterTypeSubscriptionProvider(EncounterTypeDbService dbService,
			RecordInfoDbService recordInfoDbService,
			EncounterTypeRestServiceImpl restService, Repository repository, EventBus eventBus) {
		super(dbService, recordInfoDbService, restService, repository, eventBus);
	}
}
