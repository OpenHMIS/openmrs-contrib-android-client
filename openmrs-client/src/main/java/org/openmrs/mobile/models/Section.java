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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Table(database = AppDatabase.class)
public class Section extends Resource implements Serializable {
	@SerializedName("label")
	@Expose
	@Column
	private String label;

	@ForeignKey(stubbedRelationship = true)
	private Page page;

	@SerializedName("questions")
	@Expose
	private List<Question> questions = new ArrayList<Question>();

	@OneToMany(methods = { OneToMany.Method.ALL}, variableName = "questions", isVariablePrivate = true)
	List<Question> loadQuestions() {
		questions = loadRelatedObject(Question.class, questions, () -> Question_Table.section_uuid.eq(getUuid()));

		return questions;
	}

	@Override
	public void processRelationships() {
		super.processRelationships();

		processRelatedObjects(questions, (q) -> q.setSection(this));
	}

	/**
	 * @return The label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @param label The label
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	public Page getPage() {
		return page;
	}

	public void setPage(Page page) {
		this.page = page;
	}

	/**
	 * @return The questions
	 */
	public List<Question> getQuestions() {
		return questions;
	}

	/**
	 * @param questions The questions
	 */
	public void setQuestions(List<Question> questions) {
		this.questions = questions;
	}
}
