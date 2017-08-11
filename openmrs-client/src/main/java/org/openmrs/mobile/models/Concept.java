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

import java.util.List;

@Table(database = AppDatabase.class)
public class Concept extends BaseOpenmrsAuditableObject {
	@SerializedName("datatype")
	@Expose
	@ForeignKey
	private Datatype datatype;

	@SerializedName("description")
	@Expose
	@Column
	private String description;

	@SerializedName("conceptClass")
	@Expose
	@ForeignKey
	private ConceptClass conceptClass;

	@SerializedName("answers")
	@Expose
	private List<ConceptAnswer> answers;

	@SerializedName("name")
	@Expose
	private ConceptName name;
	
	@SerializedName("mappings")
	@Expose
	private List<ConceptMapping> mappings;
	
	@SerializedName("value")
	@Expose
	@Column
	private String value;

	@OneToMany(methods = { OneToMany.Method.ALL}, variableName = "answers", isVariablePrivate = true)
	List<ConceptAnswer> loadAnswers() {
		return loadRelatedObject(ConceptAnswer.class, answers, () -> ConceptAnswer_Table.concept_uuid.eq(getUuid()));
	}

	@OneToMany(methods = { OneToMany.Method.ALL}, variableName = "mappings", isVariablePrivate = true)
	List<ConceptMapping> loadMappings() {
		return loadRelatedObject(ConceptMapping.class, mappings, () -> ConceptMapping_Table.concept_uuid.eq(getUuid()));
	}

	@Override
	public void processRelationships() {
		super.processRelationships();

		processRelatedObjects(answers, (a) -> a.setConcept(this));
		processRelatedObjects(mappings, (m) -> m.setConcept(this));
	}

	public Datatype getDatatype() {
		return datatype;
	}

	public void setDatatype(Datatype datatype) {
		this.datatype = datatype;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public ConceptClass getConceptClass() {
		return conceptClass;
	}

	public void setConceptClass(ConceptClass conceptClass) {
		this.conceptClass = conceptClass;
	}

	public List<ConceptAnswer> getAnswers() {
		return answers;
	}

	public void setAnswers(List<ConceptAnswer> answers) {
		this.answers = answers;
	}

	public ConceptName getName() {
		return name;
	}

	public void setName(ConceptName name) {
		this.name = name;
	}

	public List<ConceptMapping> getMappings() {
		return mappings;
	}

	public void setMappings(List<ConceptMapping> mappings) {
		this.mappings = mappings;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return getDisplay();
	}
}

