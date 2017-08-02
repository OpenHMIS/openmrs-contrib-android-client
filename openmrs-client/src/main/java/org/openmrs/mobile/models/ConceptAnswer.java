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
import com.google.gson.annotations.SerializedName;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.Table;

import org.openmrs.mobile.data.db.AppDatabase;

@Table(database = AppDatabase.class)
public class ConceptAnswer extends BaseOpenmrsAuditableObject {
	/**
	 * The question concept that this object is answering
	 */
	@SerializedName("concept")
	@Expose
	@ForeignKey(stubbedRelationship = true)
	private Concept concept;

	@SerializedName("answerConcept")
	@Expose
	@ForeignKey(stubbedRelationship = true)
	private Concept answerConcept;

	public Concept getConcept() {
		return concept;
	}

	public void setConcept(Concept concept) {
		this.concept = concept;
	}

	public Concept getAnswerConcept() {
		return answerConcept;
	}

	public void setAnswerConcept(Concept answerConcept) {
		this.answerConcept = answerConcept;
	}

	@Override
	public String toString() {
		return getDisplay();
	}
}
