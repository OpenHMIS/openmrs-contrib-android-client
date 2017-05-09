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
        fragmentView = inflater.inflate(R.layout.fragment_patient_dashboard, container, false);
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


    @Override
    public void showSnack(String text) {
        Snackbar.make(fragmentView, text, Snackbar.LENGTH_LONG).setAction(getString(R.string.action), null).show();
    }

    @Override
    public void updateContactCard(Patient patient) {
        if (patient != null) {
            this.patient = patient;
            Person person = patient.getPerson();
            patientDisplayName.setText(person.getName().getNameString());
            patientGender.setText(person.getGender());
            patientIdentifier.setText(patient.getIdentifier().getIdentifier());
            DateTime date = DateUtils.convertTimeString(person.getBirthdate());
            patientAge.setText(calculateAge(date.getYear(), date.getMonthOfYear(), date.getDayOfMonth()));
            mPresenter.fetchVisits(patient);
        }
    }

    @Override
    public void updateVisitsCard(List<Visit> visits) {

        if (visits.size() >= 1) {
            String visitdetailsText = "";
            String visitStartDateTime;
            String visitStopDateTime;
            mainVisit = visits.get(0);
            visitStopDateTime = mainVisit.getStopDatetime();
            visitStartDateTime = mainVisit.getStartDatetime();
            if (!StringUtils.notNull(visitStopDateTime)) {
                visitdetailsText += getString(R.string.active_visit_label) + " - " + DateUtils.convertTime1(visitStartDateTime, DateUtils.PATIENT_DASHBOARD_DATE_FORMAT);
            } else {
                visitdetailsText += DateUtils.convertTime1(visitStartDateTime, DateUtils.PATIENT_DASHBOARD_DATE_FORMAT) + " - " + DateUtils.convertTime1(visitStopDateTime, DateUtils.PATIENT_DASHBOARD_DATE_FORMAT);
            }

            visitDetails.setText(visitdetailsText);

            if (mainVisit != null) {
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
                                mPresenter.fetchEncounterObservations(encounter);
                                break;
                        }
                    }

                }

            }

            LinearLayout previousVisitsContainer = (LinearLayout) fragmentView.findViewById(R.id.previous_visits_container);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.gravity = Gravity.CENTER;
            Context context = getContext();
            for (int counter = 1; counter < visits.size(); counter++) {
                Visit visit = visits.get(counter);
                TextView pastVisitTextView = new TextView(context);
                pastVisitTextView.setText(DateUtils.convertTime1(visit.getStartDatetime(), DateUtils.PATIENT_DASHBOARD_DATE_FORMAT) + " - " + DateUtils.convertTime1(visit.getStopDatetime(), DateUtils.PATIENT_DASHBOARD_DATE_FORMAT));
                previousVisitsContainer.addView(pastVisitTextView);
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
        bundle.putSerializable(ApplicationConstants.BundleKeys.OBSERVATION, observation);
        bundle.putSerializable(ApplicationConstants.BundleKeys.PATIENT, patient);
        createEditVisitNote.setArguments(bundle);
        View.OnClickListener switchToEditMode = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((AuditDataActivity) getActivity()).createAndShowDialog(createEditVisitNote, ApplicationConstants.DialogTAG.VISIT_NOTE_TAG);
            }
        };
        editIcon.setOnClickListener(switchToEditMode);
        editText.setOnClickListener(switchToEditMode);
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
