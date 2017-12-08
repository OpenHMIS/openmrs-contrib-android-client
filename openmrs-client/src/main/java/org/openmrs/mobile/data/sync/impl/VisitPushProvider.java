package org.openmrs.mobile.data.sync.impl;

import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.data.db.impl.EncounterDbService;
import org.openmrs.mobile.data.db.impl.VisitDbService;
import org.openmrs.mobile.data.db.impl.VisitNoteDbService;
import org.openmrs.mobile.data.db.impl.VisitPhotoDbService;
import org.openmrs.mobile.data.db.impl.VisitTaskDbService;
import org.openmrs.mobile.data.rest.impl.VisitRestServiceImpl;
import org.openmrs.mobile.data.sync.BasePushProvider;
import org.openmrs.mobile.models.Encounter;
import org.openmrs.mobile.models.SyncAction;
import org.openmrs.mobile.models.SyncLog;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.models.VisitAttribute;
import org.openmrs.mobile.models.VisitNote;
import org.openmrs.mobile.models.VisitPhoto;
import org.openmrs.mobile.models.VisitTask;

import java.util.ArrayList;
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

		List<VisitPhoto> visitPhotos = visitPhotoDbService.getByVisit(originalEntity);

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
	protected void preProcess(Visit entity) {
		// remove voided attributes
		if (!entity.getAttributes().isEmpty()) {
			List<VisitAttribute> attributes = new ArrayList<>();
			for (VisitAttribute visitAttribute : entity.getAttributes()) {
				if (visitAttribute.getVoided() == false) {
					attributes.add(visitAttribute);
				}
			}

			entity.setAttributes(attributes);
		}
	}

	@Override
	protected void postProcess(Visit originalEntity, Visit restEntity, SyncLog syncLog) {
		if (restEntity != null) {
			Visit tmpEntity = dbService.getByUuid(syncLog.getKey(), QueryOptions.FULL_REP);
			// Delete any related records with local uuids, records with the server-generated uuids will be saved when
			// saving the entity below
			deleteLocalRelatedRecords(originalEntity, restEntity);

			// Check if uuid has changed
			if (!originalEntity.getUuid().equalsIgnoreCase(restEntity.getUuid())) {
				// This will update the uuid for the current entity and also save any related objects
				dbService.update(originalEntity.getUuid(), restEntity);
			}

			if (tmpEntity.getAttributes().size() != originalEntity.getAttributes().size()) {
				// void existing attributes
				dbService.voidExistingVisitAttributes(originalEntity);

				dbService.save(originalEntity);
			}
		}
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
