/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and
 * limitations under the License.
 *
 * Copyright (C) OpenHMIS.  All Rights Reserved.
 */
package org.openmrs.mobile.activities.patientlist;

import android.support.annotation.Nullable;
import org.openmrs.mobile.activities.BasePresenterContract;
import org.openmrs.mobile.activities.BaseView;
import org.openmrs.mobile.models.PatientList;
import org.openmrs.mobile.models.PatientListContext;

import java.util.List;

public interface PatientListContract {

	interface View extends BaseView<Presenter> {

		void setNumberOfPatientsView(int length);

		void setSelectedPatientList(PatientList selectedPatientList);

		void setEmptyPatientListVisibility(boolean visibility);

		void setNoPatientListsVisibility(boolean visibility);

		void updatePatientLists(List<PatientList> patientList, List<PatientList> syncingPatientLists);

		void updatePatientListData(List<PatientListContext> patientListData, boolean forceRefresh);

		void setSpinnerVisibility(boolean visibility);

		boolean isActive();

		void showPatientListProgressSpinner(boolean visible);

		void updatePagingLabel(int currentPage, int totalNumberOfPages);

		void updatePatientListSyncDisplay(List<PatientList> patientList, List<PatientList> syncingPatientLists);
	}

	interface Presenter extends BasePresenterContract {

		void refresh();

		void getPatientList();

		void getPatientListData(String patientListUuid, int startIndex, boolean wasForceRefresh);

		void setTotalNumberResults(int totalNumberResults);

		int getTotalNumberPages();

		int getPage();

		void setPage(int page);

		int getLimit();

		boolean isLoading();

		void setLoading(boolean loading);

		void loadResults(String patientListUuid, boolean loadNextResults);

		void setExistingPatientListUuid(String uuid);

		void syncSelectionsSaved();
	}
}
