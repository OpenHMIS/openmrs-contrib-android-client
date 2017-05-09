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

import android.widget.LinearLayout;

import org.openmrs.mobile.activities.BasePresenterContract;
import org.openmrs.mobile.activities.BaseView;
import org.openmrs.mobile.dao.LocationDAO;
import org.openmrs.mobile.models.Encounter;
import org.openmrs.mobile.models.Encountercreate;
import org.openmrs.mobile.models.Observation;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.utilities.ToastUtil;

import java.util.List;
import java.util.Map;

public interface PatientDashboardContract {

    interface View extends BaseView<Presenter> {
        void showSnack(String s);

        void updateContactCard(Patient patient);

        void updateVisitsCard(List<Visit> visits);

        void updateVisitNote(Observation observation);

        LinearLayout getVisitNoteContainer();
    }

    interface Presenter extends BasePresenterContract {

        Map<String, String> getCurrentLoggedInUserInfo();

        LocationDAO getLocationDAO();

        void fetchPatientData(final String patientId);

        void fetchVisits(Patient patient);

        void saveVisit(Visit visit);

        void fetchPatientObservations(Patient patient);

        void fetchEncounterObservations(Encounter encounter);

        void createEncounter(Encountercreate encounter);

        void saveObservation(Observation observation);
    }

}