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

package org.openmrs.mobile.activities.dialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.openmrs.mobile.data.DataService;
import org.openmrs.mobile.data.PagingInfo;
import org.openmrs.mobile.data.db.impl.PullSubscriptionDbService;
import org.openmrs.mobile.data.impl.PatientListDataService;
import org.openmrs.mobile.data.sync.impl.PatientListContextSubscriptionProvider;
import org.openmrs.mobile.models.PatientList;
import org.openmrs.mobile.models.PullSubscription;
import org.openmrs.mobile.sync.SyncManager;
import org.openmrs.mobile.utilities.SyncConstants;

/**
 * General class for creating dialog fragment instances
 */
public class PatientListSyncSelectionDialogPresenter implements PatientListSyncSelectionDialogContract.Presenter {

	private PatientListSyncSelectionDialogContract.View view;

	private PatientListDataService patientListDataService;
	private PullSubscriptionDbService pullSubscriptionDbService;
	private SyncManager syncManager;

	private List<PatientList> patientLists;
	private List<PatientList> selectedPatientListsToSync;
	private Map<String, PullSubscription> patientListUuidSubscriptionMap;

	public PatientListSyncSelectionDialogPresenter(PatientListSyncSelectionDialogContract.View view,
			PatientListDataService patientListDataService, PullSubscriptionDbService pullSubscriptionDbService,
			SyncManager syncManager) {
		this.view = view;
		this.patientListDataService = patientListDataService;
		this.pullSubscriptionDbService = pullSubscriptionDbService;
		this.syncManager = syncManager;

		this.view.setPresenter(this);
	}

	@Override
	public void saveUsersSyncSelections() {
		List<PullSubscription> pullSubscriptionsToAdd = new ArrayList<>();

		List<PatientList> newlySelectedLists = new ArrayList<>();
		List<PatientList> removedLists = new ArrayList<>();
		List<PatientList> usersSavedSyncLists = getPatientListToSync();

		for (PatientList patientList : selectedPatientListsToSync) {
			if (!usersSavedSyncLists.contains(patientList)) {
				newlySelectedLists.add(patientList);
			}
		}
		for (PatientList patientList : usersSavedSyncLists) {
			if (!selectedPatientListsToSync.contains(patientList)) {
				removedLists.add(patientList);
			}
		}

		int maximumIncrementalCount = 25;
		for (PatientList patientList : newlySelectedLists) {
			PullSubscription pullSubscription = new PullSubscription();
			pullSubscription.setForceSyncAfterPush(false);
			pullSubscription.setSubscriptionClass(PatientListContextSubscriptionProvider.class.getSimpleName());
			pullSubscription.setSubscriptionKey(patientList.getUuid());
			pullSubscription.setMaximumIncrementalCount(maximumIncrementalCount);
			pullSubscription.setMinimumInterval((int) SyncConstants.SYNC_INTERVAL_FOR_FREQUENT_DATA);
			pullSubscription.setUuid(UUID.randomUUID().toString());

			pullSubscriptionsToAdd.add(pullSubscription);
		}

		if (pullSubscriptionsToAdd.size() > 0) {
			pullSubscriptionDbService.saveAll(pullSubscriptionsToAdd);
		}

		for (PatientList patientList : removedLists) {
			pullSubscriptionDbService.delete(patientListUuidSubscriptionMap.get(patientList.getUuid()));
			syncManager.deleteUnsyncedPatientListData(patientList.getUuid());
		}

		syncManager.requestSync();
		view.dismissDialog();
	}

	@Override
	public void toggleSyncSelection(PatientList patientList, boolean isSelected) {
		if (isSelected) {
			selectedPatientListsToSync.add(patientList);
		} else {
			selectedPatientListsToSync.remove(patientList);
		}
	}

	@Override
	public void dialogCreated() {
		getData();
	}

	private void getData() {
		getPatientLists();
	}

	private void getPatientLists() {
		patientListDataService.getAll(null, PagingInfo.ALL,
				new DataService.GetCallback<List<PatientList>>() {
					@Override
					public void onCompleted(List<PatientList> entities) {
						if (entities != null) {
							patientLists = entities;
						} else {
							patientLists = new ArrayList<PatientList>();
						}
						continueGettingData();
					}

					@Override
					public void onError(Throwable t) {
						// TODO: We can't display the list, so we should probably move on to the next screen
					}
				});
	}

	private void continueGettingData() {
		selectedPatientListsToSync = getPatientListToSync();
		displayPatientListsIfDataRetrieved();
	}

	private List<PatientList> getPatientListToSync() {
		List<PullSubscription> pullSubscriptions =  pullSubscriptionDbService.getAll(null, null);
		List<PatientList> patientListsToSync = new ArrayList<>();
		if (pullSubscriptions != null) {
			patientListsToSync = mapPullSubscriptions(pullSubscriptions);
		}
		return patientListsToSync;
	}

	private void displayPatientListsIfDataRetrieved() {
		if (patientLists != null && selectedPatientListsToSync != null) {
			view.displayPatientLists(patientLists, selectedPatientListsToSync);
		}
	}

	private List<PatientList> mapPullSubscriptions(List<PullSubscription> pullSubscriptions) {
		List<String> patientListUuids = new ArrayList<>();
		Map<String, PatientList> patientMap = new HashMap<>();
		for (PatientList patientList : patientLists) {
			patientListUuids.add(patientList.getUuid());
			patientMap.put(patientList.getUuid(), patientList);
		}

		List<PatientList> syncingPatientLists = new ArrayList<>();
		patientListUuidSubscriptionMap = new HashMap<>();
		for (PullSubscription pullSubscription : pullSubscriptions) {
			String patientListUuid = pullSubscription.getSubscriptionKey();
			if (patientListUuid != null && patientListUuids.contains(patientListUuid)) {
				syncingPatientLists.add(patientMap.get(patientListUuid));
				patientListUuidSubscriptionMap.put(patientListUuid, pullSubscription);
			}
		}

		return syncingPatientLists;
	}
}
