package org.openmrs.mobile.data.db.impl;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.mobile.data.ModelAsserters;
import org.openmrs.mobile.data.ModelGenerators;
import org.openmrs.mobile.data.db.BaseAuditableDbServiceTest;
import org.openmrs.mobile.data.db.BaseDbService;
import org.openmrs.mobile.models.Concept;

import java.util.List;

public class ConceptDbServiceTest extends BaseAuditableDbServiceTest<Concept> {
	private ConceptDbService conceptDbService;

	@Override
	protected BaseDbService<Concept> getDbService() {
		conceptDbService = new ConceptDbService(repository);

		return conceptDbService;
	}

	@Override
	protected ModelAsserters.ModelAsserter<Concept> getAsserter() {
		return ModelAsserters.CONCEPT;
	}

	@Override
	protected ModelGenerators.ModelGenerator<Concept> getGenerator() {
		return ModelGenerators.CONCEPT;
	}

	@Test
	public void save_shouldSaveConceptName() throws Exception {
		Concept concept = generator.generate(true);
		Assert.assertNotNull(concept.getName());

		dbService.save(concept);

		Concept dbConcept = dbService.getByUuid(concept.getUuid(), null);

		Assert.assertNotNull(dbConcept);
		Assert.assertNotNull(dbConcept.getName());
		asserter.assertModel(concept, dbConcept);
	}

	@Test
	public void getByName_shouldReturnExpectedResults() throws Exception {
		Concept c1 = generator.generate(true);
		c1.getName().setName("Concept Test Nairobi");
		Concept c2 = generator.generate(true);
		c2.getName().setName("Concept Test Nairobi 2");
		Concept c3 = generator.generate(true);
		c3.getName().setName("Concept Test Mombasa");

		dbService.save(c1);
		dbService.save(c2);
		dbService.save(c3);

		Assert.assertEquals(3, dbService.getAll(null, null).size());

		List<Concept> results = conceptDbService.getByName("Test", null);
		Assert.assertEquals(3, results.size());

		results = conceptDbService.getByName("Nairobi", null);
		Assert.assertEquals(2, results.size());
		ModelAsserters.assertListContainsUuid(results, c1.getUuid());
		ModelAsserters.assertListContainsUuid(results, c2.getUuid());

		results = conceptDbService.getByName("Mombasa", null);
		Assert.assertEquals(1, results.size());
		ModelAsserters.assertListContainsUuid(results, c3.getUuid());
	}

	@Test
	public void getByName_shouldIgnoreCaseForSearch() throws Exception {
		Concept c1 = generator.generate(true);
		c1.getName().setName("Concept Test Nairobi");

		dbService.save(c1);

		Assert.assertEquals(1, dbService.getAll(null, null).size());

		List<Concept> results = conceptDbService.getByName("nairobi", null);
		Assert.assertEquals(1, results.size());
		Assert.assertEquals(c1.getUuid(), results.get(0).getUuid());
	}

	@Test
	public void getByName_shouldReturnEmptyListWhenNoResults() throws Exception {
		Concept c1 = generator.generate(true);
		c1.getName().setName("Concept Test Nairobi");

		dbService.save(c1);

		Assert.assertEquals(1, dbService.getAll(null, null).size());

		List<Concept> results = conceptDbService.getByName("nothing", null);
		Assert.assertEquals(0, results.size());
	}

	@Test
	public void getByName_shouldReturnPagedResults() throws Exception {

	}

	@Test
	public void getByName_shouldReturnFullConceptObject() throws Exception {
		Concept c1 = generator.generate(true);
		c1.getName().setName("Concept Test Nairobi");

		dbService.save(c1);

		Assert.assertEquals(1, dbService.getAll(null, null).size());

		List<Concept> results = conceptDbService.getByName("Concept Test Nairobi", null);
		Assert.assertEquals(1, results.size());
		asserter.assertModel(c1, results.get(0));
	}
}