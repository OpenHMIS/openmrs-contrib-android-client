package org.openmrs.mobile.data.sync.impl;

import android.support.annotation.NonNull;
import android.util.Log;

import com.raizlabs.android.dbflow.sql.language.SQLOperator;

import org.openmrs.mobile.data.DatabaseHelper;
import org.openmrs.mobile.data.PagingInfo;
import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.data.db.impl.EncounterDbService;
import org.openmrs.mobile.data.db.impl.ObsDbService;
import org.openmrs.mobile.data.db.impl.VisitDbService;
import org.openmrs.mobile.data.db.impl.VisitPhotoDbService;
import org.openmrs.mobile.data.db.impl.VisitTaskDbService;
import org.openmrs.mobile.data.rest.RestConstants;
import org.openmrs.mobile.data.rest.RestHelper;
import org.openmrs.mobile.data.rest.impl.EncounterRestServiceImpl;
import org.openmrs.mobile.data.rest.impl.ObsRestServiceImpl;
import org.openmrs.mobile.data.rest.impl.VisitPhotoRestServiceImpl;
import org.openmrs.mobile.data.rest.impl.VisitRestServiceImpl;
import org.openmrs.mobile.data.rest.impl.VisitTaskRestServiceImpl;
import org.openmrs.mobile.models.Encounter;
import org.openmrs.mobile.models.Encounter_Table;
import org.openmrs.mobile.models.Observation;
import org.openmrs.mobile.models.Observation_Table;
import org.openmrs.mobile.models.PullSubscription;
import org.openmrs.mobile.models.RecordInfo;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.models.VisitPhoto;
import org.openmrs.mobile.models.VisitTask;
import org.openmrs.mobile.models.VisitTask_Table;
import org.openmrs.mobile.models.Visit_Table;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.DateUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import okhttp3.ResponseBody;

import static com.google.common.base.Preconditions.checkNotNull;

public class VisitPullProvider {
	private static final String TAG = VisitPullProvider.class.getSimpleName();

	private VisitDbService visitDbService;
	private VisitRestServiceImpl visitRestService;
	private EncounterDbService encounterDbService;
	private EncounterRestServiceImpl encounterRestService;
	private ObsDbService obsDbService;
	private ObsRestServiceImpl obsRestService;
	private VisitPhotoDbService visitPhotoDbService;
	private VisitPhotoRestServiceImpl visitPhotoRestService;
	private VisitTaskDbService visitTaskDbService;
	private VisitTaskRestServiceImpl visitTaskRestService;

	private DatabaseHelper databaseHelper;

	@Inject
	public VisitPullProvider(VisitDbService visitDbService,
			VisitRestServiceImpl visitRestService, EncounterDbService encounterDbService,
			EncounterRestServiceImpl encounterRestService, ObsDbService obsDbService, ObsRestServiceImpl obsRestService,
			VisitPhotoDbService visitPhotoDbService, VisitPhotoRestServiceImpl visitPhotoRestService,
			VisitTaskDbService visitTaskDbService, VisitTaskRestServiceImpl visitTaskRestService,
			DatabaseHelper databaseHelper) {
		this.visitDbService = visitDbService;
		this.visitRestService = visitRestService;
		this.encounterDbService = encounterDbService;
		this.encounterRestService = encounterRestService;
		this.obsDbService = obsDbService;
		this.obsRestService = obsRestService;
		this.visitPhotoDbService = visitPhotoDbService;
		this.visitPhotoRestService = visitPhotoRestService;
		this.visitTaskDbService = visitTaskDbService;
		this.visitTaskRestService = visitTaskRestService;
		this.databaseHelper = databaseHelper;
	}

	public void pull(@NonNull PullSubscription subscription, @NonNull List<RecordInfo> patientInfo) {
		checkNotNull(subscription);
		checkNotNull(patientInfo);

		for (RecordInfo patientRecord : patientInfo) {
			List<RecordInfo> visitInfo = pullVisits(subscription, patientRecord);

			pullVisitTasks(subscription, patientRecord, visitInfo);

			pullVisitDocuments(subscription, patientRecord, visitInfo);

			pullVisitEncounters(subscription, patientRecord, visitInfo);
		}
	}

	protected List<RecordInfo> pullVisits(@NonNull PullSubscription subscription, RecordInfo patientRecord) {
		// Get record info for patient visits
		List<RecordInfo> visitInfo = RestHelper.getCallListValue(
				visitRestService.getRecordInfoByPatient(patientRecord.getUuid(),
						new QueryOptions.Builder().includeInactive(true).build(), PagingInfo.ALL));

		// Delete any missing visits
		databaseHelper.diffDelete(Visit.class, Visit_Table.patient_uuid.eq(patientRecord.getUuid()), visitInfo);

		// Pull any updated visit information
		List<RecordInfo> checkedVisits = new ArrayList<>();
		List<Visit> visits = new ArrayList<>();
		for (int i = 0; i < ApplicationConstants.Request.PATIENT_VISIT_COUNT && i < visitInfo.size(); i++) {
			RecordInfo visitRecord = visitInfo.get(i);
			if (visitRecord.isUpdatedSince(subscription)) {
				Visit visit = RestHelper.getCallValue(
						visitRestService.getByUuid(visitRecord.getUuid(), QueryOptions.INCLUDE_ALL_FULL_REP));

				visit.processRelationships();
				visits.add(visit);
			}

			checkedVisits.add(visitRecord);
		}

		// Save the updated visits
		if (!visits.isEmpty()) {
			visitDbService.saveAll(visits);
		}

		return checkedVisits;
	}

	protected void pullVisitTasks(PullSubscription subscription, RecordInfo patientRecord, List<RecordInfo> visitInfo) {
		QueryOptions options = new QueryOptions.Builder().customRepresentation(RestConstants.Representations.VISIT_TASKS)
				.build();

		for (RecordInfo visitRecord : visitInfo) {
			List<RecordInfo> visitTasks = RestHelper.getCallListValue(visitTaskRestService.getRecordInfoByVisit
					(visitRecord.getUuid(), null, PagingInfo.ALL));

			databaseHelper.diffDelete(VisitTask.class, VisitTask_Table.visit_uuid.eq(visitRecord.getUuid()), visitTasks);

			List<VisitTask> visitTaskList = new ArrayList<>();
			for (RecordInfo visitTasksRecord : visitTasks) {
				if (visitTasksRecord.isUpdatedSince(subscription)) {
					VisitTask visitTask = RestHelper.getCallValue(visitTaskRestService.getByUuid(visitTasksRecord.getUuid
							(), options));
					visitTask.processRelationships();
					visitTaskList.add(visitTask);
				}
			}

			if (!visitTaskList.isEmpty()) {
				visitTaskDbService.saveAll(visitTaskList);
			}
		}
	}

	protected void pullVisitDocuments(PullSubscription subscription, RecordInfo patientRecord, List<RecordInfo> visitInfo) {
		QueryOptions options =
				new QueryOptions.Builder().customRepresentation(RestConstants.Representations.OBSERVATION).build();

		for (RecordInfo recordInfo : visitInfo) {
			List<RecordInfo> observationInfo = RestHelper.getCallListValue(obsRestService
					.getVisitDocumentsObsRecordInfoByPatientAndConceptList(patientRecord.getUuid()));

			List<SQLOperator> observationsOperators = new ArrayList<>();
			observationsOperators.add(Observation_Table.uuid.eq(recordInfo.getUuid()));
			observationsOperators.add(Observation_Table.concept_uuid.in(Arrays.asList(ApplicationConstants.ConceptSets
					.VISIT_DOCUMENT_UUID.split(","))));

			databaseHelper
					.diffDelete(Observation.class, observationsOperators, observationInfo);

			List<VisitPhoto> visitPhotos = new ArrayList<>();
			List<Observation> observations = new ArrayList<>();
			for (RecordInfo observationRecord : observationInfo) {
				if (observationRecord.isUpdatedSince(subscription)) {
					Observation observation =
							RestHelper.getCallValue(obsRestService.getByUuid(observationRecord.getUuid(), options));
					observation.processRelationships();
					observations.add(observation);

					Visit visit = new Visit();
					visit.setUuid(recordInfo.getUuid());

					VisitPhoto visitPhoto = new VisitPhoto();
					visitPhoto.setVisit(visit);
					visitPhoto.setObservation(observation);
					visitPhoto.setFileCaption(observation.getComment());
					visitPhoto.setDateCreated(new Date(DateUtils.convertTime(observation.getObsDatetime())));
					visitPhoto.setCreator(observation.getCreator());
					visitPhotos.add(visitPhoto);
				}
			}

			if (!observations.isEmpty()) {
				obsDbService.saveAll(observations);
			}

			for (VisitPhoto visitPhotoRecord : visitPhotos) {
				ResponseBody image = RestHelper.getCallValue(
						visitPhotoRestService.downloadPhoto(visitPhotoRecord.getObservation().getUuid(), null));
				try {
					visitPhotoRecord.setImage(image.bytes());
				} catch (IOException ex) {
					Log.e(TAG, "Visit image could not be saved.", ex);
				}
			}

			if (!visitPhotos.isEmpty()) {
				visitPhotoDbService.saveAll(visitPhotos);
			}
		}
	}

	protected void pullVisitEncounters(PullSubscription subscription, RecordInfo patientRecord, List<RecordInfo>
			visitInfo) {
		QueryOptions options = new QueryOptions.Builder()
				.customRepresentation(RestConstants.Representations.VISIT_ENCOUNTER)
				.build();

		for (RecordInfo visitRecord : visitInfo) {
			// Get record info for visit encounters
			List<RecordInfo> encounterInfo = RestHelper.getCallListValue(
					encounterRestService.getRecordInfoByVisit(visitRecord.getUuid(), null, PagingInfo.ALL));

			// Delete any missing encounters
			databaseHelper.diffDelete(Encounter.class, Encounter_Table.visit_uuid.eq(visitRecord.getUuid()), encounterInfo);

			// Pull any updated encounter information
			List<Encounter> encounters = new ArrayList<>();
			for (RecordInfo encounterRecord : encounterInfo) {
				if (encounterRecord.isUpdatedSince(subscription)) {
					Encounter encounter = RestHelper.getCallValue(
							encounterRestService.getByUuid(encounterRecord.getUuid(), options));

					encounter.processRelationships();
					encounters.add(encounter);
				}
			}

			// Save the updated encounters
			if (!encounters.isEmpty()) {
				encounterDbService.saveAll(encounters);
			}
		}
	}
}
