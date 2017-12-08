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
import com.raizlabs.android.dbflow.annotation.OneToMany;
import com.raizlabs.android.dbflow.annotation.Table;

import org.openmrs.mobile.data.db.AppDatabase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Table(database = AppDatabase.class)
public class Form extends BaseOpenmrsObject implements Serializable {
	@SerializedName("name")
	@Expose
	@Column
	private String name;

	@SerializedName("processor")
	@Expose
	@Column
	private String processor;

	@SerializedName("pages")
	@Expose
	private List<Page> pages = new ArrayList<Page>();

	@SerializedName("valueReference")
	@Expose
	@Column
	private String valueReference;

	@OneToMany(methods = { OneToMany.Method.ALL}, variableName = "pages", isVariablePrivate = true)
	List<Page> loadPages() {
		pages = loadRelatedObject(Page.class, pages, () -> Page_Table.form_uuid.eq(getUuid()));

		return pages;
	}

	@Override
	public void processRelationships() {
		super.processRelationships();

		processRelatedObjects(pages, (p) -> p.setForm(this));
	}

	/**
	 * @return The name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name The name
	 */
	public void setName(String name) {
		this.name = name;
	}

	public String getValueReference() {
		return valueReference;
	}

	public void setValueReference(String valueReference) {
		this.valueReference = valueReference;
	}

	/**
	 * @return The processor
	 */
	public String getProcessor() {
		return processor;
	}

	/**
	 * @param processor The processor
	 */
	public void setProcessor(String processor) {
		this.processor = processor;
	}

	/**
	 * @return The pages
	 */
	public List<Page> getPages() {
		return pages;
	}

	/**
	 * @param pages The pages
	 */
	public void setPages(List<Page> pages) {
		this.pages = pages;
	}
}
