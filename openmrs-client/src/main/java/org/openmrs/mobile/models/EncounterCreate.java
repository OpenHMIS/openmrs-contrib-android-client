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

import java.io.Serializable;
import java.util.ArrayList;

public class EncounterCreate extends Resource implements Serializable {

	@SerializedName("obs")
	@Expose
	private ArrayList<Observation> obs;

	@SerializedName("patient")
	@Expose
	private String patient;

	@SerializedName("form")
	@Expose
	private String form;

	@SerializedName("location")
	@Expose
	private String location;

	@SerializedName("voided")
	@Expose
	private String voided;

	@SerializedName("visit")
	@Expose
	private String visit;

	@SerializedName("orders")
	@Expose
	private String orders;

	@SerializedName("encounterProviders")
	@Expose
	private String encounterProviders;

	@SerializedName("auditInfo")
	@Expose
	private String auditInfo;

	@SerializedName("encounterType")
	@Expose
	private String encounterType;

	@SerializedName("encounterDatetime")
	@Expose
	private String encounterDatetime;

	public String getPatient() {
		return patient;
	}

	public void setPatient(String patient) {
		this.patient = patient;
	}

	public String getForm() {
		return form;
	}

	public void setForm(String form) {
		this.form = form;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getVoided() {
		return voided;
	}

	public void setVoided(String voided) {
		this.voided = voided;
	}

	public String getVisit() {
		return visit;
	}

	public void setVisit(String visit) {
		this.visit = visit;
	}

	public String getOrders() {
		return orders;
	}

	public void setOrders(String orders) {
		this.orders = orders;
	}

	public String getEncounterProviders() {
		return encounterProviders;
	}

	public void setEncounterProviders(String encounterProviders) {
		this.encounterProviders = encounterProviders;
	}

	public String getAuditInfo() {
		return auditInfo;
	}

	public void setAuditInfo(String auditInfo) {
		this.auditInfo = auditInfo;
	}

	public String getEncounterType() {
		return encounterType;
	}

	public void setEncounterType(String encounterType) {
		this.encounterType = encounterType;
	}

	public String getEncounterDatetime() {
		return encounterDatetime;
	}

	public void setEncounterDatetime(String encounterDatetime) {
		this.encounterDatetime = encounterDatetime;
	}

	public ArrayList<Observation> getObs() {
		return obs;
	}

	public void setObs(ArrayList<Observation> obs) {
		this.obs = obs;
	}
}
