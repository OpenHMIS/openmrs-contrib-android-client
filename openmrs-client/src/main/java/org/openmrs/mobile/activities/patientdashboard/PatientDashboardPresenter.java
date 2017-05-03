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
import org.openmrs.mobile.data.impl.EncounterDataService;
import org.openmrs.mobile.data.impl.ObservationDataService;
import org.openmrs.mobile.data.impl.PatientDataService;
import org.openmrs.mobile.data.impl.VisitDataService;
import org.openmrs.mobile.data.rest.EncounterRestService;
import org.openmrs.mobile.models.Encounter;
import org.openmrs.mobile.models.Encountercreate;
import org.openmrs.mobile.models.Observation;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.utilities.ConsoleLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PatientDashboardPresenter extends BasePresenter implements PatientDashboardContract.Presenter {


    private LocationDAO locationDAO;
    private PatientDashboardContract.View patientDashboardView;
    private PatientDataService patientDataService;
    private VisitDataService visitDataService;
    protected OpenMRS openMRS;
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

        //ConsoleLogger.dump("Succeeded");

        EncounterDataService encounterDataService = new EncounterDataService();

        encounterDataService.getByUUID(encounter.getUuid(), new DataService.GetSingleCallback<Encounter>() {
            @Override
            public void onCompleted(Encounter entity) {
                ConsoleLogger.dump("Succeeded");

                ConsoleLogger.dump("Attempting to save encounter: " + entity.getUuid());

                encounter.setDisplay("Note Visit 05/03/2017");

                entity.setObservations(new ArrayList<Observation>());

                encounterDataService.update(entity, new DataService.GetSingleCallback<Encounter>() {
                    @Override
                    public void onCompleted(Encounter entity) {
                        ConsoleLogger.dump("Succeeded");
                        ConsoleLogger.dump("Saved encounter: " + entity.getUuid());
                        ConsoleLogger.dump("Saved encounter: " + entity.getLinks().get(0).getUri());
                    }

                    @Override
                    public void onError(Throwable t) {
                        ConsoleLogger.dump("Failed");
                        t.printStackTrace();
                    }
                });
            }

            @Override
            public void onError(Throwable t) {
                ConsoleLogger.dump("Failed");
                t.printStackTrace();
            }
        });

        /*encounterDataService.update(encounter, new DataService.GetSingleCallback<Encounter>() {
            @Override
            public void onCompleted(Encounter entity) {
                ConsoleLogger.dump("Succeeded");
                ConsoleLogger.dump(entity);
            }

            @Override
            public void onError(Throwable t) {
                ConsoleLogger.dump("Failed");
                t.printStackTrace();
            }
        });*/

        /*observationDataService.getByEncounter(encounter, true, new PagingInfo(0, 20), new DataService.GetMultipleCallback<Observation>() {
            @Override
            public void onCompleted(List<Observation> observations) {

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

    @Override
    public void saveObservation(Observation observation) {
        observation.setVoided(true);
        ConsoleLogger.dump(observation.getVoided());

        observationDataService.update(observation, new DataService.GetSingleCallback<Observation>() {
            @Override
            public void onCompleted(Observation entity) {
                ConsoleLogger.dump("Observation save completed");
                ConsoleLogger.dump(entity);
            }

            @Override
            public void onError(Throwable t) {
                ConsoleLogger.dump("error occured");
                t.printStackTrace();
                patientDashboardView.showSnack("Error occured: Unable to reach searver");
            }
        });
    }
}
