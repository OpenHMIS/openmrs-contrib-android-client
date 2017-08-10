package org.openmrs.mobile.models;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.Table;

import org.openmrs.mobile.data.db.AppDatabase;

import java.util.Date;

@Table(database = AppDatabase.class)
public class PullSubscription extends BaseOpenmrsObject {
	@Column
	private String subscriptionClass;

	@Column
	private String subscriptionKey;

	@Column
	private Date lastSync;

	@Column
	private Integer minimumInterval;

	@Column
	private Integer maximumIncrementalCount;

	@Column
	private Boolean forceSyncAfterPush;

	public String getSubscriptionClass() {
		return subscriptionClass;
	}

	public void setSubscriptionClass(String subscriptionClass) {
		this.subscriptionClass = subscriptionClass;
	}

	public String getSubscriptionKey() {
		return subscriptionKey;
	}

	public void setSubscriptionKey(String subscriptionKey) {
		this.subscriptionKey = subscriptionKey;
	}

	public Date getLastSync() {
		return lastSync;
	}

	public void setLastSync(Date lastSync) {
		this.lastSync = lastSync;
	}

	public Integer getMinimumInterval() {
		return minimumInterval;
	}

	public void setMinimumInterval(Integer minimumInterval) {
		this.minimumInterval = minimumInterval;
	}

	public Integer getMaximumIncrementalCount() {
		return maximumIncrementalCount;
	}

	public void setMaximumIncrementalCount(Integer maximumIncrementalCount) {
		this.maximumIncrementalCount = maximumIncrementalCount;
	}

	public Boolean getForceSyncAfterPush() {
		return forceSyncAfterPush;
	}

	Boolean isForceSyncAfterPush() {
		return getForceSyncAfterPush();
	}

	public void setForceSyncAfterPush(Boolean forceSyncAfterPush) {
		this.forceSyncAfterPush = forceSyncAfterPush;
	}
}
