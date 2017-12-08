package org.openmrs.mobile.listeners;

import org.openmrs.mobile.models.PatientList;

public interface PatientListSyncSwitchToggle {

	void toggleSyncSelection(PatientList patientList, boolean isSelected);
}
