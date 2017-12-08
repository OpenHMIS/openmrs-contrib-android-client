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
import java.util.List;

@Table(database = AppDatabase.class)
public class QuestionOptions extends Resource implements Serializable {
	@SerializedName("rendering")
	@Expose
	@Column
	private String rendering;

	@SerializedName("concept")
	@Expose
	@Column
	private String concept;

	// For numeric values
	@SerializedName("max")
	@Expose
	@Column
	private String max;

	// For numeric values
	@SerializedName("min")
	@Expose
	@Column
	private String min;

	// For numeric values
	@SerializedName("allowDecimal")
	@Expose
	@Column
	private boolean allowDecimal;

	// For select radio boxes
	@SerializedName("answers")
	@Expose
	private List<Answer> answers;

	@OneToMany(methods = { OneToMany.Method.ALL}, variableName = "answers", isVariablePrivate = true)
	List<Answer> loadAnswers() {
		answers = loadRelatedObject(Answer.class, answers, () -> Answer_Table.questionOptions_uuid.eq(getUuid()));

		return answers;
	}

	@Override
	public void processRelationships() {
		super.processRelationships();

		processRelatedObjects(answers, (a) -> a.setQuestionOptions(this));
	}

	/**
	 * @return The rendering
	 */
	public String getRendering() {
		return rendering;
	}

	/**
	 * @param rendering The rendering
	 */
	public void setRendering(String rendering) {
		this.rendering = rendering;
	}

	/**
	 * @return The concept
	 */
	public String getConcept() {
		return concept;
	}

	/**
	 * @param concept The concept
	 */
	public void setConcept(String concept) {
		this.concept = concept;
	}

	public String getMax() {
		return max;
	}

	public void setMax(String max) {
		this.max = max;
	}

	public String getMin() {
		return min;
	}

	public void setMin(String min) {
		this.min = min;
	}

	public boolean isAllowDecimal() {
		return allowDecimal;
	}

	public void setAllowDecimal(boolean allowDecimal) {
		this.allowDecimal = allowDecimal;
	}

	public List<Answer> getAnswers() {
		return answers;
	}

	public void setAnswers(List<Answer> answers) {
		this.answers = answers;
	}
}
