package org.openmrs.mobile.data.sync.impl;

import org.openmrs.mobile.data.db.Repository;
import org.openmrs.mobile.data.db.impl.PersonAttributeTypeDbService;
import org.openmrs.mobile.data.db.impl.RecordInfoDbService;
import org.openmrs.mobile.data.rest.impl.PersonAttributeTypeRestServiceImpl;
import org.openmrs.mobile.data.sync.AdaptiveSubscriptionProvider;
import org.openmrs.mobile.models.PersonAttributeType;

import javax.inject.Inject;

public class PersonAttributeTypeSubscriptionProvider extends AdaptiveSubscriptionProvider<PersonAttributeType,
		PersonAttributeTypeDbService, PersonAttributeTypeRestServiceImpl> {
	@Inject
	public PersonAttributeTypeSubscriptionProvider(PersonAttributeTypeDbService dbService,
			RecordInfoDbService recordInfoDbService,
			PersonAttributeTypeRestServiceImpl restService, Repository repository) {
		super(dbService, recordInfoDbService, restService, repository);
	}
}
