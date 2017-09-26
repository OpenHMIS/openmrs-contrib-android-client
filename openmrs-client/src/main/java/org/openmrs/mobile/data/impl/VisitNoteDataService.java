package org.openmrs.mobile.data.impl;

import android.support.annotation.NonNull;

import org.openmrs.mobile.data.BaseDataService;
import org.openmrs.mobile.data.DataService;
import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.data.RequestStrategy;
import org.openmrs.mobile.data.db.impl.EncounterDbService;
import org.openmrs.mobile.data.db.impl.ObsDbService;
import org.openmrs.mobile.data.db.impl.VisitNoteDbService;
import org.openmrs.mobile.data.rest.impl.VisitNoteRestServiceImpl;
import org.openmrs.mobile.models.SyncAction;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.models.VisitNote;

import javax.inject.Inject;

public class VisitNoteDataService extends BaseDataService<VisitNote, VisitNoteDbService, VisitNoteRestServiceImpl>
		implements DataService<VisitNote> {

	private ObsDbService obsDbService;
	private EncounterDbService encounterDbService;

	@Inject
	public VisitNoteDataService(ObsDbService obsDbService, EncounterDbService encounterDbService) {
		this.obsDbService = obsDbService;
		this.encounterDbService = encounterDbService;
	}

	public void save(VisitNote visitNote, @NonNull GetCallback<VisitNote> callback) {
		executeSingleCallback(callback,
				new QueryOptions.Builder().requestStrategy(RequestStrategy.REMOTE_THEN_LOCAL).build(),
				() -> {
					visitNote.processRelationships();
					VisitNote result = dbService.save(visitNote);
					syncLogService.save(result, SyncAction.UPDATED);
					return result;
				}, () -> restService.save(visitNote),
				(e) -> {
					// void existing obs
					if (visitNote.getEncounter() != null) {
						obsDbService.voidExistingObs(visitNote.getEncounter());
					}
					// save new obs
					e.getEncounter().processRelationships();
					encounterDbService.save(e.getEncounter());
					// visit note no longer required
					dbService.deleteLocalRelatedObjects(visitNote);
					dbService.delete(visitNote.getUuid());
				});
	}

	public VisitNote getByVisit(Visit visit) {
		return dbService.getByVisit(visit);
	}
}
