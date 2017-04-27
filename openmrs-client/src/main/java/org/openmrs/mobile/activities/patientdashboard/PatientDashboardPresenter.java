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
import org.openmrs.mobile.data.impl.PatientDataService;
import org.openmrs.mobile.data.impl.VisitDataService;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.Visit;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class PatientDashboardPresenter extends BasePresenter implements PatientDashboardContract.Presenter {


    private PatientDashboardContract.View patientDashboardView;
    private PatientDataService patientDataService;
    private VisitDataService visitDataService;

    public PatientDashboardPresenter(PatientDashboardContract.View view, OpenMRS openMRS) {
        this.patientDashboardView = view;
        this.patientDashboardView.setPresenter(this);
        this.patientDataService = new PatientDataService();
        this.visitDataService = new VisitDataService();
    }

    @Override
    public void subscribe() {

    }


    @Override
    public void fetchPatientData(String patientId) {


        patientDataService.getByUUID(patientId, new DataService.GetSingleCallback<Patient>() {
            @Override
            public void onCompleted(Patient patient) {
                if (patient != null) {
                    patientDashboardView.updateUI(patient);
                    fetchVisits(patient);
                }
            }

            @Override
            public void onError(Throwable t) {
                System.out.println("===========================");
                System.out.println("Error: ");
                System.out.println(t.getLocalizedMessage());
                System.out.println("===========================");
            }


        });


    }

    @Override
    public void saveVisit(Visit visit) {
        System.out.println("===========================");
        System.out.println("Save start");
        System.out.println("===========================");
        DataService.GetSingleCallback<Visit> callback = new DataService.GetSingleCallback<Visit>() {
            @Override
            public void onCompleted(Visit entity) {
                System.out.println("===========================");
                System.out.println("Visit Saved: " + entity);
                System.out.println("===========================");
            }

            @Override
            public void onError(Throwable t) {
                System.out.println("===========================");
                System.out.println("Error: ");
                System.out.println(t.getLocalizedMessage());
                System.out.println("===========================");
            }
        };
        visitDataService.update(visit, callback);
    }

    private void fetchVisits(Patient patient) {


        PagingInfo pagingInfo = new PagingInfo(0, 20);
        DataService.GetMultipleCallback<Visit> callback = new DataService.GetMultipleCallback<Visit>() {
            @Override
            public void onCompleted(List<Visit> visits) {
                patientDashboardView.updateUI(visits);
            }

            @Override
            public void onError(Throwable t) {
                System.out.println("===========================");
                System.out.println("Error: ");
                System.out.println(t.getLocalizedMessage());
                System.out.println("===========================");
            }
        };
        visitDataService.getByPatient(patient, true, pagingInfo, callback);

    }
}
