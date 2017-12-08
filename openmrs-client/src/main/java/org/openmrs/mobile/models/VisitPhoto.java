package org.openmrs.mobile.models;

import com.google.gson.annotations.Expose;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.data.Blob;

import org.openmrs.mobile.data.db.AppDatabase;

@Table(database = AppDatabase.class)
public class VisitPhoto extends BaseOpenmrsEntity {
	@ForeignKey(stubbedRelationship = true)
	private Visit visit;

	@ForeignKey(stubbedRelationship = true)
	private Patient patient;

	@ForeignKey(stubbedRelationship = true)
	private Provider provider;

	@Column
	private String fileCaption;

	@Column
	private String instructions;

	@Column
	private Blob imageColumn;

	@Expose
	@ForeignKey(stubbedRelationship = true)
	private Observation observation;

	public Visit getVisit() {
		return visit;
	}

	public void setVisit(Visit visit) {
		this.visit = visit;
	}

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	public Provider getProvider() {
		return provider;
	}

	public void setProvider(Provider provider) {
		this.provider = provider;
	}

	public String getFileCaption() {
		return fileCaption;
	}

	public void setFileCaption(String fileCaption) {
		this.fileCaption = fileCaption;
	}

	public String getInstructions() {
		return instructions;
	}

	public void setInstructions(String instructions) {
		this.instructions = instructions;
	}

	public void setImage(byte[] image) {
		if (image != null) {
			this.imageColumn = new Blob(image);
		} else {
			this.imageColumn = null;
		}
	}

	public Observation getObservation() {
		return observation;
	}

	public void setObservation(Observation observation) {
		this.observation = observation;
	}

	public Blob getImageColumn() {
		return imageColumn;
	}

	public void setImageColumn(Blob imageColumn) {
		this.imageColumn = imageColumn;
	}
}
