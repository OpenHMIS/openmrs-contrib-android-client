package org.openmrs.mobile.data.db;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openmrs.mobile.BuildConfig;
import org.openmrs.mobile.data.CoreTestData;
import org.openmrs.mobile.data.DBFlowRule;
import org.openmrs.mobile.data.ModelAsserters;
import org.openmrs.mobile.data.ModelGenerators;
import org.openmrs.mobile.data.db.impl.RepositoryImpl;
import org.openmrs.mobile.models.BaseOpenmrsObject;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public abstract class BaseDbServiceTest<E extends BaseOpenmrsObject> {
	@Rule
	public DBFlowRule dbflowTestRule = DBFlowRule.create();

	protected BaseDbService<E> dbService;
	protected ModelAsserters.ModelAsserter<E> asserter;
	protected ModelGenerators.ModelGenerator<E> generator;

	protected Repository repository = new RepositoryImpl();

	protected abstract BaseDbService<E> getDbService();

	protected abstract ModelAsserters.ModelAsserter<E> getAsserter();

	protected abstract ModelGenerators.ModelGenerator<E> getGenerator();

	@Before
	public void setUp() throws Exception {
		this.dbService = getDbService();
		this.asserter = getAsserter();
		this.generator = getGenerator();

		CoreTestData.load();
	}

	@After
	public void tearDown() throws Exception {
		CoreTestData.clear();
	}

	@Test
	public void getAll_shouldLoadAllEntities() throws Exception {

	}

	@Test
	public void getAll_shouldLoadRelatedEntities() throws  Exception {

	}

	@Test
	public void getAll_shouldLoadPagedEntities() throws Exception {

	}

	@Test
	public void getAll_shouldReturnEmptyListWhenNoEntities() throws Exception {

	}

	@Test
	public void getByUuid_shouldReturnCorrectEntity() throws Exception {

	}

	@Test
	public void getByUuid_shouldReturnNullWhenNoEntityFound() throws Exception {

	}

	@Test
	public void getByUuid_shouldLoadRelatedEntities() throws Exception {
		if (generator == null) return;

		E initial = generator.generate(true);
		if (initial != null) {
			dbService.save(initial);

			E entity = dbService.getByUuid(initial.getUuid(), null);
			asserter.assertModel(initial, entity);
		}
	}

	@Test
	public void getByUuid_shouldThrowExceptionWithNullUuid() throws Exception {

	}

	@Test
	public void saveAll_shouldSaveAllEntities() throws Exception {

	}

	@Test
	public void saveAll_shouldSaveSingleEntity() throws Exception {

	}

	@Test
	public void saveAll_shouldDoNothingWithEmptyList() throws Exception {

	}

	@Test
	public void saveAll_shouldThrowExceptionWithNullList() throws Exception {

	}

	@Test
	public void save_shouldInsertNewEntity() throws Exception {

	}

	@Test
	public void save_shouldUpdateExistingEntity() throws Exception {

	}

	@Test
	public void save_shouldThrowExceptionWhenEntityIsNull() throws Exception {

	}

	@Test
	public void delete_shouldDeleteEntity() throws Exception {

	}

	@Test
	public void delete_shouldNotThrowWhenEntityDoesNotExist() throws Exception {

	}

	@Test
	public void delete_shouldThrowExceptionWhenEntityIsNull() throws Exception {

	}
}