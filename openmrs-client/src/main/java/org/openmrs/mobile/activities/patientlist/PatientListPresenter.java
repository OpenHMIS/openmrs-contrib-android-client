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

import android.support.annotation.NonNull;

import org.openmrs.mobile.activities.BasePresenter;
import org.openmrs.mobile.data.DataService;
import org.openmrs.mobile.data.PagingInfo;
import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.data.impl.PatientListContextDataService;
import org.openmrs.mobile.data.impl.PatientListDataService;
import org.openmrs.mobile.models.PatientList;
import org.openmrs.mobile.models.PatientListContext;
import org.openmrs.mobile.utilities.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class PatientListPresenter extends BasePresenter implements PatientListContract.Presenter {

	@NonNull
	private PatientListContract.View patientListView;
	private int limit = 10;
	private int page = 1;
	private int totalNumberResults, totalNumberPages;
	private boolean loading;
	private String patientListUuid;

	private PatientListDataService patientListDataService;
	private PatientListContextDataService patientListContextDataService;
	private List<PatientList> patientLists;

	public PatientListPresenter(@NonNull PatientListContract.View patientListView) {
		this(patientListView, null, null);
	}

	public PatientListPresenter(@NonNull PatientListContract.View patientListView,
			PatientListDataService patientListDataService,
			PatientListContextDataService patientListContextDataService) {
		super();

		this.patientListView = patientListView;
		this.patientListView.setPresenter(this);

		if (patientListDataService == null) {
			this.patientListDataService = dataAccess().patientList();
		} else {
			this.patientListDataService = patientListDataService;
		}

		if (patientListContextDataService == null) {
			this.patientListContextDataService = dataAccess().patientListContext();
		} else {
			this.patientListContextDataService = patientListContextDataService;
		}
	}

	@Override
	public void subscribe() {
		// get all patient lists
		if (patientLists == null) {
			getPatientList();
		}
	}

	@Override
	public void setExistingPatientListUuid(String uuid) {
		this.patientListUuid = uuid;
	}

	@Override
	public void getPatientList() {
		patientListView.showPatientListProgressSpinner(true);
		setPage(1);
		patientListDataService.getAll(null, new PagingInfo(1, 100),
				new DataService.GetCallback<List<PatientList>>() {
					@Override
					public void onCompleted(List<PatientList> entities) {
						if (entities != null) {
							patientLists = entities;
							patientListView.showPatientListProgressSpinner(false);
							patientListView.setNoPatientListsVisibility(false);
							patientListView.updatePatientLists(entities);
							if (StringUtils.notNull(patientListUuid)) {
								getPatientListData(patientListUuid, getPage());
							}
						}
					}

					@Override
					public void onError(Throwable t) {
						patientListView.showPatientListProgressSpinner(false);
						patientListView.setNoPatientListsVisibility(true);
					}
				});
	}

	@Override
	public void getPatientListData(String patientListUuid, int page) {
		if (page <= 0) {
			return;
		}
		setPage(page);
		setLoading(true);
		setViewBeforeLoadData();
		setTotalNumberResults(0);
		setExistingPatientListUuid(patientListUuid);
		PagingInfo pagingInfo = new PagingInfo(page, limit);
		patientListContextDataService.getListPatients(patientListUuid, null, pagingInfo,
				new DataService.GetCallback<List<PatientListContext>>() {
					@Override
					public void onCompleted(List<PatientListContext> entities) {
						if (entities.isEmpty()) {
							setViewAfterLoadData(true);
							patientListView.setNumberOfPatientsView(0);
							patientListView.updatePatientListData(entities);
						} else {
							setViewAfterLoadData(false);
							patientListView.updatePatientListData(entities);
							setTotalNumberResults(pagingInfo.getTotalRecordCount());
							if (pagingInfo.getTotalRecordCount() > 0) {
								patientListView.setNumberOfPatientsView(pagingInfo.getTotalRecordCount());
								totalNumberPages = pagingInfo.getTotalPages();
								patientListView.updatePagingLabel(page, totalNumberPages);
							}
						}
						setLoading(false);
					}

					@Override
					public void onError(Throwable t) {
						patientListView.updatePatientListData(new ArrayList<>());
						setViewAfterLoadData(true);
						patientListView.setNumberOfPatientsView(0);
						setLoading(false);
					}
				});
	}

	@Override
	public void loadResults(String patientListUuid, boolean loadNextResults) {
		getPatientListData(patientListUuid, computePage(loadNextResults));
	}

	@Override
	public int getPage() {
		return page;
	}

	@Override
	public void setPage(int page) {
		this.page = page;
	}

	@Override
	public int getTotalNumberPages() {
		return totalNumberPages;
	}

	@Override
	public void setTotalNumberResults(int totalNumberResults) {
		this.totalNumberResults = totalNumberResults;
	}

	private int computePage(boolean next) {
		int tmpPage = getPage();
		// check if pagination is required.
		int totalPages = (limit + totalNumberResults - 1) / limit;
		if (page <= totalPages) {
			if (next) {
				// set next page
				tmpPage += 1;
			} else {
				// set previous page.
				tmpPage -= 1;
			}
		}
		return tmpPage > totalPages ? -1 : tmpPage;
	}

	@Override
	public boolean isLoading() {
		return loading;
	}

	@Override
	public void setLoading(boolean loading) {
		this.loading = loading;
	}

	@Override
	public void refresh() {
	}

	private void setViewBeforeLoadData() {
		patientListView.setSpinnerVisibility(true);
		patientListView.setEmptyPatientListVisibility(false);
	}

	private void setViewAfterLoadData(boolean visible) {
		patientListView.setSpinnerVisibility(false);
		patientListView.setEmptyPatientListVisibility(visible);
	}

	@Override
	public int getLimit() {
		return limit;
	}
}
