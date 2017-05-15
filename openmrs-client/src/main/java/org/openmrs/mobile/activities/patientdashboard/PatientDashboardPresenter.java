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
import org.openmrs.mobile.data.impl.ObsDataService;
import org.openmrs.mobile.data.impl.PatientDataService;
import org.openmrs.mobile.data.impl.ProviderDataService;
import org.openmrs.mobile.data.impl.VisitDataService;
import org.openmrs.mobile.models.Encounter;
import org.openmrs.mobile.models.Observation;
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
	private ObsDataService observationDataService;
	private ProviderDataService providerDataService;
	private final static int page = 1;
	private int startIndex = 1;
	private int limit = 5;


	public PatientDashboardPresenter(PatientDashboardContract.View view) {
		this.patientDashboardView = view;
		this.patientDashboardView.setPresenter(this);
		this.patientDataService = new PatientDataService();
		this.visitDataService = new VisitDataService();
		this.observationDataService = new ObsDataService();
		this.providerDataService = new ProviderDataService();
	}

	@Override
	public void subscribe() {
		getCurrentProvider();
	}

	public void setStartIndex(int startIndex) {
		this.startIndex = startIndex;
	}

	@Override
	public void setLimit(int limit) {
		this.limit = limit;
	}

	@Override
	public void fetchPatientData(String uuid) {
		patientDataService.getByUUID(uuid, QueryOptions.LOAD_RELATED_OBJECTS, new DataService.GetCallback<Patient>() {
			@Override
			public void onCompleted(Patient patient) {
				if (patient != null) {
					patientDashboardView.updateContactCard(patient);
				}
			}

			@Override
			public void onError(Throwable t) {
				t.printStackTrace();
				patientDashboardView.showSnack("Error occured: Unable to reach searver");
			}
		});
	}

	@Override
	public void fetchVisits(Patient patient) {
		patientDashboardView.getVisitNoteContainer().removeAllViews();
		visitDataService.getByPatient(patient, new QueryOptions(true, true), new PagingInfo(startIndex, limit),
				new DataService.GetCallback<List<Visit>>() {
					@Override
					public void onCompleted(List<Visit> visits) {
						if (!visits.isEmpty()) {
							patientDashboardView.updateActiveVisitCard(visits);
						} else {
							patientDashboardView.showSnack("No visits found");
						}
					}

					@Override
					public void onError(Throwable t) {
						t.printStackTrace();
						patientDashboardView.showSnack("Visit fetch error");
					}
				});
	}

	@Override
	public void fetchEncounterObservations(Encounter encounter) {
		observationDataService.getByEncounter(encounter, QueryOptions.INCLUDE_INACTIVE, new PagingInfo(startIndex, limit),
				new DataService.GetCallback<List<Observation>>() {
					@Override
					public void onCompleted(List<Observation> observations) {
						for (Observation observation : observations) {
							if (observation.getDiagnosisNote() != null && !observation.getDiagnosisNote()
									.equals(ApplicationConstants.EMPTY_STRING)) {
								patientDashboardView.updateActiveVisitObservationsCard(observation);
							}
						}
					}

					@Override
					public void onError(Throwable t) {
						patientDashboardView.showSnack("Error fetching observations");
						t.printStackTrace();
					}
				});
	}

	@Override
	public Patient getPatient() {
		return patientDashboardView.getPatient();
	}

	/**
	 * TODO: create a service to getProviderByPerson, move code to commons
	 */
	private void getCurrentProvider() {

		String personUuid = OpenMRS.getInstance().getCurrentLoggedInUserInfo().get(ApplicationConstants.UserKeys.USER_UUID);
		if (StringUtils.notEmpty(personUuid)) {
			providerDataService.getAll(QueryOptions.LOAD_RELATED_OBJECTS, new PagingInfo(page, 100),
					new DataService.GetCallback<List<Provider>>() {
						@Override
						public void onCompleted(List<Provider> entities) {
							for (Provider entity : entities) {
								if (null != entity.getPerson() && personUuid
										.equalsIgnoreCase(entity.getPerson().getUuid())) {
									patientDashboardView.setProviderUuid(entity.getUuid());
								}
							}
						}

						@Override
						public void onError(Throwable t) {
							ToastUtil.error(t.getMessage());
						}
					});
		}
	}

}