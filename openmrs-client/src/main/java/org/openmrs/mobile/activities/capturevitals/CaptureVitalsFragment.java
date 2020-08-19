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

package org.openmrs.mobile.activities.capturevitals;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.joda.time.LocalDateTime;
import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseActivity;
import org.openmrs.mobile.activities.ACBaseFragment;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.models.Concept;
import org.openmrs.mobile.models.Encounter;
import org.openmrs.mobile.models.EncounterType;
import org.openmrs.mobile.models.Form;
import org.openmrs.mobile.models.Location;
import org.openmrs.mobile.models.Observation;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.Person;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.FontsUtil;
import org.openmrs.mobile.utilities.StringUtils;
import org.openmrs.mobile.utilities.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import static org.openmrs.mobile.utilities.ApplicationConstants.EncounterTypeEntity.VITALS_UUID;
import static org.openmrs.mobile.utilities.ApplicationConstants.FORM_UUIDS.VITALS_FORM_UUID;
import static org.openmrs.mobile.utilities.ApplicationConstants.VITALSFormConcepts.CONCEPT_BLOOD_OXYGEN_SATURATION;
import static org.openmrs.mobile.utilities.ApplicationConstants.VITALSFormConcepts.CONCEPT_BLOOD_PRESSURE_DIASTOLIC;
import static org.openmrs.mobile.utilities.ApplicationConstants.VITALSFormConcepts.CONCEPT_BLOOD_PRESSURE_SYSTOLIC;
import static org.openmrs.mobile.utilities.ApplicationConstants.VITALSFormConcepts.CONCEPT_HEIGHT;
import static org.openmrs.mobile.utilities.ApplicationConstants.VITALSFormConcepts.CONCEPT_PULSE;
import static org.openmrs.mobile.utilities.ApplicationConstants.VITALSFormConcepts.CONCEPT_RESPIRATORY_RATE;
import static org.openmrs.mobile.utilities.ApplicationConstants.VITALSFormConcepts.CONCEPT_TEMPERATURE;
import static org.openmrs.mobile.utilities.ApplicationConstants.VITALSFormConcepts.CONCEPT_WEIGHT;
import static org.openmrs.mobile.utilities.ApplicationConstants.ValidationFieldValues.VITALS_BLOOD_OXYGEN_MAX;
import static org.openmrs.mobile.utilities.ApplicationConstants.ValidationFieldValues.VITALS_BLOOD_OXYGEN_MIN;
import static org.openmrs.mobile.utilities.ApplicationConstants.ValidationFieldValues.VITALS_DIASTOLICBP_MAX;
import static org.openmrs.mobile.utilities.ApplicationConstants.ValidationFieldValues.VITALS_DIASTOLICBP_MIN;
import static org.openmrs.mobile.utilities.ApplicationConstants.ValidationFieldValues.VITALS_HEIGHT_MAX;
import static org.openmrs.mobile.utilities.ApplicationConstants.ValidationFieldValues.VITALS_HEIGHT_MIN;
import static org.openmrs.mobile.utilities.ApplicationConstants.ValidationFieldValues.VITALS_PULSE_MAX;
import static org.openmrs.mobile.utilities.ApplicationConstants.ValidationFieldValues.VITALS_PULSE_MIN;
import static org.openmrs.mobile.utilities.ApplicationConstants.ValidationFieldValues.VITALS_RESPIRATORYRATE_MAX;
import static org.openmrs.mobile.utilities.ApplicationConstants.ValidationFieldValues.VITALS_RESPIRATORYRATE_MIN;
import static org.openmrs.mobile.utilities.ApplicationConstants.ValidationFieldValues.VITALS_SYSTOLICBP_MAX;
import static org.openmrs.mobile.utilities.ApplicationConstants.ValidationFieldValues.VITALS_SYSTOLICBP_MIN;
import static org.openmrs.mobile.utilities.ApplicationConstants.ValidationFieldValues.VITALS_TEMPERATURE_MAX;
import static org.openmrs.mobile.utilities.ApplicationConstants.ValidationFieldValues.VITALS_TEMPERATURE_MIN;
import static org.openmrs.mobile.utilities.ApplicationConstants.ValidationFieldValues.VITALS_WEIGHT_MAX;
import static org.openmrs.mobile.utilities.ApplicationConstants.ValidationFieldValues.VITALS_WEIGHT_MIN;

public class CaptureVitalsFragment extends ACBaseFragment<CaptureVitalsContract.Presenter>
		implements CaptureVitalsContract.View {

	private static final String ARG_PATIENT_UUID = "patientUuid";
	private static final String ARG_VISIT_UUID = "visitUuid";

	private OnFragmentInteractionListener listener;

	private Location location;
	private EditText patientHeight, patientWeight, patientTemperature, patientPulse, patientRespiratoryRate,
			patientBloodPressureSystolic, patientBloodPressureDiastolic, patientBloodOxygenSaturation;
	private TextView patientHeightError, patientWeightError, patientBmiError, patientTemperatureError, patientPulseError,
			patientRespiratoryRateError, patientBloodPressureError, patientBloodOxygenSaturationError;
	private String encounterUuid = null;
	private String visitUuid, patientUuid, visitStopDate;
	private OpenMRS instance = OpenMRS.getInstance();
	private Button submitForm;
	private RelativeLayout progressBar, captureVitalsProgressBar;
	private LinearLayout captureVitalsScreen;
	private ScrollView captureVitalsScrollView;

	public static CaptureVitalsFragment newInstance(String patientUuid, String visitUuid) {
		CaptureVitalsFragment fragment = new CaptureVitalsFragment();
		Bundle args = new Bundle();
		args.putString(ARG_PATIENT_UUID, patientUuid);
		args.putString(ARG_VISIT_UUID, visitUuid);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			patientUuid = getArguments().getString(ARG_PATIENT_UUID);
			visitUuid = getArguments().getString(ARG_VISIT_UUID);
			presenter = new CaptureVitalsPresenter(this);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.fragment_capture_vitals, container, false);

		resolveViews(root);

		//We start by fetching by location, required for creating encounters
		String locationUuid = instance.getLocation();
		if (StringUtils.isNullOrEmpty(locationUuid)) {
			logger.e("Location UUID is missing on Capture Vitals.");
			showToast(ApplicationConstants.entityName.LOCATION +
					ApplicationConstants.toastMessages.fetchErrorMessage, ToastUtil.ToastType.ERROR);
		} else {
			presenter.fetchLocation(locationUuid);
		}

		// Font config
		FontsUtil.setFont(context.findViewById(android.R.id.content));

		return root;
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		if (context instanceof OnFragmentInteractionListener) {
			listener = (OnFragmentInteractionListener) context;
		} else {
			throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		listener = null;
	}

	private void resolveViews(View root) {

		//Le text input fields
		patientHeight = (EditText)root.findViewById(R.id.patientHeight);
		patientWeight = (EditText)root.findViewById(R.id.patientWeight);
		//patientBMI = (EditText)root.findViewById(R.id.patientBmi);
		patientTemperature = (EditText)root.findViewById(R.id.patientTemperature);
		patientPulse = (EditText)root.findViewById(R.id.patientPulse);
		patientRespiratoryRate = (EditText)root.findViewById(R.id.patientRespiratoryRate);
		patientBloodPressureSystolic = (EditText)root.findViewById(R.id.patientBloodPressureSystolic);
		patientBloodPressureDiastolic = (EditText)root.findViewById(R.id.patientBloodPressureDiastolic);
		patientBloodOxygenSaturation = (EditText)root.findViewById(R.id.patientBloodOxygenSaturation);

		//The error message fields
		patientHeightError = (TextView)root.findViewById(R.id.patientHeightError);
		patientWeightError = (TextView)root.findViewById(R.id.patientWeightError);
		patientBmiError = (TextView)root.findViewById(R.id.patientBmiError);
		patientTemperatureError = (TextView)root.findViewById(R.id.patientTemperatureError);
		patientPulseError = (TextView)root.findViewById(R.id.patientPulseError);
		patientRespiratoryRateError = (TextView)root.findViewById(R.id.patientRespiratoryRateError);
		patientBloodPressureError = (TextView)root.findViewById(R.id.patientBloodPressureError);
		patientBloodOxygenSaturationError = (TextView)root.findViewById(R.id.patientBloodOxygenSaturationError);

		progressBar = (RelativeLayout)root.findViewById(R.id.captureVitalsSavingProgressBar);
		captureVitalsProgressBar = (RelativeLayout)root.findViewById(R.id.captureVitalsProgressBar);
		captureVitalsScreen = (LinearLayout)root.findViewById(R.id.captureVitalsScreen);
		captureVitalsScrollView = (ScrollView)root.findViewById(R.id.captureVitalsForm);

		submitForm = (Button)root.findViewById(R.id.submitConfirm);
		submitForm.setOnClickListener(v -> {
			try {
				if (validateFields()) {
					Encounter encounter = buildEncounter();
					presenter.attemptSave(encounter);
				}
			} catch (NumberFormatException ex) {
				ex.printStackTrace();
			}
		});
	}

	private Observation setObservationFields(String conceptUuid, String inputValue) {

		Observation observation = new Observation();

		LocalDateTime localDateTime = new LocalDateTime();
		String timeString = localDateTime.toString();

		Concept concept = new Concept();
		concept.setUuid(conceptUuid);

		Person person = new Person();
		person.setUuid(patientUuid);

		observation.setConcept(concept);
		observation.setPerson(person);
		observation.setObsDatetime(timeString);
		observation.setValue(inputValue);

		return observation;
	}

	private boolean validateFields() throws NumberFormatException {

		boolean heightFilled, weightFilled, temperatureFilled, pulseFilled, respiratoryRateFilled,
				systolicBpFilled, diastolicBpFilled, bloodOxygenFilled;

		heightFilled = weightFilled = temperatureFilled = pulseFilled = respiratoryRateFilled =
				systolicBpFilled = diastolicBpFilled = bloodOxygenFilled = false;

		showErrorView(patientHeightError, patientWeightError, patientTemperatureError, patientPulseError,
				patientRespiratoryRateError, patientBloodPressureError, patientBloodOxygenSaturationError);

		String height = patientHeight.getText().toString();
		if (Integer.valueOf(height) < VITALS_HEIGHT_MIN) {
			patientHeightError.setText(getString(R.string.error_text_vitals_minimum, VITALS_HEIGHT_MIN));
		} else if (Integer.parseInt(height) > VITALS_HEIGHT_MAX) {
			patientHeightError.setText(getString(R.string.error_text_vitals_maximum, VITALS_HEIGHT_MAX));
		} else {
			hideErrorView(patientHeightError);
			heightFilled = true;
		}

		String weight = patientWeight.getText().toString();
		if (Integer.valueOf(weight) < VITALS_WEIGHT_MIN) {
			patientWeightError.setText(getString(R.string.error_text_vitals_minimum, VITALS_WEIGHT_MIN));
		} else if (Integer.parseInt(weight) > VITALS_WEIGHT_MAX) {
			patientWeightError.setText(getString(R.string.error_text_vitals_maximum, VITALS_WEIGHT_MAX));
		} else {
			hideErrorView(patientWeightError);
			weightFilled = true;
		}

		String temperature = patientTemperature.getText().toString();
		if (Integer.valueOf(temperature) < VITALS_TEMPERATURE_MIN) {
			patientTemperatureError.setText(getString(R.string.error_text_vitals_minimum, VITALS_TEMPERATURE_MIN));
		} else if (Integer.parseInt(temperature) > VITALS_TEMPERATURE_MAX) {
			patientTemperatureError.setText(getString(R.string.error_text_vitals_maximum, VITALS_TEMPERATURE_MAX));
		} else {
			hideErrorView(patientTemperatureError);
			temperatureFilled = true;
		}

		String pulse = patientPulse.getText().toString();
		if (Integer.valueOf(pulse) < VITALS_PULSE_MIN) {
			patientPulseError.setText(getString(R.string.error_text_vitals_minimum, VITALS_PULSE_MIN));
		} else if (Integer.parseInt(pulse) > VITALS_PULSE_MAX) {
			patientPulseError.setText(getString(R.string.error_text_vitals_maximum, VITALS_PULSE_MAX));
		} else {
			hideErrorView(patientPulseError);
			pulseFilled = true;
		}

		String respiratoryRate = patientRespiratoryRate.getText().toString();
		if (Integer.valueOf(respiratoryRate) < VITALS_RESPIRATORYRATE_MIN) {
			patientRespiratoryRateError.setText(getString(R.string.error_text_vitals_minimum, VITALS_RESPIRATORYRATE_MIN));
		} else if (Integer.parseInt(respiratoryRate) > VITALS_RESPIRATORYRATE_MAX) {
			patientRespiratoryRateError.setText(getString(R.string.error_text_vitals_maximum, VITALS_RESPIRATORYRATE_MAX));
		} else {
			hideErrorView(patientRespiratoryRateError);
			respiratoryRateFilled = true;
		}

		String systolicBp = patientBloodPressureSystolic.getText().toString();
		if (Integer.valueOf(systolicBp) < VITALS_SYSTOLICBP_MIN) {
			patientBloodPressureError.setText(getString(R.string.error_text_vitals_minimum, VITALS_SYSTOLICBP_MIN));
			setViewFocus(patientBloodPressureSystolic);
		} else if (Integer.parseInt(systolicBp) > VITALS_SYSTOLICBP_MAX) {
			patientBloodPressureError.setText(getString(R.string.error_text_vitals_maximum, VITALS_SYSTOLICBP_MAX));
			setViewFocus(patientBloodPressureSystolic);
		} else {
			systolicBpFilled = true;
		}

		String diastolicBp = patientBloodPressureDiastolic.getText().toString();
		if (Integer.valueOf(diastolicBp) < VITALS_DIASTOLICBP_MIN) {
			patientBloodPressureError.setText(getString(R.string.error_text_vitals_minimum, VITALS_DIASTOLICBP_MIN));
			setViewFocus(patientBloodPressureDiastolic);
		} else if (Integer.parseInt(diastolicBp) > VITALS_DIASTOLICBP_MAX) {
			patientBloodPressureError.setText(getString(R.string.error_text_vitals_maximum, VITALS_DIASTOLICBP_MAX));
			setViewFocus(patientBloodPressureDiastolic);
		} else {
			diastolicBpFilled = true;
		}

		if (systolicBpFilled && diastolicBpFilled) {
			hideErrorView(patientBloodPressureError);
		}

		String bloodOxygen = patientBloodOxygenSaturation.getText().toString();
		if (Integer.valueOf(bloodOxygen) < VITALS_BLOOD_OXYGEN_MIN) {
			patientBloodOxygenSaturationError
					.setText(getString(R.string.error_text_vitals_minimum, VITALS_BLOOD_OXYGEN_MIN));
		} else if (Integer.parseInt(bloodOxygen) > VITALS_BLOOD_OXYGEN_MAX) {
			patientBloodOxygenSaturationError
					.setText(getString(R.string.error_text_vitals_maximum, VITALS_BLOOD_OXYGEN_MAX));
		} else {
			hideErrorView(patientBloodOxygenSaturationError);
			bloodOxygenFilled = true;
		}

		return (heightFilled && weightFilled && temperatureFilled && pulseFilled && respiratoryRateFilled &&
				systolicBpFilled && diastolicBpFilled && bloodOxygenFilled);
	}

	private void setViewFocus(View view) {
		view.requestFocus();
	}

	private Encounter buildEncounter() {

		//create arraylist to carry observations
		List<Observation> observations = new ArrayList<>();

		//Create encounter observations then add them to the arrylist
		Observation patientHeightObservation = setObservationFields(CONCEPT_HEIGHT, patientHeight.getText().toString());
		observations.add(patientHeightObservation);

		Observation patientWeightObservation = setObservationFields(CONCEPT_WEIGHT, patientWeight.getText().toString());
		observations.add(patientWeightObservation);

		Observation patientTemperatureObservation =
				setObservationFields(CONCEPT_TEMPERATURE, patientTemperature.getText().toString());
		observations.add(patientTemperatureObservation);

		Observation patientPulseObservation = setObservationFields(CONCEPT_PULSE, patientPulse.getText().toString());
		observations.add(patientPulseObservation);

		Observation patientRespiratoryRateObservation =
				setObservationFields(CONCEPT_RESPIRATORY_RATE, patientRespiratoryRate.getText()
						.toString());
		observations.add(patientRespiratoryRateObservation);

		Observation patientBpSystolicObservation =
				setObservationFields(CONCEPT_BLOOD_PRESSURE_SYSTOLIC, patientBloodPressureSystolic.getText().toString());
		observations.add(patientBpSystolicObservation);

		Observation patientBpDiastolicObservation =
				setObservationFields(CONCEPT_BLOOD_PRESSURE_DIASTOLIC, patientBloodPressureDiastolic.getText().toString());
		observations.add(patientBpDiastolicObservation);

		Observation patientBloodOxygenSaturationObservation =
				setObservationFields(CONCEPT_BLOOD_OXYGEN_SATURATION, patientBloodOxygenSaturation.getText().toString());
		observations.add(patientBloodOxygenSaturationObservation);

		Patient patient = new Patient();
		patient.setUuid(patientUuid);

		EncounterType encounterType = new EncounterType();
		encounterType.setUuid(VITALS_UUID);

		Form form = new Form();
		form.setUuid(VITALS_FORM_UUID);

		Encounter encounter = new Encounter();
		encounter.setObs(observations);
		encounter.setPatient(patient);
		encounter.setForm(form);
		encounter.setLocation(location);
		encounter.setVisit(new Visit(visitUuid));
		encounter.setProvider(instance.getCurrentLoggedInUserInfo().get(ApplicationConstants.UserKeys.USER_UUID));
		encounter.setEncounterType(encounterType);

		return encounter;
	}

	@Override
	public void showErrorView(View... view) {
		for (View v : view) {
			v.setVisibility(View.VISIBLE);
		}

	}

	@Override
	public void hideErrorView(View... view) {
		for (View v : view) {
			v.setVisibility(View.GONE);
		}
	}

	@Override
	public void setLocation(Location location) {
		this.location = location;
	}

	@Override
	public void disableButton() {
		submitForm.setEnabled(false);
	}

	@Override
	public void showProgressBar(Boolean visibility) {
		if (visibility) {
			progressBar.setVisibility(View.VISIBLE);
			captureVitalsScrollView.setVisibility(View.GONE);
		} else {
			progressBar.setVisibility(View.GONE);
			captureVitalsScrollView.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void showPageSpinner(boolean visibility) {
		if (visibility) {
			captureVitalsScreen.setVisibility(View.GONE);
			captureVitalsProgressBar.setVisibility(View.VISIBLE);
		} else {
			captureVitalsScreen.setVisibility(View.VISIBLE);
			captureVitalsProgressBar.setVisibility(View.GONE);
		}
	}

	@Override
	public void hideSoftKeys() {
		ACBaseActivity.hideSoftKeyboard(getActivity());
	}

	@Override
	public void captureVitalsSaveComplete() {
		if (listener != null) {
			listener.captureVitalsSaveComplete(patientUuid, visitUuid);
		}
	}

	public interface OnFragmentInteractionListener {

		void captureVitalsSaveComplete(String patientUuid, String visitUuid);
	}
}
