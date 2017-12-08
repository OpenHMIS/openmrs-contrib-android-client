package org.openmrs.mobile.data.sync.impl;

import android.support.annotation.NonNull;

import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.data.db.impl.PatientDbService;
import org.openmrs.mobile.data.rest.RestHelper;
import org.openmrs.mobile.data.rest.impl.PatientRestServiceImpl;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.PullSubscription;
import org.openmrs.mobile.models.RecordInfo;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

public class PatientPullProvider {
	private PatientDbService patientDbService;
	private PatientRestServiceImpl patientRestService;

	@Inject
	public PatientPullProvider(PatientDbService patientDbService,
			PatientRestServiceImpl patientRestService) {
		this.patientDbService = patientDbService;
		this.patientRestService = patientRestService;
	}

	public void pull(@NonNull PullSubscription subscription, @NonNull List<RecordInfo> patientInfo) {
		checkNotNull(subscription);
		checkNotNull(patientInfo);

		// Calculate the patients that need to be pulled
		List<RecordInfo> patients = new ArrayList<>();
		for (RecordInfo record : patientInfo) {
			if (subscription.getLastSync() == null || (record.getDateChanged() != null
					&& record.getDateChanged().compareTo(subscription.getLastSync()) > 0)) {
				patients.add(record);
			}
		}

		for (RecordInfo info : patients) {
			// Get full patient data
			Patient patient = RestHelper.getCallValue(patientRestService.getByUuid(info.getUuid(), QueryOptions.FULL_REP));

			// Save patient in db
			if (patient != null) {
				patient.processRelationships();

				patientDbService.save(patient);
			}
		}
	}
}

