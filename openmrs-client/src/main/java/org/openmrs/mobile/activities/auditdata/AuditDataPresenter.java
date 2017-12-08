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

import org.openmrs.mobile.activities.BasePresenter;
import org.openmrs.mobile.data.DataService;
import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.data.impl.ConceptDataService;
import org.openmrs.mobile.data.impl.EncounterDataService;
import org.openmrs.mobile.data.impl.VisitDataService;
import org.openmrs.mobile.models.Concept;
import org.openmrs.mobile.models.Encounter;
import org.openmrs.mobile.models.Observation;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.utilities.ApplicationConstants;

import java.util.List;

public class AuditDataPresenter extends BasePresenter implements AuditDataContract.Presenter {

	private AuditDataContract.View auditDataView;
	private VisitDataService visitDataService;
	private ConceptDataService conceptDataService;
	private String visitUuid;

	private EncounterDataService encounterDataService;

	private Encounter currentEncounter = null;

	public AuditDataPresenter(AuditDataContract.View view, String visitUuid) {
		this.auditDataView = view;
		this.auditDataView.setPresenter(this);

		this.visitDataService = dataAccess().visit();
		this.encounterDataService = dataAccess().encounter();
		this.conceptDataService = dataAccess().concept();
		this.visitUuid = visitUuid;
	}

	@Override
	public void subscribe() {
	}

	@Override
	public void fetchInpatientTypeServices() {
		DataService.GetCallback<Concept> conceptGetCallback = new DataService.GetCallback<Concept>() {
			@Override
			public void onCompleted(Concept concept) {
				if (concept != null) {
					if (!concept.getAnswers().isEmpty()) {
						auditDataView.setInpatientTypeServices(concept.getAnswers());
						fetchVisit(visitUuid);
					}
				}
			}

			@Override
			public void onError(Throwable t) {
				t.printStackTrace();
			}
		};
		conceptDataService.getByUuid(ApplicationConstants.AuditFormConcepts.CONCEPT_INPATIENT_SERVICE_TYPE,
				QueryOptions.FULL_REP, conceptGetCallback);
	}

	@Override
	public void fetchVisit(String visitUuid) {
		auditDataView.showPageSpinner(true);
		DataService.GetCallback<Visit> fetchEncountersCallback = new DataService.GetCallback<Visit>() {
			@Override
			public void onCompleted(Visit visit) {
				auditDataView.setVisit(visit);
				for (Encounter encounter : visit.getEncounters()) {
					if (encounter.getVoided() != null && encounter.getVoided()) {
						continue;
					}

					if (encounter.getEncounterType().getUuid()
							.equalsIgnoreCase(ApplicationConstants.EncounterTypeEntity.AUDIT_DATA_UUID)) {
						fetchEncounter(encounter.getUuid());
					}
				}
				auditDataView.showPageSpinner(false);
			}

			@Override
			public void onError(Throwable t) {
				auditDataView.showPageSpinner(false);
				t.printStackTrace();
			}
		};
		visitDataService.getByUuid(visitUuid, QueryOptions.FULL_REP, fetchEncountersCallback);
	}

	private void fetchEncounter(String uuid) {
		auditDataView.showPageSpinner(true);
		DataService.GetCallback<Encounter> fetchEncountercallback = new DataService.GetCallback<Encounter>() {
			@Override
			public void onCompleted(Encounter encounter) {
				currentEncounter = encounter;
				auditDataView.showPageSpinner(false);
				auditDataView.setEncounterUuid(encounter.getUuid());
				auditDataView.updateFormFields(encounter);
			}

			@Override
			public void onError(Throwable t) {
				auditDataView.showPageSpinner(false);
				t.printStackTrace();
			}
		};
		encounterDataService.getByUuid(uuid, QueryOptions.FULL_REP, fetchEncountercallback);
	}

	@Override
	public void saveUpdateEncounter(Encounter encounter, boolean isNewEncounter) {
		auditDataView.showProgressBar(true);
		DataService.GetCallback<Encounter> serverResponceCallback = new DataService.GetCallback<Encounter>() {
			@Override
			public void onCompleted(Encounter encounter) {
				auditDataView.showProgressBar(true);
				if (encounter == null) {
					auditDataView.showProgressBar(false);
					auditDataView.hideSoftKeys();
				} else {
					auditDataView.hideSoftKeys();
					((AuditDataActivity)auditDataView.getContext()).finish();
				}
			}

			@Override
			public void onError(Throwable t) {
				auditDataView.showProgressBar(false);
				t.printStackTrace();
			}
		};

		if (isNewEncounter) {
			encounterDataService.create(encounter, serverResponceCallback);
		} else {
			encounterDataService.update(encounter, serverResponceCallback);
		}
	}

	@Override
	public boolean isObservationExistingForCurrentEncounter(Observation observation) {
		if (currentEncounter == null) {
			return false;
		}
		String obsUuid = observation.getUuid();
		for (Observation oldObservation : currentEncounter.getObs()) {
			if (oldObservation.getUuid().equalsIgnoreCase(obsUuid)) {
				return true;
			}
		}
		return false;
	}
}

