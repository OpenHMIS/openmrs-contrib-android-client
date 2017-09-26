package org.openmrs.mobile.models;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.Table;

import org.openmrs.mobile.data.db.AppDatabase;

@Table(database = AppDatabase.class)
public class SyncLog extends BaseOpenmrsObject {
	@Column
	String type;

	@Column
	String key;

	@Column
	SyncAction action;

	@Column
	Integer order;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public SyncAction getAction() {
		return action;
	}

	public void setAction(SyncAction action) {
		this.action = action;
	}

	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}
}
