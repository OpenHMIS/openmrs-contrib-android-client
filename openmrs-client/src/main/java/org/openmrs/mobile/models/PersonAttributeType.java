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
public class PersonAttributeType extends BaseOpenmrsMetadata {
	@Expose
	@SerializedName("concept")
	@ForeignKey(stubbedRelationship = true)
	private Concept concept;

	@Expose
	@SerializedName("format")
	@Column
	private String format;

	@Expose
	@SerializedName("foreignKey")
	@Column
	private Integer foreignKey;

	@Expose
	@SerializedName("sortWeight")
	@Column
	private Double sortWeight;

	@Expose
	@SerializedName("searchable")
	@Column
	private Boolean searchable = false;

	@Expose
	@SerializedName("editPrivilege")
	@ForeignKey(stubbedRelationship = true)
	private Privilege editPrivilege;

	public Concept getConcept() {
		return concept;
	}

	public void setConcept(Concept concept) {
		this.concept = concept;
	}

	/**
	 * @return Returns the format.
	 */
	public String getFormat() {
		return format;
	}

	/**
	 * @param format The format to set.
	 */
	public void setFormat(String format) {
		this.format = format;
	}

	/**
	 * @return the foreignKey
	 */
	public Integer getForeignKey() {
		return foreignKey;
	}

	/**
	 * @param foreignKey the foreignKey to set
	 */
	public void setForeignKey(Integer foreignKey) {
		this.foreignKey = foreignKey;
	}

	/**
	 * @return the sortWeight
	 */
	public Double getSortWeight() {
		return sortWeight;
	}

	/**
	 * @param sortWeight the formOrder to set
	 */
	public void setSortWeight(Double sortWeight) {
		this.sortWeight = sortWeight;
	}

	/**
	 * @return the searchable status
	 */
	public Boolean isSearchable() {
		return getSearchable();
	}

	public Boolean getSearchable() {
		return searchable;
	}

	/**
	 * @param searchable the searchable to set
	 */
	public void setSearchable(Boolean searchable) {
		this.searchable = searchable;
	}

	/**
	 * The privilege required in order to edit this attribute
	 * @return Returns the required privilege
	 * @since 1.5
	 */
	public Privilege getEditPrivilege() {
		return editPrivilege;
	}

	/**
	 * The privilege required in order to edit this attribute If <code>editPrivilege</code> is null,
	 * no extra permissions are required to edit this attribute
	 * @param editPrivilege
	 * @since 1.5
	 */
	public void setEditPrivilege(Privilege editPrivilege) {
		this.editPrivilege = editPrivilege;
	}

}
