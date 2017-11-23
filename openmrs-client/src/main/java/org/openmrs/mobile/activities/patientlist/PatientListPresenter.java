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
import org.openmrs.mobile.data.db.impl.PullSubscriptionDbService;
import org.openmrs.mobile.data.impl.PatientListContextDataService;
import org.openmrs.mobile.data.impl.PatientListDataService;
import org.openmrs.mobile.models.PatientList;
import org.openmrs.mobile.models.PatientListContext;
import org.openmrs.mobile.models.PullSubscription;
import org.openmrs.mobile.utilities.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	private PullSubscriptionDbService pullSubscriptionDbService;
	private List<PatientList> patientLists;

	public PatientListPresenter(@NonNull PatientListContract.View patientListView) {
		this(patientListView, null, null, null);
	}

	public PatientListPresenter(@NonNull PatientListContract.View patientListView,
			PatientListDataService patientListDataService, PatientListContextDataService patientListContextDataService,
			PullSubscriptionDbService pullSubscriptionDbService) {
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

		if (pullSubscriptionDbService == null) {
			this.pullSubscriptionDbService = pullSubscriptionDbService();
		} else {
			this.pullSubscriptionDbService = pullSubscriptionDbService;
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
							List<PatientList> patientListsToSync = getPatientListToSync();
							patientListView.showPatientListProgressSpinner(false);
							patientListView.setNoPatientListsVisibility(false);
							patientListView.updatePatientLists(entities, patientListsToSync);
							if (StringUtils.notNull(patientListUuid)) {
								getPatientListData(patientListUuid, getPage(), false);
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
	public void getPatientListData(String patientListUuid, int page, boolean forceRefresh) {
		if (page <= 0) {
			return;
		}
		setPage(page);
		setLoading(true);
		if (!forceRefresh) {
			setViewBeforeLoadData();
		}
		setTotalNumberResults(0);
		setExistingPatientListUuid(patientListUuid);
		PagingInfo pagingInfo = new PagingInfo(page, limit);

		QueryOptions queryOptions = null;
		if (forceRefresh) {
			queryOptions = QueryOptions.REMOTE;
		}
		patientListContextDataService.getListPatients(patientListUuid, queryOptions, pagingInfo,
				new DataService.GetCallback<List<PatientListContext>>() {
					@Override
					public void onCompleted(List<PatientListContext> entities) {
						if (entities.isEmpty()) {
							setViewAfterLoadData(true);
							patientListView.setNumberOfPatientsView(0);
							patientListView.updatePatientListData(entities, forceRefresh);
						} else {
							setViewAfterLoadData(false);
							patientListView.updatePatientListData(entities, forceRefresh);
							setTotalNumberResults(pagingInfo.getTotalRecordCount() != null ? pagingInfo
									.getTotalRecordCount() : 0);
							if (pagingInfo.getTotalRecordCount() != null && pagingInfo.getTotalRecordCount() > 0) {
								patientListView.setNumberOfPatientsView(pagingInfo.getTotalRecordCount());
								totalNumberPages = pagingInfo.getTotalPages();
								patientListView.updatePagingLabel(page, totalNumberPages);
							}
						}
						setLoading(false);
					}

					@Override
					public void onError(Throwable t) {
						patientListView.updatePatientListData(new ArrayList<>(), false);
						setViewAfterLoadData(true);
						patientListView.setNumberOfPatientsView(0);
						setLoading(false);
					}
				});
	}

	@Override
	public void loadResults(String patientListUuid, boolean loadNextResults) {
		getPatientListData(patientListUuid, computePage(loadNextResults), false);
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

	public void syncSelectionsSaved() {
		List<PatientList> patientListsToSync = getPatientListToSync();
		patientListView.updatePatientListSyncDisplay(patientLists, patientListsToSync);
	}

	private List<PatientList> getPatientListToSync() {
		List<PullSubscription> pullSubscriptions = pullSubscriptionDbService.getAll(null, null);
		List<PatientList> patientListsToSync = new ArrayList<>();
		if (pullSubscriptions != null) {
			patientListsToSync = mapPullSubscriptions(pullSubscriptions);
		}
		return patientListsToSync;
	}

	private List<PatientList> mapPullSubscriptions(List<PullSubscription> pullSubscriptions) {
		List<String> patientListUuids = new ArrayList<>();
		Map<String, PatientList> patientMap = new HashMap<>();
		for (PatientList patientList : patientLists) {
			patientListUuids.add(patientList.getUuid());
			patientMap.put(patientList.getUuid(), patientList);
		}

		List<PatientList> syncingPatientLists = new ArrayList<>();
		for (PullSubscription pullSubscription : pullSubscriptions) {
			String patientListUuid = pullSubscription.getSubscriptionKey();
			if (patientListUuid != null && patientListUuids.contains(patientListUuid)) {
				syncingPatientLists.add(patientMap.get(patientListUuid));
			}
		}

		return syncingPatientLists;
	}
}
