package org.openmrs.mobile.models;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.Table;

import org.openmrs.mobile.data.db.AppDatabase;

import java.util.Date;

@Table(database = AppDatabase.class)
public class PullSubscription extends BaseOpenmrsObject {
	@Column
	String subscriptionType;

	@Column
	String subscriptionKey;

	@Column
	Date lastSync;

	@Column
	Integer minimumInterval;

	@Column
	Integer maximumIncrementalCount;

	@Column
	Boolean forceSyncAfterPush;

	public String getSubscriptionType() {
		return subscriptionType;
	}

	public void setSubscriptionType(String subscriptionType) {
		this.subscriptionType = subscriptionType;
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

	public void setForceSyncAfterPush(Boolean forceSyncAfterPush) {
		this.forceSyncAfterPush = forceSyncAfterPush;
	}
}
