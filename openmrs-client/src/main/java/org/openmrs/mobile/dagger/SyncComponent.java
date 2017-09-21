package org.openmrs.mobile.dagger;

import org.openmrs.mobile.data.db.impl.PullSubscriptionDbService;
import org.openmrs.mobile.data.sync.SyncService;
import org.openmrs.mobile.data.sync.impl.ConceptAnswerSubscriptionProvider;
import org.openmrs.mobile.data.sync.impl.ConceptClassSubscriptionProvider;
import org.openmrs.mobile.data.sync.impl.DiagnosisConceptSubscriptionProvider;
import org.openmrs.mobile.data.sync.impl.EncounterTypeSubscriptionProvider;
import org.openmrs.mobile.data.sync.impl.LocationSubscriptionProvider;
import org.openmrs.mobile.data.sync.impl.PatientIdentifierTypeSubscriptionProvider;
import org.openmrs.mobile.data.sync.impl.PatientListContextSubscriptionProvider;
import org.openmrs.mobile.data.sync.impl.PatientListSubscriptionProvider;
import org.openmrs.mobile.data.sync.impl.PatientTrimProvider;
import org.openmrs.mobile.data.sync.impl.PersonAttributeTypeSubscriptionProvider;
import org.openmrs.mobile.data.sync.impl.VisitAttributeTypeSubscriptionProvider;
import org.openmrs.mobile.data.sync.impl.VisitNotePushProvider;
import org.openmrs.mobile.data.sync.impl.VisitPhotoPushProvider;
import org.openmrs.mobile.data.sync.impl.VisitPredefinedTaskSubscriptionProvider;
import org.openmrs.mobile.data.sync.impl.VisitTypeSubscriptionProvider;
import org.openmrs.mobile.data.sync.impl.EncounterPushProvider;
import org.openmrs.mobile.data.sync.impl.ObservationPushProvider;
import org.openmrs.mobile.data.sync.impl.PatientPushProvider;
import org.openmrs.mobile.data.sync.impl.VisitPushProvider;
import org.openmrs.mobile.data.sync.impl.VisitTaskPushProvider;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = { DbModule.class, SyncModule.class, ContextModule.class })
public interface SyncComponent {
	SyncService syncService();

	PullSubscriptionDbService pullSubscriptionDbService();

	PatientTrimProvider patientTrimProvider();

	DiagnosisConceptSubscriptionProvider diagnosisConceptSubscriptionProvider();

	LocationSubscriptionProvider locationSubscriptionProvider();

	PatientListContextSubscriptionProvider patientListContextSubscriptionProvider();

	PatientListSubscriptionProvider patientListSubscriptionProvider();

	ConceptClassSubscriptionProvider conceptClassSubscriptionProvider();

	EncounterTypeSubscriptionProvider encounterTypeSubscriptionProvider();

	PatientIdentifierTypeSubscriptionProvider patientIdentifierTypeSubscriptionProvider();

	PersonAttributeTypeSubscriptionProvider personAttributeTypeSubscriptionProvider();

	VisitAttributeTypeSubscriptionProvider visitAttributeTypeSubscriptionProvider();

	ConceptAnswerSubscriptionProvider conceptAnswerSubscriptionProvider();

	VisitPredefinedTaskSubscriptionProvider visitPredefinedTaskSubscriptionProvider();

	VisitTypeSubscriptionProvider visitTypeSubscriptionProvider();

	PatientPushProvider patientPushProvider();

	EncounterPushProvider encounterPushProvider();

	ObservationPushProvider observationPushProvider();

	VisitPushProvider visitPushProvider();

	VisitTaskPushProvider visitTaskPushProvider();

	VisitNotePushProvider visitNotePushProvider();

	VisitPhotoPushProvider visitPhotoPushProvider();
}
