/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and
 * limitations under the License.
 *
 * Copyright (C) OpenHMIS.  All Rights Reserved.
 */
package org.openmrs.mobile.activities.addeditvisit;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseFragment;
import org.openmrs.mobile.activities.patientdashboard.PatientDashboardActivity;
import org.openmrs.mobile.activities.visit.VisitActivity;
import org.openmrs.mobile.models.BaseOpenmrsObject;
import org.openmrs.mobile.models.ConceptAnswer;
import org.openmrs.mobile.models.Resource;
import org.openmrs.mobile.models.VisitAttribute;
import org.openmrs.mobile.models.VisitAttributeType;
import org.openmrs.mobile.models.VisitType;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.DateUtils;
import org.openmrs.mobile.utilities.StringUtils;
import org.openmrs.mobile.utilities.ViewUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddEditVisitFragment extends ACBaseFragment<AddEditVisitContract.Presenter>
		implements AddEditVisitContract.View {

	private static TableRow.LayoutParams marginParams;
	private TableLayout visitTableLayout;
	private RelativeLayout progressBar, addEditVisitProgressBar;
	private LinearLayout addEditVisitScreen;
	private Spinner visitTypeDropdown;
	private Button visitSubmitButton;
	private Map<String, VisitAttribute> visitAttributeMap = new HashMap<>();
	private Map<View, VisitAttributeType> viewVisitAttributeTypeMap = new HashMap<>();
	private String patientUuid, visitUuid, providerUuid, visitStopDate;
	private EditText visitStartDateInput, visitEndDateInput;
	private TextView visitStartDateLabel, visitEndDateLabel;
	private TableRow visitTypeRow;

	public static AddEditVisitFragment newInstance() {
		return new AddEditVisitFragment();
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.fragment_add_edit_visit, container, false);
		visitTableLayout = (TableLayout)root.findViewById(R.id.visitTableLayout);
		progressBar = (RelativeLayout)root.findViewById(R.id.visitLoadingProgressBar);
		addEditVisitProgressBar = (RelativeLayout)root.findViewById(R.id.addEditVisitProgressBar);
		addEditVisitScreen = (LinearLayout)root.findViewById(R.id.addEditVisitScreen);
		visitTypeDropdown = (Spinner)root.findViewById(R.id.visit_type);
		visitSubmitButton = (Button)root.findViewById(R.id.visitSubmitButton);
		visitStartDateLabel = (TextView)root.findViewById(R.id.visitStartDateLabel);
		visitEndDateLabel = (TextView)root.findViewById(R.id.visitEndDateLabel);
		visitStartDateInput = (EditText)root.findViewById(R.id.visitStartDateInput);
		visitEndDateInput = (EditText)root.findViewById(R.id.visitEndDateInput);
		visitTypeRow = (TableRow)root.findViewById(R.id.visitTypeRow);

		this.patientUuid = getActivity().getIntent().getStringExtra(ApplicationConstants.BundleKeys.PATIENT_UUID_BUNDLE);
		this.visitUuid = getActivity().getIntent().getStringExtra(ApplicationConstants.BundleKeys.VISIT_UUID_BUNDLE);
		this.providerUuid = getActivity().getIntent().getStringExtra(ApplicationConstants.BundleKeys.PROVIDER_UUID_BUNDLE);
		this.visitStopDate = getActivity().getIntent().getStringExtra(ApplicationConstants.BundleKeys.VISIT_CLOSED_DATE);

		addListeners();
		buildMarginLayout();

		return root;
	}

	private void addListeners() {
		visitSubmitButton.setOnClickListener(v -> {
			if (!mPresenter.isProcessing()) {
				buildVisitAttributeValues();
			}
		});

		visitStartDateInput.setOnClickListener(v -> {
			DateTime dateTime = DateUtils.convertTimeString(
					DateUtils.convertTime(mPresenter.getVisit().getStartDatetime().getTime(),
							DateUtils.OPEN_MRS_REQUEST_FORMAT));

			createVisitDatePicker(dateTime, System.currentTimeMillis(), true);
		});

		visitEndDateInput.setOnClickListener(v -> {
			if (mPresenter.getVisit().getStopDatetime() != null) {
				DateTime dateTime = DateUtils.convertTimeString(
						DateUtils.convertTime(mPresenter.getVisit().getStopDatetime().getTime(),
								DateUtils.OPEN_MRS_REQUEST_FORMAT));

				createVisitDatePicker(dateTime, 0, false);
			}
		});
	}

	private void createVisitDatePicker(DateTime dateTime, long maxDate, boolean startDate) {
		int currentYear = dateTime.getYear();
		int currentMonth = dateTime.getMonthOfYear() - 1;
		int currentDay = dateTime.getDayOfMonth();

		DatePickerDialog mDatePicker = new DatePickerDialog(AddEditVisitFragment.this.getActivity(),
				(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) -> {
					if (startDate) {
						mPresenter.getVisit()
								.setStartDatetime(DateUtils.constructDate(selectedyear, selectedmonth, selectedday));
						visitStartDateInput.setText(DateUtils.convertTime(mPresenter.getVisit().getStartDatetime()
										.getTime(),
								DateUtils.OPEN_MRS_REQUEST_PATIENT_FORMAT));
					} else {
						mPresenter.getVisit()
								.setStopDatetime(DateUtils.constructDate(selectedyear, selectedmonth, selectedday));
						visitEndDateInput.setText(DateUtils.convertTime(mPresenter.getVisit().getStopDatetime().getTime(),
								DateUtils.OPEN_MRS_REQUEST_PATIENT_FORMAT));
					}
				}, currentYear, currentMonth, currentDay);

		if (maxDate > 0)
			mDatePicker.getDatePicker().setMaxDate(maxDate);

		mDatePicker.show();
	}

	@Override
	public void initView(boolean startVisit) {
		Toolbar toolbar = (Toolbar)getActivity().findViewById(R.id.toolbar);
		if (startVisit) {
			toolbar.setTitle(getString(R.string.label_start_visit));
		} else {
			visitSubmitButton.setText(R.string.update_visit);
			toolbar.setTitle(getString(R.string.label_edit_visit));
		}

		visitStartDateInput.setText(
				DateUtils.convertTime(mPresenter.getVisit().getStartDatetime().getTime(),
						DateUtils.OPEN_MRS_REQUEST_PATIENT_FORMAT));

		if (mPresenter.getVisit().getStopDatetime() != null) {
			visitEndDateLabel.setVisibility(View.VISIBLE);
			visitEndDateInput.setVisibility(View.VISIBLE);
			visitEndDateInput.setText(
					DateUtils.convertTime(mPresenter.getVisit().getStopDatetime().getTime(),
							DateUtils.OPEN_MRS_REQUEST_PATIENT_FORMAT));
			if (mPresenter.isEndVisit()) {
				visitStartDateInput.setVisibility(View.GONE);
				visitStartDateLabel.setVisibility(View.GONE);
				loadEndVisitView();
			}
		}

		setSpinnerVisibility(false);
	}

	@Override
	public void loadEndVisitView() {
		showPageSpinner(false);
		visitSubmitButton.setText(R.string.label_end_visit);
		visitTypeRow.setVisibility(View.GONE);

		visitSubmitButton.setOnClickListener(v -> {
			if (!mPresenter.isProcessing()) {
				mPresenter.endVisit();
			}
		});

	}

	private void buildVisitAttributeValues() {
		for (Map.Entry<View, VisitAttributeType> set : viewVisitAttributeTypeMap.entrySet()) {
			View componentType = set.getKey();
			VisitAttribute visitAttribute = new VisitAttribute();
			visitAttribute.setAttributeType(set.getValue());

			if (componentType instanceof RadioButton) {
				visitAttribute.setValue(((RadioButton)componentType).isChecked() ? "true" : "false");
			} else if (componentType instanceof EditText) {
				visitAttribute.setValue(ViewUtils.getInput((EditText)componentType));
			}

			if (visitAttribute.getValue() != null) {
				visitAttributeMap.put(set.getValue().getUuid(), visitAttribute);
			}
		}

		if (!mPresenter.isProcessing()) {
			setSpinnerVisibility(true);
			if (Resource.isLocalUuid(mPresenter.getVisit().getUuid())) {
				mPresenter.startVisit(new ArrayList<>(visitAttributeMap.values()));
			} else {
				mPresenter.updateVisit(new ArrayList<>(visitAttributeMap.values()));
			}
		}
	}

	@Override
	public void loadVisitAttributeTypeFields(List<VisitAttributeType> visitAttributeTypes) {
		for (VisitAttributeType visitAttributeType : visitAttributeTypes) {
			TableRow row = new TableRow(getContext());
			row.setPadding(0, 20, 0, 10);
			TextView label = new TextView(getContext());
			label.setText(visitAttributeType.getDisplay() + ":");
			label.setTextSize(17);
			label.setTextColor(getResources().getColor(R.color.dark_grey));
			row.addView(label, 0);

			String datatypeClass = visitAttributeType.getDatatypeClassname();
			if (StringUtils.isBlank(datatypeClass)) {
				continue;
			}

			if (datatypeClass.equalsIgnoreCase("org.openmrs.customdatatype.datatype.BooleanDatatype")) {
				RadioButton booleanType = new RadioButton(getContext());
				booleanType.setLayoutParams(marginParams);

				// set default value
				Boolean defaultValue = new Boolean(mPresenter.searchVisitAttributeValueByType(visitAttributeType));
				if (defaultValue != null) {
					booleanType.setChecked(defaultValue);
				}

				row.addView(booleanType, 1);
				viewVisitAttributeTypeMap.put(booleanType, visitAttributeType);
			} else if (datatypeClass.equalsIgnoreCase("org.openmrs.customdatatype.datatype.DateDatatype")) {
				EditText dateType = new EditText(getContext());
				dateType.setFocusable(true);
				dateType.setTextSize(17);
				dateType.setLayoutParams(marginParams);

				// set default value
				String defaultValue = mPresenter.searchVisitAttributeValueByType(visitAttributeType);
				if (StringUtils.notEmpty(defaultValue)) {
					dateType.setText(defaultValue);
				}
				row.addView(dateType, 1);
				viewVisitAttributeTypeMap.put(dateType, visitAttributeType);
			} else if (datatypeClass.equalsIgnoreCase("org.openmrs.module.coreapps.customdatatype.CodedConceptDatatype")) {
				// get coded concept uuid
				String conceptUuid = visitAttributeType.getDatatypeConfig();
				Spinner conceptAnswersDropdown = new Spinner(getContext());
				conceptAnswersDropdown.setLayoutParams(marginParams);
				mPresenter.getConceptAnswer(conceptUuid, conceptAnswersDropdown);
				row.addView(conceptAnswersDropdown, 1);
				viewVisitAttributeTypeMap.put(conceptAnswersDropdown, visitAttributeType);
			} else if (datatypeClass.equalsIgnoreCase("org.openmrs.customdatatype.datatype.FreeTextDatatype")) {
				EditText freeTextType = new EditText(getContext());
				freeTextType.setFocusable(true);
				freeTextType.setTextSize(17);
				freeTextType.setLayoutParams(marginParams);

				// set default value
				String defaultValue = mPresenter.searchVisitAttributeValueByType(visitAttributeType);
				if (StringUtils.notEmpty(defaultValue)) {
					freeTextType.setText(defaultValue);
				}

				row.addView(freeTextType, 1);
				viewVisitAttributeTypeMap.put(freeTextType, visitAttributeType);
			}

			visitTableLayout.addView(row);
		}
	}

	@Override
	public void updateConceptAnswersView(Spinner conceptNamesDropdown, List<ConceptAnswer> conceptAnswers) {
		VisitAttributeType visitAttributeType = viewVisitAttributeTypeMap.get(conceptNamesDropdown);
		ArrayAdapter<ConceptAnswer> conceptNameArrayAdapter = new ArrayAdapter<>(this.getActivity(),
				android.R.layout.simple_spinner_dropdown_item, conceptAnswers);
		conceptNamesDropdown.setAdapter(conceptNameArrayAdapter);

		// set existing visit attribute if any
		String visitTypeUuid = mPresenter.searchVisitAttributeValueByType(visitAttributeType);
		if (visitTypeUuid != null) {
			setDefaultDropdownSelection(conceptNameArrayAdapter, visitTypeUuid, conceptNamesDropdown);
		}

		conceptNamesDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				ConceptAnswer conceptAnswer = conceptAnswers.get(position);
				VisitAttribute visitAttribute = new VisitAttribute();
				visitAttribute.setValue(conceptAnswer.getUuid());
				visitAttribute.setAttributeType(visitAttributeType);
				removeVisitAttributeTypeInMap(visitAttributeType, visitAttributeMap);
				visitAttributeMap.put(conceptAnswer.getUuid(), visitAttribute);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
	}

	private void removeVisitAttributeTypeInMap(VisitAttributeType visitAttributeType,
			Map<String, VisitAttribute> visitAttributeMap) {
		if (visitAttributeMap == null || visitAttributeType == null) {
			return;
		}

		for (Map.Entry<String, VisitAttribute> set : visitAttributeMap.entrySet()) {
			if (set.getValue().getAttributeType().equals(visitAttributeType)) {
				visitAttributeMap.remove(set.getKey());
				break;
			}
		}
	}

	@Override
	public void updateVisitTypes(List<VisitType> visitTypes) {
		ArrayAdapter<VisitType> visitTypeArrayAdapter = new ArrayAdapter<>(this.getActivity(),
				android.R.layout.simple_spinner_dropdown_item, visitTypes);
		visitTypeDropdown.setAdapter(visitTypeArrayAdapter);

		// set existing visit type if any
		if (mPresenter.getVisit().getVisitType() != null) {
			setDefaultDropdownSelection(visitTypeArrayAdapter, mPresenter.getVisit().getVisitType().getUuid(),
					visitTypeDropdown);
		}

		visitTypeDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				mPresenter.getVisit().setVisitType(visitTypes.get(position));
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
	}

	@Override
	public void setSpinnerVisibility(boolean visible) {
		progressBar.setVisibility(visible ? View.VISIBLE : View.GONE);
	}

	@Override
	public boolean isActive() {
		return isAdded();
	}

	@Override
	public void showPatientDashboard() {
		getActivity().finish();
		Intent intent = new Intent(getContext(), PatientDashboardActivity.class);
		intent.putExtra(ApplicationConstants.BundleKeys.PATIENT_UUID_BUNDLE, patientUuid);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		getContext().startActivity(intent);
	}

	@Override
	public void showVisitDetails(String visitUUID, boolean isNewInstance) {
		visitSubmitButton.setEnabled(false);
		getActivity().finish();
		Intent intent = new Intent(getContext(), VisitActivity.class);
		intent.putExtra(ApplicationConstants.BundleKeys.PATIENT_UUID_BUNDLE, patientUuid);
		if (visitUUID == null) {
			intent.putExtra(ApplicationConstants.BundleKeys.VISIT_UUID_BUNDLE, visitUuid);
		} else {
			intent.putExtra(ApplicationConstants.BundleKeys.VISIT_UUID_BUNDLE, visitUUID);
			intent.putExtra(ApplicationConstants.BundleKeys.VISIT_CLOSED_DATE, visitStopDate);
		}
		getContext().startActivity(intent);
	}

	@Override
	public void showPageSpinner(boolean visibility) {
		if (visibility) {
			addEditVisitProgressBar.setVisibility(View.VISIBLE);
			addEditVisitScreen.setVisibility(View.GONE);
		} else {
			addEditVisitProgressBar.setVisibility(View.GONE);
			addEditVisitScreen.setVisibility(View.VISIBLE);
		}
	}

	private void buildMarginLayout() {
		if (marginParams == null) {
			marginParams = new TableRow.LayoutParams(
					TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT);
			marginParams.setMargins(70, 0, 0, 0);
		}
	}

	private <T extends BaseOpenmrsObject> void setDefaultDropdownSelection(ArrayAdapter<T> arrayAdapter, String searchUuid,
			Spinner dropdown) {
		for (int count = 0; count < arrayAdapter.getCount(); count++) {
			if (arrayAdapter.getItem(count).getUuid().equalsIgnoreCase(searchUuid)) {
				dropdown.setSelection(count);
			}
		}
	}
}
