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
import com.raizlabs.android.dbflow.annotation.ManyToMany;
import com.raizlabs.android.dbflow.annotation.OneToMany;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.openmrs.mobile.data.db.AppDatabase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Table(database = AppDatabase.class)
@ManyToMany(referencedTable = Provider.class)
public class Encounter extends BaseOpenmrsAuditableObject implements Serializable {
	@SerializedName("encounterDatetime")
	@Expose
	@Column
	private Date encounterDatetime;

	@SerializedName("patient")
	@Expose
	@ForeignKey(stubbedRelationship = true)
	private Patient patient;

	@SerializedName("location")
	@Expose
	@ForeignKey(stubbedRelationship = true)
	private Location location;

	@SerializedName("form")
	@Expose
	@ForeignKey(stubbedRelationship = true)
	private Form form;

	@SerializedName("encounterType")
	@Expose
	@ForeignKey(saveForeignKeyModel = true)
	private EncounterType encounterType;

	@SerializedName("obs")
	@Expose
	private List<Observation> obs = new ArrayList<>();

	@SerializedName("orders")
	@Expose
	private List<Object> orders = new ArrayList<>();

	@SerializedName("voided")
	@Expose
	@Column
	private Boolean voided;

	@SerializedName("visit")
	@Expose
	@ForeignKey(stubbedRelationship = true)
	private Visit visit;

	@SerializedName("encounterProviders")
	@Expose
	private List<Provider> encounterProviders = new ArrayList<Provider>();

	@SerializedName("provider")
	@Expose
	private String provider;

	@SerializedName("resourceVersion")
	@Expose
	private String resourceVersion;

	@OneToMany(methods = { OneToMany.Method.ALL}, variableName = "obs", isVariablePrivate = true)
	List<Observation> loadObservations() {
		obs = loadRelatedObject(Observation.class, obs, () -> Observation_Table.encounter_uuid.eq(getUuid()));
		return obs;
	}

	@Override
	public void processRelationships() {
		super.processRelationships();

		if (form != null) {
			form.processRelationships();
		}

		processRelatedObjects(obs, (e) -> e.setEncounter(this));
	}

	/**
	 * @return The uuid
	 */
	public String getUuid() {
		return uuid;
	}

	/**
	 * @param uuid The uuid
	 */
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	/**
	 * @return The display
	 */
	public String getDisplay() {
		return display;
	}

	/**
	 * @param display The display
	 */
	public void setDisplay(String display) {
		this.display = display;
	}

	/**
	 * @return The encounterDatetime
	 */
	public Date getEncounterDatetime() {
		return encounterDatetime;
	}

	/**
	 * @param encounterDatetime The encounterDatetime
	 */
	public void setEncounterDatetime(Date encounterDatetime) {
		this.encounterDatetime = encounterDatetime;
	}

	/**
	 * @return The patient
	 */
	public Patient getPatient() {
		return patient;
	}

	/**
	 * @param patient The patient
	 */
	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	/**
	 * @return The location
	 */
	public Location getLocation() {
		return location;
	}

	/**
	 * @param location The location
	 */
	public void setLocation(Location location) {
		this.location = location;
	}

	/**
	 * @return The form
	 */
	public Form getForm() {
		return form;
	}

	/**
	 * @param form The form
	 */
	public void setForm(Form form) {
		this.form = form;
	}

	/**
	 * @return The encounterTypeToken
	 */
	public EncounterType getEncounterType() {
		return encounterType;
	}

	public void setEncounterType(EncounterType encounterType) {
		this.encounterType = encounterType;
	}

	/**
	 * @return The obs
	 */
	public List<Observation> getObs() {
		return obs;
	}

	/**
	 * The obs
	 */
	public void setObs(List<Observation> obs) {
		this.obs = obs;
	}

	/**
	 * @return The orders
	 */
	public List<Object> getOrders() {
		return orders;
	}

	/**
	 * @param orders The orders
	 */
	public void setOrders(List<Object> orders) {
		this.orders = orders;
	}

	/**
	 * @return The voided
	 */
	public Boolean getVoided() {
		return voided;
	}

	Boolean isVoided() {
		return getVoided();
	}

	/**
	 * @param voided The voided
	 */
	public void setVoided(Boolean voided) {
		this.voided = voided;
	}

	/**
	 * @return The visit
	 */
	public Visit getVisit() {
		return visit;
	}

	/**
	 * @param visit The visit
	 */
	public void setVisit(Visit visit) {
		this.visit = visit;
	}

	/**
	 * @return The encounterProviders
	 */
	public List<Provider> getEncounterProviders() {
		if (encounterProviders == null || encounterProviders.isEmpty()) {
			List<Encounter_Provider> providers = SQLite.select()
					.from(Encounter_Provider.class)
					.where(Encounter_Provider_Table.encounter_uuid.eq(getUuid()))
					.queryList();

			for (Encounter_Provider join : providers) {
				encounterProviders.add(join.getProvider());
			}
		}

		return encounterProviders;
	}

	/**
	 * @param encounterProviders The encounterProviders
	 */
	public void setEncounterProviders(List<Provider> encounterProviders) {
		this.encounterProviders = encounterProviders;

		// Clear out the encounter providers and save them
		SQLite.delete(Encounter_Provider.class).where(Encounter_Provider_Table.encounter_uuid.eq(getUuid()));

		// Add the providers for the encounter
		if (encounterProviders != null && !encounterProviders.isEmpty()) {
			for (Provider provider : encounterProviders) {
				Encounter_Provider join = new Encounter_Provider();
				join.setEncounter(this);
				join.setProvider(provider);

				join.save();
			}
		}
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

	public String getFormUuid() {
		if (form != null)
			return form.getUuid();
		else
			return null;
	}

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}
}
