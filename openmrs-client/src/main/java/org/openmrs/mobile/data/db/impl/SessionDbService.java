package org.openmrs.mobile.data.db.impl;

import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.structure.ModelAdapter;

import org.openmrs.mobile.data.db.BaseMetadataDbService;
import org.openmrs.mobile.data.db.MetadataDbService;
import org.openmrs.mobile.models.Session;
import org.openmrs.mobile.models.Session_Table;

public class SessionDbService extends BaseMetadataDbService<Session> implements MetadataDbService<Session> {
	@Override
	protected ModelAdapter<Session> getEntityTable() {
		return (Session_Table)FlowManager.getInstanceAdapter(Session.class);
	}
}
