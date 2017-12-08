/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.mobile.activities.patientdashboard;

import android.content.Context;

import org.openmrs.mobile.activities.BasePresenterContract;
import org.openmrs.mobile.activities.BaseView;
import org.openmrs.mobile.activities.IBaseDiagnosisView;
import org.openmrs.mobile.models.Encounter;
import org.openmrs.mobile.models.Location;
import org.openmrs.mobile.models.Observation;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.Visit;

import java.util.LinkedList;
import java.util.List;

public interface PatientDashboardContract {

	interface View extends BaseView<Presenter>, IBaseDiagnosisView {

		void patientContacts(Patient patient);

		void setPatientVisits(LinkedList<Visit> visits);

		void addPatientVisits(LinkedList<Visit> visits);

		void notifyAllPatientVisitsFetched();

		Patient getPatient();

		void setProviderUuid(String providerUuid);

		void setLocation(Location location);

		Context getContext();

		void showSavingClinicalNoteProgressBar(boolean show);

		void showPageSpinner(boolean visibility);

		void showNoVisits(boolean visibility);

		void updateClinicVisitNote(Observation observation, Encounter encounter);

		void showNoPatientData(boolean visible);

		void navigateBack();

		void alertOfflineAndPatientNotFound();

		void displayRefreshingData(boolean visible);
	}

	interface Presenter extends BasePresenterContract {

		void fetchPatientData();

		Patient getPatient();

		boolean isLoading();

		void setLoading(boolean loading);

		void loadResults();

		void dataRefreshWasRequested();
	}
}