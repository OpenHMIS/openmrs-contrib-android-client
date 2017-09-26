package org.openmrs.mobile.data.sync.impl;

import org.openmrs.mobile.data.db.impl.EncounterDbService;
import org.openmrs.mobile.data.db.impl.ObsDbService;
import org.openmrs.mobile.data.db.impl.VisitNoteDbService;
import org.openmrs.mobile.data.rest.impl.VisitNoteRestServiceImpl;
import org.openmrs.mobile.data.sync.BasePushProvider;
import org.openmrs.mobile.models.SyncLog;
import org.openmrs.mobile.models.VisitNote;

import javax.inject.Inject;

import retrofit2.Call;

public class VisitNotePushProvider extends BasePushProvider<VisitNote, VisitNoteDbService, VisitNoteRestServiceImpl> {
	private VisitNoteRestServiceImpl restService;
	private ObsDbService obsDbService;
	private EncounterDbService encounterDbService;

	@Inject
	public VisitNotePushProvider(VisitNoteDbService dbService, VisitNoteRestServiceImpl restService,
			ObsDbService obsDbService, EncounterDbService encounterDbService) {
		super(dbService, restService);

		this.restService = restService;
		this.obsDbService = obsDbService;
		this.encounterDbService = encounterDbService;
	}

	@Override
	protected void deleteLocalRelatedRecords(VisitNote originalEntity, VisitNote restEntity) {
	}
	
	@Override
	protected Call<VisitNote> update(VisitNote entity) {
		return restService.save(entity);
	}

	@Override
	protected void postProcess(VisitNote originalEntity, VisitNote restEntity, SyncLog syncLog) {
		// void existing obs
		if(originalEntity.getEncounter() != null) {
			obsDbService.voidExistingObs(originalEntity.getEncounter());
		}
		// save new obs
		encounterDbService.save(restEntity.getEncounter());

		// visit note no longer required
		dbService.deleteLocalRelatedObjects(originalEntity);
		dbService.delete(syncLog.getKey());
	}
}
