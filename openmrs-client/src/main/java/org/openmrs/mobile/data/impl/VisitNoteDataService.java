package org.openmrs.mobile.data.impl;

import android.support.annotation.NonNull;

import org.openmrs.mobile.data.BaseDataService;
import org.openmrs.mobile.data.DataService;
import org.openmrs.mobile.data.db.impl.VisitNoteDbService;
import org.openmrs.mobile.data.rest.impl.VisitNoteRestServiceImpl;
import org.openmrs.mobile.models.SyncAction;
import org.openmrs.mobile.models.VisitNote;

import javax.inject.Inject;

public class VisitNoteDataService extends BaseDataService<VisitNote, VisitNoteDbService, VisitNoteRestServiceImpl>
		implements DataService<VisitNote> {
	@Inject
	public VisitNoteDataService() {
	}

	public void save(VisitNote visitNote, @NonNull GetCallback<VisitNote> callback) {
		executeSingleCallback(callback, null,
				() -> {
					visitNote.setUuid(VisitNote.generateUuid());
					VisitNote result = dbService.save(visitNote);
					syncLogDbService.save(createSyncLog(result, SyncAction.UPDATED));
					return result;
				},
				() -> restService.save(visitNote));
	}
}
