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
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import org.joda.time.DateTime;
import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseFragment;
import org.openmrs.mobile.activities.addeditpatient.AddEditPatientActivity;
import org.openmrs.mobile.activities.addeditvisit.AddEditVisitActivity;
import org.openmrs.mobile.activities.auditdata.AuditDataActivity;
import org.openmrs.mobile.activities.dialog.CustomFragmentDialog;
import org.openmrs.mobile.activities.visitphoto.upload.UploadVisitPhotoActivity;
import org.openmrs.mobile.activities.visittasks.VisitTasksActivity;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.bundle.CustomDialogBundle;
import org.openmrs.mobile.models.Concept;
import org.openmrs.mobile.models.Encounter;
import org.openmrs.mobile.models.EncounterCreate;
import org.openmrs.mobile.models.Observation;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.Person;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.DateUtils;
import org.openmrs.mobile.utilities.FontsUtil;
import org.openmrs.mobile.utilities.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class PatientDashboardFragment extends ACBaseFragment<PatientDashboardContract.Presenter>
		implements PatientDashboardContract.View {

	private View fragmentView;
	private TextView patientDisplayName, patientGender, patientAge, patientIdentifier, visitDetails, observationTextView,
			patientDob;
	private Visit activeVisit;
	private LinearLayout observationsContainer;
	private CustomDialogBundle createEditVisitNoteDialog;
	View.OnClickListener switchToEditMode = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			((PatientDashboardActivity)getActivity())
					.createAndShowDialog(createEditVisitNoteDialog, ApplicationConstants.DialogTAG.VISIT_NOTE_TAG);
		}
	};
	private Bundle dialogBundle;
	private FloatingActionButton startAuditFormButton, addVisitImageButton,
			addVisitTaskButton, startVisitButton, editVisitButton, endVisitButton, editPatient;
	private Patient patient;
	private OpenMRS instance = OpenMRS.getInstance();
	private SharedPreferences sharedPreferences = instance.getOpenMRSSharedPreferences();
	private int visitsStartIndex = 0;
	private int visitsStartLimit = 10;

	public static PatientDashboardFragment newInstance() {
		return new PatientDashboardFragment();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		fragmentView = inflater.inflate(R.layout.fragment_patient_dashboard, container, false);
		observationsContainer = (LinearLayout)fragmentView.findViewById(R.id.observationsContainer);
		String patientId = getActivity().getIntent().getStringExtra(ApplicationConstants.BundleKeys.PATIENT_UUID_BUNDLE);
		initViewFields();
		initializeListeners(startAuditFormButton, addVisitImageButton, addVisitTaskButton, startVisitButton,
				editVisitButton, endVisitButton, editVisitButton, endVisitButton, editPatient);
		mPresenter.fetchPatientData(patientId);
		FontsUtil.setFont((ViewGroup)this.getActivity().findViewById(android.R.id.content));
		return fragmentView;
	}

	private void initializeListeners(FloatingActionButton... params) {
		for (FloatingActionButton patientActionButtons : params) {
			patientActionButtons.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					startSelectedPatientDashboardActivity(patientActionButtons.getId());
				}
			});
		}
	}

	private void startSelectedPatientDashboardActivity(int selectedId) {
		switch (selectedId) {
			case R.id.add_visit_image:
				Intent intent = new Intent(getContext(), UploadVisitPhotoActivity.class);
				intent.putExtra(ApplicationConstants.BundleKeys.PATIENT_UUID_BUNDLE, sharedPreferences.getString
						(ApplicationConstants.BundleKeys.PATIENT_UUID_BUNDLE, ApplicationConstants.EMPTY_STRING));
				intent.putExtra(ApplicationConstants.BundleKeys.VISIT_UUID_BUNDLE, sharedPreferences.getString
						(ApplicationConstants.BundleKeys.VISIT_UUID_BUNDLE, ApplicationConstants.EMPTY_STRING));
				intent.putExtra(ApplicationConstants.BundleKeys.PROVIDER_UUID_BUNDLE, sharedPreferences.getString
						(ApplicationConstants.BundleKeys.PROVIDER_UUID_BUNDLE, ApplicationConstants.EMPTY_STRING));
				startActivity(intent);
				break;
			case R.id.add_visit_task:
				intent = new Intent(getContext(), VisitTasksActivity.class);
				intent.putExtra(ApplicationConstants.BundleKeys.PATIENT_UUID_BUNDLE, sharedPreferences.getString
						(ApplicationConstants.BundleKeys.PATIENT_UUID_BUNDLE, ApplicationConstants.EMPTY_STRING));
				intent.putExtra(ApplicationConstants.BundleKeys.VISIT_UUID_BUNDLE, sharedPreferences.getString
						(ApplicationConstants.BundleKeys.VISIT_UUID_BUNDLE, ApplicationConstants.EMPTY_STRING));
				startActivity(intent);
				break;
			case R.id.audit_data_form:
				intent = new Intent(getContext(), AuditDataActivity.class);
				intent.putExtra(ApplicationConstants.BundleKeys.PATIENT_UUID_BUNDLE, patient.getUuid());
				startActivity(intent);
				break;
			case R.id.start_visit:
				intent = new Intent(getContext(), AddEditVisitActivity.class);
				intent.putExtra(ApplicationConstants.BundleKeys.PATIENT_UUID_BUNDLE, sharedPreferences.getString
						(ApplicationConstants.BundleKeys.PATIENT_UUID_BUNDLE, ApplicationConstants.EMPTY_STRING));
				startActivity(intent);
				break;
			case R.id.edit_visit:
				intent = new Intent(getContext(), AddEditVisitActivity.class);
				intent.putExtra(ApplicationConstants.BundleKeys.PATIENT_UUID_BUNDLE, sharedPreferences.getString
						(ApplicationConstants.BundleKeys.PATIENT_UUID_BUNDLE, ApplicationConstants.EMPTY_STRING));
				intent.putExtra(ApplicationConstants.BundleKeys.VISIT_UUID_BUNDLE, sharedPreferences.getString
						(ApplicationConstants.BundleKeys.VISIT_UUID_BUNDLE, ApplicationConstants.EMPTY_STRING));
				startActivity(intent);
				break;
			case R.id.end_visit:
				intent = new Intent(getContext(), AddEditVisitActivity.class);
				intent.putExtra(ApplicationConstants.BundleKeys.PATIENT_UUID_BUNDLE, sharedPreferences.getString
						(ApplicationConstants.BundleKeys.PATIENT_UUID_BUNDLE, ApplicationConstants.EMPTY_STRING));
				intent.putExtra(ApplicationConstants.BundleKeys.VISIT_UUID_BUNDLE, sharedPreferences.getString
						(ApplicationConstants.BundleKeys.VISIT_UUID_BUNDLE, ApplicationConstants.EMPTY_STRING));
				intent.putExtra(ApplicationConstants.BundleKeys.END_VISIT_TAG, true);
				startActivity(intent);
				break;
			case R.id.edit_Patient:
				intent = new Intent(getContext(), AddEditPatientActivity.class);
				intent.putExtra(ApplicationConstants.BundleKeys.PATIENT_UUID_BUNDLE, instance.getPatientUuid());
				startActivity(intent);

				break;
		}
	}

	private void initViewFields() {
		patientDisplayName = (TextView)fragmentView.findViewById(R.id.fetchedPatientDisplayName);
		patientIdentifier = (TextView)fragmentView.findViewById(R.id.fetchedPatientIdentifier);
		patientGender = (TextView)fragmentView.findViewById(R.id.fetchedPatientGender);
		patientAge = (TextView)fragmentView.findViewById(R.id.fetchedPatientAge);
		visitDetails = (TextView)fragmentView.findViewById(R.id.visitDetails);
		patientDob = (TextView)fragmentView.findViewById(R.id.fetchedPatientBirthDate);
		addVisitImageButton = (FloatingActionButton)getActivity().findViewById(R.id.add_visit_image);
		addVisitTaskButton = (FloatingActionButton)getActivity().findViewById(R.id.add_visit_task);
		startAuditFormButton = (FloatingActionButton)getActivity().findViewById(R.id.audit_data_form);
		startVisitButton = (FloatingActionButton)getActivity().findViewById(R.id.start_visit);
		editVisitButton = (FloatingActionButton)getActivity().findViewById(R.id.edit_visit);
		endVisitButton = (FloatingActionButton)getActivity().findViewById(R.id.end_visit);
		editPatient = (FloatingActionButton)getActivity().findViewById(R.id.edit_Patient);
		FloatingActionMenu floatingActionMenu = (FloatingActionMenu)getActivity().findViewById(R.id.floatingActionMenu);
		floatingActionMenu.setVisibility(View.VISIBLE);
		startAuditFormButton = (FloatingActionButton)getActivity().findViewById(R.id.audit_data_form);
		createEditVisitNoteDialog = new CustomDialogBundle();
		createEditVisitNoteDialog.setTitleViewMessage(getString(R.string.visit_note));
		createEditVisitNoteDialog.setRightButtonText(getString(R.string.label_save));
		dialogBundle = new Bundle();
	}

	@Override
	public void showSnack(String text) {
		Snackbar.make(fragmentView, text, Snackbar.LENGTH_LONG).setAction(getString(R.string.action), null).show();
	}

	@Override
	public void updateContactCard(Patient patient) {
		this.patient = patient;
		Person person = patient.getPerson();
		patientDisplayName.setText(person.getName().getNameString());
		patientGender.setText(person.getGender());
		patientIdentifier.setText(patient.getIdentifier().getIdentifier());
		DateTime date = DateUtils.convertTimeString(person.getBirthdate());
		patientAge.setText(DateUtils.calculateAge(date.getYear(), date.getMonthOfYear(), date.getDayOfMonth()));
		patientDob.setText(DateUtils.convertTime1(person.getBirthdate(), DateUtils.PATIENT_DASHBOARD_DOB_DATE_FORMAT));
		mPresenter.setStartIndex(visitsStartIndex);
		mPresenter.setLimit(visitsStartLimit);
		mPresenter.fetchVisits(patient);
		setPatientUuid(patient);
	}

	@Override
	public void updateActiveVisitCard(List<Visit> visits) {
		for (Visit visit : visits) {
			if (!StringUtils.notNull(visit.getStopDatetime())) {
				this.activeVisit = visit;
				setVisitUuid(visit);
				visits.remove(visit);
				break;
			}
		}
		if (activeVisit != null) {
			startVisitButton.setVisibility(View.GONE);
			editVisitButton.setVisibility(View.VISIBLE);
			endVisitButton.setVisibility(View.VISIBLE);
			//fragmentView.findViewById(R.id.visitDetailsCardView).setVisibility(View.VISIBLE);

			fragmentView.findViewById(R.id.observationsCardView).setVisibility(View.VISIBLE);
			visitDetails.setText(getString(R.string.active_visit_label) + ": " + DateUtils
					.convertTime1(activeVisit.getStartDatetime(), DateUtils.PATIENT_DASHBOARD_VISIT_DATE_FORMAT));
			if (activeVisit.getEncounters().size() == 0) {
				//We add a view to create a visit note
				View row = LayoutInflater.from(getContext()).inflate(R.layout.visits_obervation_row, null);
				TextView visitNote = (TextView)row.findViewById(R.id.text);
				ImageView visitNoteIcon = (ImageView)row.findViewById(R.id.icon);
				visitNote.setHint(getResources().getString(R.string.add_a_note));

				//visitNote.setOnClickListener(switchToEditMode);
				//visitNoteIcon.setOnClickListener(switchToEditMode);

				Observation observation = new Observation();
				observation.setConcept(new Concept());
				observation.setPerson(patient.getPerson());
				observation.setObsDatetime(DateUtils.now(DateUtils.OPEN_MRS_REQUEST_FORMAT));

				ArrayList<Observation> observations = new ArrayList<>();
				observations.add(observation);
				EncounterCreate encounterCreate = new EncounterCreate();
				encounterCreate.setPatient(patient.getUuid());
				encounterCreate.setEncounterType(ApplicationConstants.EncounterTypeEntitys.VISIT_NOTE);
				encounterCreate.setObs(observations);

				//ConsoleLogger.dumpToJson(encounterCreate);

				observationsContainer.addView(row);
				createEditVisitNoteDialog.setEditNoteTextViewMessage("");
				createEditVisitNoteDialog.setRightButtonAction(CustomFragmentDialog.OnClickAction.CREATE_VISIT_NOTE);
				createEditVisitNoteDialog.setArguments(dialogBundle);

			} else {
				for (Encounter encounter : activeVisit.getEncounters()) {
					switch (encounter.getEncounterType().getDisplay()) {
						case ApplicationConstants.EncounterTypeEntitys.VISIT_NOTE:
							mPresenter.fetchEncounterObservations(encounter);
							break;
					}
				}
			}
		}

		RecyclerView pastVisits = (RecyclerView)fragmentView.findViewById(R.id.pastVisits);
		pastVisits.setLayoutManager(new LinearLayoutManager(getContext()));
		PastVisitsRecyclerAdapter
				pastVisitsRecyclerAdapter = new PastVisitsRecyclerAdapter(pastVisits, visits, getActivity());
		pastVisits.setAdapter(pastVisitsRecyclerAdapter);

		pastVisitsRecyclerAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
			@Override
			public void onLoadMore() {
				//pastVisitsRecyclerAdapter.notifyItemRemoved();
				/**
				 * Load more here
				 */
				//ConsoleLogger.dump("Loading more");
				//pastVisitsRecyclerAdapter.notifyDataSetChanged();
				//pastVisitsRecyclerAdapter.setLoaded();
			}
		});
	}

	@Override
	public void updateActiveVisitObservationsCard(Observation observation) {
		Observation newObs = new Observation();
		newObs.setUuid(observation.getUuid());
		newObs.setConcept(observation.getConcept());
		newObs.setPerson(patient.getPerson());

		View row = LayoutInflater.from(getContext()).inflate(R.layout.visits_obervation_row, null);
		TextView visitNote = (TextView)row.findViewById(R.id.text);
		ImageView visitNoteIcon = (ImageView)row.findViewById(R.id.icon);
		visitNote.setHint(getResources().getString(R.string.add_a_note));
		visitNote.setText(observation.getDiagnosisNote());
		visitNote.setMovementMethod(new ScrollingMovementMethod());

		visitNoteIcon.setOnClickListener(switchToEditMode);
		//visitNote.setOnClickListener(switchToEditMode);
		observationsContainer.addView(row);

		dialogBundle.putSerializable(ApplicationConstants.BundleKeys.OBSERVATION, newObs);
		createEditVisitNoteDialog.setRightButtonAction(CustomFragmentDialog.OnClickAction.SAVE_VISIT_NOTE);
		createEditVisitNoteDialog.setEditNoteTextViewMessage(observation.getDiagnosisNote());
		createEditVisitNoteDialog.setArguments(dialogBundle);
	}

	@Override
	public LinearLayout getVisitNoteContainer() {
		return observationsContainer;
	}

	@Override
	public Patient getPatient() {
		return patient;
	}

	public void setPatientUuid(Patient patient) {
		SharedPreferences.Editor editor = instance.getOpenMRSSharedPreferences().edit();
		editor.putString(ApplicationConstants.BundleKeys.PATIENT_UUID_BUNDLE, patient.getPerson().getUuid());
		editor.commit();
	}

	public void setVisitUuid(Visit visit) {
		SharedPreferences.Editor editor = instance.getOpenMRSSharedPreferences().edit();
		editor.putString(ApplicationConstants.BundleKeys.VISIT_UUID_BUNDLE, visit.getUuid());
		editor.commit();
	}

	@Override
	public void setProviderUuid(String providerUuid) {
		if (StringUtils.isBlank(providerUuid))
			return;
		SharedPreferences.Editor editor = instance.getOpenMRSSharedPreferences().edit();
		editor.putString(ApplicationConstants.BundleKeys.PROVIDER_UUID_BUNDLE, providerUuid);
		editor.commit();
	}

}