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
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.BaseDiagnosisFragment;
import org.openmrs.mobile.activities.IBaseDiagnosisView;
import org.openmrs.mobile.activities.addeditpatient.AddEditPatientActivity;
import org.openmrs.mobile.activities.addeditvisit.AddEditVisitActivity;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.models.Encounter;
import org.openmrs.mobile.models.Location;
import org.openmrs.mobile.models.Observation;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.FontsUtil;
import org.openmrs.mobile.utilities.StringUtils;
import org.openmrs.mobile.utilities.ToastUtil;

import java.util.HashMap;
import java.util.List;

import static org.openmrs.mobile.utilities.ApplicationConstants.BundleKeys.LOCATION_UUID_BUNDLE;
import static org.openmrs.mobile.utilities.ApplicationConstants.BundleKeys.PATIENT_UUID_BUNDLE;

public class PatientDashboardFragment extends BaseDiagnosisFragment<PatientDashboardContract.Presenter>
		implements PatientDashboardContract.View {

	private FloatingActionButton startVisitButton, editPatient;
	private Patient patient;
	private OpenMRS openMRS = OpenMRS.getInstance();
	private Intent intent;
	private Location location;
	private RelativeLayout dashboardScreen, noPatientDataLayout;
	private ProgressBar dashboardProgressBar;
	private TextView noVisitNoteLabel, noPatientDataLabel;
	private String patientUuid;
	private PatientVisitsRecyclerAdapter patientVisitsRecyclerAdapter;
	private FloatingActionMenu patientDashboardMenu;
	private RecyclerView patientVisitsRecyclerView;
	private SwipeRefreshLayout patientVisitsSwipeRefreshView;

	public static PatientDashboardFragment newInstance() {
		return new PatientDashboardFragment();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		patientVisitsRecyclerView.removeOnScrollListener(new RecyclerView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
				super.onScrollStateChanged(recyclerView, newState);
			}

			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
				super.onScrolled(recyclerView, dx, dy);

				View patientContactInfo = recyclerView.findViewById(R.id.container_patient_address_info);
				if (patientContactInfo == null) {
					((PatientDashboardActivity)getActivity()).updateHeaderShadowLine(true);
				} else {
					((PatientDashboardActivity)getActivity()).updateHeaderShadowLine(false);
				}

				if (!mPresenter.isLoading()) {
					if (!recyclerView.canScrollVertically(1)) {
						// load next page
						mPresenter.loadResults(patient, true);
					}

					if (!recyclerView.canScrollVertically(-1) && dy < 0) {
						// load previous page
						mPresenter.loadResults(patient, false);
					}
				}
			}
		});
	}

	@Override
	public void navigateBack() {
		getActivity().onBackPressed();
	}

	@Override
	public void alertOfflineAndPatientNotFound() {
		ToastUtil.notify(getString(R.string.no_network_and_no_patient_data_in_database));
	}

	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View fragmentView = inflater.inflate(R.layout.fragment_patient_dashboard, container, false);

		initializeViewFields(fragmentView);
		initializeListeners(startVisitButton, editPatient);

		//set start index incase it's cached somewhere
		mPresenter.fetchPatientData(false);
		FontsUtil.setFont((ViewGroup)this.getActivity().findViewById(android.R.id.content));

		return fragmentView;
	}

	private void initializeListeners(FloatingActionButton... params) {
		for (FloatingActionButton patientActionButtons : params) {
			patientActionButtons.setOnClickListener(
					view -> startSelectedPatientDashboardActivity(patientActionButtons.getId()));
		}

		patientVisitsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
				super.onScrollStateChanged(recyclerView, newState);
			}

			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
				super.onScrolled(recyclerView, dx, dy);
				//Contact address header
				View patientContactInfo = recyclerView.findViewById(R.id.container_patient_address_info);
				if (patientContactInfo == null) {
					((PatientDashboardActivity)getActivity()).updateHeaderShadowLine(true);
				} else {
					((PatientDashboardActivity)getActivity()).updateHeaderShadowLine(false);
				}

				if (!mPresenter.isLoading()) {
					if (!recyclerView.canScrollVertically(1)) {
						// load next page
						mPresenter.loadResults(patient, true);
					}

					if (!recyclerView.canScrollVertically(-1) && dy < 0) {
						// load previous page
						mPresenter.loadResults(patient, false);
					}
				}
			}
		});

		patientVisitsSwipeRefreshView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

			@Override
			public void onRefresh() {
				mPresenter.dataRefreshWasRequested();
			}
		});
	}

	private void startSelectedPatientDashboardActivity(int selectedId) {
		patientDashboardMenu.close(true);
		switch (selectedId) {
			case R.id.start_visit:
				intent = new Intent(getContext(), AddEditVisitActivity.class);
				intent.putExtra(ApplicationConstants.BundleKeys.PATIENT_UUID_BUNDLE, patientUuid);
				startActivity(intent);
				break;
			case R.id.edit_Patient:
				intent = new Intent(getContext(), AddEditPatientActivity.class);
				intent.putExtra(ApplicationConstants.BundleKeys.PATIENT_UUID_BUNDLE, patientUuid);
				startActivity(intent);
				break;
		}
	}

	@Override
	public void initializeViewFields(View fragmentView) {
		startVisitButton = (FloatingActionButton)fragmentView.findViewById(R.id.start_visit);
		editPatient = (FloatingActionButton)fragmentView.findViewById(R.id.edit_Patient);
		dashboardScreen = (RelativeLayout)fragmentView.findViewById(R.id.dashboardScreen);
		dashboardProgressBar = (ProgressBar)fragmentView.findViewById(R.id.dashboardProgressBar);
		noVisitNoteLabel = (TextView)fragmentView.findViewById(R.id.noVisitNoteLabel);
		patientDashboardMenu = (FloatingActionMenu)fragmentView.findViewById(R.id.patientDashboardMenu);
		patientDashboardMenu.setClosedOnTouchOutside(true);
		patientVisitsRecyclerView = (RecyclerView)fragmentView.findViewById(R.id.patientVisitsRecyclerView);
		noPatientDataLabel = (TextView)fragmentView.findViewById(R.id.noPatientDataLabel);
		noPatientDataLayout = (RelativeLayout)fragmentView.findViewById(R.id.noPatientDataLayout);
		patientVisitsSwipeRefreshView = (SwipeRefreshLayout)fragmentView.findViewById(R.id.patientVisitsSwipeRefreshView);
	}

	@Override
	public void patientContacts(Patient patient) {
		this.patient = patient;
		openMRS.setPatientUuid(patient.getPerson().getUuid());
	}

	@Override
	public void patientVisits(List<Visit> visits) {
		//hasActiveVisit = false;
		for (Visit visit : visits) {
			if (visit.getStopDatetime() == null) {
				//hasActiveVisit = true;
				startVisitButton.setVisibility(View.GONE);
				openMRS.setVisitUuid(visit.getUuid());
				break;
			}
		}

		HashMap<String, String> uuidsHashmap = new HashMap<>();
		uuidsHashmap.put(PATIENT_UUID_BUNDLE, patient == null ? "" : patient.getUuid());
		uuidsHashmap.put(LOCATION_UUID_BUNDLE, location == null ? "" : location.getUuid());

		if (patientVisitsRecyclerAdapter != null) {
			patientVisitsRecyclerAdapter.destroy();
		}
		patientVisitsRecyclerAdapter =
				new PatientVisitsRecyclerAdapter(patientVisitsRecyclerView, visits, getActivity(), this);
		patientVisitsRecyclerAdapter.setUuids(uuidsHashmap);
		patientVisitsRecyclerView.setAdapter(patientVisitsRecyclerAdapter);

		patientVisitsSwipeRefreshView.setRefreshing(false);
	}

	@Override
	public Patient getPatient() {
		return patient;
	}

	@Override
	public void setProviderUuid(String providerUuid) {
		if (StringUtils.isBlank(providerUuid))
			return;
		SharedPreferences.Editor editor = openMRS.getPreferences().edit();
		editor.putString(ApplicationConstants.BundleKeys.PROVIDER_UUID_BUNDLE, providerUuid);
		editor.commit();
	}

	@Override
	public void setLocation(Location location) {
		this.location = location;
	}

	@Override
	public void showSavingClinicalNoteProgressBar(boolean show) {
		patientVisitsRecyclerAdapter.updateSavingClinicalNoteProgressBar(show);
	}

	@Override
	public void showPageSpinner(boolean visibility) {
		if (visibility) {
			dashboardProgressBar.setVisibility(View.VISIBLE);
			dashboardScreen.setVisibility(View.GONE);
		} else {
			dashboardProgressBar.setVisibility(View.GONE);
			dashboardScreen.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void showNoVisits(boolean visibility) {
		if (visibility) {
			noVisitNoteLabel.setVisibility(View.VISIBLE);
		} else {
			noVisitNoteLabel.setVisibility(View.GONE);
		}
	}

	@Override
	public void updateClinicVisitNote(Observation observation, Encounter encounter) {
		patientVisitsRecyclerAdapter.updateClinicalNoteObs(observation, encounter);
	}

	@Override
	public void showNoPatientData(boolean visible) {
		noPatientDataLayout.setVisibility(visible ? View.VISIBLE : View.GONE);
		dashboardProgressBar.setVisibility(visible ? View.GONE : View.VISIBLE);
		dashboardScreen.setVisibility(visible ? View.GONE : View.VISIBLE);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		openMRS.setPatientUuid(ApplicationConstants.EMPTY_STRING);
	}

	@Override
	public IBaseDiagnosisView getDiagnosisView() {
		return this;
	}

	@Override
	public IBaseDiagnosisView getBaseDiagnosisView() {
		return this;
	}

	@Override
	public void showTabSpinner(boolean show) {
		showSavingClinicalNoteProgressBar(show);
	}
}
