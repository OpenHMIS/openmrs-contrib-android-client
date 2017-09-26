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

import org.openmrs.mobile.activities.BasePresenter;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.data.DataOperationException;
import org.openmrs.mobile.data.DataService;
import org.openmrs.mobile.data.PagingInfo;
import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.data.RequestStrategy;
import org.openmrs.mobile.data.impl.LocationDataService;
import org.openmrs.mobile.data.impl.PatientDataService;
import org.openmrs.mobile.data.impl.UserDataService;
import org.openmrs.mobile.data.impl.VisitDataService;
import org.openmrs.mobile.data.rest.RestConstants;
import org.openmrs.mobile.models.Location;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.User;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.StringUtils;
import org.openmrs.mobile.utilities.ToastUtil;

import java.util.List;

public class PatientDashboardPresenter extends BasePresenter implements PatientDashboardContract.Presenter {

	private PatientDashboardContract.View patientDashboardView;
	private PatientDataService patientDataService;
	private VisitDataService visitDataService;
	private LocationDataService locationDataService;
	private UserDataService userDataService;
	private int startIndex = 0;
	private int totalNumberResults;
	private Patient patient;
	private boolean loading;
	private OpenMRS openMRS;

	public PatientDashboardPresenter(PatientDashboardContract.View view, OpenMRS openMRS) {
		this.patientDashboardView = view;
		this.patientDashboardView.setPresenter(this);
		this.openMRS = openMRS;
		this.patientDataService = dataAccess().patient();
		this.visitDataService = dataAccess().visit();
		this.userDataService = dataAccess().user();
		this.locationDataService = dataAccess().location();
	}

	@Override
	public void subscribe() {
		getCurrentProvider();
		getCurrentLocation();
	}

	@Override
	public void fetchPatientData(String uuid) {
		patientDashboardView.showPageSpinner(true);
		patientDataService.getByUuid(uuid, QueryOptions.FULL_REP, new DataService.GetCallback<Patient>() {
			@Override
			public void onCompleted(Patient patient) {
				setPatient(patient);
				fetchVisits(patient, startIndex);
			}

			@Override
			public void onError(Throwable t) {
				if (t instanceof DataOperationException && !openMRS.getNetworkUtils().hasNetwork()) {
					patientDashboardView.showNoPatientData(true);
				} else {
					patientDashboardView.showPageSpinner(false);
				}
				t.printStackTrace();
			}
		});
	}

	@Override
	public void fetchVisits(Patient patient, int startIndex) {
		if (startIndex < 0) {
			return;
		}
		setLoading(true);
		totalNumberResults = 0;
		patientDashboardView.showPageSpinner(true);
		setLoading(true);
		PagingInfo pagingInfo = new PagingInfo(startIndex, ApplicationConstants.Request.PATIENT_VISIT_COUNT);
		DataService.GetCallback<List<Visit>> fetchVisitsCallback = new DataService.GetCallback<List<Visit>>() {
			@Override
			public void onCompleted(List<Visit> visits) {
				setLoading(false);
				patientDashboardView.patientContacts(patient);
				patientDashboardView.patientVisits(visits);

				if (!visits.isEmpty() && pagingInfo.getTotalRecordCount() != null) {
					totalNumberResults = pagingInfo.getTotalRecordCount();
				}
				patientDashboardView.showPageSpinner(false);
			}

			@Override
			public void onError(Throwable t) {
				t.printStackTrace();
				patientDashboardView.showPageSpinner(false);
				setLoading(false);
			}
		};
		QueryOptions options = new QueryOptions.Builder()
				.includeInactive(true)
				.customRepresentation(RestConstants.Representations.FULL)
				.requestStrategy(RequestStrategy.REMOTE_THEN_LOCAL)
				.build();
		visitDataService.getByPatient(patient, options, pagingInfo, fetchVisitsCallback);
	}

	@Override
	public Patient getPatient() {
		return patientDashboardView.getPatient();
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	/**
	 * TODO: create a service to getProviderByPerson, move code to commons
	 */
	private void getCurrentProvider() {
		String personUuid = openMRS.getCurrentUserUuid();
		if (StringUtils.isNullOrEmpty(personUuid)) {
			userDataService.getByUuid(openMRS.getLoginUserUuid(), QueryOptions.FULL_REP,
					new DataService.GetCallback<User>() {
						@Override
						public void onCompleted(User entity) {
							if(entity != null) {
								openMRS.setCurrentUserUuid(entity.getPerson().getUuid());
								patientDashboardView.setProviderUuid(entity.getPerson().getUuid());
							}
						}

						@Override
						public void onError(Throwable t) {
							ToastUtil.error(t.getMessage());
						}
					});
		} else {
			patientDashboardView.setProviderUuid(openMRS.getCurrentUserUuid());
		}
	}

	public void getCurrentLocation() {
		//We start by fetching by location, required for creating encounters
		String locationUuid = OpenMRS.getInstance().getLocation();
		if (StringUtils.notEmpty(locationUuid)) {
			DataService.GetCallback<Location> locationDataServiceCallback = new DataService.GetCallback<Location>() {
				@Override
				public void onCompleted(Location location) {
					//set location in the fragment and start loading other fields
					patientDashboardView.setLocation(location);
				}

				@Override
				public void onError(Throwable t) {
					t.printStackTrace();
				}
			};

			locationDataService.getByUuid(locationUuid, QueryOptions.FULL_REP, locationDataServiceCallback);
		}

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
	public void loadResults(Patient patient, boolean loadNextResults) {
		fetchVisits(patient, computePage(loadNextResults));
	}

	private int computePage(boolean next) {
		int tmpPage = startIndex;
		// check if pagination is required.
		if (startIndex < (Math.round(totalNumberResults / ApplicationConstants.Request.PATIENT_VISIT_COUNT))) {
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
}