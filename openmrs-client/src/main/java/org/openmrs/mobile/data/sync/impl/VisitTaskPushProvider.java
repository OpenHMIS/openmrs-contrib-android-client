package org.openmrs.mobile.data.sync.impl;

import org.openmrs.mobile.data.db.impl.VisitTaskDbService;
import org.openmrs.mobile.data.rest.impl.VisitTaskRestServiceImpl;
import org.openmrs.mobile.data.sync.BasePushProvider;
import org.openmrs.mobile.models.VisitTask;

import javax.inject.Inject;

public class VisitTaskPushProvider extends BasePushProvider<VisitTask, VisitTaskDbService, VisitTaskRestServiceImpl> {
	@Inject
	public VisitTaskPushProvider(VisitTaskDbService dbService, VisitTaskRestServiceImpl restService) {
		super(dbService, restService);
	}

	@Override
	protected void deleteLocalRelatedRecords(VisitTask originalEntity, VisitTask restEntity) {

	}
}
