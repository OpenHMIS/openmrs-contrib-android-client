/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and
 * limitations under the License.
 *
 * Copyright (C) OpenHMIS.  All Rights Reserved.
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

/**
 * Model class that represents a Patient List.
 */
@Table(database = AppDatabase.class)
public class PatientList extends BaseOpenmrsMetadata implements Serializable {
	@SerializedName("patientListConditions")
	@Expose
	private List<PatientListCondition> patientListConditions;

	@SerializedName("patientListOrders")
	@Expose
	private List<PatientListOrder> patientListOrders;

	@SerializedName("headerTemplate")
	@Expose
	@Column
	private String headerTemplate;

	@SerializedName("bodyTemplate")
	@Expose
	@Column
	private String bodyTemplate;

	@OneToMany(methods = { OneToMany.Method.ALL}, variableName = "patientListConditions", isVariablePrivate = true)
	List<PatientListCondition> loadConditions() {
		patientListConditions = loadRelatedObject(PatientListCondition.class, patientListConditions,
				() -> PatientListCondition_Table.patientList_uuid.eq(super.getUuid()));

		return patientListConditions;
	}

	@OneToMany(methods = { OneToMany.Method.ALL}, variableName = "patientListOrders", isVariablePrivate = true)
	List<PatientListOrder> loadOrdering() {
		patientListOrders = loadRelatedObject(PatientListOrder.class, patientListOrders,
				() -> PatientListOrder_Table.patientList_uuid.eq(super.getUuid()));

		return patientListOrders;
	}

	public List<PatientListCondition> getPatientListConditions() {
		return patientListConditions;
	}

	public void setPatientListConditions(List<PatientListCondition> patientListConditions) {
		this.patientListConditions = patientListConditions;
	}

	public List<PatientListOrder> getPatientListOrders() {
		return patientListOrders;
	}

	public void setPatientListOrders(List<PatientListOrder> patientListOrders) {
		this.patientListOrders = patientListOrders;
	}

	public String getHeaderTemplate() {
		return headerTemplate;
	}

	public void setHeaderTemplate(String headerTemplate) {
		this.headerTemplate = headerTemplate;
	}

	public String getBodyTemplate() {
		return bodyTemplate;
	}

	public void setBodyTemplate(String bodyTemplate) {
		this.bodyTemplate = bodyTemplate;
	}

	@Override
	public String toString() {
		return getName();
	}
}
