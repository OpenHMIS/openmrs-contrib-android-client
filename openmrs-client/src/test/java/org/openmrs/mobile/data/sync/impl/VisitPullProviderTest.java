package org.openmrs.mobile.data.sync.impl;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.openmrs.mobile.BuildConfig;
import org.openmrs.mobile.data.CoreTestData;
import org.openmrs.mobile.data.DBFlowRule;
import org.openmrs.mobile.data.DatabaseHelper;
import org.openmrs.mobile.data.ModelAsserters;
import org.openmrs.mobile.data.ModelGenerators;
import org.openmrs.mobile.data.db.Repository;
import org.openmrs.mobile.data.db.impl.EncounterDbService;
import org.openmrs.mobile.data.db.impl.ObsDbService;
import org.openmrs.mobile.data.db.impl.PatientDbService;
import org.openmrs.mobile.data.db.impl.RepositoryImpl;
import org.openmrs.mobile.data.db.impl.VisitDbService;
import org.openmrs.mobile.data.db.impl.VisitPhotoDbService;
import org.openmrs.mobile.data.db.impl.VisitTaskDbService;
import org.openmrs.mobile.data.rest.impl.EncounterRestServiceImpl;
import org.openmrs.mobile.data.rest.impl.ObsRestServiceImpl;
import org.openmrs.mobile.data.rest.impl.VisitPhotoRestServiceImpl;
import org.openmrs.mobile.data.rest.impl.VisitRestServiceImpl;
import org.openmrs.mobile.data.rest.impl.VisitTaskRestServiceImpl;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.PullSubscription;
import org.openmrs.mobile.models.RecordInfo;
import org.openmrs.mobile.models.Results;
import org.openmrs.mobile.models.Visit;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Response;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class VisitPullProviderTest {
	@Rule
	public DBFlowRule dbflowTestRule = DBFlowRule.create();

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();

	@Mock VisitDbService visitDbService;
	@Mock VisitRestServiceImpl visitRestService;
	@Mock EncounterDbService encounterDbService;
	@Mock EncounterRestServiceImpl encounterRestService;
	@Mock VisitTaskDbService visitTaskDbService;
	@Mock VisitTaskRestServiceImpl visitTaskRestService;
	@Mock DatabaseHelper databaseHelper;
	@Mock ObsDbService obsDbService;
	@Mock ObsRestServiceImpl obsRestService;
	@Mock VisitPhotoDbService visitPhotoDbService;
	@Mock VisitPhotoRestServiceImpl visitPhotoRestService;
	@Mock PatientDbService patientDbService;

	Repository repository = new RepositoryImpl();

	private VisitPullProvider provider;

	@Before
	public void before() {
		CoreTestData.load();

		visitDbService = new VisitDbService(repository);

		provider = new VisitPullProvider(visitDbService, visitRestService, encounterDbService, encounterRestService,
				obsDbService, obsRestService, visitPhotoDbService, visitPhotoRestService, visitTaskDbService,
				visitTaskRestService, databaseHelper, patientDbService);
	}

	@After
	public void after() {
		CoreTestData.clear();
	}

	@Test
	public void pullVisits_shouldPullPatientVisit() throws Exception {
		PullSubscription subscription = new PullSubscription();
		subscription.setLastSync(new Date(0, 1, 1));

		Patient patient = ModelGenerators.PATIENT.generate(false);
		patient.setUuid(UUID.randomUUID().toString());
		RecordInfo patientInfo = new RecordInfo();
		patientInfo.setUuid(patient.getUuid());

		// Setup mocks
		ModelGenerators.VISIT.setPatient(patient);
		Visit visit = ModelGenerators.VISIT.generate(true);
		visit.setUuid(UUID.randomUUID().toString());
		RecordInfo visitInfo = new RecordInfo();
		visitInfo.setUuid(visit.getUuid());
		visitInfo.setDateCreated(new Date());
		List<RecordInfo> tempList = new ArrayList<>(1);
		tempList.add(visitInfo);

		Call<Results<RecordInfo>> call = mock(Call.class);
		Response<Results<RecordInfo>> response = Response.success(new Results<>(tempList));
		when(visitRestService.getRecordInfoByPatient(any(), any(), any())).thenReturn(call);
		try {
			when(call.execute()).thenReturn(response);
		} catch (IOException e) { }

		Call<Visit> call2 = mock(Call.class);
		Response<Visit> response2 = Response.success(visit);
		when(visitRestService.getByUuid(eq(visitInfo.getUuid()), any())).thenReturn(call2);
		try {
			when(call2.execute()).thenReturn(response2);
		} catch (IOException e) { }

		Visit dbVisit = visitDbService.getByUuid(visit.getUuid(), null);
		Assert.assertNull(dbVisit);

		provider.pullVisits(subscription, patientInfo);

		dbVisit = visitDbService.getByUuid(visit.getUuid(), null);

		Assert.assertNotNull(dbVisit);
		ModelAsserters.VISIT.assertModel(visit, dbVisit);
	}
}
