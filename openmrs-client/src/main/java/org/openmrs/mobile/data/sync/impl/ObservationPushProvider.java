package org.openmrs.mobile.data.sync.impl;

import org.openmrs.mobile.data.db.impl.ObsDbService;
import org.openmrs.mobile.data.rest.impl.ObsRestServiceImpl;
import org.openmrs.mobile.data.sync.BasePushProvider;
import org.openmrs.mobile.models.Observation;

import javax.inject.Inject;

public class ObservationPushProvider extends BasePushProvider<Observation, ObsDbService, ObsRestServiceImpl> {
	@Inject
	public ObservationPushProvider(ObsDbService dbService, ObsRestServiceImpl restService) {
		super(dbService, restService);
	}

	@Override
	protected void deleteLocalRelatedRecords(Observation originalEntity, Observation restEntity) {

	}
}
