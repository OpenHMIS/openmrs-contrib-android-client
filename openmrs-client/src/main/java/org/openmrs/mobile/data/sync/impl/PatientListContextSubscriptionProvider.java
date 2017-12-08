package org.openmrs.mobile.data.sync.impl;

import org.greenrobot.eventbus.EventBus;
import org.openmrs.mobile.data.DatabaseHelper;
import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.data.db.impl.EntitySyncInfoDbService;
import org.openmrs.mobile.data.db.impl.PatientDbService;
import org.openmrs.mobile.data.db.impl.PatientListContextDbService;
import org.openmrs.mobile.data.db.impl.PatientListDbService;
import org.openmrs.mobile.data.db.impl.VisitDbService;
import org.openmrs.mobile.data.rest.RestConstants;
import org.openmrs.mobile.data.rest.RestHelper;
import org.openmrs.mobile.data.rest.impl.PatientListContextRestServiceImpl;
import org.openmrs.mobile.data.sync.BaseSubscriptionProvider;
import org.openmrs.mobile.event.SyncPullEvent;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.PatientListContext;
import org.openmrs.mobile.models.PatientListContext_Table;
import org.openmrs.mobile.models.PullSubscription;
import org.openmrs.mobile.models.RecordInfo;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

public class PatientListContextSubscriptionProvider extends BaseSubscriptionProvider {
	private PatientListContextDbService listPatientDbService;
	private PatientListContextRestServiceImpl listPatientRestService;
	private PatientListDbService patientListDbService;
	private PatientDbService patientDbService;
	private VisitDbService visitDbService;
	private EntitySyncInfoDbService entitySyncInfoDbService;

	private DatabaseHelper databaseHelper;

	private PatientPullProvider patientListPullProvider;
	private VisitPullProvider visitPullProvider;
	private EventBus eventBus;

	private String patientListUuid;

	@Inject
	public PatientListContextSubscriptionProvider(PatientListContextDbService dbService,
			PatientListContextRestServiceImpl restService, PatientPullProvider patientPullProvider,
			VisitPullProvider visitPullProvider, DatabaseHelper databaseHelper, EventBus eventBus,
			PatientListDbService patientListDbService, PatientDbService patientDbService, VisitDbService visitDbService,
			EntitySyncInfoDbService entitySyncInfoDbService) {

		this.listPatientDbService = dbService;
		this.listPatientRestService = restService;
		this.patientListPullProvider = patientPullProvider;
		this.visitPullProvider = visitPullProvider;
		this.databaseHelper = databaseHelper;
		this.eventBus = eventBus;
		this.entitySyncInfoDbService = entitySyncInfoDbService;

		this.patientListDbService = patientListDbService;
		this.patientDbService = patientDbService;
		this.visitDbService = visitDbService;
	}

	@Override
	public void initialize(PullSubscription subscription) {
		super.initialize(subscription);

		// Get list key
		patientListUuid = subscription.getSubscriptionKey();
	}

	@Override
	public void pull(PullSubscription subscription) {
		if (StringUtils.isBlank(patientListUuid)) {
			return;
		}

		// Get list patients
		QueryOptions options = new QueryOptions.Builder()
				.customRepresentation(RestConstants.Representations.PATIENT_LIST_PATIENTS_CONTEXT)
				.build();
		List<PatientListContext> patientListContexts = RestHelper.getCallListValue(
				listPatientRestService.getListPatients(patientListUuid, options, null));

		// Create record info records
		int size = patientListContexts == null ? 0 : patientListContexts.size();
		List<RecordInfo> patientRecordInfoList = new ArrayList<>(size);
		if (patientListContexts != null && !patientListContexts.isEmpty()) {
			for (PatientListContext patient : patientListContexts) {
				RecordInfo record = RecordInfo.fromEntity(patient.getPatient());
				patientRecordInfoList.add(record);
			}
		}

		// Delete context records that are no longer in patient list
		databaseHelper.diffDelete(PatientListContext.class, PatientListContext_Table.patientList_uuid.eq(patientListUuid),
				patientRecordInfoList);

		if (patientListContexts == null || patientListContexts.isEmpty()) {
			return;
		}

		String syncEntityName = "patients";
		eventBus.post(new SyncPullEvent(ApplicationConstants.EventMessages.Sync.Pull.ENTITY_REMOTE_PULL_STARTING,
				syncEntityName, null));
		// Pull patient information
		patientListPullProvider.pull(subscription, patientRecordInfoList);
		eventBus.post(new SyncPullEvent(ApplicationConstants.EventMessages.Sync.Pull.ENTITY_REMOTE_PULL_COMPLETE,
				syncEntityName, null));

		syncEntityName = "visits";
		eventBus.post(new SyncPullEvent(ApplicationConstants.EventMessages.Sync.Pull.ENTITY_REMOTE_PULL_STARTING,
				syncEntityName, null));
		// Pull visit information
		visitPullProvider.pull(subscription, patientRecordInfoList);
		eventBus.post(new SyncPullEvent(ApplicationConstants.EventMessages.Sync.Pull.ENTITY_REMOTE_PULL_COMPLETE,
				syncEntityName, null));

		savePatientLastSyncInfo(patientRecordInfoList);

		// Insert/update context records
		loadPatientListContextRelationshipsAndSave(patientListContexts);
	}

	private void savePatientLastSyncInfo(List<RecordInfo> patientRecordInfoList) {
		for (RecordInfo patientRecordInfo : patientRecordInfoList) {
			entitySyncInfoDbService.saveLastSyncInfo(Patient.class, patientRecordInfo.getUuid(), new Date());
		}
	}

	private void loadPatientListContextRelationshipsAndSave(List<PatientListContext> patientListContexts) {
		for (PatientListContext patientListContext : patientListContexts) {
			patientListContext.setPatientList(
					patientListDbService.getByUuid(patientListContext.getPatientList().getUuid(), null));
			patientListContext.setPatient(
					patientDbService.getByUuid(patientListContext.getPatient().getUuid(), null));
			if (patientListContext.getVisit() != null) {
				patientListContext.setVisit(
						visitDbService.getByUuid(patientListContext.getVisit().getUuid(), null));
			}
		}
		listPatientDbService.saveAll(patientListContexts);
	}
}
