package org.openmrs.mobile.data.sync.impl;

import org.openmrs.mobile.data.db.Repository;
import org.openmrs.mobile.data.db.impl.PatientIdentifierTypeDbService;
import org.openmrs.mobile.data.db.impl.RecordInfoDbService;
import org.openmrs.mobile.data.rest.impl.PatientIdentifierTypeRestServiceImpl;
import org.openmrs.mobile.data.sync.AdaptiveSubscriptionProvider;
import org.openmrs.mobile.models.PatientIdentifierType;

import javax.inject.Inject;

public class PatientIdentifierTypeSubscriptionProvider extends AdaptiveSubscriptionProvider<PatientIdentifierType,
		PatientIdentifierTypeDbService, PatientIdentifierTypeRestServiceImpl> {
	@Inject
	public PatientIdentifierTypeSubscriptionProvider(PatientIdentifierTypeDbService dbService,
			RecordInfoDbService recordInfoDbService,
			PatientIdentifierTypeRestServiceImpl restService, Repository repository) {
		super(dbService, recordInfoDbService, restService, repository);
	}
}
