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
package org.openmrs.mobile.activities.visit.detail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.flexbox.FlexboxLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.BaseDiagnosisFragment;
import org.openmrs.mobile.activities.IBaseDiagnosisView;
import org.openmrs.mobile.activities.auditdata.AuditDataActivity;
import org.openmrs.mobile.activities.capturevitals.CaptureVitalsActivity;
import org.openmrs.mobile.activities.dialog.CustomFragmentDialog;
import org.openmrs.mobile.activities.visit.VisitActivity;
import org.openmrs.mobile.activities.visit.VisitContract;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.bundle.CustomDialogBundle;
import org.openmrs.mobile.event.VisitDashboardDataRefreshEvent;
import org.openmrs.mobile.models.Concept;
import org.openmrs.mobile.models.Encounter;
import org.openmrs.mobile.models.Observation;
import org.openmrs.mobile.models.Provider;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.models.VisitAttribute;
import org.openmrs.mobile.models.VisitAttributeType;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.DateUtils;
import org.openmrs.mobile.utilities.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VisitDetailsFragment extends BaseDiagnosisFragment<VisitContract.VisitDashboardPagePresenter>
		implements VisitContract.VisitDetailsView {

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(DateUtils.PATIENT_DASHBOARD_VISIT_DATE_FORMAT);

	private TextView visitStartDate;
	private TextView activeVisitBadge;
	private TextView startDuration;
	private TextView visitDuration;
	private TextView visitEndDate;
	private TextView visitType, noVitals, noAuditData, visitNoteProvider,
			visitNoteDate, visitVitalsProvider, visitVitalsDate, auditDataCompleteness, auditDataMetadataProvider,
			auditDataMetadataDate;
	private Visit visit;
	private TableLayout visitVitalsTableLayout, auditInfoTableLayout;
	private LinearLayoutManager primaryDiagnosisLayoutManager, secondaryDiagnosisLayoutManager;
	private ImageButton addAuditData, addVisitVitals;
	private String patientUuid;
	private String visitUuid;
	private String providerUuid, visitStopDate;
	private Intent intent;
	private FlexboxLayout visitAttributesLayout;
	private RelativeLayout visitNoteAuditInfo, visitVitalsAuditInfo, auditDataMetadata, visitDetailsProgressBar;
	private ScrollView visitDetailsScrollView;
	private SwipeRefreshLayout visitDetailsSwipeRefreshLayout;

	private Map<String, Integer> auditDataSortOrder;

	private EventBus eventBus;

	public static VisitDetailsFragment newInstance() {
		return new VisitDetailsFragment();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setPresenter(mPresenter);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.fragment_visit_details, container, false);
		initializeViewFields(root);
		initializeAuditDataSortOrder();
		addListeners();
		primaryDiagnosisLayoutManager = new LinearLayoutManager(this.getActivity());
		secondaryDiagnosisLayoutManager = new LinearLayoutManager(this.getActivity());

		primaryDiagnosesRecycler.setLayoutManager(primaryDiagnosisLayoutManager);
		secondaryDiagnosesRecycler.setLayoutManager(secondaryDiagnosisLayoutManager);

		((VisitDetailsPresenter)mPresenter).getVisit();
		((VisitDetailsPresenter)mPresenter).getPatientUUID();
		((VisitDetailsPresenter)mPresenter).getVisitUUID();
		((VisitDetailsPresenter)mPresenter).getProviderUUID();
		//buildMarginLayout();
		initializeListeners();
		return root;
	}

	@Override
	public void onStart() {
		super.onStart();
		OpenMRS.getInstance().getEventBus().register(this);
	}

	@Override
	public void onStop() {
		OpenMRS.getInstance().getEventBus().unregister(this);
		super.onStop();
	}

	private void initializeAuditDataSortOrder() {
		// This matches what is coded on the Fragment XML
		// TODO
		auditDataSortOrder = new HashMap<>();
		auditDataSortOrder.put(ApplicationConstants.ObservationLocators.TYPE_OF_INPATIENT_SERVICE, 0);
		auditDataSortOrder.put(ApplicationConstants.ObservationLocators.HOSPITAL_DEATH, 1);
		auditDataSortOrder.put(ApplicationConstants.ObservationLocators.PREOP_RISK_ASSESSMENT, 2);
		auditDataSortOrder.put(ApplicationConstants.ObservationLocators.PALLIATIVE_CARE_CONSULT, 3);
		auditDataSortOrder.put(ApplicationConstants.ObservationLocators.HIV_INFECTED, 4);
		auditDataSortOrder.put(ApplicationConstants.ObservationLocators.CD4_COUNT, 5);
		auditDataSortOrder.put(ApplicationConstants.ObservationLocators.PATIENT_DIABETIC, 6);
		auditDataSortOrder.put(ApplicationConstants.ObservationLocators.GLYCOSYLATED_HEMOGLOBIN, 7);
		auditDataSortOrder.put(ApplicationConstants.ObservationLocators.WARD_STAY_DURING_ADMISSION, 8);
		auditDataSortOrder.put(ApplicationConstants.ObservationLocators.HDU_STAY_DURING_ADMISSION, 9);
		auditDataSortOrder.put(ApplicationConstants.ObservationLocators.HDU_COMGMT, 10);
		auditDataSortOrder.put(ApplicationConstants.ObservationLocators.ICU_STAY_DURING_ADMISSION, 11);
		auditDataSortOrder.put(ApplicationConstants.ObservationLocators.MECHANICAL_VENTILATION, 12);
		auditDataSortOrder.put(ApplicationConstants.ObservationLocators.RECEIVED_VAOSPRESSORS, 13);
		auditDataSortOrder.put(ApplicationConstants.ObservationLocators.INFECTION_CONFIRMED_SUSPECTED, 14);
		auditDataSortOrder.put(ApplicationConstants.ObservationLocators.FIRST_SBP_ICU, 15);
		auditDataSortOrder.put(ApplicationConstants.ObservationLocators.FIRST_MAP, 16);
		auditDataSortOrder.put(ApplicationConstants.ObservationLocators.SURGERY_DURING_HOSPITAL_STAY, 17);
		auditDataSortOrder.put(ApplicationConstants.ObservationLocators.FIRST_HEART_RATE, 18);
		auditDataSortOrder.put(ApplicationConstants.ObservationLocators.FIRST_RESPIRATORY_RATE, 19);
		auditDataSortOrder.put(ApplicationConstants.ObservationLocators.FIRST_GCS_SCORE, 20);
		auditDataSortOrder.put(ApplicationConstants.ObservationLocators.INTUBATION_ON_FIRST_GCS, 21);
		auditDataSortOrder.put(ApplicationConstants.ObservationLocators.SEDETION_PRIOR_FIRST_GCS_SCORE_ICU, 22);
		auditDataSortOrder.put(ApplicationConstants.ObservationLocators.AUDIT_DATA_COMPLETE, 23);
	}

	@Override
	public void initializeViewFields(View v) {
		visitStartDate = (TextView)v.findViewById(R.id.visitStartDate);
		visitType = (TextView)v.findViewById(R.id.visitType);
		noVitals = (TextView)v.findViewById(R.id.noVitals);
		visitVitalsTableLayout = (TableLayout)v.findViewById(R.id.visitVitalsTable);
		auditInfoTableLayout = (TableLayout)v.findViewById(R.id.auditInfoTable);
		visitDetailsSwipeRefreshLayout = (SwipeRefreshLayout)v.findViewById(R.id.visitDetailsTab);

		// diagnoses components
		searchDiagnosis = (AutoCompleteTextView)v.findViewById(R.id.searchDiagnosis);
		noPrimaryDiagnoses = (TextView)v.findViewById(R.id.noPrimaryDiagnosis);
		noSecondaryDiagnoses = (TextView)v.findViewById(R.id.noSecondaryDiagnosis);
		primaryDiagnosesRecycler = (RecyclerView)v.findViewById(R.id.primaryDiagnosisRecyclerView);
		secondaryDiagnosesRecycler = (RecyclerView)v.findViewById(R.id.secondaryDiagnosisRecyclerView);
		clinicalNoteView = (TextInputEditText)v.findViewById(R.id.clinicalNotes);

		noAuditData = (TextView)v.findViewById(R.id.noAuditInfo);
		activeVisitBadge = (TextView)v.findViewById(R.id.activeVisitBadge);
		visitEndDate = (TextView)v.findViewById(R.id.visitEndDate);
		visitDuration = (TextView)v.findViewById(R.id.visitDuration);
		startDuration = (TextView)v.findViewById(R.id.startDuration);
		addVisitVitals = (ImageButton)v.findViewById(R.id.visitVitalsAdd);
		addAuditData = (ImageButton)v.findViewById(R.id.visitAuditInfoAdd);
		visitAttributesLayout = (FlexboxLayout)v.findViewById(R.id.visitAttributesLayout);
		visitNoteProvider = (TextView)v.findViewById(R.id.visitNoteProvider);
		visitNoteDate = (TextView)v.findViewById(R.id.visitNoteDate);
		visitVitalsDate = (TextView)v.findViewById(R.id.visitVitalsDate);
		visitVitalsProvider = (TextView)v.findViewById(R.id.visitVitalsProvider);
		auditDataCompleteness = (TextView)v.findViewById(R.id.auditDataCompleteness);
		auditDataMetadataProvider = (TextView)v.findViewById(R.id.auditDataMetadataProvider);
		auditDataMetadataDate = (TextView)v.findViewById(R.id.auditDataMetadataDate);

		visitNoteAuditInfo = (RelativeLayout)v.findViewById(R.id.visitNoteAuditInfo);
		visitVitalsAuditInfo = (RelativeLayout)v.findViewById(R.id.visitVitalsAuditInfo);
		auditDataMetadata = (RelativeLayout)v.findViewById(R.id.auditDataMetadata);

		visitDetailsProgressBar = (RelativeLayout)v.findViewById(R.id.visitDetailsTabProgressBar);
		visitDetailsScrollView = (ScrollView)v.findViewById(R.id.visitDetailsTabScrollView);
		setLoadingProgressBar((RelativeLayout)v.findViewById(R.id.loadingDiagnoses));
		setDiagnosesContent((LinearLayout)v.findViewById(R.id.diagnosesContent));
	}

	@Override
	public void setVisit(Visit visit) {
		this.visit = visit;
		visitDetailsSwipeRefreshLayout.setRefreshing(false);
		if (visit != null) {
			OpenMRS.getInstance().setVisitUuid(visit.getUuid());
			setVisitDates(visit);
			setVisitType(visit);
			setClinicalNote(visit);
			setDiagnoses(visit);
			setAuditData(visit);
		}
	}

	@Override
	public void setPatientUUID(String uuid) {
		this.patientUuid = uuid;
	}

	@Override
	public void setVisitUUID(String uuid) {
		this.visitUuid = uuid;
	}

	@Override
	public void setProviderUUID(String uuid) {
		this.providerUuid = uuid;
	}

	@Override
	public void setVisitStopDate(String visitStopDate) {
		this.visitStopDate = visitStopDate;
	}

	@Override
	public void setConcept(Concept concept) {
	}

	private void addListeners() {
		addAuditData.setOnClickListener(
				v -> {
					intent = new Intent(getContext(), AuditDataActivity.class);
					intent.putExtra(ApplicationConstants.BundleKeys.PATIENT_UUID_BUNDLE, patientUuid);
					intent.putExtra(ApplicationConstants.BundleKeys.VISIT_UUID_BUNDLE, visitUuid);
					intent.putExtra(ApplicationConstants.BundleKeys.PROVIDER_UUID_BUNDLE, providerUuid);
					intent.putExtra(ApplicationConstants.BundleKeys.VISIT_CLOSED_DATE, visitStopDate);
					startActivity(intent);
				});

		addVisitVitals.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				intent = new Intent(getContext(), CaptureVitalsActivity.class);
				intent.putExtra(ApplicationConstants.BundleKeys.PATIENT_UUID_BUNDLE, patientUuid);
				intent.putExtra(ApplicationConstants.BundleKeys.VISIT_UUID_BUNDLE, visitUuid);
				intent.putExtra(ApplicationConstants.BundleKeys.PROVIDER_UUID_BUNDLE, providerUuid);
				intent.putExtra(ApplicationConstants.BundleKeys.VISIT_CLOSED_DATE, visitStopDate);
				startActivity(intent);
			}
		});

		auditDataCompleteness.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				intent = new Intent(getContext(), AuditDataActivity.class);
				intent.putExtra(ApplicationConstants.BundleKeys.PATIENT_UUID_BUNDLE, patientUuid);
				intent.putExtra(ApplicationConstants.BundleKeys.VISIT_UUID_BUNDLE, visitUuid);
				intent.putExtra(ApplicationConstants.BundleKeys.PROVIDER_UUID_BUNDLE, providerUuid);
				intent.putExtra(ApplicationConstants.BundleKeys.VISIT_CLOSED_DATE, visitStopDate);
				startActivity(intent);
			}
		});

		visitDetailsSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

			@Override
			public void onRefresh() {
				mPresenter.dataRefreshWasRequested();
			}
		});
	}

	public void setVisitDates(Visit visit) {
		if (visit.getStopDatetime() != null) {
			activeVisitBadge.setVisibility(View.GONE);
			visitStartDate.setText(DATE_FORMAT.format(visit.getStartDatetime()));
			visitEndDate.setText(getContext().getResources().getString(R.string.date_closed) + ": " + DATE_FORMAT
					.format(visit.getStopDatetime()));
			startDuration.setText(DateUtils.calculateTimeDifference(visit.getStartDatetime()));
			visitDuration.setText(getContext().getString(R.string.visit_duration,
					DateUtils.calculateTimeDifference(visit.getStartDatetime(), visit.getStopDatetime())));
		} else {
			activeVisitBadge.setVisibility(View.VISIBLE);
			visitEndDate.setVisibility(View.GONE);
			visitDuration.setVisibility(View.GONE);
			visitStartDate.setText(DATE_FORMAT.format(visit.getStartDatetime()));
			startDuration.setText(DateUtils.calculateTimeDifference(visit.getStartDatetime()));
		}
	}

	public void setVisitType(Visit visit) {
		if (visit.getVisitType() != null) {
			visitType.setText(visit.getVisitType().getDisplay());
		} else {
			visitType.setText(ApplicationConstants.EMPTY_STRING);
		}
	}

	@Override
	public void setAttributeTypes(List<VisitAttributeType> visitAttributeTypes) {
		visitAttributesLayout.removeAllViews();
		if (visit.getAttributes().size() == 0) {
			for (VisitAttributeType visitAttributeType : visitAttributeTypes) {
				createVisitAttributeTypesLayout(visitAttributeType);
			}
		} else {
			for (VisitAttribute visitAttribute : visit.getAttributes()) {
				if (!visitAttribute.getVoided()) {
					loadVisitAttributeType(visitAttribute, visitAttributeTypes);
					createVisitAttributeLayout(visitAttribute);
				}
			}
		}
	}

	@Override
	public void showTabSpinner(boolean visibility) {
		if (visibility) {
			visitDetailsProgressBar.setVisibility(View.VISIBLE);
			visitDetailsScrollView.setVisibility(View.GONE);
		} else {
			visitDetailsProgressBar.setVisibility(View.GONE);
			visitDetailsScrollView.setVisibility(View.VISIBLE);
		}
	}

	private void createVisitAttributeTypesLayout(VisitAttributeType visitAttributeType) {
		LinearLayout linearLayout = new LinearLayout(getContext());
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		linearLayout.setLayoutParams(params);
		linearLayout.setOrientation(LinearLayout.HORIZONTAL);

		String valueLabel = String.valueOf(ApplicationConstants.EMPTY_STRING);
		TextView nameLabelView = new TextView(getContext());
		nameLabelView.setPadding(0, 10, 10, 10);
		nameLabelView.setText(visitAttributeType.getDisplay() + ":");
		linearLayout.addView(nameLabelView);

		TextView valueLabelView = new TextView(getContext());
		valueLabelView.setPadding(20, 10, 10, 10);
		valueLabelView.setText(valueLabel);

		linearLayout.addView(valueLabelView);

		visitAttributesLayout.addView(linearLayout);
	}

	private void createVisitAttributeLayout(VisitAttribute visitAttribute) {
		LinearLayout linearLayout = new LinearLayout(getContext());
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		linearLayout.setLayoutParams(params);
		linearLayout.setOrientation(LinearLayout.HORIZONTAL);

		String valueLabel = String.valueOf(visitAttribute.getValue());
		TextView nameLabelView = new TextView(getContext());
		nameLabelView.setPadding(0, 10, 10, 10);
		nameLabelView.setText(visitAttribute.getAttributeType().getDisplay() + ":");
		linearLayout.addView(nameLabelView);

		TextView valueLabelView = new TextView(getContext());
		valueLabelView.setPadding(20, 10, 10, 10);

		if (visitAttribute.getAttributeType().getDatatypeConfig() != null) {
			((VisitDetailsPresenter)mPresenter).getConceptAnswer(visitAttribute.getAttributeType().getDatatypeConfig(),
					(String)visitAttribute.getValue(), valueLabelView);
		} else {
			valueLabelView.setText(valueLabel);
		}

		linearLayout.addView(valueLabelView);

		visitAttributesLayout.addView(linearLayout);
	}

	private void loadVisitAttributeType(VisitAttribute visitAttribute, List<VisitAttributeType> attributeTypes) {
		for (VisitAttributeType type : attributeTypes) {
			if (visitAttribute.getAttributeType() != null) {
				if (type.getUuid().equalsIgnoreCase(visitAttribute.getAttributeType().getUuid())) {
					visitAttribute.setAttributeType(type);
					break;
				}
			} else {
				String[] visitAttributeTypeAndValue = visitAttribute.getDisplay().split(": ");
				if (visitAttributeTypeAndValue.length > 1 &&
						type.getDisplay().contains(visitAttributeTypeAndValue[0])) {
					visitAttribute.setAttributeType(type);
					visitAttribute.setValue(visitAttributeTypeAndValue[1]);
					break;
				}
			}
		}
	}

	public void setVitals(Visit visit) {
		if (visit.getEncounters().size() > 0) {
			for (int i = 0; i < visit.getEncounters().size(); i++) {
				if (visit.getEncounters().get(i).getDisplay().contains(ApplicationConstants.EncounterTypeDisplays.VITALS)) {
					if (visit.getEncounters().get(i).getEncounterType().getUuid()
							.equalsIgnoreCase(ApplicationConstants.EncounterTypeEntity.VITALS_UUID)) {

						if (visit.getEncounters().get(i).getObs().size() != 0) {
							visitVitalsAuditInfo.setVisibility(View.VISIBLE);
							visitVitalsDate.setText(DATE_FORMAT.format(
									visit.getEncounters().get(i).getEncounterDatetime()));

							for (int v = 0; v < visit.getEncounters().get(i).getEncounterProviders().size(); v++) {
								if (v == 0) {

									ArrayList names = StringUtils.splitStrings(
											visit.getEncounters().get(i).getEncounterProviders().get(v).getDisplay(), ":");
									visitVitalsProvider.setText(names.get(0).toString());
								}
							}
							noVitals.setVisibility(View.GONE);
							addVisitVitals.setVisibility(View.GONE);
							visitVitalsTableLayout.setVisibility(View.VISIBLE);
							visitVitalsTableLayout.removeAllViews();
							loadObservationFields(visit.getEncounters().get(i).getObs(), EncounterTypeData.VITALS);
						} else {
							if (visit.getStopDatetime() == null) {
								noVitals.setVisibility(View.VISIBLE);
								addVisitVitals.setVisibility(View.VISIBLE);
								visitVitalsTableLayout.setVisibility(View.GONE);
							}
						}
					}
					break;
				}
			}
		} else {
			addVisitVitals.setVisibility(visit.getStopDatetime() == null ? View.VISIBLE : View.GONE);
			noVitals.setVisibility(View.VISIBLE);
			visitVitalsTableLayout.setVisibility(View.GONE);
		}
	}

	public void setAuditData(Visit visit) {
		if (visit.getEncounters().size() > 0) {
			for (Encounter encounter : visit.getEncounters()) {
				if (encounter.getVoided() != null && encounter.getVoided()) {
					continue;
				}

				if ((encounter.getEncounterType().getUuid().equalsIgnoreCase(
						ApplicationConstants.EncounterTypeEntity.AUDIT_DATA_UUID) ||
						encounter.getEncounterType().getDisplay().equalsIgnoreCase(
								ApplicationConstants.EncounterTypeDisplays.AUDITDATA))) {

					if (!encounter.getObs().isEmpty()) {
						auditDataMetadata.setVisibility(View.VISIBLE);
						auditDataMetadataDate.setText(
								DATE_FORMAT.format(encounter.getEncounterDatetime()));

						for (Provider provider : encounter.getEncounterProviders()) {
							ArrayList names = StringUtils.splitStrings(provider.getDisplay(), ":");
							auditDataMetadataProvider.setText(names.get(0).toString());
							break;
						}
					}

					noAuditData.setVisibility(View.GONE);
					addAuditData.setVisibility(View.GONE);
					auditInfoTableLayout.setVisibility(View.VISIBLE);
					auditInfoTableLayout.removeAllViews();
					loadObservationFields(encounter.getObs(), EncounterTypeData.AUDIT_DATA);

				}
			}
		}
	}

	public void setClinicalNote(Visit visit) {
		if (visit.getEncounters().size() != 0) {
			for (int i = 0; i < visit.getEncounters().size(); i++) {
				Encounter encounter = visit.getEncounters().get(i);
				if (encounter.getVoided() != null && encounter.getVoided())
					continue;

				if (encounter.getEncounterType().getDisplay()
						.equalsIgnoreCase(ApplicationConstants.EncounterTypeDisplays.VISIT_NOTE)) {
					if (visit.getEncounters().get(i).getObs().size() != 0) {
						setEncounter(encounter);
						visitNoteAuditInfo.setVisibility(View.VISIBLE);
						visitNoteDate
								.setText(DateUtils.convertTime(visit.getEncounters().get(i).getEncounterDatetime()
										.getTime(), DateUtils.PATIENT_DASHBOARD_VISIT_DATE_FORMAT));

						for (int v = 0; v < visit.getEncounters().get(i).getEncounterProviders().size(); v++) {
							if (v == 0) {

								ArrayList names = StringUtils.splitStrings(
										visit.getEncounters().get(i).getEncounterProviders().get(v).getDisplay(), ":");
								visitNoteProvider.setText(names.get(0).toString());
							}
						}
					}

					for (Observation obs : encounter.getObs()) {
						ArrayList<String> clinicalNote = StringUtils.splitStrings(obs.getDisplay(),
								ApplicationConstants.ObservationLocators.CLINICAL_NOTE);
						if (clinicalNote.size() > 1) {
							initialClinicNoteHashcode = clinicalNote.get(1).hashCode();
							clinicalNoteView.setText(clinicalNote.get(1));
						}
					}
				}
			}
		}
	}

	public void loadObservationFields(List<Observation> observations, EncounterTypeData type) {
		List<TableRow> rowsToDisplay = new ArrayList<>();

		for (Observation observation : observations) {
			TableRow row = new TableRow(getContext());
			row.setPadding(0, 5, 0, 5);
			row.setGravity(Gravity.CENTER);

			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			row.setLayoutParams(params);

			ArrayList splitValues = StringUtils.splitStrings(observation.getDisplay(), ":");

			TextView label = new TextView(getContext());
			TableRow.LayoutParams labelParams = new TableRow.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
			labelParams.weight = 1;

			label.setTextSize(14);
			if (type == EncounterTypeData.VITALS) {
				label.setText(splitValues.get(0) + " :");
				label.setGravity(Gravity.RIGHT | Gravity.END);
			} else {
				label.setText(splitValues.get(0).toString());
				label.setGravity(Gravity.LEFT | Gravity.START);
			}
			label.setLayoutParams(labelParams);
			label.setTextColor(getResources().getColor(R.color.black));
			row.addView(label, 0);

			TextView value = new TextView(getContext());
			value.setTextSize(14);
			if (type == EncounterTypeData.VITALS) {
				value.setText(splitValues.get(1).toString());
			} else {
				value.setText(": " + splitValues.get(1).toString());
			}
			value.setLayoutParams(labelParams);
			row.addView(value, 1);

			rowsToDisplay.add(row);

			if (type == EncounterTypeData.AUDIT_DATA &&
					observation.getDisplay().contains(ApplicationConstants.EncounterTypeDisplays.AUDIT_DATA_COMPLETENESS)
					&& (observation.getDisplay().contains("No") || observation.getDisplay() == null)) {
				auditDataCompleteness.setVisibility(View.VISIBLE);
			}
		}

		TableLayout layoutThatWillDisplayRows = visitVitalsTableLayout;
		if (type == EncounterTypeData.AUDIT_DATA) {
			layoutThatWillDisplayRows = auditInfoTableLayout;

			// Sort rows according to hard-coded sort order (matching what is on the fragment XML)
			Collections.sort(rowsToDisplay, new Comparator<TableRow>() {

				@Override
				public int compare(TableRow o1, TableRow o2) {
					return getObservationSortOrderFromTableRow(o1) - getObservationSortOrderFromTableRow(o2);
				}

				private int getObservationSortOrderFromTableRow(TableRow tableRow) {
					String labelMatchingConstants = ((TextView) tableRow.getVirtualChildAt(0)).getText() + ": ";
					if (!auditDataSortOrder.containsKey(labelMatchingConstants)) {
						return 0;
					}
					return auditDataSortOrder.get(labelMatchingConstants);
				}
			});
		}

		for (TableRow rowToDisplay : rowsToDisplay) {
			layoutThatWillDisplayRows.addView(rowToDisplay);
		}
	}

	@Override
	public VisitContract.VisitDetailsView getDiagnosisView() {
		return this;
	}

	@Override
	public void onPause() {
		super.onPause();
		/*if (changesMade || (initialPrimaryDiagnosesListHashcode != subsequentPrimaryDiagnosesListHashcode) ||
				(initialSecondaryDiagnosesListHashcode != subsequentSecondaryDiagnosesListHashcode)) {
			showPendingVisitNoteChangesDialog();
		}*/
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		OpenMRS.getInstance().setVisitUuid(ApplicationConstants.EMPTY_STRING);
	}

	private void showPendingVisitNoteChangesDialog() {
		CustomDialogBundle bundle = new CustomDialogBundle();
		bundle.setTitleViewMessage(getString(R.string.visit_note_changes_pending_title));
		bundle.setTextViewMessage(getString(R.string.visit_note_changes_pending_message));
		bundle.setRightButtonAction(CustomFragmentDialog.OnClickAction.END_VISIT);
		bundle.setRightButtonText(getString(R.string.dialog_button_confirm));
		((VisitActivity)this.getActivity())
				.createAndShowDialog(bundle, ApplicationConstants.DialogTAG.PENDING_VISIT_NOTE_CHANGES_TAG);
	}

	@Override
	public IBaseDiagnosisView getBaseDiagnosisView() {
		return this;
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		// Make sure that we are currently visible
		if (this.isVisible()) {
			// If we are becoming invisible, then...
			if (!isVisibleToUser) {
				try {
					InputMethodManager inputMethodManager =
							(InputMethodManager)this.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
					inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
				} catch (Exception e) {

				}
			}
		}
	}

	@Override
	public void displayRefreshingData(boolean visible) {
		visitDetailsSwipeRefreshLayout.setRefreshing(visible);
	}

	@Override
	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onVisitDashboardRefreshEvent(VisitDashboardDataRefreshEvent event) {
		mPresenter.dataRefreshEventOccurred(event);
	}
}
