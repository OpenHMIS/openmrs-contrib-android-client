package org.openmrs.mobile.data.sync.impl;

import org.greenrobot.eventbus.EventBus;
import org.openmrs.mobile.data.db.Repository;
import org.openmrs.mobile.data.db.impl.ConceptAnswerDbService;
import org.openmrs.mobile.data.db.impl.RecordInfoDbService;
import org.openmrs.mobile.data.db.impl.VisitAttributeTypeDbService;
import org.openmrs.mobile.data.rest.RestHelper;
import org.openmrs.mobile.data.rest.impl.ConceptAnswerRestServiceImpl;
import org.openmrs.mobile.data.rest.impl.VisitAttributeTypeRestServiceImpl;
import org.openmrs.mobile.data.sync.AdaptiveSubscriptionProvider;
import org.openmrs.mobile.models.ConceptAnswer;
import org.openmrs.mobile.models.PullSubscription;
import org.openmrs.mobile.models.VisitAttributeType;

import java.util.List;

import javax.inject.Inject;

public class ConceptAnswerSubscriptionProvider extends AdaptiveSubscriptionProvider<ConceptAnswer,
		ConceptAnswerDbService, ConceptAnswerRestServiceImpl> {

	private VisitAttributeTypeDbService visitAttributeTypeDbService;
	private List<VisitAttributeType> visitAttributeTypes;

	@Inject
	public ConceptAnswerSubscriptionProvider(ConceptAnswerDbService dbService,
			RecordInfoDbService recordInfoDbService,
			ConceptAnswerRestServiceImpl restService, Repository repository,
			EventBus eventBus, VisitAttributeTypeDbService visitAttributeTypeDbService) {
		super(dbService, recordInfoDbService, restService, repository, eventBus);

		this.visitAttributeTypeDbService = visitAttributeTypeDbService;
	}

	@Override
	public void initialize(PullSubscription subscription) {
		visitAttributeTypes = visitAttributeTypeDbService.getAll(null, null);
	}

	@Override
	public void pull(PullSubscription subscription) {
		for(VisitAttributeType visitAttributeType : visitAttributeTypes) {
			if(visitAttributeType.getDatatypeClassname()
					.equalsIgnoreCase("org.openmrs.module.coreapps.customdatatype.CodedConceptDatatype")){
				List<ConceptAnswer> conceptAnswers = RestHelper.getCallListValue(
						restService.getByConceptUuid(visitAttributeType.getDatatypeConfig(), null));
				saveAllDb(conceptAnswers);
			}
		}
	}
}

