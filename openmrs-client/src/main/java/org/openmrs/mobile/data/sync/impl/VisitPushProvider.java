package org.openmrs.mobile.data.sync.impl;

import org.openmrs.mobile.data.db.impl.EncounterDbService;
import org.openmrs.mobile.data.db.impl.VisitDbService;
import org.openmrs.mobile.data.db.impl.VisitNoteDbService;
import org.openmrs.mobile.data.db.impl.VisitPhotoDbService;
import org.openmrs.mobile.data.db.impl.VisitTaskDbService;
import org.openmrs.mobile.data.rest.impl.VisitRestServiceImpl;
import org.openmrs.mobile.data.sync.BasePushProvider;
import org.openmrs.mobile.models.Encounter;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.models.VisitNote;
import org.openmrs.mobile.models.VisitPhoto;
import org.openmrs.mobile.models.VisitTask;

import java.util.List;

import javax.inject.Inject;

import retrofit2.Call;

public class VisitPushProvider extends BasePushProvider<Visit, VisitDbService, VisitRestServiceImpl> {
	private VisitRestServiceImpl restService;
	private VisitTaskDbService visitTaskDbService;
	private EncounterDbService encounterDbService;
	private VisitPhotoDbService visitPhotoDbService;
	private VisitNoteDbService visitNoteDbService;

	@Inject
	public VisitPushProvider(VisitDbService dbService, VisitRestServiceImpl restService,
			VisitTaskDbService visitTaskDbService, EncounterDbService encounterDbService,
			VisitPhotoDbService visitPhotoDbService, VisitNoteDbService visitNoteDbService) {
		super(dbService, restService);
		this.restService = restService;
		this.visitTaskDbService = visitTaskDbService;
		this.encounterDbService = encounterDbService;
		this.visitPhotoDbService = visitPhotoDbService;
		this.visitNoteDbService = visitNoteDbService;
	}

	@Override
	protected void deleteLocalRelatedRecords(Visit originalEntity, Visit restEntity) {
		List<VisitTask> visitTasks = visitTaskDbService.getByVisit(originalEntity.getUuid(), null, null);
		if (!visitTasks.isEmpty()) {
			for (VisitTask visitTask : visitTasks) {
				visitTask.setVisit(restEntity);
			}
			visitTaskDbService.saveAll(visitTasks);
		}

		List<Encounter> encounters = encounterDbService.getByVisit(originalEntity.getUuid(), null, null);
		if (!encounters.isEmpty()) {
			for (Encounter encounter : encounters) {
				encounter.setVisit(restEntity);
			}
			encounterDbService.saveAll(encounters);
		}

		List<VisitPhoto> visitPhotos = visitPhotoDbService.getByVisit(originalEntity.getUuid());
		if (!visitPhotos.isEmpty()) {
			for (VisitPhoto visitPhoto : visitPhotos) {
				visitPhoto.setVisit(restEntity);
			}
			visitPhotoDbService.saveAll(visitPhotos);
		}

		VisitNote visitNote = visitNoteDbService.getByVisit(originalEntity);
		if (visitNote != null) {
			visitNote.setVisit(restEntity);
			visitNoteDbService.save(visitNote);
		}

		dbService.deleteLocalRelatedObjects(originalEntity);
	}

	@Override
	protected Call<Visit> update(Visit entity) {
		return restService.updateVisit(entity);
	}

	@Override
	protected Call<Visit> purge(Visit entity) {
		return restService.endVisit(entity.getUuid(), entity);
	}
}
