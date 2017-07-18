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

package org.openmrs.mobile.activities.auditdata;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;

import org.joda.time.LocalDateTime;
import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseActivity;
import org.openmrs.mobile.activities.ACBaseFragment;
import org.openmrs.mobile.activities.visit.VisitActivity;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.models.Concept;
import org.openmrs.mobile.models.ConceptAnswer;
import org.openmrs.mobile.models.Encounter;
import org.openmrs.mobile.models.EncounterType;
import org.openmrs.mobile.models.Form;
import org.openmrs.mobile.models.Location;
import org.openmrs.mobile.models.Observation;
import org.openmrs.mobile.models.Person;
import org.openmrs.mobile.models.Provider;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.FontsUtil;
import org.openmrs.mobile.utilities.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static org.openmrs.mobile.utilities.ApplicationConstants.AuditFormAnswers.ANSWER_NA;
import static org.openmrs.mobile.utilities.ApplicationConstants.AuditFormAnswers.ANSWER_NEGATIVE;
import static org.openmrs.mobile.utilities.ApplicationConstants.AuditFormAnswers.ANSWER_NO;
import static org.openmrs.mobile.utilities.ApplicationConstants.AuditFormAnswers.ANSWER_PLANNED;
import static org.openmrs.mobile.utilities.ApplicationConstants.AuditFormAnswers.ANSWER_POSITIVE;
import static org.openmrs.mobile.utilities.ApplicationConstants.AuditFormAnswers.ANSWER_UNKNOWN;
import static org.openmrs.mobile.utilities.ApplicationConstants.AuditFormAnswers.ANSWER_UNPLANNED;
import static org.openmrs.mobile.utilities.ApplicationConstants.AuditFormAnswers.ANSWER_YES;
import static org.openmrs.mobile.utilities.ApplicationConstants.AuditFormConcepts.CONCEPT_ANSWER_NA;
import static org.openmrs.mobile.utilities.ApplicationConstants.AuditFormConcepts.CONCEPT_ANSWER_NEGATIVE;
import static org.openmrs.mobile.utilities.ApplicationConstants.AuditFormConcepts.CONCEPT_ANSWER_NO;
import static org.openmrs.mobile.utilities.ApplicationConstants.AuditFormConcepts.CONCEPT_ANSWER_PLANNED;
import static org.openmrs.mobile.utilities.ApplicationConstants.AuditFormConcepts.CONCEPT_ANSWER_POSITIVE;
import static org.openmrs.mobile.utilities.ApplicationConstants.AuditFormConcepts.CONCEPT_ANSWER_UNKNOWN;
import static org.openmrs.mobile.utilities.ApplicationConstants.AuditFormConcepts.CONCEPT_ANSWER_UNPLANNED;
import static org.openmrs.mobile.utilities.ApplicationConstants.AuditFormConcepts.CONCEPT_ANSWER_YES;
import static org.openmrs.mobile.utilities.ApplicationConstants.AuditFormConcepts.CONCEPT_AUDIT_COMPLETE;
import static org.openmrs.mobile.utilities.ApplicationConstants.AuditFormConcepts.CONCEPT_CD4_COUNT;
import static org.openmrs.mobile.utilities.ApplicationConstants.AuditFormConcepts.CONCEPT_DEATH_IN_HOSPITAL;
import static org.openmrs.mobile.utilities.ApplicationConstants.AuditFormConcepts.CONCEPT_FIRST_GCS_SCORE_ICU;
import static org.openmrs.mobile.utilities.ApplicationConstants.AuditFormConcepts.CONCEPT_FIRST_HEART_RATE_ICU;
import static org.openmrs.mobile.utilities.ApplicationConstants.AuditFormConcepts.CONCEPT_FIRST_MAP_ICU;
import static org.openmrs.mobile.utilities.ApplicationConstants.AuditFormConcepts.CONCEPT_FIRST_SBP_ICU;
import static org.openmrs.mobile.utilities.ApplicationConstants.AuditFormConcepts.CONCEPT_HBA1C;
import static org.openmrs.mobile.utilities.ApplicationConstants.AuditFormConcepts.CONCEPT_HDU_COMGMT;
import static org.openmrs.mobile.utilities.ApplicationConstants.AuditFormConcepts.CONCEPT_HDU_STAY;
import static org.openmrs.mobile.utilities.ApplicationConstants.AuditFormConcepts.CONCEPT_HIV_STATUS;
import static org.openmrs.mobile.utilities.ApplicationConstants.AuditFormConcepts.CONCEPT_ICU_STAY;
import static org.openmrs.mobile.utilities.ApplicationConstants.AuditFormConcepts.CONCEPT_INFECTION_CONFIRMED_SUSPECTED;
import static org.openmrs.mobile.utilities.ApplicationConstants.AuditFormConcepts.CONCEPT_INPATIENT_SERVICE_TYPE;
import static org.openmrs.mobile.utilities.ApplicationConstants.AuditFormConcepts.CONCEPT_MECHANICAL_VENTILATIN;
import static org.openmrs.mobile.utilities.ApplicationConstants.AuditFormConcepts.CONCEPT_PALLIATIVE_CONSULT;
import static org.openmrs.mobile.utilities.ApplicationConstants.AuditFormConcepts.CONCEPT_PREOP_RISK_ASSESMENT;
import static org.openmrs.mobile.utilities.ApplicationConstants.AuditFormConcepts.CONCEPT_RECIEVED_VAOSPRESSORS;
import static org.openmrs.mobile.utilities.ApplicationConstants.AuditFormConcepts.CONCEPT_SEDETION_PRIOR_FIRST_GCS_SCORE_ICU;
import static org.openmrs.mobile.utilities.ApplicationConstants.AuditFormConcepts.CONCEPT_SURGERY_HOSPITAL_STAY;
import static org.openmrs.mobile.utilities.ApplicationConstants.EncounterTypeEntity.AUDIT_DATA_UUID;
import static org.openmrs.mobile.utilities.ApplicationConstants.FORM_UUIDS.AUDIT_DATA_FORM_UUID;

public class AuditDataFragment extends ACBaseFragment<AuditDataContract.Presenter>
		implements AuditDataContract.View {

	private Visit visit;
	//private Patient patient;
	private View fragmentView;
	private String locationUuid;
	private EditText cd4, hBa1c, firstIcuHeartRate, firstGcsScore;
	private String encounterUuid = null;
	private String visitUuid, patientUuid, visitStopDate;
	private OpenMRS instance = OpenMRS.getInstance();

	private Observation deathInHospitalObservation, palliativeConsultObservation, preopRiskAssessmentObservation,
			icuStayObservation, hduStayObservation, hduComgmtObservation, hivPositiveObservation, cd4Observation,
			hBa1cObservation, inpatientServiceTypeObservation, auditCompleteObservation, mechanicalVentilationObservation,
			vaospressorsObservation, confirmedInfectionObservation, firstSbpObservation, firstMapObservation,
			priorSedetionObservation, surgeryObservation, firstIcuHeartRateObservation, firstGcsScoreObservation;
	private RadioButton deathInHospitalYes, deathInHospitalNo, palliativeConsultYes, palliativeConsultNo,
			palliativeConsultUknown, preopRiskAssessmentYes, preopRiskAssessmentNo, preopRiskAssessmentUknown, icuStayYes,
			icuStayNo, icuStayUnknown, hduStayYes, hduStayNo, hduStayUnknown, hduComgmtYes, hduComgmtNo, hduComgmtUnknown,
			hivPositiveYes, hivPositiveNo, hivPositiveUnknown, mechanical_ventilation_yes, mechanical_ventilation_no,
			mechanical_ventilation_unknown, vaospressors_yes, vaospressors_no, vaospressors_unknown,
			confirmed_infection_yes, confirmed_infection_no, confirmed_infection_unknown, first_sbp_yes, first_sbp_no,
			first_sbp_unknown, any_prior_sedetion_yes, any_prior_sedetion_no, any_prior_sedetion_unknown, surgery_na,
			surgery_planned, surgery_unplanned, first_map_yes, first_map_no, first_map_unknown;
	private CheckBox auditComplete;

	private Spinner inpatientServiceType;
	private RelativeLayout progressBar, auditDataFormProgressBar;
	private LinearLayout auditDataFormScreen, extraFormAdditions;
	private ScrollView auditScrollView;
	private Button submitForm;
	private List<ConceptAnswer> conceptAnswerList;
	private String inpatientServiceTypeSelectedUuid, displayInpatientServiceType;
	private Boolean displayExtraFormFields;

	public static AuditDataFragment newInstance() {
		return new AuditDataFragment();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

		this.patientUuid = getActivity().getIntent().getStringExtra(ApplicationConstants.BundleKeys.PATIENT_UUID_BUNDLE);
		this.visitUuid = getActivity().getIntent().getStringExtra(ApplicationConstants.BundleKeys.VISIT_UUID_BUNDLE);
		this.visitStopDate = getActivity().getIntent().getStringExtra(ApplicationConstants.BundleKeys.VISIT_CLOSED_DATE);

		fragmentView = inflater.inflate(R.layout.fragment_audit_form, container, false);

		initViewFields();

		initRadioButtonListeners(deathInHospitalYes, deathInHospitalNo, palliativeConsultYes, palliativeConsultNo,
				palliativeConsultUknown, preopRiskAssessmentYes, preopRiskAssessmentNo, preopRiskAssessmentUknown,
				icuStayYes, icuStayNo, icuStayUnknown, hduStayYes, hduStayNo, hduStayUnknown, hduComgmtYes, hduComgmtNo,
				hduComgmtUnknown, hivPositiveYes, hivPositiveNo, hivPositiveUnknown, mechanical_ventilation_yes,
				mechanical_ventilation_no, mechanical_ventilation_unknown, vaospressors_yes, vaospressors_no,
				vaospressors_unknown, confirmed_infection_yes, confirmed_infection_no, confirmed_infection_unknown,
				first_sbp_yes, first_sbp_no, first_sbp_unknown, any_prior_sedetion_yes, any_prior_sedetion_no,
				any_prior_sedetion_unknown, surgery_na, surgery_planned, surgery_unplanned, first_map_yes, first_map_no,
				first_map_unknown);

		initCheckboxListeners(auditComplete);

		//We start by fetching by location, required for creating encounters
		if (!instance.getLocation().equalsIgnoreCase(null)) {
			locationUuid = instance.getParentLocationUuid();
		}

		mPresenter.fetchVisit(visitUuid);

		initObservations();
		addListeners();
		displayExtraFormFields = false;
		// Font config
		FontsUtil.setFont((ViewGroup)this.getActivity().findViewById(android.R.id.content));

		return fragmentView;
	}

	private void addListeners() {
		inpatientServiceType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				ConceptAnswer conceptAnswer = conceptAnswerList.get(position);
				inpatientServiceTypeSelectedUuid = conceptAnswer.getUuid();
				inpatientServiceTypeObservation =
						setObservationFields(inpatientServiceTypeObservation, CONCEPT_INPATIENT_SERVICE_TYPE,
								conceptAnswer.getUuid());
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});
	}

	private void initViewFields() {

		deathInHospitalYes = (RadioButton)fragmentView.findViewById(R.id.is_death_in_hospital_yes);
		deathInHospitalNo = (RadioButton)fragmentView.findViewById(R.id.is_death_in_hospital_no);
		palliativeConsultYes = (RadioButton)fragmentView.findViewById(R.id.is_palliative_consult_yes);
		palliativeConsultNo = (RadioButton)fragmentView.findViewById(R.id.is_palliative_consult_no);
		palliativeConsultUknown = (RadioButton)fragmentView.findViewById(R.id.is_palliative_consult_unknown);
		preopRiskAssessmentYes = (RadioButton)fragmentView.findViewById(R.id.is_preop_risk_assessment_only_yes);
		preopRiskAssessmentNo = (RadioButton)fragmentView.findViewById(R.id.is_preop_risk_assessment_only_no);
		preopRiskAssessmentUknown = (RadioButton)fragmentView.findViewById(R.id.is_preop_risk_assessment_only_unknown);
		icuStayYes = (RadioButton)fragmentView.findViewById(R.id.is_icu_stay_yes);
		icuStayNo = (RadioButton)fragmentView.findViewById(R.id.is_icu_stay_no);
		icuStayUnknown = (RadioButton)fragmentView.findViewById(R.id.is_icu_stay_unknown);
		hduStayYes = (RadioButton)fragmentView.findViewById(R.id.is_hdu_stay_yes);
		hduStayNo = (RadioButton)fragmentView.findViewById(R.id.is_hdu_stay_no);
		hduStayUnknown = (RadioButton)fragmentView.findViewById(R.id.is_hdu_stay_unknown);
		hduComgmtYes = (RadioButton)fragmentView.findViewById(R.id.is_hdu_comgmt_yes);
		hduComgmtNo = (RadioButton)fragmentView.findViewById(R.id.is_hdu_comgmt_no);
		hduComgmtUnknown = (RadioButton)fragmentView.findViewById(R.id.is_hdu_comgmt_unknown);
		hivPositiveYes = (RadioButton)fragmentView.findViewById(R.id.is_hiv_positive_yes);
		hivPositiveNo = (RadioButton)fragmentView.findViewById(R.id.is_hiv_positive_no);
		hivPositiveUnknown = (RadioButton)fragmentView.findViewById(R.id.is_hiv_positive_unknown);
		auditComplete = (CheckBox)fragmentView.findViewById(R.id.audit_complete);
		cd4 = (EditText)fragmentView.findViewById(R.id.cd4);
		hBa1c = (EditText)fragmentView.findViewById(R.id.hba1c);
		inpatientServiceType = (Spinner)fragmentView.findViewById(R.id.inpatient_service_type);
		submitForm = (Button)fragmentView.findViewById(R.id.submitConfirm);
		extraFormAdditions = (LinearLayout)fragmentView.findViewById(R.id.extraFormAdditions);
		firstIcuHeartRate = (EditText)fragmentView.findViewById(R.id.firstIcuHeartRate);
		firstGcsScore = (EditText)fragmentView.findViewById(R.id.firstGcsScore);
		mechanical_ventilation_yes = (RadioButton)fragmentView.findViewById(R.id.mechanical_ventilation_yes);
		mechanical_ventilation_no = (RadioButton)fragmentView.findViewById(R.id.mechanical_ventilation_no);
		mechanical_ventilation_unknown = (RadioButton)fragmentView.findViewById(R.id.mechanical_ventilation_unknown);
		vaospressors_yes = (RadioButton)fragmentView.findViewById(R.id.vaospressors_yes);
		vaospressors_no = (RadioButton)fragmentView.findViewById(R.id.vaospressors_no);
		vaospressors_unknown = (RadioButton)fragmentView.findViewById(R.id.vaospressors_unknown);
		confirmed_infection_yes = (RadioButton)fragmentView.findViewById(R.id.confirmed_infection_yes);
		confirmed_infection_no = (RadioButton)fragmentView.findViewById(R.id.confirmed_infection_no);
		confirmed_infection_unknown = (RadioButton)fragmentView.findViewById(R.id.confirmed_infection_unknown);
		first_sbp_yes = (RadioButton)fragmentView.findViewById(R.id.first_sbp_yes);
		first_sbp_no = (RadioButton)fragmentView.findViewById(R.id.first_sbp_no);
		first_sbp_unknown = (RadioButton)fragmentView.findViewById(R.id.first_sbp_unknown);
		any_prior_sedetion_yes = (RadioButton)fragmentView.findViewById(R.id.any_prior_sedetion_yes);
		any_prior_sedetion_no = (RadioButton)fragmentView.findViewById(R.id.any_prior_sedetion_no);
		any_prior_sedetion_unknown = (RadioButton)fragmentView.findViewById(R.id.any_prior_sedetion_unknown);
		surgery_na = (RadioButton)fragmentView.findViewById(R.id.surgery_na);
		surgery_planned = (RadioButton)fragmentView.findViewById(R.id.surgery_planned);
		surgery_unplanned = (RadioButton)fragmentView.findViewById(R.id.surgery_unplanned);
		first_map_yes = (RadioButton)fragmentView.findViewById(R.id.first_map_yes);
		first_map_no = (RadioButton)fragmentView.findViewById(R.id.first_map_no);
		first_map_unknown = (RadioButton)fragmentView.findViewById(R.id.first_map_unknown);

		submitForm.setOnClickListener(v -> {
			performDataSend();
		});

		progressBar = (RelativeLayout)fragmentView.findViewById(R.id.auditDataRelativeView);
		auditDataFormProgressBar = (RelativeLayout)fragmentView.findViewById(R.id.auditDataFormProgressBar);
		auditDataFormScreen = (LinearLayout)fragmentView.findViewById(R.id.auditDataFormScreen);
		auditScrollView = (ScrollView)fragmentView.findViewById(R.id.auditDataForm);
	}

	private void initObservations() {
		mechanicalVentilationObservation = vaospressorsObservation = confirmedInfectionObservation = firstSbpObservation =
				firstMapObservation = priorSedetionObservation = surgeryObservation = firstIcuHeartRateObservation =
						firstGcsScoreObservation = deathInHospitalObservation = palliativeConsultObservation =
								preopRiskAssessmentObservation = icuStayObservation = hduStayObservation =
										hduComgmtObservation = hivPositiveObservation =
												auditCompleteObservation = hBa1cObservation = cd4Observation = null;

	}

	private void initRadioButtonListeners(RadioButton... params) {
		for (RadioButton radioButton : params) {
			radioButton.setOnClickListener(
					view -> applyEvent(radioButton.getId()));
		}
	}

	private void initCheckboxListeners(CheckBox... params) {
		for (CheckBox checkBox : params) {
			checkBox.setOnClickListener(
					view -> applyEvent(checkBox.getId()));
		}
	}

	private void applyEvent(int id) {
		switch (id) {
			case R.id.is_death_in_hospital_yes:
				deathInHospitalObservation = setObservationFields(deathInHospitalObservation, CONCEPT_DEATH_IN_HOSPITAL,
						CONCEPT_ANSWER_YES);
				break;

			case R.id.is_death_in_hospital_no:
				deathInHospitalObservation = setObservationFields(deathInHospitalObservation, CONCEPT_DEATH_IN_HOSPITAL,
						CONCEPT_ANSWER_NO);
				break;

			case R.id.is_palliative_consult_yes:
				palliativeConsultObservation = setObservationFields(palliativeConsultObservation,
						CONCEPT_PALLIATIVE_CONSULT, CONCEPT_ANSWER_YES);

				break;

			case R.id.is_palliative_consult_no:
				palliativeConsultObservation = setObservationFields(palliativeConsultObservation,
						CONCEPT_PALLIATIVE_CONSULT,
						CONCEPT_ANSWER_NO);
				break;

			case R.id.is_palliative_consult_unknown:
				palliativeConsultObservation = setObservationFields(palliativeConsultObservation,
						CONCEPT_PALLIATIVE_CONSULT,
						CONCEPT_ANSWER_UNKNOWN);
				break;

			case R.id.is_preop_risk_assessment_only_yes:
				preopRiskAssessmentObservation = setObservationFields(preopRiskAssessmentObservation,
						CONCEPT_PREOP_RISK_ASSESMENT, CONCEPT_ANSWER_YES);
				break;

			case R.id.is_preop_risk_assessment_only_no:
				preopRiskAssessmentObservation = setObservationFields(preopRiskAssessmentObservation,
						CONCEPT_PREOP_RISK_ASSESMENT, CONCEPT_ANSWER_NO);
				break;

			case R.id.is_preop_risk_assessment_only_unknown:
				preopRiskAssessmentObservation = setObservationFields(preopRiskAssessmentObservation,
						CONCEPT_PREOP_RISK_ASSESMENT, CONCEPT_ANSWER_UNKNOWN);
				break;

			case R.id.is_icu_stay_yes:
				icuStayObservation = setObservationFields(icuStayObservation,
						CONCEPT_ICU_STAY, CONCEPT_ANSWER_YES);
				extraFormAdditions.setVisibility(View.VISIBLE);
				extraFormAdditions.animate().alpha(1.0f).setDuration(2000);
				displayExtraFormFields = true;
				break;

			case R.id.is_icu_stay_no:
				icuStayObservation = setObservationFields(icuStayObservation,
						CONCEPT_ICU_STAY, CONCEPT_ANSWER_NO);
				extraFormAdditions.setVisibility(View.GONE);
				extraFormAdditions.animate().alpha(0.0f).setDuration(2000);
				displayExtraFormFields = false;
				clearExtraObservation();
				break;

			case R.id.is_icu_stay_unknown:
				icuStayObservation = setObservationFields(icuStayObservation,
						CONCEPT_ICU_STAY, CONCEPT_ANSWER_UNKNOWN);
				extraFormAdditions.setVisibility(View.GONE);
				displayExtraFormFields = false;
				clearExtraObservation();
				break;

			case R.id.is_hdu_stay_yes:
				hduStayObservation = setObservationFields(hduStayObservation,
						CONCEPT_HDU_STAY, CONCEPT_ANSWER_YES);
				break;

			case R.id.is_hdu_stay_no:
				hduStayObservation = setObservationFields(hduStayObservation,
						CONCEPT_HDU_STAY, CONCEPT_ANSWER_NO);
				break;

			case R.id.is_hdu_stay_unknown:
				hduStayObservation = setObservationFields(hduStayObservation, CONCEPT_HDU_STAY, CONCEPT_ANSWER_UNKNOWN);
				break;

			case R.id.is_hdu_comgmt_yes:
				hduComgmtObservation = setObservationFields(hduComgmtObservation,
						CONCEPT_HDU_COMGMT, CONCEPT_ANSWER_YES);
				break;

			case R.id.is_hdu_comgmt_no:
				hduComgmtObservation = setObservationFields(hduComgmtObservation,
						CONCEPT_HDU_COMGMT, CONCEPT_ANSWER_NO);

				break;
			case R.id.is_hdu_comgmt_unknown:
				hduComgmtObservation = setObservationFields(hduComgmtObservation,
						CONCEPT_HDU_COMGMT, CONCEPT_ANSWER_UNKNOWN);

				break;

			case R.id.is_hiv_positive_yes:
				hivPositiveObservation =
						setObservationFields(hivPositiveObservation, CONCEPT_HIV_STATUS,
								CONCEPT_ANSWER_POSITIVE);
				break;

			case R.id.is_hiv_positive_no:
				hivPositiveObservation = setObservationFields(hivPositiveObservation,
						CONCEPT_HIV_STATUS, CONCEPT_ANSWER_NEGATIVE);
				break;

			case R.id.is_hiv_positive_unknown:
				hivPositiveObservation = setObservationFields(hivPositiveObservation,
						CONCEPT_HIV_STATUS, CONCEPT_ANSWER_UNKNOWN);
				break;

			case R.id.mechanical_ventilation_yes:
				mechanicalVentilationObservation =
						setObservationFields(mechanicalVentilationObservation, CONCEPT_MECHANICAL_VENTILATIN,
								CONCEPT_ANSWER_YES);
				break;

			case R.id.mechanical_ventilation_no:
				mechanicalVentilationObservation =
						setObservationFields(mechanicalVentilationObservation, CONCEPT_MECHANICAL_VENTILATIN,
								CONCEPT_ANSWER_NO);
				break;

			case R.id.mechanical_ventilation_unknown:
				mechanicalVentilationObservation =
						setObservationFields(mechanicalVentilationObservation, CONCEPT_MECHANICAL_VENTILATIN,
								CONCEPT_ANSWER_UNKNOWN);
				break;

			case R.id.confirmed_infection_yes:
				confirmedInfectionObservation =
						setObservationFields(confirmedInfectionObservation, CONCEPT_INFECTION_CONFIRMED_SUSPECTED,
								CONCEPT_ANSWER_YES);
				break;

			case R.id.confirmed_infection_no:
				confirmedInfectionObservation =
						setObservationFields(confirmedInfectionObservation, CONCEPT_INFECTION_CONFIRMED_SUSPECTED,
								CONCEPT_ANSWER_NO);
				break;

			case R.id.confirmed_infection_unknown:
				confirmedInfectionObservation =
						setObservationFields(confirmedInfectionObservation, CONCEPT_INFECTION_CONFIRMED_SUSPECTED,
								CONCEPT_ANSWER_UNKNOWN);
				break;

			case R.id.vaospressors_yes:
				vaospressorsObservation =
						setObservationFields(vaospressorsObservation, CONCEPT_RECIEVED_VAOSPRESSORS,
								CONCEPT_ANSWER_YES);
				break;

			case R.id.vaospressors_no:
				vaospressorsObservation =
						setObservationFields(vaospressorsObservation, CONCEPT_RECIEVED_VAOSPRESSORS,
								CONCEPT_ANSWER_NO);
				break;

			case R.id.vaospressors_unknown:
				vaospressorsObservation =
						setObservationFields(vaospressorsObservation, CONCEPT_RECIEVED_VAOSPRESSORS,
								CONCEPT_ANSWER_UNKNOWN);
				break;

			case R.id.first_sbp_yes:
				firstSbpObservation =
						setObservationFields(firstSbpObservation, CONCEPT_FIRST_SBP_ICU,
								CONCEPT_ANSWER_YES);
				break;

			case R.id.first_sbp_no:
				firstSbpObservation =
						setObservationFields(firstSbpObservation, CONCEPT_FIRST_SBP_ICU, CONCEPT_ANSWER_NO);
				break;

			case R.id.first_sbp_unknown:
				firstSbpObservation =
						setObservationFields(firstSbpObservation, CONCEPT_FIRST_SBP_ICU, CONCEPT_ANSWER_UNKNOWN);
				break;

			case R.id.any_prior_sedetion_yes:
				priorSedetionObservation =
						setObservationFields(priorSedetionObservation, CONCEPT_SEDETION_PRIOR_FIRST_GCS_SCORE_ICU,
								CONCEPT_ANSWER_YES);
				break;

			case R.id.any_prior_sedetion_no:
				priorSedetionObservation =
						setObservationFields(priorSedetionObservation, CONCEPT_SEDETION_PRIOR_FIRST_GCS_SCORE_ICU,
								CONCEPT_ANSWER_NO);
				break;

			case R.id.any_prior_sedetion_unknown:
				priorSedetionObservation =
						setObservationFields(priorSedetionObservation, CONCEPT_SEDETION_PRIOR_FIRST_GCS_SCORE_ICU,
								CONCEPT_ANSWER_UNKNOWN);
				break;

			case R.id.surgery_na:
				surgeryObservation =
						setObservationFields(surgeryObservation, CONCEPT_SURGERY_HOSPITAL_STAY,
								CONCEPT_ANSWER_NA);
				break;

			case R.id.surgery_planned:
				surgeryObservation =
						setObservationFields(surgeryObservation, CONCEPT_SURGERY_HOSPITAL_STAY, CONCEPT_ANSWER_PLANNED);
				break;

			case R.id.surgery_unplanned:
				surgeryObservation =
						setObservationFields(surgeryObservation, CONCEPT_SURGERY_HOSPITAL_STAY, CONCEPT_ANSWER_UNPLANNED);
				break;

			case R.id.first_map_yes:
				firstMapObservation =
						setObservationFields(firstMapObservation, CONCEPT_FIRST_MAP_ICU,
								CONCEPT_ANSWER_YES);
				break;

			case R.id.first_map_no:
				firstMapObservation =
						setObservationFields(firstMapObservation, CONCEPT_FIRST_MAP_ICU, CONCEPT_ANSWER_NO);
				break;

			case R.id.first_map_unknown:
				firstMapObservation =
						setObservationFields(firstMapObservation, CONCEPT_FIRST_MAP_ICU, CONCEPT_ANSWER_UNKNOWN);
				break;

			default:
				break;
		}
	}

	/**
	 * This function  assigns values to an Observation object
	 * @param observation         the observation that you want to set the values
	 * @param questionConceptUuid The uuid of the Concept that the is used a question
	 * @param answerConceptUuid   The uuid of the Concept that the user has selected as the answer
	 */

	private Observation setObservationFields(Observation observation, String questionConceptUuid, String
			answerConceptUuid) {

		if (observation == null) {
			observation = new Observation();
		}

		//here we assign all observations current time
		LocalDateTime localDateTime = new LocalDateTime();
		String timeString = localDateTime.toString();

		Concept concept = new Concept();
		concept.setUuid(questionConceptUuid);

		Person person = new Person();
		person.setUuid(patientUuid);

		observation.setConcept(concept);
		observation.setPerson(person);
		observation.setValue(answerConceptUuid);
		observation.setObsDatetime(timeString);

		return observation;
	}

	@Override
	public void setVisit(Visit visit) {
		this.visit = visit;
	}

	@Override
	public void setEncounterUuid(String encounterUuid) {
		this.encounterUuid = encounterUuid;
	}

	@Override
	public void goBackToVisitPage() {
		Intent intent = new Intent(getContext(), VisitActivity.class);
		intent.putExtra(ApplicationConstants.BundleKeys.PATIENT_UUID_BUNDLE, patientUuid);
		intent.putExtra(ApplicationConstants.BundleKeys.VISIT_UUID_BUNDLE, visitUuid);
		intent.putExtra(ApplicationConstants.BundleKeys.VISIT_CLOSED_DATE, visitStopDate);
		getContext().startActivity(intent);
	}

	@Override
	public void updateSubmitButtonText() {
		submitForm.setText(R.string.update_audit_data);
	}

	@Override
	public void showPageSpinner(boolean visibility) {
		if (visibility) {
			auditDataFormProgressBar.setVisibility(View.VISIBLE);
			auditDataFormScreen.setVisibility(View.GONE);
		} else {
			auditDataFormProgressBar.setVisibility(View.GONE);
			auditDataFormScreen.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void hideSoftKeys() {
		goBackToVisitPage();
		ACBaseActivity.hideSoftKeyboard(getActivity());
	}

	@Override
	public void setInpatientTypeServices(List<ConceptAnswer> conceptAnswers) {
		conceptAnswerList = conceptAnswers;
		ArrayAdapter<ConceptAnswer> adapter = new ArrayAdapter<>(getContext(), android.R.layout
				.simple_spinner_dropdown_item, conceptAnswers);
		inpatientServiceType.setAdapter(adapter);
	}

	@Override
	public void showProgressBar(Boolean visibility) {
		if (visibility) {
			progressBar.setVisibility(View.VISIBLE);
			auditScrollView.setVisibility(View.GONE);
		} else {
			progressBar.setVisibility(View.GONE);
			auditScrollView.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void updateFormFields(Encounter encounter) {
		for (Observation observation : encounter.getObs()) {
			ArrayList displayString = StringUtils.splitStrings(observation.getDisplay().toLowerCase(), ":");
			String displayValue = displayString.get(1).toString().trim();

			switch (observation.getConcept().getUuid()) {
				case CONCEPT_DEATH_IN_HOSPITAL:
					if (displayValue.equalsIgnoreCase(ANSWER_YES)) {
						deathInHospitalYes.setChecked(true);
						deathInHospitalObservation =
								setObservationFields(observation, CONCEPT_DEATH_IN_HOSPITAL,
										CONCEPT_ANSWER_YES);

					} else if (displayValue.equalsIgnoreCase(ANSWER_NO)) {
						deathInHospitalNo.setChecked(true);
						deathInHospitalObservation =
								setObservationFields(observation, CONCEPT_DEATH_IN_HOSPITAL,
										CONCEPT_ANSWER_NO);
					}

					break;

				case CONCEPT_PALLIATIVE_CONSULT:
					if (displayValue.equalsIgnoreCase(ANSWER_YES)) {
						palliativeConsultYes.setChecked(true);
						palliativeConsultObservation =
								setObservationFields(observation, CONCEPT_PALLIATIVE_CONSULT,
										CONCEPT_ANSWER_YES);
					} else if (displayValue.equalsIgnoreCase(ANSWER_NO)) {
						palliativeConsultNo.setChecked(true);
						palliativeConsultObservation =
								setObservationFields(observation, CONCEPT_PALLIATIVE_CONSULT,
										CONCEPT_ANSWER_NO);
					} else if (displayValue.equalsIgnoreCase(ANSWER_UNKNOWN)) {
						palliativeConsultUknown.setChecked(true);
						palliativeConsultObservation =
								setObservationFields(observation, CONCEPT_PALLIATIVE_CONSULT, CONCEPT_ANSWER_UNKNOWN);
					}

					break;

				case CONCEPT_PREOP_RISK_ASSESMENT:

					if (displayValue.equalsIgnoreCase(ANSWER_YES)) {
						preopRiskAssessmentYes.setChecked(true);
						preopRiskAssessmentObservation =
								setObservationFields(observation, CONCEPT_PREOP_RISK_ASSESMENT,
										CONCEPT_ANSWER_YES);

					} else if (displayValue.equalsIgnoreCase(ANSWER_NO)) {
						preopRiskAssessmentNo.setChecked(true);
						preopRiskAssessmentObservation =
								setObservationFields(observation, CONCEPT_PREOP_RISK_ASSESMENT,
										CONCEPT_ANSWER_NO);

					} else if (displayValue.equalsIgnoreCase(ANSWER_UNKNOWN)) {
						preopRiskAssessmentUknown.setChecked(true);
						preopRiskAssessmentObservation =
								setObservationFields(observation, CONCEPT_PREOP_RISK_ASSESMENT, CONCEPT_ANSWER_UNKNOWN);

					}

					break;

				case CONCEPT_ICU_STAY:

					if (displayValue.equalsIgnoreCase(ANSWER_YES)) {
						icuStayYes.setChecked(true);
						icuStayObservation = setObservationFields(observation, CONCEPT_ICU_STAY,
								CONCEPT_ANSWER_YES);
						extraFormAdditions.setVisibility(View.VISIBLE);

					} else if (displayValue.equalsIgnoreCase(ANSWER_NO)) {
						icuStayObservation = setObservationFields(observation, CONCEPT_ICU_STAY,
								CONCEPT_ANSWER_NO);
						icuStayNo.setChecked(true);
						extraFormAdditions.setVisibility(View.GONE);

					} else if (displayValue.equalsIgnoreCase(ANSWER_UNKNOWN)) {
						icuStayUnknown.setChecked(true);
						icuStayObservation = setObservationFields(observation, CONCEPT_ICU_STAY, CONCEPT_ANSWER_UNKNOWN);
						extraFormAdditions.setVisibility(View.GONE);
					}

					break;

				case CONCEPT_HDU_STAY:
					if (displayValue.equalsIgnoreCase(ANSWER_YES)) {
						hduStayYes.setChecked(true);
						hduStayObservation = setObservationFields(observation, CONCEPT_HDU_STAY,
								CONCEPT_ANSWER_YES);

					} else if (displayValue.equalsIgnoreCase(ANSWER_NO)) {
						hduStayNo.setChecked(true);
						hduStayObservation = setObservationFields(observation, CONCEPT_HDU_STAY,
								CONCEPT_ANSWER_NO);

					} else if (displayValue.equalsIgnoreCase(ANSWER_UNKNOWN)) {
						hduStayUnknown.setChecked(true);
						hduStayObservation = setObservationFields(observation, CONCEPT_HDU_STAY, CONCEPT_ANSWER_UNKNOWN);

					}

					break;
				case CONCEPT_HDU_COMGMT:

					if (displayValue.equalsIgnoreCase(ANSWER_YES)) {
						hduComgmtYes.setChecked(true);
						hduComgmtObservation = setObservationFields(observation, CONCEPT_HDU_COMGMT,
								CONCEPT_ANSWER_YES);

					} else if (displayValue.equalsIgnoreCase(ANSWER_NO)) {
						hduComgmtNo.setChecked(true);
						hduComgmtObservation = setObservationFields(observation, CONCEPT_HDU_COMGMT,
								CONCEPT_ANSWER_NO);

					} else if (displayValue.equalsIgnoreCase(ANSWER_UNKNOWN)) {
						hduComgmtUnknown.setChecked(true);
						hduComgmtObservation = setObservationFields(observation,
								CONCEPT_HDU_COMGMT, CONCEPT_ANSWER_UNKNOWN);
					}

					break;
				case CONCEPT_HIV_STATUS:

					if (displayValue.equalsIgnoreCase(ANSWER_POSITIVE)) {
						hivPositiveYes.setChecked(true);
						hivPositiveObservation = setObservationFields(observation, CONCEPT_HIV_STATUS,
								CONCEPT_ANSWER_POSITIVE);

					} else if (displayValue.equalsIgnoreCase(ANSWER_NEGATIVE)) {
						hivPositiveNo.setChecked(true);
						hivPositiveObservation = setObservationFields(observation, CONCEPT_HIV_STATUS,
								CONCEPT_ANSWER_NEGATIVE);

					} else if (displayValue.equalsIgnoreCase(ANSWER_UNKNOWN)) {
						hivPositiveUnknown.setChecked(true);
						hivPositiveObservation =
								setObservationFields(observation, CONCEPT_HIV_STATUS, CONCEPT_ANSWER_UNKNOWN);

					}
					break;

				case CONCEPT_CD4_COUNT:
					cd4Observation = setObservationFields(observation, CONCEPT_CD4_COUNT, displayValue);
					cd4.setText(displayValue);
					break;

				case CONCEPT_HBA1C:
					hBa1cObservation = setObservationFields(observation, CONCEPT_HBA1C, displayValue);
					hBa1c.setText(displayValue);
					break;

				case CONCEPT_INPATIENT_SERVICE_TYPE:
					if (!conceptAnswerList.isEmpty()) {
						for (int i = 0; i < conceptAnswerList.size(); i++) {
							if (conceptAnswerList.get(i).getDisplay().equalsIgnoreCase(displayValue)) {
								inpatientServiceType.setSelection(i);
								inpatientServiceTypeSelectedUuid = conceptAnswerList.get(i).getUuid();
							}
						}
					}
					inpatientServiceTypeObservation = setObservationFields(observation, CONCEPT_INPATIENT_SERVICE_TYPE,
							inpatientServiceTypeSelectedUuid);
					break;

				case CONCEPT_AUDIT_COMPLETE:
					if (displayValue.equalsIgnoreCase(ANSWER_YES)) {
						auditComplete.setChecked(true);
						auditCompleteObservation = setObservationFields(observation, CONCEPT_AUDIT_COMPLETE,
								CONCEPT_ANSWER_YES);
					} else {
						auditComplete.setChecked(false);
						auditCompleteObservation =
								setObservationFields(observation, CONCEPT_AUDIT_COMPLETE, CONCEPT_ANSWER_NO);
					}
					break;

				case CONCEPT_FIRST_HEART_RATE_ICU:
					firstIcuHeartRateObservation =
							setObservationFields(observation, CONCEPT_FIRST_HEART_RATE_ICU, displayValue);
					firstIcuHeartRate.setText(displayValue);
					break;

				case CONCEPT_FIRST_GCS_SCORE_ICU:
					firstGcsScoreObservation = setObservationFields(observation, CONCEPT_FIRST_GCS_SCORE_ICU, displayValue);
					firstGcsScore.setText(displayValue);
					break;

				case CONCEPT_MECHANICAL_VENTILATIN:
					if (displayValue.equalsIgnoreCase(ANSWER_YES)) {
						mechanical_ventilation_yes.setChecked(true);
						mechanicalVentilationObservation = setObservationFields(observation, CONCEPT_MECHANICAL_VENTILATIN,
								CONCEPT_ANSWER_YES);

					} else if (displayValue.equalsIgnoreCase(ANSWER_NO)) {
						mechanical_ventilation_no.setChecked(true);
						mechanicalVentilationObservation =
								setObservationFields(observation, CONCEPT_MECHANICAL_VENTILATIN, CONCEPT_ANSWER_NO);

					} else if (displayValue.equalsIgnoreCase(ANSWER_UNKNOWN)) {
						mechanical_ventilation_unknown.setChecked(true);
						mechanicalVentilationObservation =
								setObservationFields(observation, CONCEPT_MECHANICAL_VENTILATIN, CONCEPT_ANSWER_UNKNOWN);

					}

					break;
				case CONCEPT_RECIEVED_VAOSPRESSORS:
					if (displayValue.equalsIgnoreCase(ANSWER_YES)) {
						vaospressors_yes.setChecked(true);
						vaospressorsObservation = setObservationFields(observation, CONCEPT_RECIEVED_VAOSPRESSORS,
								CONCEPT_ANSWER_YES);

					} else if (displayValue.equalsIgnoreCase(ANSWER_NO)) {
						vaospressors_no.setChecked(true);
						vaospressorsObservation = setObservationFields(observation, CONCEPT_RECIEVED_VAOSPRESSORS,
								CONCEPT_ANSWER_NO);

					} else if (displayValue.equalsIgnoreCase(ANSWER_UNKNOWN)) {
						vaospressors_unknown.setChecked(true);
						vaospressorsObservation = setObservationFields(observation, CONCEPT_RECIEVED_VAOSPRESSORS,
								CONCEPT_ANSWER_UNKNOWN);

					}

					break;
				case CONCEPT_SURGERY_HOSPITAL_STAY:
					if (displayValue.equalsIgnoreCase(ANSWER_NA)) {
						surgery_na.setChecked(true);
						surgeryObservation = setObservationFields(observation, CONCEPT_SURGERY_HOSPITAL_STAY,
								CONCEPT_ANSWER_NA);

					} else if (displayValue.equalsIgnoreCase(ANSWER_PLANNED)) {
						surgery_planned.setChecked(true);
						surgeryObservation = setObservationFields(observation, CONCEPT_SURGERY_HOSPITAL_STAY,
								CONCEPT_ANSWER_PLANNED);

					} else if (displayValue.equalsIgnoreCase(ANSWER_UNPLANNED)) {
						surgery_unplanned.setChecked(true);
						surgeryObservation = setObservationFields(observation, CONCEPT_SURGERY_HOSPITAL_STAY,
								CONCEPT_ANSWER_UNPLANNED);

					}

					break;
				case CONCEPT_INFECTION_CONFIRMED_SUSPECTED:
					if (displayValue.equalsIgnoreCase(ANSWER_YES)) {
						confirmed_infection_yes.setChecked(true);
						confirmedInfectionObservation =
								setObservationFields(observation, CONCEPT_INFECTION_CONFIRMED_SUSPECTED,
										CONCEPT_ANSWER_YES);

					} else if (displayValue.equalsIgnoreCase(ANSWER_NO)) {
						confirmed_infection_no.setChecked(true);
						confirmedInfectionObservation =
								setObservationFields(observation, CONCEPT_INFECTION_CONFIRMED_SUSPECTED,
										CONCEPT_ANSWER_NO);

					} else if (displayValue.equalsIgnoreCase(ANSWER_UNKNOWN)) {
						confirmed_infection_unknown.setChecked(true);
						confirmedInfectionObservation =
								setObservationFields(observation, CONCEPT_INFECTION_CONFIRMED_SUSPECTED,
										CONCEPT_ANSWER_UNKNOWN);

					}

					break;
				case CONCEPT_FIRST_SBP_ICU:
					if (displayValue.equalsIgnoreCase(ANSWER_YES)) {
						first_sbp_yes.setChecked(true);
						firstSbpObservation = setObservationFields(observation, CONCEPT_FIRST_SBP_ICU,
								CONCEPT_ANSWER_YES);

					} else if (displayValue.equalsIgnoreCase(ANSWER_NO)) {
						first_sbp_no.setChecked(true);
						firstSbpObservation = setObservationFields(observation, CONCEPT_FIRST_SBP_ICU,
								CONCEPT_ANSWER_NO);

					} else if (displayValue.equalsIgnoreCase(ANSWER_UNKNOWN)) {
						first_sbp_unknown.setChecked(true);
						firstSbpObservation =
								setObservationFields(observation, CONCEPT_FIRST_SBP_ICU, CONCEPT_ANSWER_UNKNOWN);

					}

					break;
				case CONCEPT_FIRST_MAP_ICU:
					if (displayValue.equalsIgnoreCase(ANSWER_YES)) {
						first_map_yes.setChecked(true);
						firstMapObservation = setObservationFields(observation, CONCEPT_FIRST_MAP_ICU,
								CONCEPT_ANSWER_YES);

					} else if (displayValue.equalsIgnoreCase(ANSWER_NO)) {
						first_map_no.setChecked(true);
						firstMapObservation = setObservationFields(observation, CONCEPT_FIRST_MAP_ICU,
								CONCEPT_ANSWER_NO);

					} else if (displayValue.equalsIgnoreCase(ANSWER_UNKNOWN)) {
						first_map_unknown.setChecked(true);
						firstMapObservation =
								setObservationFields(observation, CONCEPT_FIRST_MAP_ICU, CONCEPT_ANSWER_UNKNOWN);

					}

					break;
				case CONCEPT_SEDETION_PRIOR_FIRST_GCS_SCORE_ICU:
					if (displayValue.equalsIgnoreCase(ANSWER_YES)) {
						any_prior_sedetion_yes.setChecked(true);
						priorSedetionObservation =
								setObservationFields(observation, CONCEPT_SEDETION_PRIOR_FIRST_GCS_SCORE_ICU,
										CONCEPT_ANSWER_YES);

					} else if (displayValue.equalsIgnoreCase(ANSWER_NO)) {
						any_prior_sedetion_no.setChecked(true);
						priorSedetionObservation =
								setObservationFields(observation, CONCEPT_SEDETION_PRIOR_FIRST_GCS_SCORE_ICU,
										CONCEPT_ANSWER_NO);

					} else if (displayValue.equalsIgnoreCase(ANSWER_UNKNOWN)) {
						any_prior_sedetion_unknown.setChecked(true);
						priorSedetionObservation =
								setObservationFields(observation, CONCEPT_SEDETION_PRIOR_FIRST_GCS_SCORE_ICU,
										CONCEPT_ANSWER_UNKNOWN);

					}

					break;

				default:
					break;

			}
		}

	}

	private void performDataSend() {
		//create location instance
		Location location = new Location();
		location.setUuid(locationUuid);

		//create form instance
		Form auditDataForm = new Form();
		auditDataForm.setUuid(AUDIT_DATA_FORM_UUID);

		//create encountertype
		EncounterType auditFormEncounterType = new EncounterType();
		auditFormEncounterType.setUuid(AUDIT_DATA_UUID);

		//create provider
		Provider provider = new Provider();
		provider.setUuid(instance.getCurrentLoggedInUserInfo().get(ApplicationConstants.UserKeys.USER_UUID));

		List<Observation> observations = new ArrayList<Observation>();
		List<Provider> providers = new ArrayList<Provider>();
		providers.add(provider);

		if (deathInHospitalObservation != null) {
			observations.add(deathInHospitalObservation);
		}

		if (palliativeConsultObservation != null) {
			observations.add(palliativeConsultObservation);
		}

		if (preopRiskAssessmentObservation != null) {
			observations.add(preopRiskAssessmentObservation);
		}

		if (icuStayObservation != null) {
			observations.add(icuStayObservation);
		}

		if (hduStayObservation != null) {
			observations.add(hduStayObservation);
		}

		if (hduComgmtObservation != null) {
			observations.add(hduComgmtObservation);
		}

		if (hivPositiveObservation != null) {
			observations.add(hivPositiveObservation);
		}

		if (mechanicalVentilationObservation != null) {
			observations.add(mechanicalVentilationObservation);
		}

		if (vaospressorsObservation != null) {
			observations.add(vaospressorsObservation);
		}

		if (surgeryObservation != null) {
			observations.add(surgeryObservation);
		}
		if (confirmedInfectionObservation != null) {
			observations.add(confirmedInfectionObservation);
		}

		if (firstSbpObservation != null) {
			observations.add(firstSbpObservation);
		}

		if (firstMapObservation != null) {
			observations.add(firstMapObservation);
		}
		if (priorSedetionObservation != null) {
			observations.add(priorSedetionObservation);
		}

		if (firstIcuHeartRate.getText().length() > 0) {
			if (Float.valueOf(firstIcuHeartRate.getText().toString()) >= 0
					&& Float.valueOf(firstIcuHeartRate.getText().toString()) <= 240) {
				firstIcuHeartRateObservation =
						setObservationFields(firstIcuHeartRateObservation, CONCEPT_FIRST_HEART_RATE_ICU,
								firstIcuHeartRate.getText().toString());
				observations.add(firstIcuHeartRateObservation);
			}
		}

		if (firstGcsScore.getText().length() > 0) {
			if (Float.valueOf(firstGcsScore.getText().toString()) >= 1
					&& Float.valueOf(firstGcsScore.getText().toString()) <= 15) {
				firstGcsScoreObservation = setObservationFields(firstGcsScoreObservation, CONCEPT_FIRST_GCS_SCORE_ICU,
						firstGcsScore.getText().toString());
				observations.add(firstGcsScoreObservation);
			}
		}

		if (cd4.getText().length() > 0) {
			cd4Observation = setObservationFields(cd4Observation, CONCEPT_CD4_COUNT, cd4.getText().toString());
			observations.add(cd4Observation);
		}

		if (hBa1c.getText().length() > 0) {
			hBa1cObservation = setObservationFields(hBa1cObservation, CONCEPT_HBA1C, hBa1c.getText().toString());
			observations.add(hBa1cObservation);
		}

		if (inpatientServiceTypeObservation != null) {
			inpatientServiceTypeObservation = setObservationFields(inpatientServiceTypeObservation,
					CONCEPT_INPATIENT_SERVICE_TYPE, inpatientServiceTypeSelectedUuid);
			observations.add(inpatientServiceTypeObservation);
		}

		if (auditComplete.isChecked()) {
			auditCompleteObservation = setObservationFields(auditCompleteObservation,
					CONCEPT_AUDIT_COMPLETE, CONCEPT_ANSWER_YES);
		} else {
			auditCompleteObservation = setObservationFields(auditCompleteObservation,
					CONCEPT_AUDIT_COMPLETE, CONCEPT_ANSWER_NO);
		}
		observations.add(auditCompleteObservation);

		boolean isNewEncounter = false;
		Encounter encounter = new Encounter();
		if (encounterUuid == null) {
			isNewEncounter = true;
		} else {
			encounter.setUuid(encounterUuid);
		}

		encounter.setObs(observations);
		encounter.setPatient(visit.getPatient());
		encounter.setForm(auditDataForm);
		encounter.setLocation(location);
		encounter.setVisit(visit);
		encounter.setProvider(instance.getCurrentLoggedInUserInfo().get(ApplicationConstants.UserKeys.USER_UUID));
		encounter.setEncounterType(auditFormEncounterType);
		encounter.setEncounterDatetime(visit.getStartDatetime());

		mPresenter.saveUpdateEncounter(encounter, isNewEncounter);
	}

	public void clearExtraObservation() {
		if (mechanicalVentilationObservation != null) {
			mechanicalVentilationObservation.setVoided(true);
		}
		if (vaospressorsObservation != null) {
			vaospressorsObservation.setVoided(true);
		}
		if (surgeryObservation != null) {
			surgeryObservation.setVoided(true);
		}
		if (confirmedInfectionObservation != null) {
			confirmedInfectionObservation.setVoided(true);
		}
		if (firstSbpObservation != null) {
			firstSbpObservation.setVoided(true);
		}
		if (firstMapObservation != null) {
			firstMapObservation.setVoided(true);
		}
		if (firstIcuHeartRateObservation != null) {
			firstIcuHeartRateObservation.setVoided(true);
		}
		if (firstGcsScoreObservation != null) {
			firstGcsScoreObservation.setVoided(true);
		}
		if (priorSedetionObservation != null) {
			priorSedetionObservation.setVoided(true);
		}
	}

}
