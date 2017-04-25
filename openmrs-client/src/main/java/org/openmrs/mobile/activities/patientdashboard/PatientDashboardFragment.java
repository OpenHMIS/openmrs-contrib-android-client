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

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseFragment;
import org.openmrs.mobile.models.Encounter;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.Person;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.utilities.DateUtils;
import org.openmrs.mobile.utilities.FontsUtil;
import org.openmrs.mobile.utilities.StringUtils;

import java.util.Calendar;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class PatientDashboardFragment extends ACBaseFragment<PatientDashboardContract.Presenter> implements PatientDashboardContract.View {

    private View rootView;
    private TextView given_name;
    private TextView middle_name;
    private TextView family_name;
    private ImageView genderIcon;
    private TextView age;
    private TextView patient_id;
    private ImageView activeVisitIcon;
    private TextView more_label;
    private TextView visit_details;

    public static PatientDashboardFragment newInstance() {
        return new PatientDashboardFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_patient_dashboard, container, false);
        initViewFields();
        mPresenter.fetchPatientData("34ae9d4b-8afb-4da1-b7d2-8e459429aabe");
        FontsUtil.setFont((ViewGroup) this.getActivity().findViewById(android.R.id.content));

        initializeListeners();

        return rootView;
    }

    private void initializeListeners() {
        rootView.findViewById(R.id.loadVisitNoteEditor)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), VisitNoteActivity.class);
                        startActivity(intent);
                    }
                });
        more_label = (TextView) rootView.findViewById(R.id.more_label);
        visit_details = (TextView) rootView.findViewById(R.id.visit_details);
    }


    @Override
    public void showSnack(String text) {
        Snackbar.make(rootView, text, Snackbar.LENGTH_LONG).setAction(getString(R.string.action), null).show();
    }

    @Override
    public void updateUI(Patient patient) {
        Person person = patient.getPerson();
        given_name.setText(person.getName().getGivenName());
        middle_name.setText(person.getName().getMiddleName());
        family_name.setText(person.getName().getFamilyName());
        genderIcon.setImageResource(String.valueOf(person.getGender()).toLowerCase().equals("m") ? R.drawable.ic_male : R.drawable.ic_female);
        DateTime date = DateUtils.convertTimeString(person.getBirthdate());
        age.setText(calculateAge(date.getYear(), date.getMonthOfYear(), date.getDayOfMonth()));
        patient_id.setText(patient.getIdentifier().getIdentifier());
    }

    @Override
    public void updateUI(List<Visit> visits) {

        if (visits.size() >= 1) {

            String string = "";
            String start_date_time = "";
            String stop_date_time = "";
            Visit visit = visits.get(0);
            stop_date_time = visit.getStopDatetime();
            start_date_time = visit.getStartDatetime();


            if (!StringUtils.notNull(stop_date_time)) {
                string += getString(R.string.active_visit_label) + " - " + DateUtils.convertTime1(start_date_time, DateUtils.PATIENT_DASHBOARD_DATE_FORMAT);
            } else {
                string += DateUtils.convertTime1(start_date_time, DateUtils.PATIENT_DASHBOARD_DATE_FORMAT) + " - " + DateUtils.convertTime1(stop_date_time, DateUtils.PATIENT_DASHBOARD_DATE_FORMAT);
            }
            visit_details.setText(string);

            if (visit.getEncounters().size() > 0) {
                List<Encounter> encounters = visit.getEncounters();

                System.out.println("===========================");
                System.out.println(visit.getEncounters().size());
                System.out.println("===========================");
            }
        }

        for (int index = 1; index < visits.size(); index++) {
            Visit visit = visits.get(index);

        }
    }

    private void initViewFields() {
        given_name = (TextView) rootView.findViewById(R.id.given_name);
        middle_name = (TextView) rootView.findViewById(R.id.middle_name);
        family_name = (TextView) rootView.findViewById(R.id.family_name);
        genderIcon = (ImageView) rootView.findViewById(R.id.genderIcon);
        age = (TextView) rootView.findViewById(R.id.age);
        patient_id = (TextView) rootView.findViewById(R.id.patient_id);
        activeVisitIcon = (ImageView) rootView.findViewById(R.id.activeVisitIcon);
    }

    private String calculateAge(int year, int month, int day) {
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();
        dob.set(year, month, day);
        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }
        Integer ageInt = new Integer(age);
        String ageS = ageInt.toString();
        return ageS;
    }


}
