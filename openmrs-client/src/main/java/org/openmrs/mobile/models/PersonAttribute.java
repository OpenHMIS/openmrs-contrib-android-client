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

import java.io.Serializable;

@Table(database = AppDatabase.class)
public class PersonAttribute extends BaseOpenmrsObject implements Serializable {
	@SerializedName("attributeType")
	@Expose
	@ForeignKey
	private PersonAttributeType attributeType;

	@SerializedName("value")
	@Expose
	private Object value;

	@Column
	private String stringValue;

	@ForeignKey(stubbedRelationship = true)
	private Concept conceptValue;

	@ForeignKey(stubbedRelationship = true)
	private Person person;

	@Override
	public void processRelationships() {
		super.processRelationships();

		if (conceptValue != null) {
			conceptValue.processRelationships();
		}
	}

	public PersonAttributeType getAttributeType() {
		return attributeType;
	}

	public void setAttributeType(PersonAttributeType attributeType) {
		this.attributeType = attributeType;
	}

	/**
	 * @return The value
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * @param value The value
	 */
	public void setValue(Object value) {
		this.value = value;

		if (value == null) {
			setConceptValue(null);
			setStringValue(null);
		} else if (value instanceof Concept) {
			setConceptValue((Concept)value);
			setStringValue(null);
		} else {
			setConceptValue(null);
			setStringValue(value.toString());
		}
	}

	public String getStringValue() {
		return stringValue;
	}

	public void setStringValue(String stringValue) {
		this.stringValue = stringValue;
	}

	public Concept getConceptValue() {
		return conceptValue;
	}

	public void setConceptValue(Concept conceptValue) {
		this.conceptValue = conceptValue;
	}

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}
}

