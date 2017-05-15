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

import android.widget.LinearLayout;

import org.openmrs.mobile.activities.BasePresenterContract;
import org.openmrs.mobile.activities.BaseView;
import org.openmrs.mobile.models.Encounter;
import org.openmrs.mobile.models.Observation;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.Visit;

import java.util.List;

public interface PatientDashboardContract {

	interface View extends BaseView<Presenter> {
		void showSnack(String s);

		void updateContactCard(Patient patient);

		void updateActiveVisitCard(List<Visit> visits);

		void updateActiveVisitObservationsCard(Observation observation);

		LinearLayout getVisitNoteContainer();

		Patient getPatient();

		void setProviderUuid(String providerUuid);

	}

	interface Presenter extends BasePresenterContract {

		void fetchPatientData(final String patientId);

		void fetchVisits(Patient patient);

		void fetchEncounterObservations(Encounter encounter);

		Patient getPatient();

		void setStartIndex(int startIndex);

		void setLimit(int limit);
	}
}