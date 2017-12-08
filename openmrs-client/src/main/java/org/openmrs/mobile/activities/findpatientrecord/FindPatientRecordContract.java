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

package org.openmrs.mobile.activities.findpatientrecord;

import android.os.Bundle;

import org.openmrs.mobile.activities.BasePresenterContract;
import org.openmrs.mobile.activities.BaseView;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.utilities.ToastUtil;

import java.util.List;

public interface FindPatientRecordContract {

	interface View extends BaseView<Presenter> {

		void setNumberOfPatientsView(int length);

		void setNoPatientsVisibility(boolean visibility);

		void fetchPatients(List<Patient> patients);

		void setProgressBarVisibility(boolean visibility);

		void showRegistration();

	}

	interface Presenter extends BasePresenterContract {

		void onSaveInstanceState(Bundle outState);

		void findPatient(String name);

		void getLastViewed();

		boolean isLoading();

		void setLoading(boolean loading);

		void loadResults(boolean loadNextResults);

		void refresh();

		int getPage();

		void setPage(int page);
	}

}