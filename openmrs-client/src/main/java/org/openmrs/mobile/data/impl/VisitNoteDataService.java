package org.openmrs.mobile.data.impl;

import android.support.annotation.NonNull;

import org.openmrs.mobile.data.BaseDataService;
import org.openmrs.mobile.data.DataService;
import org.openmrs.mobile.data.DatabaseHelper;
import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.data.db.impl.EncounterDbService;
import org.openmrs.mobile.data.db.impl.ObsDbService;
import org.openmrs.mobile.data.db.impl.VisitNoteDbService;
import org.openmrs.mobile.data.rest.impl.VisitNoteRestServiceImpl;
import org.openmrs.mobile.models.EncounterDiagnosis;
import org.openmrs.mobile.models.EncounterDiagnosis_Table;
import org.openmrs.mobile.models.SyncAction;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.models.VisitNote;

import javax.inject.Inject;

public class VisitNoteDataService extends BaseDataService<VisitNote, VisitNoteDbService, VisitNoteRestServiceImpl>
		implements DataService<VisitNote> {

	private ObsDbService obsDbService;
	private EncounterDbService encounterDbService;
	private DatabaseHelper databaseHelper;

	@Inject
	public VisitNoteDataService(ObsDbService obsDbService, EncounterDbService encounterDbService,
			DatabaseHelper databaseHelper) {
		this.obsDbService = obsDbService;
		this.encounterDbService = encounterDbService;
		this.databaseHelper = databaseHelper;
	}

	public void save(VisitNote visitNote, @NonNull GetCallback<VisitNote> callback) {
		executeSingleCallback(callback,
				QueryOptions.REMOTE,
				() -> {
					// clean up encounter diagnoses. helpful if a diagnosis has been removed while offline
					databaseHelper.diffDelete(EncounterDiagnosis.class,
							EncounterDiagnosis_Table.visitNote_uuid.eq(visitNote.getUuid()),
							visitNote.getEncounterDiagnoses());

					visitNote.processRelationships();
					VisitNote result = dbService.save(visitNote);
					if (networkUtils.isOnline() != true) {
						syncLogService.save(result, SyncAction.UPDATED);
					}
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
