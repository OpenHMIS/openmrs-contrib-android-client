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

package org.openmrs.mobile.activities.auditdata;

import android.content.Context;

import org.openmrs.mobile.activities.BasePresenterContract;
import org.openmrs.mobile.activities.BaseView;
import org.openmrs.mobile.models.ConceptAnswer;
import org.openmrs.mobile.models.Encounter;
import org.openmrs.mobile.models.Location;
import org.openmrs.mobile.models.Observation;
import org.openmrs.mobile.models.Visit;

import java.util.List;

public interface AuditDataContract {

	interface View extends BaseView<Presenter> {

		void setVisit(Visit visit);

		void updateFormFields(Encounter encounter);

		void setEncounterUuid(String uuid);

		Context getContext();

		void showProgressBar(Boolean visibility);

		void goBackToVisitPage();

		void updateSubmitButtonText();

		void showPageSpinner(boolean visibility);

		void hideSoftKeys();

		void setInpatientTypeServices(List<ConceptAnswer> conceptAnswers);
	}

	interface Presenter extends BasePresenterContract {

		void fetchInpatientTypeServices();

		void fetchVisit(String patientId);

		void saveUpdateEncounter(Encounter encounter, boolean isNewEncounter);

		boolean isObservationExistingForCurrentEncounter(Observation observation);
	}

}