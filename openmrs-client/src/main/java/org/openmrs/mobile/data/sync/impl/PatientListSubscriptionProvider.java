package org.openmrs.mobile.data.sync.impl;

import org.greenrobot.eventbus.EventBus;
import org.openmrs.mobile.data.db.Repository;
import org.openmrs.mobile.data.db.impl.PatientListDbService;
import org.openmrs.mobile.data.db.impl.RecordInfoDbService;
import org.openmrs.mobile.data.rest.impl.PatientListRestServiceImpl;
import org.openmrs.mobile.data.sync.AdaptiveSubscriptionProvider;
import org.openmrs.mobile.models.PatientList;

import javax.inject.Inject;

public class PatientListSubscriptionProvider extends AdaptiveSubscriptionProvider<PatientList, PatientListDbService,
		PatientListRestServiceImpl> {
	@Inject
	public PatientListSubscriptionProvider(PatientListDbService dbService, RecordInfoDbService recordInfoDbService,
			PatientListRestServiceImpl restService, Repository repository, EventBus eventBus) {
		super(dbService, recordInfoDbService, restService, repository, eventBus);
	}
}
