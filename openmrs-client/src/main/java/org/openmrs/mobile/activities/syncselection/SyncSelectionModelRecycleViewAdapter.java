package org.openmrs.mobile.activities.syncselection;

import java.util.ArrayList;
import java.util.List;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import org.openmrs.mobile.R;
import org.openmrs.mobile.listeners.PatientListSyncSwitchToggle;
import org.openmrs.mobile.models.PatientList;

public class SyncSelectionModelRecycleViewAdapter
		extends RecyclerView.Adapter<SyncSelectionModelRecycleViewAdapter.SyncSelectionModelViewHolder> {

	private PatientListSyncSwitchToggle viewCallback;
	private List<PatientList> items;
	private List<PatientList> selectedItems;

	public SyncSelectionModelRecycleViewAdapter(PatientListSyncSwitchToggle viewCallback) {
		this.viewCallback = viewCallback;
		this.selectedItems = new ArrayList<>();
	}

	@Override
	public SyncSelectionModelViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.sync_selection_model_row, parent, false);
		return new SyncSelectionModelViewHolder(itemView);
	}

	@Override
	public void onBindViewHolder(SyncSelectionModelViewHolder holder, int position) {
		PatientList patientList = items.get(position);

		holder.patientListChoice.setText(patientList.toString());
		if (selectedItems.contains(patientList)) {
			holder.patientListChoice.setChecked(true);
		}
		holder.patientListChoice.setOnCheckedChangeListener((CompoundButton buttonView, boolean isChecked) -> {
			viewCallback.toggleSyncSelection(patientList, isChecked);
		});
	}

	@Override
	public int getItemCount() {
		if (items != null) {
			return items.size();
		} else {
			return 0;
		}
	}

	public void setItems(List<PatientList> items) {
		this.items = items;

		notifyDataSetChanged();
	}

	public void setItems(List<PatientList> items, List<PatientList> selectedItems) {
		this.items = items;
		this.selectedItems = selectedItems;

		notifyDataSetChanged();
	}

	class SyncSelectionModelViewHolder extends RecyclerView.ViewHolder {
		private Switch patientListChoice;

		public SyncSelectionModelViewHolder(View itemView) {
			super(itemView);
			patientListChoice = (Switch) itemView.findViewById(R.id.syncSelectionChoice);
		}
	}
}
