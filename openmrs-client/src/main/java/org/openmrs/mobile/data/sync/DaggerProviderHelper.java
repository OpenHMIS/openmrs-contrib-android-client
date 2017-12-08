package org.openmrs.mobile.data.sync;

import android.support.annotation.NonNull;
import android.util.Log;

import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.dagger.DaggerSyncComponent;
import org.openmrs.mobile.dagger.SyncComponent;
import org.openmrs.mobile.dagger.SyncModule;
import org.openmrs.mobile.data.sync.impl.ConceptAnswerSubscriptionProvider;
import org.openmrs.mobile.data.sync.impl.ConceptClassSubscriptionProvider;
import org.openmrs.mobile.data.sync.impl.ConceptSubscriptionProvider;
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
import org.openmrs.mobile.models.Encounter;
import org.openmrs.mobile.models.Observation;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.models.VisitNote;
import org.openmrs.mobile.models.VisitPhoto;
import org.openmrs.mobile.models.VisitTask;

import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

public class DaggerProviderHelper {
	private static final String TAG = DaggerProviderHelper.class.getSimpleName();

	private static final String DIAGNOSIS_CONCEPT_SUBSCRIPTION = DiagnosisConceptSubscriptionProvider.class.getSimpleName();
	private static final String LOCATION_SUBSCRIPTION = LocationSubscriptionProvider.class.getSimpleName();
	private static final String PATIENT_LIST_CONTEXT_SUBSCRIPTION =
			PatientListContextSubscriptionProvider.class.getSimpleName();
	private static final String PATIENT_LIST_SUBSCRIPTION = PatientListSubscriptionProvider.class.getSimpleName();
	private static final String CONCEPT_CLASS_SUBSCRIPTION = ConceptClassSubscriptionProvider.class.getSimpleName();
	private static final String ENCOUNTER_TYPE_SUBSCRIPTION = EncounterTypeSubscriptionProvider.class.getSimpleName();
	private static final String PATIENT_IDENTIFIER_TYPE_SUBSCRIPTION =
			PatientIdentifierTypeSubscriptionProvider.class.getSimpleName();
	private static final String PERSON_ATTRIBUTE_TYPE_SUBSCRIPTION =
			PersonAttributeTypeSubscriptionProvider.class.getSimpleName();
	private static final String VISIT_ATTRIBUTE_TYPE_SUBSCRIPTION =
			VisitAttributeTypeSubscriptionProvider.class.getSimpleName();
	private static final String CONCEPT_ANSWER_SUBSCRIPTION =
			ConceptAnswerSubscriptionProvider.class.getSimpleName();
	private static final String VISIT_PREDEFINED_TASK_SUBSCRIPTION =
			VisitPredefinedTaskSubscriptionProvider.class.getSimpleName();
	private static final String VISIT_TYPE_SUBSCRIPTION =
			VisitTypeSubscriptionProvider.class.getSimpleName();
	private static final String PATIENT_PUSH_SYNC = Patient.class.getSimpleName();
	private static final String ENCOUNTER_PUSH_SYNC = Encounter.class.getSimpleName();
	private static final String OBSERVATION_PUSH_SYNC = Observation.class.getSimpleName();
	private static final String VISIT_PUSH_SYNC = Visit.class.getSimpleName();
	private static final String VISIT_TASK_PUSH_SYNC = VisitTask.class.getSimpleName();
	private static final String VISIT_NOTE_PUSH_SYNC = VisitNote.class.getSimpleName();
	private static final String VISIT_PHOTO_PUSH_SYNC = VisitPhoto.class.getSimpleName();
	private static final String CONCEPT_SUBSCRIPTION = ConceptSubscriptionProvider.class.getSimpleName();

	private SyncComponent syncComponent;

	@Inject
	public DaggerProviderHelper(OpenMRS openMRS) {
		this.syncComponent = DaggerSyncComponent.builder().syncModule(new SyncModule(openMRS)).build();
	}

	public SubscriptionProvider getSubscriptionProvider(@NonNull String className) {
		checkNotNull(className);

		SubscriptionProvider provider = null;
		if (className.endsWith(DIAGNOSIS_CONCEPT_SUBSCRIPTION)) {
			provider = syncComponent.diagnosisConceptSubscriptionProvider();
		} else if (className.endsWith(LOCATION_SUBSCRIPTION)) {
			provider = syncComponent.locationSubscriptionProvider();
		} else if (className.endsWith(PATIENT_LIST_CONTEXT_SUBSCRIPTION)) {
			provider = syncComponent.patientListContextSubscriptionProvider();
		} else if (className.endsWith(PATIENT_LIST_SUBSCRIPTION)) {
			provider = syncComponent.patientListSubscriptionProvider();
		} else if (className.endsWith(CONCEPT_CLASS_SUBSCRIPTION)) {
			provider = syncComponent.conceptClassSubscriptionProvider();
		} else if (className.endsWith(ENCOUNTER_TYPE_SUBSCRIPTION)) {
			provider = syncComponent.encounterTypeSubscriptionProvider();
		} else if (className.endsWith(PATIENT_IDENTIFIER_TYPE_SUBSCRIPTION)) {
			provider = syncComponent.patientIdentifierTypeSubscriptionProvider();
		} else if (className.endsWith(PERSON_ATTRIBUTE_TYPE_SUBSCRIPTION)) {
			provider = syncComponent.personAttributeTypeSubscriptionProvider();
		} else if (className.endsWith(VISIT_ATTRIBUTE_TYPE_SUBSCRIPTION)) {
			provider = syncComponent.visitAttributeTypeSubscriptionProvider();
		} else if (className.endsWith(VISIT_PREDEFINED_TASK_SUBSCRIPTION)) {
			provider = syncComponent.visitPredefinedTaskSubscriptionProvider();
		} else if (className.endsWith(VISIT_TYPE_SUBSCRIPTION)) {
			provider = syncComponent.visitTypeSubscriptionProvider();
		} else if (className.endsWith(CONCEPT_ANSWER_SUBSCRIPTION)) {
			provider = syncComponent.conceptAnswerSubscriptionProvider();
		} else if (className.endsWith(CONCEPT_SUBSCRIPTION)) {
			provider = syncComponent.conceptSubscriptionProvider();
		}

		if (provider == null) {
			Log.e(TAG, "Unknown subscription provider '" + className + "'");
		}

		return provider;
	}

	public PushProvider getSyncProvider(String className) {
		checkNotNull(className);

		PushProvider provider = null;
		if (className.endsWith(PATIENT_PUSH_SYNC)) {
			provider = syncComponent.patientPushProvider();
		} else if (className.endsWith(ENCOUNTER_PUSH_SYNC)) {
			provider = syncComponent.encounterPushProvider();
		} else if (className.endsWith(OBSERVATION_PUSH_SYNC)) {
			provider = syncComponent.observationPushProvider();
		} else if (className.endsWith(VISIT_PUSH_SYNC)) {
			provider = syncComponent.visitPushProvider();
		} else if (className.endsWith(VISIT_TASK_PUSH_SYNC)) {
			provider = syncComponent.visitTaskPushProvider();
		} else if (className.endsWith(VISIT_NOTE_PUSH_SYNC)) {
			provider = syncComponent.visitNotePushProvider();
		} else if (className.endsWith(VISIT_PHOTO_PUSH_SYNC)) {
			provider = syncComponent.visitPhotoPushProvider();
		}

		return provider;
	}
}
