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
package org.openmrs.mobile.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.OneToMany;
import com.raizlabs.android.dbflow.annotation.Table;

import org.openmrs.mobile.data.db.AppDatabase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Table(database = AppDatabase.class)
public class Visit extends BaseOpenmrsEntity implements Serializable {
	@SerializedName("visitType")
	@Expose
	@ForeignKey(saveForeignKeyModel = true)
	private VisitType visitType;

	@SerializedName("location")
	@Expose
	@ForeignKey(saveForeignKeyModel = true)
	private Location location;

	@SerializedName("startDatetime")
	@Expose
	@Column
	private Date startDatetime;

	@SerializedName("stopDatetime")
	@Expose
	@Column
	private Date stopDatetime;

	@SerializedName("encounters")
	@Expose
	private List<Encounter> encounters = new ArrayList<>();

	@SerializedName("attributes")
	@Expose
	private List<VisitAttribute> attributes = new ArrayList<>();

	@Expose
	@SerializedName("patient")
	@ForeignKey(stubbedRelationship = true)
	private Patient patient;

	public Visit() { }

    public Visit(String visitUuid) {
		this.uuid = visitUuid;
	}

	public Visit(Date stopDatetime) {
		this.stopDatetime = stopDatetime;
	}

	@OneToMany(methods = { OneToMany.Method.ALL}, variableName = "encounters", isVariablePrivate = true)
	List<Encounter> loadEncounters() {
		encounters = loadRelatedObject(Encounter.class, encounters, () -> Encounter_Table.visit_uuid.eq(getUuid()));

		return encounters;
	}

	@OneToMany(methods = { OneToMany.Method.ALL}, variableName = "attributes", isVariablePrivate = true)
	List<VisitAttribute> loadAttributes() {
		attributes =loadRelatedObject(VisitAttribute.class, attributes, () -> VisitAttribute_Table.visit_uuid.eq(getUuid()));

		return attributes;
	}

	@Override
	public void processRelationships() {
		super.processRelationships();

		processRelatedObjects(encounters, (e) -> e.setVisit(this));
		processRelatedObjects(attributes, (a) -> a.setVisit(this));
	}

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	public VisitType getVisitType() {
		return visitType;
	}

	public void setVisitType(VisitType visitType) {
		this.visitType = visitType;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public Date getStartDatetime() {
		return startDatetime;
	}

	public void setStartDatetime(Date startDatetime) {
		this.startDatetime = startDatetime;
	}

	public Date getStopDatetime() {
		return stopDatetime;
	}

	public void setStopDatetime(Date stopDatetime) {
		this.stopDatetime = stopDatetime;
	}

	public List<Encounter> getEncounters() {
		return encounters;
	}

	public void setEncounters(List<Encounter> encounters) {
		this.encounters = encounters;
	}

	public List<VisitAttribute> getAttributes() {
		return attributes;
	}

	public void setAttributes(List<VisitAttribute> attributes) {
		this.attributes = attributes;
	}

}
