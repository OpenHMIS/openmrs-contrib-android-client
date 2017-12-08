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

package org.openmrs.mobile.activities.visit;

import android.widget.TextView;

import org.openmrs.mobile.activities.IBaseDiagnosisView;
import org.openmrs.mobile.activities.BasePresenterContract;
import org.openmrs.mobile.activities.BaseView;
import org.openmrs.mobile.event.VisitDashboardDataRefreshEvent;
import org.openmrs.mobile.models.Concept;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.models.VisitAttributeType;
import org.openmrs.mobile.models.VisitPhoto;
import org.openmrs.mobile.models.VisitPredefinedTask;
import org.openmrs.mobile.models.VisitTask;
import org.openmrs.mobile.utilities.ToastUtil;

import java.util.List;

public interface VisitContract {

	interface VisitDashboardPageView extends BaseView<VisitDashboardPagePresenter> {

		void displayRefreshingData(boolean visible);

		void onVisitDashboardRefreshEvent(VisitDashboardDataRefreshEvent event);
	}

	interface VisitTasksView extends VisitDashboardPageView {

		void setOpenVisitTasks(List<VisitTask> visitTaskList);

		void setClosedVisitTasks(List<VisitTask> visitTaskList);

		void setPredefinedTasks(List<VisitPredefinedTask> predefinedTasks);

		void setSelectedVisitTask(VisitTask visitTask);

		void setUnSelectedVisitTask(VisitTask visitTask);

		void setVisit(Visit visit);

		void clearTextField();

		void showTabSpinner(boolean visibility);

	}

	interface VisitDetailsView extends VisitDashboardPageView, IBaseDiagnosisView {

		void setVisit(Visit visit);

		void setPatientUUID(String uuid);

		void setVisitUUID(String uuid);

		void setProviderUUID(String uuid);

		void setVisitStopDate(String visitStopDate);

		void setConcept(Concept concept);

		void setAttributeTypes(List<VisitAttributeType> visitAttributeTypes);

		void showTabSpinner(boolean visibility);

	}

	interface VisitPhotoView extends VisitDashboardPageView {
		void updateVisitImageMetadata(List<VisitPhoto> visitPhotos);

		void deleteImage(VisitPhoto visitPhoto);

		void refresh();

		void showNoVisitPhoto();

		String formatVisitImageDescription(String description, String uploadedOn, String uploadedBy);

		void showTabSpinner(boolean visibility);
	}

	/*
	* Presenters
	*/
	interface VisitDashboardPagePresenter extends BasePresenterContract {

		void dataRefreshWasRequested();

		void dataRefreshEventOccurred(VisitDashboardDataRefreshEvent event);
	}

	interface VisitTasksPresenter extends VisitDashboardPagePresenter {

		void addVisitTasks(VisitTask visitTasks);

		void updateVisitTask(VisitTask visitTask);

		void createVisitTasksObject(String visitTask);
	}

	interface VisitPhotoPresenter extends VisitDashboardPagePresenter {
		boolean isLoading();

		void setLoading(boolean loading);

		void uploadImage();

		VisitPhoto getVisitPhoto();

		void deleteImage(VisitPhoto visitPhoto);
	}

	interface VisitDetailsPresenter extends VisitDashboardPagePresenter {

		void getVisit();

		void getPatientUUID();

		void getVisitUUID();

		void getProviderUUID();

		void getVisitStopDate();

		void getConceptAnswer(String uuid, String searchValue, TextView textView);
	}
}
