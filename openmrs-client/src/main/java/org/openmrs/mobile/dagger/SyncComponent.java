package org.openmrs.mobile.dagger;

import org.openmrs.mobile.data.db.impl.PatientIdentifierTypeDbService;
import org.openmrs.mobile.data.sync.SyncService;
import org.openmrs.mobile.data.sync.impl.ConceptClassSubscriptionProvider;
import org.openmrs.mobile.data.sync.impl.DiagnosisConceptSubscriptionProvider;
import org.openmrs.mobile.data.sync.impl.EncounterTypeSubscriptionProvider;
import org.openmrs.mobile.data.sync.impl.LocationSubscriptionProvider;
import org.openmrs.mobile.data.sync.impl.PatientIdentifierTypeSubscriptionProvider;
import org.openmrs.mobile.data.sync.impl.PatientListContextSubscriptionProvider;
import org.openmrs.mobile.data.sync.impl.PatientListSubscriptionProvider;
import org.openmrs.mobile.data.sync.impl.PersonAttributeTypeSubscriptionProvider;
import org.openmrs.mobile.data.sync.impl.VisitAttributeTypeSubscriptionProvider;
import org.openmrs.mobile.data.sync.impl.VisitPredefinedTaskSubscriptionProvider;
import org.openmrs.mobile.data.sync.impl.VisitTypeSubscriptionProvider;
import org.openmrs.mobile.sync.AndroidSyncService;
import org.openmrs.mobile.sync.SyncAdapter;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = { SyncModule.class, DbModule.class })
public interface SyncComponent {
	void inject(SyncAdapter syncAdapter);
	void inject(AndroidSyncService androidSyncService);

	SyncService syncService();

	DiagnosisConceptSubscriptionProvider diagnosisConceptSubscriptionProvider();

	LocationSubscriptionProvider locationSubscriptionProvider();

	PatientListContextSubscriptionProvider patientListContextSubscriptionProvider();

	PatientListSubscriptionProvider patientListSubscriptionProvider();

	ConceptClassSubscriptionProvider conceptClassSubscriptionProvider();

	EncounterTypeSubscriptionProvider encounterTypeSubscriptionProvider();

	PatientIdentifierTypeSubscriptionProvider patientIdentifierTypeSubscriptionProvider();

	PersonAttributeTypeSubscriptionProvider personAttributeTypeSubscriptionProvider();

	VisitAttributeTypeSubscriptionProvider visitAttributeTypeSubscriptionProvider();

	VisitPredefinedTaskSubscriptionProvider visitPredefinedTaskSubscriptionProvider();

	VisitTypeSubscriptionProvider visitTypeSubscriptionProvider();
}
