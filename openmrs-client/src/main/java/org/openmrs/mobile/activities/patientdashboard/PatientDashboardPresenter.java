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
import org.openmrs.mobile.api.RestApi;
import org.openmrs.mobile.api.RestServiceBuilder;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.dao.LocationDAO;
import org.openmrs.mobile.data.DataService;
import org.openmrs.mobile.data.PagingInfo;
import org.openmrs.mobile.data.impl.ObservationService;
import org.openmrs.mobile.data.impl.PatientDataService;
import org.openmrs.mobile.data.impl.VisitDataService;
import org.openmrs.mobile.models.Encounter;
import org.openmrs.mobile.models.EncounterType;
import org.openmrs.mobile.models.Encountercreate;
import org.openmrs.mobile.models.Location;
import org.openmrs.mobile.models.Observation;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.Results;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.utilities.ConsoleLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PatientDashboardPresenter extends BasePresenter implements PatientDashboardContract.Presenter {


    private LocationDAO locationDAO;
    private RestApi restApi;
    private PatientDashboardContract.View patientDashboardView;
    private PatientDataService patientDataService;
    private VisitDataService visitDataService;
    protected OpenMRS openMRS;

    public PatientDashboardPresenter(PatientDashboardContract.View view, OpenMRS openMRS) {
        this.patientDashboardView = view;
        this.openMRS = openMRS;
        this.patientDashboardView.setPresenter(this);
        this.patientDataService = new PatientDataService();
        this.visitDataService = new VisitDataService();
        this.locationDAO = new LocationDAO();
        this.restApi = RestServiceBuilder.createService(RestApi.class);
    }

    @Override
    public void subscribe() {

    }


    @Override
    public void fetchPatientData(String uuid) {
        patientDataService.getByUUID(uuid, new DataService.GetSingleCallback<Patient>() {
            @Override
            public void onCompleted(Patient patient) {
                patientDashboardView.updateUI(patient);
            }

            @Override
            public void onError(Throwable t) {

            }
        });
    }

    @Override
    public void fetchVisits(Patient patient) {

        visitDataService.getByPatient(patient, true, new PagingInfo(0, 20), new DataService.GetMultipleCallback<Visit>() {
            @Override
            public void onCompleted(List<Visit> visits) {
                ObservationService observationService = new ObservationService();
                observationService.getByPatient(patient, true, new PagingInfo(0, 20), new DataService.GetMultipleCallback<Observation>() {
                    @Override
                    public void onCompleted(List<Observation> entities) {

                    }

                    @Override
                    public void onError(Throwable t) {

                    }
                });

            }

            @Override
            public void onError(Throwable t) {

            }
        });
    }

    @Override
    public void saveVisit(Visit visit) {

    }

    @Override
    public Map<String, String> getCurrentLoggedInUserInfo() {
        return openMRS.getCurrentLoggedInUserInfo();
    }

    @Override
    public LocationDAO getLocationDAO() {
        return this.locationDAO;
    }

    @Override
    public void createEncounter(Encountercreate encounter) {

    }
}
