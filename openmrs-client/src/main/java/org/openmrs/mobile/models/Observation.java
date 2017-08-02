/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.mobile.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.Table;

import org.openmrs.mobile.data.db.AppDatabase;

import java.io.Serializable;

@Table(database = AppDatabase.class)
public class Observation extends BaseOpenmrsEntity implements Serializable {
	@SerializedName("concept")
	@Expose
	@ForeignKey(stubbedRelationship = true)
	private Concept concept;

	@SerializedName("person")
	@Expose
	@ForeignKey(stubbedRelationship = true)
	private Person person;

	@SerializedName("obsDatetime")
	@Expose
	@Column
	private String obsDatetime;

	@SerializedName("accessionNumber")
	@Expose
	@Column
	private String accessionNumber;

	@SerializedName("obsGroup")
	@Expose
	@ForeignKey(stubbedRelationship = true)
	private Observation obsGroup;

	@SerializedName("valueCodedName")
	@Expose
	@Column
	private String valueCodedName;

	@SerializedName("comment")
	@Expose
	@Column
	private String comment;

	@SerializedName("location")
	@Expose
	@Column
	private String location = null;

	@SerializedName("encounter")
	@Expose
	@ForeignKey(stubbedRelationship = true)
	private Encounter encounter = null;

	@SerializedName("formFieldPath")
	@Expose
	@Column
	private String formFieldPath;

	@SerializedName("formFieldNamespace")
	@Expose
	@Column
	private String formFieldNamespace;

	@SerializedName("resourceVersion")
	@Expose
	@Column
	private String resourceVersion;

	@SerializedName("value")
	@Expose
	private Object value;

	@Expose
	@ForeignKey(stubbedRelationship = true)
	private Provider provider;

	private String diagnosisList;
	private String diagnosisCertainty;
	private String diagnosisOrder;

	private String diagnosisNote;

	/**
	 * @return The concept
	 */
	public Concept getConcept() {
		return concept;
	}

	/**
	 * @param concept The concept
	 */
	public void setConcept(Concept concept) {
		this.concept = concept;
	}

	/**
	 * @return The person
	 */
	public Person getPerson() {
		return person;
	}

	/**
	 * @param person The person
	 */
	public void setPerson(Person person) {
		this.person = person;
	}

	/**
	 * @return The obsDatetime
	 */
	public String getObsDatetime() {
		return obsDatetime;
	}

	/**
	 * @param obsDatetime The obsDatetime
	 */
	public void setObsDatetime(String obsDatetime) {
		this.obsDatetime = obsDatetime;
	}

	/**
	 * @return The accessionNumber
	 */
	public String getAccessionNumber() {
		return accessionNumber;
	}

	/**
	 * @param accessionNumber The accessionNumber
	 */
	public void setAccessionNumber(String accessionNumber) {
		this.accessionNumber = accessionNumber;
	}

	/**
	 * @return The obsGroup
	 */
	public Observation getObsGroup() {
		return obsGroup;
	}

	/**
	 * @param obsGroup The obsGroup
	 */
	public void setObsGroup(Observation obsGroup) {
		this.obsGroup = obsGroup;
	}

	/**
	 * @return The valueCodedName
	 */
	public String getValueCodedName() {
		return valueCodedName;
	}

	/**
	 * @param valueCodedName The valueCodedName
	 */
	public void setValueCodedName(String valueCodedName) {
		this.valueCodedName = valueCodedName;
	}

	/**
	 * @return The comment
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * @param comment The comment
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}

	/**
	 * @return The location
	 */
	public String getLocation() {
		return location;
	}

	/**
	 * @param location The location
	 */
	public void setLocation(String location) {
		this.location = location;
	}

	/**
	 * @return The encounter
	 */
	public Encounter getEncounter() {
		return encounter;
	}

	/**
	 * @param encounter The encounter
	 */
	public void setEncounter(Encounter encounter) {
		this.encounter = encounter;
	}

	/**
	 * @return The formFieldPath
	 */
	public String getFormFieldPath() {
		return formFieldPath;
	}

	/**
	 * @param formFieldPath The formFieldPath
	 */
	public void setFormFieldPath(String formFieldPath) {
		this.formFieldPath = formFieldPath;
	}

	/**
	 * @return The formFieldNamespace
	 */
	public String getFormFieldNamespace() {
		return formFieldNamespace;
	}

	/**
	 * @param formFieldNamespace The formFieldNamespace
	 */
	public void setFormFieldNamespace(String formFieldNamespace) {
		this.formFieldNamespace = formFieldNamespace;
	}

	/**
	 * @return The resourceVersion
	 */
	public String getResourceVersion() {
		return resourceVersion;
	}

	/**
	 * @param resourceVersion The resourceVersion
	 */
	public void setResourceVersion(String resourceVersion) {
		this.resourceVersion = resourceVersion;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public void setProvider(Provider provider) {
		this.provider = provider;
	}

	public Provider getProvider() {
		return provider;
	}

	public String getShortDiagnosisCertainty() {
		return diagnosisCertainty.split(" ")[0];
	}

	public String getDiagnosisCertainty() {
		return diagnosisCertainty;
	}

	public void setDiagnosisCertanity(String certanity) {
		this.diagnosisCertainty = certanity;
	}

	public void setDiagnosisCertainty(String diagnosisCertainty) {
		this.diagnosisCertainty = diagnosisCertainty;
	}

	public String getDiagnosisOrder() {
		return diagnosisOrder;
	}

	public void setDiagnosisOrder(String diagnosisOrder) {
		this.diagnosisOrder = diagnosisOrder;
	}

	public String getDiagnosisList() {
		return diagnosisList;
	}

	public void setDiagnosisList(String diagnosisList) {
		this.diagnosisList = diagnosisList;
	}

	public String getDiagnosisNote() {
		return diagnosisNote;
	}

	public void setDiagnosisNote(String diagnosisNote) {
		this.diagnosisNote = diagnosisNote;
	}

}
