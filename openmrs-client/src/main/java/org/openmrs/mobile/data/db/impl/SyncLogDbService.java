package org.openmrs.mobile.data.db.impl;

import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.structure.ModelAdapter;

import org.openmrs.mobile.data.db.BaseDbService;
import org.openmrs.mobile.data.db.Repository;
import org.openmrs.mobile.models.SyncLog;
import org.openmrs.mobile.models.SyncLog_Table;

import java.util.List;

import javax.inject.Inject;

public class SyncLogDbService extends BaseDbService<SyncLog> {
	@Inject
	public SyncLogDbService(Repository repository) {
		super(repository);
	}

	@Override
	protected ModelAdapter<SyncLog> getEntityTable() {
		return (SyncLog_Table)FlowManager.getInstanceAdapter(SyncLog.class);
	}

	public SyncLog getByKey(String key) {
		return repository.querySingle(getEntityTable(), SyncLog_Table.key.eq(key));
	}

	public List<SyncLog> getOrderedList() {
		return executeQuery(null, null,
				getEntityClass(), (e) -> e.orderBy(SyncLog_Table.order, true));
	}
}
