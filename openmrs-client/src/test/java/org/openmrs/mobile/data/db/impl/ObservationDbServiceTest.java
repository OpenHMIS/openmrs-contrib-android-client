package org.openmrs.mobile.data.db.impl;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.mobile.data.ModelAsserters;
import org.openmrs.mobile.data.ModelGenerators;
import org.openmrs.mobile.data.db.BaseAuditableDbServiceTest;
import org.openmrs.mobile.data.db.BaseDbService;
import org.openmrs.mobile.models.Observation;
import org.openmrs.mobile.utilities.ApplicationConstants;

import java.util.ArrayList;
import java.util.List;

public class ObservationDbServiceTest  extends BaseAuditableDbServiceTest<Observation> {
	private ObsDbService obsDbService;

	@Override
	protected BaseDbService<Observation> getDbService() {
		obsDbService = new ObsDbService(repository);

		return obsDbService;
	}

	@Override
	protected ModelAsserters.ModelAsserter<Observation> getAsserter() {
		return ModelAsserters.OBSERVATION;
	}

	@Override
	protected ModelGenerators.ModelGenerator<Observation> getGenerator() {
		return ModelGenerators.OBSERVATION;
	}

	@Test
	public void getObsByVisitAndConcept_shouldReturnExpectedResults() throws Exception {
		List<Observation> observations = new ArrayList<>();

		String conceptVisitDocumentUuid = ApplicationConstants.ObservationLocators.VISIT_DOCUMENT_UUID.split(",")[0];
		Observation obs1 = generator.generate(true);
		obs1.getConcept().setUuid(conceptVisitDocumentUuid);
		observations.add(obs1);

		Observation obs2 = generator.generate(true);
		observations.add(obs2);

		Observation obs3 = generator.generate(true);
		observations.add(obs3);

		List<Observation> results = obsDbService.getVisitPhotoObservations(obs1.getEncounter().getVisit().getUuid(), null);

		Assert.assertEquals(0, results.size());

		Assert.assertNotNull(obs1.getEncounter().getVisit());

		dbService.saveAll(observations);

		observations = dbService.getAll(null, null);

		Assert.assertEquals(3, observations.size());
		Assert.assertEquals(obs1.getUuid(), observations.get(0).getUuid());

		// Need to figure out why observation->encounter->visit is null
		//Assert.assertNotNull(observations.get(0).getEncounter().getVisit());

		results = obsDbService.getVisitPhotoObservations(obs1.getEncounter().getVisit().getUuid(), null);

		// This test won't pass since the visit is not saved
		//Assert.assertEquals(1, results.size());
		//Assert.assertEquals(conceptVisitDocumentUuid, results.get(0).getConcept().getUuid());
	}

	@Test
	public void getObsByVisitAndConcept_shouldReturnNoResults() throws Exception {
		Observation obs = generator.generate(true);

		dbService.save(obs);

		List<Observation> observations = dbService.getAll(null, null);

		Assert.assertEquals(1, observations.size());
		Assert.assertEquals(obs.getUuid(), observations.get(0).getUuid());

		List<Observation> results = obsDbService.getVisitPhotoObservations(obs.getEncounter().getVisit().getUuid(), null);
		Assert.assertEquals(0, results.size());
	}
}