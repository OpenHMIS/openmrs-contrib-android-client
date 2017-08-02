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

@Table(database = AppDatabase.class)
public class PersonName extends BaseOpenmrsEntity {
	@SerializedName("givenName")
	@Expose
	@Column
	private String givenName;

	@SerializedName("middleName")
	@Expose
	@Column
	private String middleName;

	@SerializedName("familyName")
	@Expose
	@Column
	private String familyName;

	@ForeignKey(stubbedRelationship = true)
	private Person person;

	/**
	 * @return The givenName
	 */
	public String getGivenName() {
		return givenName;
	}

	/**
	 * @param givenName The givenName
	 */
	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}

	/**
	 * @return The familyName
	 */
	public String getFamilyName() {
		return familyName;
	}

	/**
	 * @param familyName The familyName
	 */
	public void setFamilyName(String familyName) {
		this.familyName = familyName;
	}

	/**
	 * @return The middleName
	 */
	public String getMiddleName() {
		return middleName;
	}

	/**
	 * @param middleName The middleName
	 */
	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getNameString() {
		if (middleName == null || middleName.equals("null")) {
			return givenName + " " + familyName;
		} else {
			return givenName + " " + middleName + " " + familyName;
		}
	}

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}
}
