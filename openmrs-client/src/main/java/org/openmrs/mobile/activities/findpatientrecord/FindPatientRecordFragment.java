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

package org.openmrs.mobile.activities.findpatientrecord;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseFragment;
import org.openmrs.mobile.activities.addeditpatient.AddEditPatientActivity;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.net.AuthorizationManager;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.FontsUtil;
import org.openmrs.mobile.utilities.ToastUtil;

import java.util.List;

public class FindPatientRecordFragment extends ACBaseFragment<FindPatientRecordContract.Presenter>
		implements FindPatientRecordContract.View {

	private View mRootView;
	private RecyclerView findPatientRecyclerView;
	private TextView noPatientFound, numberOfFetchedPatients, searchForPatient, patientSearchTitle, noPatientFoundTitle,
			searchQuery;
	private LinearLayoutManager layoutManager;
	private RelativeLayout findPatientProgressBar, foundPatientsLayout;
	private LinearLayout findPatientLayout, noPatientsFoundLayout, patientListLayout;
	private OpenMRS openMRS = OpenMRS.getInstance();
	private AuthorizationManager authorizationManager;

	private RecyclerView.OnScrollListener recyclerViewOnScrollListener = new RecyclerView.OnScrollListener() {

		@Override
		public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
			super.onScrollStateChanged(recyclerView, newState);
		}

		@Override
		public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
			super.onScrolled(recyclerView, dx, dy);
			if (!mPresenter.isLoading()) {
				if (!recyclerView.canScrollVertically(1)) {
					// load next page
					mPresenter.loadResults(true);
				}

				if (!recyclerView.canScrollVertically(-1) && dy < 0) {
					// load previous page
					mPresenter.loadResults(false);
				}
			}
		}
	};

	public static FindPatientRecordFragment newInstance() {
		return new FindPatientRecordFragment();
	}

	private void resolveViews(View v) {
		noPatientFound = (TextView)v.findViewById(R.id.noPatientsFound);
		findPatientRecyclerView = (RecyclerView)v.findViewById(R.id.findPatientModelRecyclerView);

		findPatientProgressBar = (RelativeLayout)v.findViewById(R.id.findPatientLoadingProgressBar);
		numberOfFetchedPatients = (TextView)v.findViewById(R.id.numberOfFetchedPatients);
		searchQuery = (TextView)v.findViewById(R.id.searchQuery);
		noPatientsFoundLayout = (LinearLayout)v.findViewById(R.id.noPatientsFoundLayout);
		foundPatientsLayout = (RelativeLayout)v.findViewById(R.id.resultsLayout);
		patientListLayout = (LinearLayout)v.findViewById(R.id.patientsCardViewLayout);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		findPatientRecyclerView.removeOnScrollListener(recyclerViewOnScrollListener);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.fragment_find_patient_record, container, false);
		resolveViews(mRootView);
		setNumberOfPatientsView(0);
		//Adding the Recycler view
		layoutManager = new LinearLayoutManager(this.getActivity());
		findPatientRecyclerView = (RecyclerView)mRootView.findViewById(R.id.findPatientModelRecyclerView);
		findPatientRecyclerView.setLayoutManager(layoutManager);

		// Font config
		FontsUtil.setFont((ViewGroup)this.getActivity().findViewById(android.R.id.content));
		authorizationManager = openMRS.getAuthorizationManager();
		if (authorizationManager.isUserLoggedIn()) {
			if (!OpenMRS.getInstance().getSearchQuery().equalsIgnoreCase(ApplicationConstants.EMPTY_STRING)) {
				mPresenter.findPatient(OpenMRS.getInstance().getSearchQuery());
			}

		}
		return mRootView;
	}

	@Override
	public void setNumberOfPatientsView(int length) {
		numberOfFetchedPatients.setText(getString(R.string.number_of_patients, String.valueOf(length)));
		foundPatientsLayout.setVisibility(length <= 0 ? View.GONE : View.VISIBLE);
		patientListLayout.setVisibility(length <= 0 ? View.GONE : View.VISIBLE);
		searchQuery.setText(getString(R.string.search_query_label, String.valueOf(OpenMRS.getInstance().getSearchQuery())));
	}

	@Override
	public void setNoPatientsVisibility(boolean visibility) {
		noPatientsFoundLayout.setVisibility(visibility ? View.VISIBLE : View.GONE);
	}

	@Override
	public void fetchPatients(List<Patient> patients) {
		FindPatientRecyclerViewAdapter adapter = new FindPatientRecyclerViewAdapter(this.getActivity(), patients, this);
		findPatientRecyclerView.setAdapter(adapter);
	}

	@Override
	public void setProgressBarVisibility(boolean visibility) {
		findPatientProgressBar.setVisibility(visibility ? View.VISIBLE : View.GONE);
	}

	@Override
	public void showRegistration() {
		Intent intent = new Intent(openMRS.getApplicationContext(), AddEditPatientActivity.class);
		openMRS.getApplicationContext().startActivity(intent);
	}
}
