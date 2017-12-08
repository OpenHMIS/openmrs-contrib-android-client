package org.openmrs.mobile.data.sync.impl;

import org.openmrs.mobile.data.db.impl.EncounterDbService;
import org.openmrs.mobile.data.rest.impl.EncounterRestServiceImpl;
import org.openmrs.mobile.data.sync.BasePushProvider;
import org.openmrs.mobile.models.Encounter;

import javax.inject.Inject;

public class EncounterPushProvider extends BasePushProvider<Encounter, EncounterDbService, EncounterRestServiceImpl> {
	@Inject
	public EncounterPushProvider(EncounterDbService encounterDbService, EncounterRestServiceImpl encounterRestService) {
		super(encounterDbService, encounterRestService);
	}

	@Override
	protected void deleteLocalRelatedRecords(Encounter originalEntity, Encounter restEntity) {
		dbService.deleteLocalRelatedObjects(originalEntity);
	}
}
