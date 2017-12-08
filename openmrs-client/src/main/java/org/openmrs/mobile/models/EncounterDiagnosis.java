package org.openmrs.mobile.models;

import com.google.gson.annotations.Expose;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.Table;

import org.openmrs.mobile.data.db.AppDatabase;

@Table(database = AppDatabase.class)
public class EncounterDiagnosis extends BaseOpenmrsObject {
	@Expose
	@Column
	private String certainty;

	@Expose
	@Column
	private String order;

	@Expose
	@Column
	private String diagnosis;

	@Expose
	@Column
	private String existingObs;

	@ForeignKey(stubbedRelationship = true)
	private VisitNote visitNote;

	public String getCertainty() {
		return certainty;
	}

	public void setCertainty(String certainty) {
		this.certainty = certainty;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public String getDiagnosis() {
		return diagnosis;
	}

	public void setDiagnosis(String diagnosis) {
		this.diagnosis = diagnosis;
	}

	public String getExistingObs() {
		return existingObs;
	}

	public void setExistingObs(String existingObs) {
		this.existingObs = existingObs;
	}

	public VisitNote getVisitNote() {
		return visitNote;
	}

	public void setVisitNote(VisitNote visitNote) {
		this.visitNote = visitNote;
	}
}
