package org.openmrs.mobile.activities.syncselection;

import java.util.List;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseFragment;
import org.openmrs.mobile.models.PatientList;

public class SyncSelectionFragment extends ACBaseFragment<SyncSelectionContract.Presenter>
		implements SyncSelectionContract.View {

	private LinearLayoutManager layoutManager;
	private RecyclerView syncSelectionModelRecyclerView;
	private ProgressBar screenProgressBar;
	private Button advanceButton;

	private SyncSelectionModelRecycleViewAdapter adapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_sync_selection, container, false);

		initViewFields(rootView);

		return rootView;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		adapter = new SyncSelectionModelRecycleViewAdapter(this);
		syncSelectionModelRecyclerView.setAdapter(adapter);

		syncSelectionModelRecyclerView.setLayoutManager(layoutManager);
		syncSelectionModelRecyclerView.setNestedScrollingEnabled(false);
	}

	private void initViewFields(View rootView) {
		screenProgressBar = (ProgressBar) rootView.findViewById(R.id.syncSelectionScreenProgressBar);
		advanceButton = (Button) rootView.findViewById(R.id.moveForwardButton);

		layoutManager = new LinearLayoutManager(this.getActivity());
		syncSelectionModelRecyclerView = (RecyclerView) rootView.findViewById(R.id.syncSelectionModelRecyclerView);
	}

	public static SyncSelectionFragment newInstance() {
		return new SyncSelectionFragment();
	}

	public void toggleScreenProgressBar(boolean makeVisible) {
		if (makeVisible) {
			screenProgressBar.setVisibility(View.VISIBLE);
		} else {
			screenProgressBar.setVisibility(View.INVISIBLE);
		}
	}

	public void toggleSyncSelection(PatientList patientList, boolean isSelected) {
		mPresenter.toggleSyncSelection(patientList, isSelected);
	}

	public void updateAdvanceButton(boolean isAtLeastOnePatientListSelected) {
		if (isAtLeastOnePatientListSelected) {
			advanceButton.setText(getString(R.string.save_patient_list_sync_selections));
		} else {
			advanceButton.setText(getString(R.string.skip_patient_list_sync_selections));
		}
	}

	public void displayPatientLists(List<PatientList> patientLists) {
		adapter.setItems(patientLists);
	}
}
