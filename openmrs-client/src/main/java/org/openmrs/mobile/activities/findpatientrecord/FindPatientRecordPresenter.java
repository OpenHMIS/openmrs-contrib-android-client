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
import android.support.annotation.NonNull;

import org.openmrs.mobile.activities.BasePresenter;
import org.openmrs.mobile.data.DataService;
import org.openmrs.mobile.data.PagingInfo;
import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.data.RequestStrategy;
import org.openmrs.mobile.data.impl.PatientDataService;
import org.openmrs.mobile.data.rest.RestConstants;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.utilities.ApplicationConstants;

import java.util.List;

public class FindPatientRecordPresenter extends BasePresenter implements FindPatientRecordContract.Presenter {

	@NonNull
	private FindPatientRecordContract.View findPatientView;
	private int page = PagingInfo.DEFAULT.getPage();
	private PatientDataService patientDataService;
	private boolean loading;

	public FindPatientRecordPresenter(@NonNull FindPatientRecordContract.View view, String lastQuery) {
		this.findPatientView = view;
		this.findPatientView.setPresenter(this);

		this.patientDataService = dataAccess().patient();
	}

	public FindPatientRecordPresenter(@NonNull FindPatientRecordContract.View view) {
		this.findPatientView = view;
		this.findPatientView.setPresenter(this);

		this.patientDataService = dataAccess().patient();
	}

	@Override
	public void subscribe() {
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {

	}

	public void findPatient(String query) {
		findPatientView.setProgressBarVisibility(true);
		PagingInfo pagingInfo = PagingInfo.ALL;
		DataService.GetCallback<List<Patient>> getMultipleCallback = new DataService.GetCallback<List<Patient>>() {
			@Override
			public void onCompleted(List<Patient> patients) {
				findPatientView.setProgressBarVisibility(false);
				if (patients.isEmpty()) {
					findPatientView.setNumberOfPatientsView(0);
					findPatientView.setNoPatientsVisibility(true);
				} else {
					findPatientView.setNoPatientsVisibility(false);
					findPatientView.setNumberOfPatientsView(patients.size());
					findPatientView.fetchPatients(patients);
				}
			}

			@Override
			public void onError(Throwable t) {
				findPatientView.setProgressBarVisibility(false);
			}
		};

		QueryOptions options = new QueryOptions.Builder()
				.includeInactive(true)
				.customRepresentation(RestConstants.Representations.FULL)
				.requestStrategy(RequestStrategy.REMOTE_THEN_LOCAL)
				.build();

		patientDataService.getByNameOrIdentifier(query, options, pagingInfo, getMultipleCallback);
	}

	public void getLastViewed() {
		findPatientView.setProgressBarVisibility(true);
		setLoading(true);
		PagingInfo pagingInfo = PagingInfo.DEFAULT;
		patientDataService.getLastViewed(ApplicationConstants.EMPTY_STRING, QueryOptions.FULL_REP, pagingInfo,
				new DataService.GetCallback<List<Patient>>() {
					@Override
					public void onCompleted(List<Patient> patients) {
						findPatientView.setProgressBarVisibility(false);

						if (!patients.isEmpty()) {
							findPatientView.setNumberOfPatientsView(0);
							findPatientView.fetchPatients(patients);
						} else {
							findPatientView.setNumberOfPatientsView(patients.size());
						}
						setLoading(false);
					}

					@Override
					public void onError(Throwable t) {
						setLoading(false);
						findPatientView.setProgressBarVisibility(false);
					}
				});
	}

	@Override
	public boolean isLoading() {
		return loading;
	}

	@Override
	public void setLoading(boolean loading) {
		this.loading = loading;
	}

	@Override
	public void loadResults(boolean loadNextResults) {
		getLastViewed();
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
