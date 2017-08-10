package org.openmrs.mobile.models;

import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.Table;

import org.openmrs.mobile.data.db.AppDatabase;
import org.openmrs.mobile.utilities.DateUtils;

import java.util.Date;

import static com.google.common.base.Preconditions.checkNotNull;

@Table(database = AppDatabase.class)
public class RecordInfo extends BaseOpenmrsObject {
	@Column
	@Expose
	private Date dateCreated;

	@Column
	@Expose
	private Date dateChanged;

	public static RecordInfo fromEntity(@NonNull BaseOpenmrsAuditableObject entity) {
		checkNotNull(entity);

		RecordInfo info = new RecordInfo();
		info.setUuid(entity.getUuid());
		info.setDateCreated(entity.getDateCreated());
		info.setDateChanged(entity.getDateChanged());

		return info;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public Date getDateChanged() {
		return dateChanged;
	}

	public void setDateChanged(Date dateChanged) {
		this.dateChanged = dateChanged;
	}

	public Date getLastUpdated() {
		Date result = null;

		if (dateCreated != null || dateChanged != null) {
			if (dateChanged == null) {
				result = dateCreated;
			} else if (dateCreated != null) {
				result = dateChanged.after(dateCreated) ? dateChanged : dateCreated;
			}
		}

		return result;
	}

	public boolean isUpdatedSince(@NonNull PullSubscription subscription) {
		checkNotNull(subscription);

		boolean result = false;
		Date updated = getLastUpdated();

		if (updated != null) {
			if (subscription.getLastSync() == null) {
				result = true;
			} else {
				result = updated.compareTo(subscription.getLastSync()) > 0;
			}
		}

		return result;
	}
}
