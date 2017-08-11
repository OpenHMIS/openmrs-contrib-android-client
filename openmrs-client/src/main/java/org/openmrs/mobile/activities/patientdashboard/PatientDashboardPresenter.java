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
import org.openmrs.mobile.data.DataService;
import org.openmrs.mobile.data.PagingInfo;
import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.data.impl.LocationDataService;
import org.openmrs.mobile.data.impl.PatientDataService;
import org.openmrs.mobile.data.impl.ProviderDataService;
import org.openmrs.mobile.data.impl.VisitDataService;
import org.openmrs.mobile.models.Location;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.Provider;
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
	private ProviderDataService providerDataService;
	private int startIndex = 0;
	private int totalNumberResults;
	private int limit = 10;
	private int page;
	private Patient patient;
	private boolean loading;

	public PatientDashboardPresenter(PatientDashboardContract.View view) {
		this.patientDashboardView = view;
		this.patientDashboardView.setPresenter(this);

		this.patientDataService = dataAccess().patient();
		this.visitDataService = dataAccess().visit();
		this.providerDataService = dataAccess().provider();
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
				fetchVisits(patient, getStartIndex());
			}

			@Override
			public void onError(Throwable t) {
				patientDashboardView.showPageSpinner(false);
				t.printStackTrace();
			}
		});
	}

	@Override
	public void fetchVisits(Patient patient, int startIndex) {
		if (startIndex < 0) {
			return;
		}
		setStartIndex(startIndex);
		setLoading(true);
		setTotalNumberResults(0);
		patientDashboardView.showPageSpinner(true);
		setLoading(true);
		PagingInfo pagingInfo = new PagingInfo(startIndex, limit);
		DataService.GetCallback<List<Visit>> fetchVisitsCallback = new DataService.GetCallback<List<Visit>>() {
			@Override
			public void onCompleted(List<Visit> visits) {
				setLoading(false);
				patientDashboardView.patientContacts(patient);
				patientDashboardView.patientVisits(visits);

				if (!visits.isEmpty()) {
					///patientDashboardView.showNoVisits(true);
					setTotalNumberResults(pagingInfo.getTotalRecordCount());
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
		visitDataService.getByPatient(patient, QueryOptions.INCLUDE_ALL_FULL_REP, pagingInfo, fetchVisitsCallback);
	}

	@Override
	public Patient getPatient() {
		return patientDashboardView.getPatient();
	}

	/**
	 * TODO: create a service to getProviderByPerson, move code to commons
	 */
	private void getCurrentProvider() {
		patientDashboardView.showPageSpinner(true);
		String personUuid = OpenMRS.getInstance().getCurrentLoggedInUserInfo().get(ApplicationConstants.UserKeys.USER_UUID);
		if (StringUtils.notEmpty(personUuid)) {
			providerDataService.getAll(QueryOptions.FULL_REP, null,
					new DataService.GetCallback<List<Provider>>() {
						@Override
						public void onCompleted(List<Provider> entities) {
							for (Provider entity : entities) {
								if (null != entity.getPerson() && personUuid
										.equalsIgnoreCase(entity.getPerson().getUuid())) {
									patientDashboardView.setProviderUuid(entity.getUuid());
								}
							}
							patientDashboardView.showPageSpinner(false);
						}

						@Override
						public void onError(Throwable t) {
							patientDashboardView.showPageSpinner(false);
							ToastUtil.error(t.getMessage());
						}
					});
		}
	}

	public void getCurrentLocation() {
		//We start by fetching by location, required for creating encounters
		String locationUuid = OpenMRS.getInstance().getLocation();
		if (StringUtils.notEmpty(locationUuid)) {
			patientDashboardView.showPageSpinner(true);
			DataService.GetCallback<Location> locationDataServiceCallback = new DataService.GetCallback<Location>() {
				@Override
				public void onCompleted(Location location) {
					patientDashboardView.showPageSpinner(false);
					//set location in the fragment and start loading other fields
					patientDashboardView.setLocation(location);
				}

				@Override
				public void onError(Throwable t) {
					patientDashboardView.showPageSpinner(false);
					t.printStackTrace();
				}
			};

			locationDataService.getByUuid(locationUuid, QueryOptions.FULL_REP, locationDataServiceCallback);
		}

	}

	public void setPatient(Patient patient) {
		this.patient = patient;
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
	public void setLimit(int limit) {
		this.limit = limit;
	}

	@Override
	public int getLimit() {
		return limit;
	}

	@Override
	public void setStartIndex(int startIndex) {
		this.startIndex = startIndex;
	}

	@Override
	public int getStartIndex() {
		return startIndex;
	}

	@Override
	public void loadResults(Patient patient, boolean loadNextResults) {
		fetchVisits(patient, computePage(loadNextResults));
	}

	private int getTotalNumberResults() {
		return totalNumberResults;
	}

	@Override
	public void setTotalNumberResults(int totalNumberResults) {
		this.totalNumberResults = totalNumberResults;
	}

	private int computePage(boolean next) {
		int tmpPage = getStartIndex();
		// check if pagination is required.
		if (startIndex < (Math.round(getTotalNumberResults() / limit))) {
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