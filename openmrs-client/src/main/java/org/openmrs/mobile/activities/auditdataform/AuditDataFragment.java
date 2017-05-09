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

package org.openmrs.mobile.activities.auditdataform;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseFragment;
import org.openmrs.mobile.activities.dialog.CustomFragmentDialog;
import org.openmrs.mobile.bundle.CustomDialogBundle;
import org.openmrs.mobile.models.Encounter;
import org.openmrs.mobile.models.EncounterType;
import org.openmrs.mobile.models.Observation;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.Person;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.DateUtils;
import org.openmrs.mobile.utilities.FontsUtil;
import org.openmrs.mobile.utilities.StringUtils;

import java.util.Calendar;
import java.util.List;

public class AuditDataFragment extends ACBaseFragment<AuditDataContract.Presenter> implements AuditDataContract.View {

    private View fragmentView;
    private TextView patientDisplayName;
    private TextView patientGender;
    private TextView patientAge;
    private TextView patientIdentifier;
    private TextView visitDetails;
    private View floatingActionMenu;
    private Visit mainVisit;
    private Patient patient;


    private LinearLayout visitNoteContainer;


    public static AuditDataFragment newInstance() {
        return new AuditDataFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_audit_data_form, container, false);
        /*visitNoteContainer = (LinearLayout) fragmentView.findViewById(R.id.visit_note_container);
        floatingActionMenu = getActivity().findViewById(R.id.floatingActionMenu);
        floatingActionMenu.setVisibility(View.VISIBLE);
        String patientId = getActivity().getIntent().getStringExtra(ApplicationConstants.BundleKeys.PATIENT_ID_BUNDLE);
        initViewFields();
        initializeListeners();
        mPresenter.fetchPatientData(patientId);
        FontsUtil.setFont((ViewGroup) this.getActivity().findViewById(android.R.id.content));*/
        return fragmentView;
    }

    private void initializeListeners() {
        TextView moreLabel = (TextView) fragmentView.findViewById(R.id.more_label);
        visitDetails = (TextView) fragmentView.findViewById(R.id.visit_details);
    }

    private void initViewFields() {
        patientDisplayName = (TextView) fragmentView.findViewById(R.id.fetchedPatientDisplayName);
        patientIdentifier = (TextView) fragmentView.findViewById(R.id.fetchedPatientIdentifier);
        patientGender = (TextView) fragmentView.findViewById(R.id.fetchedPatientGender);
        patientAge = (TextView) fragmentView.findViewById(R.id.fetchedPatientAge);
    }



    public void updateContactCard(Patient patient) {
        /*if (patient != null) {
            this.patient = patient;
            Person person = patient.getPerson();
            patientDisplayName.setText(person.getName().getNameString());
            patientGender.setText(person.getGender());
            patientIdentifier.setText(patient.getIdentifier().getIdentifier());
            DateTime date = DateUtils.convertTimeString(person.getBirthdate());
            patientAge.setText(calculateAge(date.getYear(), date.getMonthOfYear(), date.getDayOfMonth()));
            mPresenter.fetchVisits(patient);
        }*/
    }


}
