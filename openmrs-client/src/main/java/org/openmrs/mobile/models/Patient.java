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
import com.raizlabs.android.dbflow.annotation.OneToMany;
import com.raizlabs.android.dbflow.annotation.Table;

import org.openmrs.mobile.data.db.AppDatabase;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Table(database = AppDatabase.class)
public class Patient extends BaseOpenmrsAuditableObject implements Serializable {
	private static final long serialVersionUID = 1L;

	private String encounters = ApplicationConstants.EMPTY_STRING;

	@SerializedName("identifiers")
	@Expose
	private List<PatientIdentifier> identifiers = new ArrayList<PatientIdentifier>();

	@SerializedName("person")
	@Expose
	@ForeignKey(saveForeignKeyModel = true, deleteForeignKeyModel = true)
	private Person person;

	@SerializedName("voided")
	@Expose
	@Column
	private Boolean voided;

	@SerializedName("resourceVersion")
	@Expose
	@Column
	private String resourceVersion;

	@OneToMany(methods = { OneToMany.Method.ALL }, variableName = "identifiers")
	List<PatientIdentifier> loadIdentifiers() {
		identifiers = loadRelatedObject(PatientIdentifier.class, identifiers,
				() -> PatientIdentifier_Table.patient_uuid.eq(getUuid()));

		return identifiers;
	}

	@Override
	public void processRelationships() {
		super.processRelationships();

		if (person != null) {
			person.processRelationships();
		}
		processRelatedObjects(identifiers, (i) -> i.setPatient(this));
	}

	/**
	 * @return The identifiers
	 */
	public List<PatientIdentifier> getIdentifiers() {
		return identifiers;
	}

	/**
	 * @param identifiers The identifiers
	 */
	public void setIdentifiers(List<PatientIdentifier> identifiers) {
		this.identifiers = identifiers;
	}

	public PatientIdentifier getIdentifier() {
		if (!identifiers.isEmpty()) {
			return identifiers.get(0);
		} else {
			return null;
		}
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

	public boolean isSynced() {
		return !StringUtils.isBlank(getUuid());
		//Keeping it this way until the synced flag can be made to work
	}

	public String getEncounters() {
		return encounters;
	}

	public void setEncounters(String encounters) {
		this.encounters = encounters;
	}

	public void addEncounters(Long encid) {
		this.encounters += encid + ",";
	}

	public Map<String, String> toMap() {
		Map<String, String> map = new HashMap<>();
		puToMapIfNotNull(map, "givenname", person.getName().getGivenName());
		puToMapIfNotNull(map, "middlename", person.getName().getMiddleName());
		puToMapIfNotNull(map, "familyname", person.getName().getFamilyName());
		puToMapIfNotNull(map, "gender", person.getGender());
		puToMapIfNotNull(map, "birthdate", person.getBirthdate());
		puToMapIfNotNull(map, "address1", person.getAddress().getAddress1());
		puToMapIfNotNull(map, "address2", person.getAddress().getAddress2());
		puToMapIfNotNull(map, "city", person.getAddress().getCityVillage());
		puToMapIfNotNull(map, "state", person.getAddress().getStateProvince());
		puToMapIfNotNull(map, "postalcode", person.getAddress().getPostalCode());
		puToMapIfNotNull(map, "country", person.getAddress().getCountry());
		return map;
	}

	private void puToMapIfNotNull(Map<String, String> map, String key, String value) {
		if (StringUtils.notNull(value)) {
			map.put(key, value);
		}
	}
}
