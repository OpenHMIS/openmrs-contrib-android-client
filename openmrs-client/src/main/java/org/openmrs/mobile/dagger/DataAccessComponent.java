package org.openmrs.mobile.dagger;

import org.openmrs.mobile.data.impl.ConceptAnswerDataService;
import org.openmrs.mobile.data.impl.ConceptDataService;
import org.openmrs.mobile.data.impl.EncounterDataService;
import org.openmrs.mobile.data.impl.LocationDataService;
import org.openmrs.mobile.data.impl.ObsDataService;
import org.openmrs.mobile.data.impl.PatientDataService;
import org.openmrs.mobile.data.impl.PatientIdentifierTypeDataService;
import org.openmrs.mobile.data.impl.PatientListContextDataService;
import org.openmrs.mobile.data.impl.PatientListDataService;
import org.openmrs.mobile.data.impl.PersonAttributeTypeDataService;
import org.openmrs.mobile.data.impl.ProviderDataService;
import org.openmrs.mobile.data.impl.SessionDataService;
import org.openmrs.mobile.data.impl.UserDataService;
import org.openmrs.mobile.data.impl.VisitAttributeTypeDataService;
import org.openmrs.mobile.data.impl.VisitDataService;
import org.openmrs.mobile.data.impl.VisitNoteDataService;
import org.openmrs.mobile.data.impl.VisitPhotoDataService;
import org.openmrs.mobile.data.impl.VisitPredefinedTaskDataService;
import org.openmrs.mobile.data.impl.VisitTaskDataService;
import org.openmrs.mobile.data.impl.VisitTypeDataService;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = { CacheModule.class, DbModule.class, SyncLogModule.class })
public interface DataAccessComponent {
	ConceptAnswerDataService conceptAnswer();

	ConceptDataService concept();

	EncounterDataService encounter();

	LocationDataService location();

	ObsDataService obs();

	PatientDataService patient();

	PatientIdentifierTypeDataService patientIdentifierType();

	PatientListContextDataService patientListContext();

	PatientListDataService patientList();

	PersonAttributeTypeDataService personAttributeType();

	ProviderDataService provider();

	SessionDataService session();

	UserDataService user();

	VisitAttributeTypeDataService visitAttributeType();

	VisitDataService visit();

	VisitNoteDataService visitNote();

	VisitPhotoDataService visitPhoto();

	VisitPredefinedTaskDataService visitPredefinedTask();

	VisitTaskDataService visitTask();

	VisitTypeDataService visitType();
}
