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
public class Question extends Resource implements Serializable {
	@SerializedName("type")
	@Expose
	@Column
	private String type;

	@SerializedName("label")
	@Expose
	@Column
	private String label;

	@SerializedName("questionOptions")
	@Expose
	@ForeignKey
	private QuestionOptions questionOptions;

	@ForeignKey(stubbedRelationship = true)
	private Question parentQuestion;

	@ForeignKey(stubbedRelationship = true)
	private Section section;

	@SerializedName("questions")
	@Expose
	private List<Question> questions = new ArrayList<Question>();

	@OneToMany(methods = { OneToMany.Method.ALL}, variableName = "questions", isVariablePrivate = true)
	List<Question> loadQuestions() {
		questions = loadRelatedObject(Question.class, questions, () -> Question_Table.parentQuestion_uuid.eq(getUuid()));

		return questions;
	}

	@Override
	public void processRelationships() {
		super.processRelationships();

		if (questionOptions != null) {
			questionOptions.processRelationships();
		}
		processRelatedObjects(questions, (q) -> q.setParentQuestion(this));
	}

	/**
	 * @return The subscriptionClass
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type The subscriptionClass
	 */
	public void setType(String type) {
		this.type = type;
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

	/**
	 * @return The questionOptions
	 */
	public QuestionOptions getQuestionOptions() {
		return questionOptions;
	}

	/**
	 * @param questionOptions The questionOptions
	 */
	public void setQuestionOptions(QuestionOptions questionOptions) {
		this.questionOptions = questionOptions;
	}

	public Question getParentQuestion() {
		return parentQuestion;
	}

	public void setParentQuestion(Question parentQuestion) {
		this.parentQuestion = parentQuestion;
	}

	public Section getSection() {
		return section;
	}

	public void setSection(Section section) {
		this.section = section;
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
