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
package org.openmrs.mobile.activities.patientlist;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseFragment;
import org.openmrs.mobile.models.PatientList;
import org.openmrs.mobile.models.PatientListContext;
import org.openmrs.mobile.utilities.FontsUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Main Patient List UI screen.
 */
public class PatientListFragment extends ACBaseFragment<PatientListContract.Presenter> implements PatientListContract.View {

	private Spinner patientListDropdown;
	private PatientListSpinnerAdapter patientListSpinnerAdapter;
	private TextView emptyPatientList, noPatientLists, numberOfPatients, pagingLabel;
	private RecyclerView patientListModelRecyclerView;
	private LinearLayoutManager layoutManager;
	private LinearLayout patientListScreen, patientListRecyclerView;
	private RelativeLayout patientListProgressBar, patientListLoadingProgressBar, numberOfPatientsLayout, selectPatientList;
	private SwipeRefreshLayout patientListSwipeRefreshLayout;

	private PatientList selectedPatientList;
	private PatientList patientListNotSelectedOption;

	private PatientListModelRecyclerViewAdapter adapter;

	private int currentPage = 0;

	private RecyclerView.OnScrollListener recyclerViewOnScrollListener = new RecyclerView.OnScrollListener() {

		@Override
		public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
			if (!mPresenter.isLoading()) {
				// you can't scroll up or down. load previous page if any
				if (!recyclerView.canScrollVertically(1) && !recyclerView.canScrollVertically(-1)) {
					loadPreviousPage();
				}
			}
		}

		@Override
		public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
			if (!mPresenter.isLoading()) {
				if (!recyclerView.canScrollVertically(1)) {
					// load next page
					mPresenter.loadResults(selectedPatientList.getUuid(), true);
				}

				if (!recyclerView.canScrollVertically(-1) && dy < 0) {
					loadPreviousPage();
				}

				currentPage = calculateCurrentPageOnScroll(layoutManager.findLastVisibleItemPosition());
				updatePagingLabel(currentPage);
			}
		}
	};

	public static PatientListFragment newInstance() {
		return new PatientListFragment();
	}

	private int calculateCurrentPageOnScroll(int currentPosition) {
		return (currentPosition / mPresenter.getLimit()) + 1;
	}

	private void loadPreviousPage() {
		// load previous page
		if (currentPage <= 0) {
			currentPage = mPresenter.getPage();
		}

		int previousPage = currentPage - 1;
		if (previousPage <= 0)
			previousPage = 1;

		patientListModelRecyclerView.scrollToPosition(((previousPage - 1) * mPresenter.getLimit()) + 1);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		patientListModelRecyclerView.removeOnScrollListener(recyclerViewOnScrollListener);
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.fragment_patient_list, container, false);
		patientListLoadingProgressBar = (RelativeLayout)root.findViewById(R.id.patientListLoadingProgressBar);
		patientListDropdown = (Spinner)root.findViewById(R.id.patientListDropdown);
		emptyPatientList = (TextView)root.findViewById(R.id.emptyPatientList);
		noPatientLists = (TextView)root.findViewById(R.id.noPatientLists);
		numberOfPatients = (TextView)root.findViewById(R.id.numberOfPatients);
		pagingLabel = (TextView)root.findViewById(R.id.pagingLabel);
		patientListProgressBar = (RelativeLayout)root.findViewById(R.id.patientListScreenProgressBar);
		patientListScreen = (LinearLayout)root.findViewById(R.id.patientListScreen);
		patientListRecyclerView = (LinearLayout)root.findViewById(R.id.patientListRecyclerView);
		numberOfPatientsLayout = (RelativeLayout)root.findViewById(R.id.numberOfPatientsLayout);
		selectPatientList = (RelativeLayout)root.findViewById(R.id.selectPatientList);
		patientListSwipeRefreshLayout = (SwipeRefreshLayout)root.findViewById(R.id.patientListSwipeRefreshView);
		patientListSwipeRefreshLayout.setEnabled(false);

		layoutManager = new LinearLayoutManager(this.getActivity());
		patientListModelRecyclerView = (RecyclerView)root.findViewById(R.id.patientListModelRecyclerView);

		patientListNotSelectedOption = new PatientList();
		patientListNotSelectedOption.setName(getString(R.string.select_patient_list));

		// Font config
		FontsUtil.setFont((ViewGroup)this.getActivity().findViewById(android.R.id.content));

		return root;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		adapter = new PatientListModelRecyclerViewAdapter(this.getActivity(), this);
		patientListModelRecyclerView.setAdapter(adapter);
		patientListModelRecyclerView.addOnScrollListener(recyclerViewOnScrollListener);

		patientListModelRecyclerView.setLayoutManager(layoutManager);
		patientListModelRecyclerView.setNestedScrollingEnabled(false);

		patientListSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

			@Override
			public void onRefresh() {
				mPresenter.getPatientListData(selectedPatientList.getUuid(), 1, true);
			}
		});
	}

	@Override
	public void setEmptyPatientListVisibility(boolean visible) {
		emptyPatientList.setVisibility(visible ? View.VISIBLE : View.GONE);
	}

	@Override
	public void setNoPatientListsVisibility(boolean visible) {
		noPatientLists.setVisibility(visible ? View.VISIBLE : View.GONE);
	}

	@Override
	public void showPatientListProgressSpinner(boolean visible) {
		patientListProgressBar.setVisibility(visible ? View.VISIBLE : View.GONE);
		patientListScreen.setVisibility(visible ? View.GONE : View.VISIBLE);
	}

	@Override
	public void setSpinnerVisibility(boolean visible) {
		patientListLoadingProgressBar.setVisibility(visible ? View.VISIBLE : View.GONE);
		patientListRecyclerView.setVisibility(visible ? View.GONE : View.VISIBLE);
		if (visible) {
			setNumberOfPatientsView(0);
			setNoPatientListsVisibility(false);
			setEmptyPatientListVisibility(false);
		}
	}

	@Override
	public void updatePatientLists(List<PatientList> patientLists, List<PatientList> syncingPatientLists) {
		List<PatientList> patientListsToShow = getPatientListDisplayList(patientLists);

		patientListSpinnerAdapter = new PatientListSpinnerAdapter(getContext(),
				patientListsToShow, syncingPatientLists);
		patientListDropdown.setAdapter(patientListSpinnerAdapter);

		patientListDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				setSelectedPatientList(patientListsToShow.get(position));
				currentPage = 1;
				adapter.clearItems();
				if (selectedPatientList.getUuid() == null || selectedPatientList.equals(patientListNotSelectedOption)) {
					showNoPatientListSelected(true);
					setNumberOfPatientsView(0);
					List<PatientListContext> patientListContextList = new ArrayList<>();
					updatePatientListData(patientListContextList, false);

					patientListSwipeRefreshLayout.setEnabled(false);
				} else {
					showNoPatientListSelected(false);
					mPresenter.getPatientListData(selectedPatientList.getUuid(), 1, false);
					patientListSwipeRefreshLayout.setEnabled(true);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
	}

	private List<PatientList> getPatientListDisplayList(List<PatientList> patientLists) { new ArrayList<>();
		List<PatientList> patientListsToShow = new ArrayList<>();

		patientListsToShow.add(patientListNotSelectedOption);
		patientListsToShow.addAll(patientLists);

		return patientListsToShow;
	}

	@Override
	public void setSelectedPatientList(PatientList selectedPatientList) {
		this.selectedPatientList = selectedPatientList;
	}

	@Override
	public void updatePatientListData(List<PatientListContext> patientListData, boolean wasForceRefresh) {
		if (adapter.getItems() == null || wasForceRefresh) {
			adapter.setItems(patientListData);
		} else {
			adapter.addItems(patientListData);
		}
		patientListSwipeRefreshLayout.setRefreshing(false);
	}

	@Override
	public void setNumberOfPatientsView(int length) {
		numberOfPatients.setText(getString(R.string.number_of_patients, String.valueOf(length)));
		numberOfPatientsLayout.setVisibility(length <= 0 ? View.GONE : View.VISIBLE);
	}

	@Override
	public boolean isActive() {
		return isAdded();
	}

	@Override
	public void updatePagingLabel(int currentPage, int totalNumberOfPages) {
		pagingLabel.setText(getString(R.string.paging_label, String.valueOf(currentPage), String.valueOf(totalNumberOfPages)));
	}

	@Override
	public void updatePatientListSyncDisplay(List<PatientList> patientList, List<PatientList> syncingPatientLists) {
		List<PatientList> patientListsToShow = getPatientListDisplayList(patientList);
		patientListSpinnerAdapter.setItems(patientListsToShow, syncingPatientLists);
	}

	private void updatePagingLabel(int currentPage) {
		updatePagingLabel(currentPage, mPresenter.getTotalNumberPages());
	}

	public void showNoPatientListSelected(boolean visibility) {
		selectPatientList.setVisibility(visibility ? View.VISIBLE : View.GONE);
	}
}
