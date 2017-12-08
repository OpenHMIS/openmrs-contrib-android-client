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
import com.raizlabs.android.dbflow.annotation.Table;

import org.openmrs.mobile.data.db.AppDatabase;

@Table(database = AppDatabase.class)
public class VisitTask extends BaseOpenmrsEntity {
	@SerializedName("status")
	@Expose
	@Column
	private VisitTaskStatus status;

	@SerializedName("visit")
	@Expose
	@ForeignKey(stubbedRelationship = true)
	private Visit visit;

	@SerializedName("name")
	@Expose
	@Column
	private String name;

	@SerializedName("patient")
	@Expose
	@ForeignKey(stubbedRelationship = true)
	private Patient patient;

	@SerializedName("closedOn")
	@Expose
	@Column
	private String closedOn;

	@Expose
	@ForeignKey(stubbedRelationship = true)
	private User closedBy;

	public VisitTaskStatus getStatus() {
		return status;
	}

	public void setStatus(VisitTaskStatus status) {
		this.status = status;
	}

	public Visit getVisit() {
		return visit;
	}

	public void setVisit(Visit visit) {
		this.visit = visit;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	public String getClosedOn() {
		return closedOn;
	}

	public void setClosedOn(String closedOn) {
		this.closedOn = closedOn;
	}

	public User getClosedBy() {
		return closedBy;
	}

	public void setClosedBy(User closedBy) {
		this.closedBy = closedBy;
	}
}
