package org.openmrs.mobile.activities.visit.detail;

import android.app.Activity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.IBaseDiagnosisView;
import org.openmrs.mobile.models.Encounter;
import org.openmrs.mobile.models.EncounterDiagnosis;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.utilities.ApplicationConstants;

import java.util.List;

public class DiagnosisRecyclerViewAdapter extends RecyclerView.Adapter<DiagnosisRecyclerViewAdapter.DiagnosisViewHolder> {

	private Activity context;
	private IBaseDiagnosisView visitDetailsView;
	private List<EncounterDiagnosis> diagnoses;
	private String clinicalNote;
	private Visit visit;
	private Encounter encounter;

	public DiagnosisRecyclerViewAdapter(Activity context,
			List<EncounterDiagnosis> diagnoses, Encounter encounter,
			String clinicalNote, Visit visit, IBaseDiagnosisView visitDetailsView) {
		this.context = context;
		this.diagnoses = diagnoses;
		this.encounter = encounter;
		this.clinicalNote = clinicalNote;
		this.visit = visit;
		this.visitDetailsView = visitDetailsView;
	}

	@Override
	public DiagnosisViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.diagnosis_row, parent, false);
		return new DiagnosisViewHolder(itemView);
	}

	@Override
	public void onBindViewHolder(DiagnosisViewHolder holder, int position) {
		EncounterDiagnosis encounterDiagnosis = diagnoses.get(position);

		String confirmedDiagnosis = encounterDiagnosis.getCertainty();
		String diagnosisOrder = encounterDiagnosis.getOrder();
		String diagnosis = encounterDiagnosis.getDisplay();

		if (confirmedDiagnosis != null) {
			if (confirmedDiagnosis.equalsIgnoreCase(ApplicationConstants.DiagnosisStrings.PRESUMED)) {
				holder.certainty.setChecked(false);
			} else {
				holder.certainty.setChecked(true);
			}
		}

		if (diagnosisOrder != null) {
			if (diagnosisOrder.equalsIgnoreCase(ApplicationConstants.DiagnosisStrings.SECONDARY_ORDER)) {
				holder.order.setChecked(false);
			} else {
				holder.order.setChecked(true);
			}
		}

		if (diagnosis != null) {
			holder.diagnosis.setText(diagnosis);
		}

		holder.order.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton orderCheckBox, boolean isChecked) {
				if (orderCheckBox.isChecked()) {
					encounterDiagnosis.setOrder(ApplicationConstants.DiagnosisStrings.PRIMARY_ORDER);
					visitDetailsView.setPrimaryDiagnosis(encounterDiagnosis);

				} else {
					encounterDiagnosis.setOrder(ApplicationConstants.DiagnosisStrings.SECONDARY_ORDER);
					visitDetailsView.setSecondaryDiagnosis(encounterDiagnosis);
				}

				visitDetailsView.saveVisitNote(encounter, clinicalNote, visit, true);
			}
		});

		holder.certainty.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton certaintyCheckBox, boolean isChecked) {
				if (certaintyCheckBox.isChecked()) {
					encounterDiagnosis.setCertainty(ApplicationConstants.DiagnosisStrings.CONFIRMED);
					visitDetailsView.setDiagnosisCertainty(encounterDiagnosis);
				} else {
					encounterDiagnosis.setCertainty(ApplicationConstants.DiagnosisStrings.PRESUMED);
					visitDetailsView.setDiagnosisCertainty(encounterDiagnosis);
				}

				visitDetailsView.saveVisitNote(encounter, clinicalNote, visit, true);
			}
		});

		holder.rowLayout.setLongClickable(true);
		holder.rowLayout.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				visitDetailsView.removeDiagnosis(encounterDiagnosis, diagnosisOrder);

				visitDetailsView.saveVisitNote(encounter, clinicalNote, visit, true);

				return true;
			}
		});
	}

	@Override
	public int getItemCount() {
		return diagnoses.size();
	}

	class DiagnosisViewHolder extends RecyclerView.ViewHolder {
		private CardView rowLayout;
		private TextView diagnosis;
		private CheckBox order, certainty;

		public DiagnosisViewHolder(View itemView) {
			super(itemView);
			rowLayout = (CardView)itemView;
			diagnosis = (TextView)itemView.findViewById(R.id.diagnosisDisplay);
			order = (CheckBox)itemView.findViewById(R.id.order);
			certainty = (CheckBox)itemView.findViewById(R.id.certainty);
		}
	}
}
