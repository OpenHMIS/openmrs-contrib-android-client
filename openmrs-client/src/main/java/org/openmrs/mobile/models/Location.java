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
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.Table;

import org.openmrs.mobile.data.db.AppDatabase;

@Table(database = AppDatabase.class)
public class Location extends BaseOpenmrsMetadata {
	@Expose
	private String parentLocationUuid;

	@Expose
	@Column
	private String address2;

	@Expose
	@Column
	private String address1;

	@Expose
	@Column
	private String cityVillage;

	@Expose
	@Column
	private String stateProvince;

	@Expose
	@Column
	private String country;

	@Expose
	@Column
	private String postalCode;

	@Expose
	@ForeignKey(stubbedRelationship = true)
	private Location parentLocation;

	public Location() {
	}

	public Location(String display) {
		this.display = display;
	}

	public Location(Long id, String name, String parentLocationUuid, String description, String address2, String address1,
			String cityVillage, String stateProvince, String country, String postalCode) {
		setName(name);
		this.parentLocationUuid = parentLocationUuid;
		setDescription(description);
		this.address2 = address2;
		this.address1 = address1;
		this.cityVillage = cityVillage;
		this.stateProvince = stateProvince;
		this.country = country;
		this.postalCode = postalCode;
	}

	public String getParentLocationUuid() {
		return parentLocationUuid;
	}

	public void setParentLocationUuid(String parentLocationUuid) {
		this.parentLocationUuid = parentLocationUuid;
	}

	public String getAddress2() {
		return address2;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	public String getAddress1() {
		return address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public String getCityVillage() {
		return cityVillage;
	}

	public void setCityVillage(String cityVillage) {
		this.cityVillage = cityVillage;
	}

	public String getStateProvince() {
		return stateProvince;
	}

	public void setStateProvince(String stateProvince) {
		this.stateProvince = stateProvince;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public Location getParentLocation() {
		return parentLocation;
	}

	public void setParentLocation(Location parentLocation) {
		this.parentLocation = parentLocation;
	}
}
