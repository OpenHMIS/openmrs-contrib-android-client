package org.openmrs.mobile.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.raizlabs.android.dbflow.annotation.Column;

import java.io.Serializable;

public class BaseOpenmrsMetadata extends BaseOpenmrsAuditableObject implements Serializable {

	@SerializedName("name")
	@Expose
	@Column
	private String name;

	@SerializedName("description")
	@Expose
	@Column
	private String description;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
