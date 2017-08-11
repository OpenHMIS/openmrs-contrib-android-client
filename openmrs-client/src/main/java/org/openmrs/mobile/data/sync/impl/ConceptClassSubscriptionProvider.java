package org.openmrs.mobile.data.sync.impl;

import org.openmrs.mobile.data.db.Repository;
import org.openmrs.mobile.data.db.impl.ConceptClassDbService;
import org.openmrs.mobile.data.db.impl.RecordInfoDbService;
import org.openmrs.mobile.data.rest.impl.ConceptClassRestServiceImpl;
import org.openmrs.mobile.data.sync.AdaptiveSubscriptionProvider;
import org.openmrs.mobile.models.ConceptClass;

import javax.inject.Inject;

public class ConceptClassSubscriptionProvider extends AdaptiveSubscriptionProvider<ConceptClass, ConceptClassDbService,
		ConceptClassRestServiceImpl> {
	@Inject
	public ConceptClassSubscriptionProvider(ConceptClassDbService dbService,
			RecordInfoDbService recordInfoDbService,
			ConceptClassRestServiceImpl restService, Repository repository) {
		super(dbService, recordInfoDbService, restService, repository);
	}
}
