package org.openmrs.mobile.activities.syncselection;

import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseActivity;

public class SyncSelectionActivity extends ACBaseActivity {

	public SyncSelectionContract.Presenter presenter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getLayoutInflater().inflate(R.layout.activity_sync_selection, frameLayout);
		setTitle(R.string.title_select_patient_lists_to_sync);

		disableActionBarNavigation();

		// Create fragment
		SyncSelectionFragment syncSelectionFragment =
				(SyncSelectionFragment) getSupportFragmentManager().findFragmentById(R.id.syncSelectionContentFrame);
		if (syncSelectionFragment == null) {
			syncSelectionFragment = SyncSelectionFragment.newInstance();
		}
		if (!syncSelectionFragment.isActive()) {
			addFragmentToActivity(getSupportFragmentManager(),
					syncSelectionFragment, R.id.syncSelectionContentFrame);
		}

		presenter = new SyncSelectionPresenter(syncSelectionFragment);
	}

	@Override
	public void onBackPressed() {
		if (drawer.isDrawerOpen(GravityCompat.START)) {
			drawer.closeDrawer(GravityCompat.START);
		}
	}
}
