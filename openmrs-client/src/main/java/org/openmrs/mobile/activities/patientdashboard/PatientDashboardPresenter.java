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

import org.greenrobot.eventbus.EventBus;
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
import org.openmrs.mobile.event.DataRefreshEvent;
import org.openmrs.mobile.models.Location;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.User;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.NetworkUtils;
import org.openmrs.mobile.utilities.StringUtils;
import org.openmrs.mobile.utilities.ToastUtil;

import java.util.LinkedList;
import java.util.List;

public class PatientDashboardPresenter extends BasePresenter implements PatientDashboardContract.Presenter {

	private NetworkUtils networkUtils;

	private final int INITIAL_PAGING_INDEX = PagingInfo.DEFAULT.getPage();

	private PatientDashboardContract.View patientDashboardView;
	private PatientDataService patientDataService;
	private VisitDataService visitDataService;
	private LocationDataService locationDataService;
	private UserDataService userDataService;
	private int currentPagingIndex = INITIAL_PAGING_INDEX;
	private Patient patient;
	private boolean loading;
	private OpenMRS openMRS;
	private EventBus eventBus;

	private String patientUuid;
	private boolean isPatientSynced = false;

	public PatientDashboardPresenter(PatientDashboardContract.View view, OpenMRS openMRS, String patientUuid) {
		this.patientDashboardView = view;
		this.patientDashboardView.setPresenter(this);
		this.openMRS = openMRS;
		this.patientUuid = patientUuid;

		this.patientDataService = dataAccess().patient();
		this.visitDataService = dataAccess().visit();
		this.userDataService = dataAccess().user();
		this.locationDataService = dataAccess().location();
		this.networkUtils = openMRS.getNetworkUtils();

		this.eventBus = openMRS.getEventBus();

		isPatientSynced = patientDataService.isPatientSynced(patientUuid);
	}

	@Override
	public void subscribe() {
		getCurrentProvider();
		getCurrentLocation();
	}

	@Override
	public void fetchPatientData() {
		fetchPatientData(false);
	}

	private void fetchPatientData(boolean forceRefresh) {
		if (!forceRefresh) {
			patientDashboardView.showPageSpinner(true);
		}

		QueryOptions options = QueryOptions.FULL_REP;
		if (forceRefresh) {
			options = QueryOptions.REMOTE_FULL_REP;
		}

		patientDataService.getByUuid(patientUuid, options, new DataService.GetCallback<Patient>() {
			@Override
			public void onCompleted(Patient patient) {
				if (patient == null && !networkUtils.isConnectedOrConnecting()) {
					patientDashboardView.alertOfflineAndPatientNotFound();
					patientDashboardView.navigateBack();
					return;
				}
				setPatient(patient);
				fetchVisits(patient, INITIAL_PAGING_INDEX, forceRefresh);
			}

			@Override
			public void onError(Throwable t) {
				if (t instanceof DataOperationException && !openMRS.getNetworkUtils().isConnectedOrConnecting()) {
					patientDashboardView.showNoPatientData(true);
				} else {
					patientDashboardView.showPageSpinner(false);
				}
				t.printStackTrace();
			}
		});
	}

	private void fetchVisits(Patient patient, int pagingIndex, boolean forceRefresh) {
		setLoading(true);
		PagingInfo pagingInfo = new PagingInfo(pagingIndex, ApplicationConstants.Request.PATIENT_VISIT_COUNT);
		DataService.GetCallback<List<Visit>> fetchVisitsCallback = new DataService.GetCallback<List<Visit>>() {
			@Override
			public void onCompleted(List<Visit> visits) {
				setLoading(false);
				patientDashboardView.patientContacts(patient);
				LinkedList<Visit> sortedVisits = sortVisits(visits);
				if (pagingIndex == INITIAL_PAGING_INDEX) {
					patientDashboardView.setPatientVisits(sortedVisits);
					patientDashboardView.displayRefreshingData(false);
					eventBus.post(new DataRefreshEvent(ApplicationConstants.EventMessages.DataRefresh.DATA_RETRIEVED));
				} else {
					patientDashboardView.addPatientVisits(sortedVisits);
				}
				if (visits.isEmpty() || visits.size() < ApplicationConstants.Request.PATIENT_VISIT_COUNT) {
					patientDashboardView.notifyAllPatientVisitsFetched();
				}

				patientDashboardView.showPageSpinner(false);
			}

			@Override
			public void onError(Throwable t) {
				t.printStackTrace();
				// If we're online and we're not on the first page of results, assume it's because we have all results
				if (openMRS.getNetworkUtils().isConnectedOrConnecting() && pagingIndex > INITIAL_PAGING_INDEX) {
					patientDashboardView.notifyAllPatientVisitsFetched();
				}
				patientDashboardView.showPageSpinner(false);
				setLoading(false);
			}
		};
		QueryOptions.Builder builder = new QueryOptions.Builder()
				.includeInactive(true)
				.customRepresentation(RestConstants.Representations.VISIT);
		if (forceRefresh) {
			builder = builder.requestStrategy(RequestStrategy.REMOTE_THEN_LOCAL);
		}
		QueryOptions options = builder.build();
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
							if (entity != null) {
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
	public void loadResults() {
		// Only the first five results are loaded for any patient (even in sync), so subsequent loads need new data
		fetchVisits(patient, ++currentPagingIndex, true);
	}

	@Override
	public void dataRefreshWasRequested() {
		if (openMRS.getNetworkUtils().isConnectedOrConnecting()) {
			eventBus.post(new DataRefreshEvent(ApplicationConstants.EventMessages.DataRefresh.REFRESH));
			currentPagingIndex = INITIAL_PAGING_INDEX;
			fetchPatientData(true);
		} else {
			patientDashboardView.showToast(ApplicationConstants.toastMessages.notConnected, ToastUtil.ToastType.NOTICE);
			patientDashboardView.displayRefreshingData(false);
		}
	}

	private LinkedList<Visit> sortVisits(List<Visit> visits) {
		LinkedList<Visit> sortVisits = new LinkedList<>();
		for (Visit visit : visits) {
			if (visit.getStopDatetime() == null) {
				sortVisits.add(0, visit);
			} else {
				sortVisits.add(visit);
			}
		}

		return sortVisits;
	}
}