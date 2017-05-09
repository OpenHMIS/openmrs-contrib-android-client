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

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseFragment;
import org.openmrs.mobile.activities.addeditpatient.AddEditPatientActivity;
import org.openmrs.mobile.activities.dialog.CustomFragmentDialog;
import org.openmrs.mobile.bundle.CustomDialogBundle;
import org.openmrs.mobile.models.Encounter;
import org.openmrs.mobile.models.EncounterType;
import org.openmrs.mobile.models.Observation;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.Person;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.ConsoleLogger;
import org.openmrs.mobile.utilities.DateUtils;
import org.openmrs.mobile.utilities.FontsUtil;
import org.openmrs.mobile.utilities.StringUtils;

import java.util.Calendar;
import java.util.List;

public class PatientDashboardFragment extends ACBaseFragment<PatientDashboardContract.Presenter> implements PatientDashboardContract.View {

    private View fragmentView;
    private TextView given_name;
    private TextView middle_name;
    private TextView family_name;
    private ImageView genderIcon;
    private TextView age;
    private TextView patient_id;
    private ImageView activeVisitIcon;
    private TextView more_label;
    private TextView visit_details;
    private View floatingActionMenu;
    private Visit mainVisit;
    private Patient patient;


    private LinearLayout visitNoteContainer;


    public static PatientDashboardFragment newInstance() {
        return new PatientDashboardFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_patient_dashboard, container, false);
        visitNoteContainer = (LinearLayout) fragmentView.findViewById(R.id.visit_note_container);

        floatingActionMenu = getActivity().findViewById(R.id.floatingActionMenu);
        floatingActionMenu.setVisibility(View.VISIBLE);

        initViewFields();

        String uuid = "6fd9b701-6abb-4e70-aa4a-c4b298972249";


        mPresenter.fetchPatientData(uuid);

        FontsUtil.setFont((ViewGroup) this.getActivity().findViewById(android.R.id.content));

        initializeListeners();

        return fragmentView;
    }

    private void initializeListeners() {
        more_label = (TextView) fragmentView.findViewById(R.id.more_label);
        visit_details = (TextView) fragmentView.findViewById(R.id.visit_details);
    }


    @Override
    public void showSnack(String text) {
        Snackbar.make(fragmentView, text, Snackbar.LENGTH_LONG).setAction(getString(R.string.action), null).show();
    }

    @Override
    public void updateContactCard(Patient patient) {
        if (patient != null) {
            this.patient = patient;
            Person person = patient.getPerson();
            given_name.setText(person.getName().getGivenName());
            middle_name.setText(person.getName().getMiddleName());
            family_name.setText(person.getName().getFamilyName());
            genderIcon.setImageResource(String.valueOf(person.getGender()).toLowerCase().equals("m") ? R.drawable.ic_male : R.drawable.ic_female);
            DateTime date = DateUtils.convertTimeString(person.getBirthdate());
            age.setText(calculateAge(date.getYear(), date.getMonthOfYear(), date.getDayOfMonth()));
            patient_id.setText(patient.getIdentifier().getIdentifier());

            mPresenter.fetchVisits(patient);
        }
    }

    @Override
    public void updateVisitsCard(List<Visit> visits) {

        if (visits.size() >= 1) {

            String string = "";
            String start_date_time = "";
            String stop_date_time = "";
            mainVisit = visits.get(0);
            stop_date_time = mainVisit.getStopDatetime();
            start_date_time = mainVisit.getStartDatetime();


            if (!StringUtils.notNull(stop_date_time)) {
                string += getString(R.string.active_visit_label) + " - " + DateUtils.convertTime1(start_date_time, DateUtils.PATIENT_DASHBOARD_DATE_FORMAT);
            } else {
                string += DateUtils.convertTime1(start_date_time, DateUtils.PATIENT_DASHBOARD_DATE_FORMAT) + " - " + DateUtils.convertTime1(stop_date_time, DateUtils.PATIENT_DASHBOARD_DATE_FORMAT);
            }
            visit_details.setText(string);

            if (mainVisit != null) {
                ConsoleLogger.dump("Inside  main visit UUID: " + mainVisit.getUuid());
                if (mainVisit.getEncounters().size() == 0) {
                    /*****
                     *
                     * Create new encounter
                     *
                     */
                } else {
                    for (Encounter encounter : mainVisit.getEncounters()) {
                        switch (encounter.getEncounterType().getDisplay()) {
                            case EncounterType.VISIT_NOTE:
                                ConsoleLogger.dump("Inside encounter UUID: " + encounter.getUuid());
                                mPresenter.fetchEncounterObservations(encounter);
                                break;
                        }
                    }

                }

            }

        }


    }

    @Override
    public void updateVisitNote(Observation observation) {

        ViewGroup.LayoutParams linearLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
        LinearLayout itemsContainer = new LinearLayout(getContext());
        itemsContainer.setLayoutParams(linearLayoutParams);
        itemsContainer.setOrientation(LinearLayout.HORIZONTAL);
        itemsContainer.setPadding(0, 0, 0, 0);

        ImageView editIcon = new ImageView(getContext());
        editIcon.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_menu_edit));
        editIcon.setPadding(0, 0, 0, 0);


        TextView editText = new TextView(getContext());
        editText.setPadding(10, 0, 10, 0);
        editText.setText(observation.getDiagnosisNote());
        editText.setGravity(Gravity.LEFT);

        itemsContainer.addView(editIcon);
        itemsContainer.addView(editText);


        visitNoteContainer = (LinearLayout) fragmentView.findViewById(R.id.visit_note_container);
        visitNoteContainer.addView(itemsContainer);

        CustomDialogBundle createEditVisitNote = new CustomDialogBundle();
        createEditVisitNote.setTitleViewMessage(getString(R.string.visit_note));
        createEditVisitNote.setRightButtonText(getString(R.string.label_save));
        createEditVisitNote.setRightButtonAction(CustomFragmentDialog.OnClickAction.SAVE_VISIT_NOTE);
        createEditVisitNote.setEditNoteTextViewMessage(observation.getDiagnosisNote());


        Bundle bundle = new Bundle();
        bundle.putSerializable(ApplicationConstants.Tags.OBSERVATION, observation);
        bundle.putSerializable(ApplicationConstants.Tags.PATIENT, patient);
        createEditVisitNote.setArguments(bundle);


        View.OnClickListener switchToEditMode = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((PatientDashboardActivity) getActivity()).createAndShowDialog(createEditVisitNote, ApplicationConstants.DialogTAG.VISIT_NOTE_TAG);
            }
        };
        editIcon.setOnClickListener(switchToEditMode);
        editText.setOnClickListener(switchToEditMode);


    }

    private void initViewFields() {
        given_name = (TextView) fragmentView.findViewById(R.id.given_name);
        middle_name = (TextView) fragmentView.findViewById(R.id.middle_name);
        family_name = (TextView) fragmentView.findViewById(R.id.family_name);
        genderIcon = (ImageView) fragmentView.findViewById(R.id.genderIcon);
        age = (TextView) fragmentView.findViewById(R.id.age);
        patient_id = (TextView) fragmentView.findViewById(R.id.patient_id);
        activeVisitIcon = (ImageView) fragmentView.findViewById(R.id.activeVisitIcon);
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

    public LinearLayout getVisitNoteContainer() {
        return visitNoteContainer;
    }


}
