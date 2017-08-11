package org.openmrs.mobile.data.db.impl;

import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.structure.ModelAdapter;

import org.openmrs.mobile.data.db.BaseDbService;
import org.openmrs.mobile.data.db.Repository;
import org.openmrs.mobile.models.RecordInfo;
import org.openmrs.mobile.models.RecordInfo_Table;

import javax.inject.Inject;

public class RecordInfoDbService extends BaseDbService<RecordInfo> {
	@Inject
	public RecordInfoDbService(Repository repository) {
		super(repository);
	}

	@Override
	protected ModelAdapter<RecordInfo> getEntityTable() {
		return (RecordInfo_Table)FlowManager.getInstanceAdapter(RecordInfo.class);
	}
}
