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
import org.openmrs.mobile.dao.LocationDAO;
import org.openmrs.mobile.data.DataService;
import org.openmrs.mobile.data.PagingInfo;
import org.openmrs.mobile.data.impl.ObservationDataService;
import org.openmrs.mobile.data.impl.PatientDataService;
import org.openmrs.mobile.data.impl.VisitDataService;
import org.openmrs.mobile.models.Encounter;
import org.openmrs.mobile.models.Encountercreate;
import org.openmrs.mobile.models.Observation;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.ConsoleLogger;
import org.openmrs.mobile.utilities.DateUtils;

import java.util.List;
import java.util.Map;

public class PatientDashboardPresenter extends BasePresenter implements PatientDashboardContract.Presenter {


    private LocationDAO locationDAO;
    private PatientDashboardContract.View patientDashboardView;
    private PatientDataService patientDataService;
    private VisitDataService visitDataService;
    private OpenMRS openMRS;
    private ObservationDataService observationDataService;

    public PatientDashboardPresenter(PatientDashboardContract.View view, OpenMRS openMRS) {
        this.patientDashboardView = view;
        this.openMRS = openMRS;
        this.patientDashboardView.setPresenter(this);
        this.patientDataService = new PatientDataService();
        this.visitDataService = new VisitDataService();
        this.locationDAO = new LocationDAO();
        this.observationDataService = new ObservationDataService();
    }

    @Override
    public void subscribe() {

    }


    @Override
    public void fetchPatientData(String uuid) {
        ConsoleLogger.dump("Fetching patient data " + uuid);
        patientDataService.getByUUID(uuid, new DataService.GetSingleCallback<Patient>() {
            @Override
            public void onCompleted(Patient patient) {
                if (patient != null) {
                    patientDashboardView.updateContactCard(patient);
                }
            }

            @Override
            public void onError(Throwable t) {
                ConsoleLogger.dump("error occured");
                t.printStackTrace();
                patientDashboardView.showSnack("Error occured: Unable to reach searver");
            }
        });
    }

    @Override
    public void fetchVisits(Patient patient) {
        visitDataService.getByPatient(patient, true, new PagingInfo(0, 20), new DataService.GetMultipleCallback<Visit>() {
            @Override
            public void onCompleted(List<Visit> visits) {
                patientDashboardView.updateVisitsCard(visits);
            }

            @Override
            public void onError(Throwable t) {
                ConsoleLogger.dump("error occured");
                t.printStackTrace();
                patientDashboardView.showSnack("Error occured: Unable to reach searver");
            }
        });
    }

    @Override
    public void saveVisit(Visit visit) {
        visitDataService.update(visit, new DataService.GetSingleCallback<Visit>() {
            @Override
            public void onCompleted(Visit entity) {

            }

            @Override
            public void onError(Throwable t) {
                ConsoleLogger.dump("error occured");
                t.printStackTrace();
                patientDashboardView.showSnack("Error occured: Unable to reach searver");
            }
        });
    }

    @Override
    public void fetchPatientObservations(Patient patient) {
        observationDataService.getByPatient(patient, true, new PagingInfo(0, 20), new DataService.GetMultipleCallback<Observation>() {
            @Override
            public void onCompleted(List<Observation> observations) {

            }

            @Override
            public void onError(Throwable t) {
                ConsoleLogger.dump("error occured");
                t.printStackTrace();
                patientDashboardView.showSnack("Error occured: Unable to reach searver");
            }
        });

    }

    @Override
    public void fetchEncounterObservations(Encounter encounter) {

        //observationDataService.getByPatient();
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

    @Override
    public void saveObservation(Observation observation) {
        Observation obs = new Observation();
        obs.setConcept(observation.getConcept());
        obs.setPerson(observation.getPerson());
        obs.setObsDatetime(DateUtils.now(DateUtils.OPEN_MRS_REQUEST_FORMAT));
        obs.setValue(observation.getValue());
        ConsoleLogger.dump("OBS ID: " + observation.getUuid());
        ConsoleLogger.dumpToJson(obs);

        /*observationDataService.update(observation, new DataService.GetSingleCallback<Observation>() {
            @Override
            public void onCompleted(Observation entity) {
                ConsoleLogger.dump("Observation save completed");
                ConsoleLogger.dumpToJson(entity);
            }

            @Override
            public void onError(Throwable t) {
                ConsoleLogger.dump("error occured");
                t.printStackTrace();
                patientDashboardView.showSnack("Error occured: Unable to reach searver");
            }
        });*/

        /*observationDataService.update(observation.getUuid(), new DataService.GetSingleCallback<Observation>() {
            @Override
            public void onCompleted(Observation entity) {
                ConsoleLogger.dump("Observation save completed");
                ConsoleLogger.dumpToJson(entity);
            }

            @Override
            public void onError(Throwable t) {
                ConsoleLogger.dump("error occured");
                t.printStackTrace();
                patientDashboardView.showSnack("Error occured: Unable to reach searver");
            }
        });*/
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
