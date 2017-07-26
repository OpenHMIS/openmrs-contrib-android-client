package org.openmrs.mobile.activities;

import org.openmrs.mobile.models.DiagnosisSearchResult;
import org.openmrs.mobile.models.EncounterDiagnosis;
import org.openmrs.mobile.models.Observation;
import org.openmrs.mobile.models.Visit;

import java.util.List;

public interface IBaseDiagnosisView {

	void showTabSpinner(boolean show);

	void saveVisitNote(String encounterUuid, String clinicalNote, Visit visit);

	void createEncounterDiagnosis(Observation observation, String diagnosis, String conceptNameId, boolean loadRecyclerView);

	void setPrimaryDiagnosis(EncounterDiagnosis primaryDiagnosis);

	void setSecondaryDiagnosis(EncounterDiagnosis secondaryDiagnosis);

	void setDiagnosisCertainty(EncounterDiagnosis confirmedDiagnosis);

	void removeDiagnosis(EncounterDiagnosis removeDiagnosis, String order);

	void setSearchDiagnoses(List<DiagnosisSearchResult> concepts);
}
