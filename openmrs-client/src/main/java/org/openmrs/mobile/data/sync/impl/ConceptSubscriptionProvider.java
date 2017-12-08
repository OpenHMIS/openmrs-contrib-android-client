package org.openmrs.mobile.data.sync.impl;

import org.greenrobot.eventbus.EventBus;
import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.data.db.Repository;
import org.openmrs.mobile.data.db.impl.ConceptDbService;
import org.openmrs.mobile.data.db.impl.PersonAttributeTypeDbService;
import org.openmrs.mobile.data.db.impl.RecordInfoDbService;
import org.openmrs.mobile.data.rest.RestHelper;
import org.openmrs.mobile.data.rest.impl.ConceptRestServiceImpl;
import org.openmrs.mobile.data.sync.AdaptiveSubscriptionProvider;
import org.openmrs.mobile.models.Concept;
import org.openmrs.mobile.models.PersonAttributeType;
import org.openmrs.mobile.models.PullSubscription;
import org.openmrs.mobile.utilities.ApplicationConstants;

import java.util.List;

import javax.inject.Inject;

public class ConceptSubscriptionProvider extends
		AdaptiveSubscriptionProvider<Concept, ConceptDbService, ConceptRestServiceImpl> {

	private PersonAttributeTypeDbService personAttributeTypeDbService;
	private List<PersonAttributeType> personAttributeTypes;

	@Inject
	public ConceptSubscriptionProvider(ConceptDbService dbService,
			RecordInfoDbService recordInfoDbService,
			ConceptRestServiceImpl restService, Repository repository, EventBus eventBus,
			PersonAttributeTypeDbService personAttributeTypeDbService) {
		super(dbService, recordInfoDbService, restService, repository, eventBus);

		this.personAttributeTypeDbService = personAttributeTypeDbService;
	}

	@Override
	public void initialize(PullSubscription subscription) {
		super.initialize(subscription);

		personAttributeTypes = personAttributeTypeDbService.getAll(null, null);
	}

	@Override
	public void pull(PullSubscription subscription) {
		//Load the Inpatient Service Type
		Concept concept = RestHelper.getCallValue(restService.getByUuid(ApplicationConstants.AuditFormConcepts
				.CONCEPT_INPATIENT_SERVICE_TYPE, QueryOptions.FULL_REP));
		concept.processRelationships();

		if (concept != null) {
			dbService.save(concept);
		}

		// load person attribute type concepts
		if (!personAttributeTypes.isEmpty()) {
			for (PersonAttributeType type : personAttributeTypes) {
				if (type.getConcept() != null) {
					Concept personAttrConcept = RestHelper.getCallValue(
							restService.getByUuid(type.getConcept().getUuid(), QueryOptions.FULL_REP));
					if (personAttrConcept != null) {
						dbService.save(personAttrConcept);
					}
				}
			}
		}
	}
}
