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

public class PatientListPullProvider {
	private PatientDbService patientDbService;
	private PatientRestServiceImpl patientRestService;

	@Inject
	public PatientListPullProvider(PatientDbService patientDbService,
			PatientRestServiceImpl patientRestService) {
		this.patientDbService = patientDbService;
		this.patientRestService = patientRestService;
	}

	public void pull(@NonNull PullSubscription subscription, @NonNull List<RecordInfo> patienInfo) {
		checkNotNull(subscription);
		checkNotNull(patienInfo);

		// Calculate the patients that need to be pulled
		List<RecordInfo> patients = new ArrayList<>();
		for (RecordInfo record : patienInfo) {
			if (record.getDateChanged().compareTo(subscription.getLastSync()) > 0) {
				patients.add(record);
			}
		}

		for (RecordInfo patientInfo : patients) {
			// Get full patient data
			Patient patient = RestHelper.getCallValue(
					patientRestService.getByUuid(patientInfo.getUuid(), QueryOptions.FULL_REP));

			// Save patient in db
			if (patient != null) {
				patient.processRelationships();

				patientDbService.save(patient);
			}
		}
	}
}

