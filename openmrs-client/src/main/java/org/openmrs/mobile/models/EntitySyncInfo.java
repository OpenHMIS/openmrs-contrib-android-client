package org.openmrs.mobile.models;

import java.util.Date;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.Table;
import org.openmrs.mobile.data.db.AppDatabase;

@Table(database = AppDatabase.class)
public class EntitySyncInfo extends BaseOpenmrsObject {
	@Column
	private String entityUuid;

	@Column
	private String entityClass;

	@Column
	private Date lastSync;

	public String getEntityUuid() {
		return entityUuid;
	}

	public void setEntityUuid(String entityUuid) {
		this.entityUuid = entityUuid;
	}

	public String getEntityClass() {
		return entityClass;
	}

	public void setEntityClass(String entityClass) {
		this.entityClass = entityClass;
	}

	public Date getLastSync() {
		return lastSync;
	}

	public void setLastSync(Date lastSync) {
		this.lastSync = lastSync;
	}
}
