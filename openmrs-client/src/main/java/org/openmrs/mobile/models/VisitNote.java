package org.openmrs.mobile.models;

import com.google.gson.annotations.Expose;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.OneToMany;
import com.raizlabs.android.dbflow.annotation.Table;

import org.openmrs.mobile.data.db.AppDatabase;

import java.util.ArrayList;
import java.util.List;

@Table(database = AppDatabase.class)
public class VisitNote extends BaseOpenmrsEntity {
	@Expose
	@Column
	private String personId;

	@Expose
	@Column
	private String htmlFormId;

	@Expose
	@Column
	private String createVisit;

	@Expose
	@Column
	private String formModifiedTimestamp;

	@Expose
	@Column
	private String encounterModifiedTimestamp;

	@Expose
	@ForeignKey(stubbedRelationship = true)
	private Visit visit;

	@Expose
	@Column
	private String returnUrl;

	@Expose
	@Column
	private String closeAfterSubmission;

	@Expose
	private List<EncounterDiagnosis> encounterDiagnoses;

	@Expose
	@ForeignKey(stubbedRelationship = true)
	private Encounter encounter;

	@Expose
	@Column
	private String w1;

	@Expose
	@Column
	private String w3;

	@Expose
	@Column
	private String w5;

	@Expose
	@Column
	private String w10;

	@Expose
	@Column
	private String w12;

	@Column
	@Expose
	private String observationUuid;

	@OneToMany(methods = { OneToMany.Method.ALL}, variableName = "encounterDiagnoses", isVariablePrivate = true)
	List<EncounterDiagnosis> loadEncounterDiagnoses() {
		encounterDiagnoses = loadRelatedObject(EncounterDiagnosis.class, encounterDiagnoses,
				() -> EncounterDiagnosis_Table.visitNote_uuid.eq(getUuid()));

		return encounterDiagnoses;
	}

	@Override
	public void processRelationships() {
		super.processRelationships();

		processRelatedObjects(encounterDiagnoses, (e) -> e.setVisitNote(this));
	}

	public String getPersonId() {
		return personId;
	}

	public void setPersonId(String personId) {
		this.personId = personId;
	}

	public String getHtmlFormId() {
		return htmlFormId;
	}

	public void setHtmlFormId(String htmlFormId) {
		this.htmlFormId = htmlFormId;
	}

	public String getCreateVisit() {
		return createVisit;
	}

	public void setCreateVisit(String createVisit) {
		this.createVisit = createVisit;
	}

	public String getFormModifiedTimestamp() {
		return formModifiedTimestamp;
	}

	public void setFormModifiedTimestamp(String formModifiedTimestamp) {
		this.formModifiedTimestamp = formModifiedTimestamp;
	}

	public String getEncounterModifiedTimestamp() {
		return encounterModifiedTimestamp;
	}

	public void setEncounterModifiedTimestamp(String encounterModifiedTimestamp) {
		this.encounterModifiedTimestamp = encounterModifiedTimestamp;
	}

	public Visit getVisit() {
		return visit;
	}

	public void setVisit(Visit visit) {
		this.visit = visit;
	}

	public String getReturnUrl() {
		return returnUrl;
	}

	public void setReturnUrl(String returnUrl) {
		this.returnUrl = returnUrl;
	}

	public String getCloseAfterSubmission() {
		return closeAfterSubmission;
	}

	public void setCloseAfterSubmission(String closeAfterSubmission) {
		this.closeAfterSubmission = closeAfterSubmission;
	}

	public List<EncounterDiagnosis> getEncounterDiagnoses() {
		return encounterDiagnoses;
	}

	public void setEncounterDiagnoses(List<EncounterDiagnosis> encounterDiagnoses) {
		this.encounterDiagnoses = encounterDiagnoses;
	}

	public String getW1() {
		return w1;
	}

	public void setW1(String w1) {
		this.w1 = w1;
	}

	public String getW3() {
		return w3;
	}

	public void setW3(String w3) {
		this.w3 = w3;
	}

	public String getW5() {
		return w5;
	}

	public void setW5(String w5) {
		this.w5 = w5;
	}

	public String getW10() {
		return w10;
	}

	public void setW10(String w10) {
		this.w10 = w10;
	}

	public String getW12() {
		return w12;
	}

	public void setW12(String w12) {
		this.w12 = w12;
	}

	public Encounter getEncounter() {
		return encounter;
	}

	public void setEncounter(Encounter encounter) {
		this.encounter = encounter;
	}

	public void addEncounterDiagnosis(EncounterDiagnosis encounterDiagnosis){
		if(encounterDiagnoses == null){
			encounterDiagnoses = new ArrayList<>();
		}

		encounterDiagnoses.add(encounterDiagnosis);
	}

	public String getObservationUuid() {
		return observationUuid;
	}

	public void setObservationUuid(String observationUuid) {
		this.observationUuid = observationUuid;
	}
}
