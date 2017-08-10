package org.openmrs.mobile.data.db.impl;

import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.structure.ModelAdapter;

import org.openmrs.mobile.data.db.BaseMetadataDbService;
import org.openmrs.mobile.data.db.MetadataDbService;
import org.openmrs.mobile.data.db.Repository;
import org.openmrs.mobile.models.Session;
import org.openmrs.mobile.models.Session_Table;

import javax.inject.Inject;

public class SessionDbService extends BaseMetadataDbService<Session> implements MetadataDbService<Session> {
	@Inject
	public SessionDbService(Repository repository) {
		super(repository);
	}

	@Override
	protected ModelAdapter<Session> getEntityTable() {
		return (Session_Table)FlowManager.getInstanceAdapter(Session.class);
	}
}
