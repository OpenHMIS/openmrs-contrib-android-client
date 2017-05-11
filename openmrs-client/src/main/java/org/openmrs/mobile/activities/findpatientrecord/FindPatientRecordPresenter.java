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

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import org.openmrs.mobile.activities.BasePresenter;
import org.openmrs.mobile.data.DataService;
import org.openmrs.mobile.data.PagingInfo;
import org.openmrs.mobile.data.impl.PatientDataService;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.NetworkUtils;
import org.openmrs.mobile.utilities.ToastUtil;

import java.util.List;

public class FindPatientRecordPresenter extends BasePresenter implements FindPatientRecordContract.Presenter {

	private FindPatientRecordContract.View findPatientView;
	private int totalNumberResults;
	private int page = 1;
	private int limit = 10;
	private PatientDataService patientDataService;
	private String lastQuery = "";
	private boolean loading;

	public FindPatientRecordPresenter(FindPatientRecordContract.View view, String lastQuery) {
		this.findPatientView = view;
		this.findPatientView.setPresenter(this);
		this.lastQuery = lastQuery;
		this.patientDataService = new PatientDataService();
	}

	public FindPatientRecordPresenter(FindPatientRecordContract.View view) {
		this.findPatientView = view;
		this.findPatientView.setPresenter(this);
		this.patientDataService = new PatientDataService();
	}

	@Override
	public void subscribe() {
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {

	}

	public void findPatient(String query) {
		findPatientView.setProgressBarVisibility(true);
		findPatientView.setFetchedPatientsVisibility(0);
		if (NetworkUtils.hasNetwork()) {
			DataService.GetCallback<List<Patient>> getMultipleCallback = new DataService.GetCallback<List<Patient>>() {
				@Override
				public void onCompleted(List<Patient> patients) {
					findPatientView.setProgressBarVisibility(false);
					if (patients.isEmpty()) {
						findPatientView.setNumberOfPatientsView(0);
						findPatientView.setSearchPatientVisibility(false);
						findPatientView.setNoPatientsVisibility(true);
						findPatientView.setFetchedPatientsVisibility(0);
						/*findPatientView.showToast(ApplicationConstants.toastMessages.findPatientInfo, ToastUtil.ToastType
								.NOTICE);*/
						//findPatientView.showRegistration();
					} else {
						findPatientView.setNoPatientsVisibility(false);
						findPatientView.setSearchPatientVisibility(false);
						findPatientView.setNumberOfPatientsView(patients.size());
						findPatientView.setFetchedPatientsVisibility(patients.size());
						findPatientView.fetchPatients(patients);
						findPatientView.showToast(ApplicationConstants.entityName.PATIENTS + ApplicationConstants
								.toastMessages.fetchSuccessMessage, ToastUtil.ToastType.SUCCESS);
					}
				}

				@Override
				public void onError(Throwable t) {
					findPatientView.setProgressBarVisibility(false);
					Log.e("Patient Error", "Error", t.fillInStackTrace());
					findPatientView
							.showToast(ApplicationConstants.entityName.PATIENTS + ApplicationConstants.toastMessages
									.fetchErrorMessage, ToastUtil.ToastType.ERROR);
				}
			};
			patientDataService.getByNameAndIdentifier(query, null, null, getMultipleCallback);
		} else {
			// get the users from the local storage.
		}
	}

	public void getLastViewed() {
		findPatientView.setProgressBarVisibility(true);
		findPatientView.setFetchedPatientsVisibility(0);
		if (NetworkUtils.hasNetwork()) {
			PagingInfo pagingInfo = new PagingInfo(page, limit);
			DataService.GetCallback<List<Patient>> getMultipleCallback = new DataService.GetCallback<List<Patient>>() {
				@Override
				public void onCompleted(List<Patient> patients) {
					findPatientView.setProgressBarVisibility(false);
					if (!patients.isEmpty()) {
						findPatientView.setNumberOfPatientsView(0);
						findPatientView.setFetchedPatientsVisibility(patients.size());
						findPatientView.fetchPatients(patients);
						findPatientView.showToast(ApplicationConstants.entityName.LAST_VIEWED_PATIENT + ApplicationConstants
								.toastMessages.fetchSuccessMessage, ToastUtil.ToastType.SUCCESS);
					} else {
						findPatientView.setNumberOfPatientsView(0);
						findPatientView.setFetchedPatientsVisibility(0);
						/*findPatientView
								.showToast(ApplicationConstants.toastMessages.lastviewedPatientInfo, ToastUtil.ToastType
										.NOTICE);*/
					}
				}

				@Override
				public void onError(Throwable t) {
					findPatientView.setProgressBarVisibility(false);
					Log.e("User Error", "Error", t.fillInStackTrace());
					findPatientView
							.showToast(ApplicationConstants.entityName.LAST_VIEWED_PATIENT + ApplicationConstants.toastMessages
									.fetchErrorMessage, ToastUtil.ToastType.ERROR);
				}
			};
			patientDataService.getLastViewed(ApplicationConstants.EMPTY_STRING, null, pagingInfo, getMultipleCallback);
		}
	}

	private int computePage(boolean next) {
		int tmpPage = getPage();
		// check if pagination is required.
		if (page < Math.round(getTotalNumberResults() / limit)) {
			if (next) {
				// set next page
				tmpPage += 1;
			} else {
				// set previous page.
				tmpPage -= 1;
			}
		} else {
			tmpPage = -1;
		}

		return tmpPage;
	}

	@Override
	public boolean isLoading() {
		return loading;
	}

	@Override
	public void setLoading(boolean loading) {
		this.loading = loading;
	}

	private int getTotalNumberResults() {
		return totalNumberResults;
	}

	@Override
	public void setTotalNumberResults(int totalNumberResults) {
		this.totalNumberResults = totalNumberResults;
	}

	@Override
	public void loadResults(String patientListUuid, boolean loadNextResults) {

	}

	@Override
	public void refresh() {
	}

	@Override
	public int getPage() {
		return page;
	}

	@Override
	public void setPage(int page) {

	}
}
