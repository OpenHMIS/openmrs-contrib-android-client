package org.openmrs.mobile.data.sync.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.openmrs.mobile.data.DataOperationException;
import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.data.db.impl.PatientDbService;
import org.openmrs.mobile.data.rest.impl.PatientRestServiceImpl;
import org.openmrs.mobile.data.sync.BasePushProviderTest;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.SyncLog;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;

public class PatientPushProviderTest extends BasePushProviderTest<Patient, PatientDbService, PatientRestServiceImpl>{

	@InjectMocks
	PatientPushProvider provider;

	@Before
	public void before() throws Exception {
		createSyncLogEntries();
	}

	@Test
	public void push_shouldFetchEntityDb() throws Exception {
		mockSuccessfulDbCalls();

		provider.push(syncLogEntries.get(0));
		verify(dbService).getByUuid(anyString(), any(QueryOptions.class));
	}

	@Test
	public void push_shouldCallRest() throws Exception {
		mockSuccessfulCalls();

		provider.push(syncLogEntries.get(0));
		verify(restService).create(any(Patient.class));
	}

	@Test(expected = DataOperationException.class)
	public void push_shouldFailFetchingEntity() throws Exception {
		mockErrorDbCalls();
		SyncLog log = new SyncLog();
		log.setKey("2343223423");
		provider.push(log);
		verify(dbService).getByUuid(anyString(), any(QueryOptions.class));
	}


	@Override
	protected Patient getEntity() {
		Patient patient = new Patient();
		patient.setUuid("11-22-33");

		return patient;
	}
}
