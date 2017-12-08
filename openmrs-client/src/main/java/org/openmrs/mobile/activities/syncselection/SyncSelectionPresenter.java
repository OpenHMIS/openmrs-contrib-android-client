package org.openmrs.mobile.activities.syncselection;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.openmrs.mobile.activities.BasePresenter;
import org.openmrs.mobile.data.DataService;
import org.openmrs.mobile.data.db.impl.PullSubscriptionDbService;
import org.openmrs.mobile.data.impl.PatientListDataService;
import org.openmrs.mobile.data.sync.impl.PatientListContextSubscriptionProvider;
import org.openmrs.mobile.models.PatientList;
import org.openmrs.mobile.models.PullSubscription;
import org.openmrs.mobile.utilities.SyncConstants;

public class SyncSelectionPresenter extends BasePresenter implements SyncSelectionContract.Presenter {

	private SyncSelectionContract.View view;

	private PatientListDataService patientListDataService;
	private PullSubscriptionDbService pullSubscriptionDbService;

	private List<PatientList> patientLists;
	private List<PatientList> selectedPatientListsToSync;

	public SyncSelectionPresenter(SyncSelectionContract.View view) {
		this.view = view;
		this.pullSubscriptionDbService = pullSubscriptionDbService();

		selectedPatientListsToSync = new ArrayList<>();

		this.view.setPresenter(this);
		patientListDataService = dataAccess().patientList();
	}

	@Override
	public void subscribe() {
		// get all patient lists
		if (patientLists == null) {
			getPatientLists();
		}
	}

	private void getPatientLists() {
		view.toggleScreenProgressBar(true);
		patientListDataService.getAll(null, null,
				new DataService.GetCallback<List<PatientList>>() {
					@Override
					public void onCompleted(List<PatientList> entities) {
						view.toggleScreenProgressBar(false);
						if (entities != null) {
							patientLists = entities;
							view.displayPatientLists(entities);
						}
					}

					@Override
					public void onError(Throwable t) {
						view.toggleScreenProgressBar(false);
						// TODO: We can't display the list, so we should probably move on to the next screen
					}
				});
	}

	public void toggleSyncSelection(PatientList patientList, boolean isSelected) {
		if (isSelected) {
			selectedPatientListsToSync.add(patientList);
		} else {
			selectedPatientListsToSync.remove(patientList);
		}
		updateAdvanceButtonText();
	}

	@Override
	public void saveUsersSyncSelections() {
		List<PullSubscription> pullSubscriptionsToAdd = new ArrayList<>();

		int maximumIncrementalCount = 25;
		for (PatientList patientList : selectedPatientListsToSync) {
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

		boolean skipSyncing = pullSubscriptionsToAdd.isEmpty();
		view.navigateToNextPage(skipSyncing);
	}

	private void updateAdvanceButtonText() {
		boolean isAtLeastOnePatientListSelected = selectedPatientListsToSync.size() > 0;
		view.updateAdvanceButton(isAtLeastOnePatientListSelected);
	}
}
