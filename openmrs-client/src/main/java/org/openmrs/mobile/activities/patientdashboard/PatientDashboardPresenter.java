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
import org.openmrs.mobile.data.impl.ObservationDataService;
import org.openmrs.mobile.data.impl.PatientDataService;
import org.openmrs.mobile.data.impl.VisitDataService;
import org.openmrs.mobile.models.Encounter;
import org.openmrs.mobile.models.Observation;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.utilities.ApplicationConstants;

import java.util.List;

public class PatientDashboardPresenter extends BasePresenter implements PatientDashboardContract.Presenter {

    private PatientDashboardContract.View patientDashboardView;
    private PatientDataService patientDataService;
    private VisitDataService visitDataService;
    private ObservationDataService observationDataService;

    public PatientDashboardPresenter(PatientDashboardContract.View view) {
        this.patientDashboardView = view;
        this.patientDashboardView.setPresenter(this);
        this.patientDataService = new PatientDataService();
        this.visitDataService = new VisitDataService();
        this.observationDataService = new ObservationDataService();
    }

    @Override
    public void subscribe() {

    }


    @Override
    public void fetchPatientData(String uuid) {
        patientDataService.getByUUID(uuid, new DataService.GetSingleCallback<Patient>() {
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
        visitDataService.getByPatient(patient, true, new PagingInfo(0, 20), new DataService.GetMultipleCallback<Visit>() {
            @Override
            public void onCompleted(List<Visit> visits) {
                patientDashboardView.updateVisitsCard(visits);
            }

            @Override
            public void onError(Throwable t) {
                t.printStackTrace();
                patientDashboardView.showSnack("Error occured: Unable to reach searver");
            }
        });
    }

    @Override
    public void fetchEncounterObservations(Encounter encounter) {
        observationDataService.getByEncounter(encounter, true, new PagingInfo(0, 20), new DataService.GetMultipleCallback<Observation>() {
            @Override
            public void onCompleted(List<Observation> observations) {
                for (Observation observation : observations) {
                    if (observation.getDiagnosisNote() != null && !observation.getDiagnosisNote().equals(ApplicationConstants.EMPTY_STRING)) {
                        patientDashboardView.updateVisitNote(observation);
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

}
