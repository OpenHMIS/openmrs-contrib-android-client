package org.openmrs.mobile.data.sync;

import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.structure.ModelAdapter;

import org.greenrobot.eventbus.EventBus;
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
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.data.DBFlowRule;
import org.openmrs.mobile.data.ModelAsserters;
import org.openmrs.mobile.data.ModelGenerators;
import org.openmrs.mobile.data.RelatedObject;
import org.openmrs.mobile.data.CoreTestData;
import org.openmrs.mobile.data.db.Repository;
import org.openmrs.mobile.data.db.impl.PullSubscriptionDbService;
import org.openmrs.mobile.data.db.impl.RepositoryImpl;
import org.openmrs.mobile.data.db.impl.SyncLogDbService;
import org.openmrs.mobile.data.sync.impl.PatientTrimProvider;
import org.openmrs.mobile.models.BaseOpenmrsObject;
import org.openmrs.mobile.models.SyncAction;
import org.openmrs.mobile.models.SyncLog;
import org.openmrs.mobile.models.SyncLog_Table;
import org.openmrs.mobile.utilities.NetworkUtils;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.UUID;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public abstract class PushProviderUuidTest<E extends BaseOpenmrsObject> {
	@Rule
	public DBFlowRule dbflowTestRule = DBFlowRule.create();

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();

	@Mock OpenMRS openMRS;
	@Mock SyncLogDbService syncLogDbService;
	@Mock PullSubscriptionDbService pullSubscriptionDbService;
	@Mock protected DaggerProviderHelper daggerProviderHelper;
	@Mock NetworkUtils networkUtils;
	@Mock EventBus eventBus;
	@Mock PatientTrimProvider patientTrimProvider;

	public SyncService syncService;

	protected Repository repository = new RepositoryImpl();
	protected ModelAsserters.ModelAsserter<E> asserter;
	protected ModelGenerators.ModelGenerator<E> generator;
	protected ModelAdapter<E> table;
	protected ModelAdapter<SyncLog> syncLogTable;
	protected Class<E> entityClass;

	protected abstract ModelAsserters.ModelAsserter<E> getAsserter();
	protected abstract ModelGenerators.ModelGenerator<E> getGenerator();
	protected abstract ModelAdapter<E> getTable();
	protected abstract void setupRestMocks(String newUuid, E entity, boolean createServerUuids);
	protected abstract List<RelatedObject> getRelatedObjects(E entity);

	@Before
	public void before() {
		getEntityClass();

		syncLogDbService = new SyncLogDbService(repository);
		pullSubscriptionDbService = new PullSubscriptionDbService(repository);

		syncService = new SyncService(openMRS, syncLogDbService, pullSubscriptionDbService, daggerProviderHelper,
				networkUtils, eventBus, patientTrimProvider);

		CoreTestData.load();

		asserter = getAsserter();
		generator = getGenerator();
		table = getTable();
		syncLogTable = (SyncLog_Table)FlowManager.getInstanceAdapter(SyncLog.class);
	}

	@After
	public void after() {
		CoreTestData.clear();
	}

	@Test
	public void push_shouldUpdateLocalUuid() throws Exception {
		// Create local entity with related records
		E entity = generator.generate(true);
		String originalUuid = entity.getUuid();

		// Save patient to the db
		repository.save(table, entity);

		// Check that patient and related records are present
		E savedEntity = repository.querySingle(table, table.getProperty("uuid").eq(entity.getUuid()));
		asserter.assertModel(entity, savedEntity);

		// Create sync log for record and save to the db
		SyncLog log =  new SyncLog();
		log.setAction(SyncAction.NEW);
		log.setKey(savedEntity.getUuid());
		log.setType(entityClass.getSimpleName());
		repository.save(syncLogTable, log);

		// Setup mocks to return REST results with updated uuids for all entities
		String serverUuid = UUID.randomUUID().toString();
		setupRestMocks(serverUuid, entity, true);

		// Execute push
		syncService.push();

		// Check for entities with old uuids, should not be any results
		E search = repository.querySingle(table, table.getProperty("uuid").eq(originalUuid));
		Assert.assertNull(search);

		List<RelatedObject> relatedObjects = getRelatedObjects(savedEntity);
		for (RelatedObject obj : relatedObjects) {
			ModelAdapter<? extends BaseOpenmrsObject> objTable = obj.getModelTable();

			BaseOpenmrsObject objSearch = repository.querySingle(objTable, objTable.getProperty("uuid").eq(obj.getEntity().getUuid()));
			Assert.assertNull(objSearch);
		}

		// Check for entities with new uuids
		search = repository.querySingle(table, table.getProperty("uuid").eq(serverUuid));
		Assert.assertNotNull(search);

		relatedObjects = getRelatedObjects(search);
		for (RelatedObject obj : relatedObjects) {
			ModelAdapter<? extends BaseOpenmrsObject> objTable = obj.getModelTable();

			BaseOpenmrsObject objSearch = repository.querySingle(objTable, objTable.getProperty("uuid").eq(obj.getEntity().getUuid()));
			Assert.assertNotNull(objSearch);
		}
	}

	@Test
	public void push_shouldKeepServerUuid() throws Exception {
		// Create local entity with related records
		E entity = generator.generate(true);
		entity.setUuid(UUID.randomUUID().toString());

		// Get related objects and set uuid to server uuid
		List<RelatedObject> relatedObjects = getRelatedObjects(entity);
		for (RelatedObject obj : relatedObjects) {
			obj.getEntity().setUuid(UUID.randomUUID().toString());
		}

		// Save patient to the db
		repository.save(table, entity);

		// Check that patient and related records are present
		E savedEntity = repository.querySingle(table, table.getProperty("uuid").eq(entity.getUuid()));
		asserter.assertModel(entity, savedEntity);

		// Create sync log for record and save to the db
		SyncLog log =  new SyncLog();
		log.setAction(SyncAction.NEW);
		log.setKey(savedEntity.getUuid());
		log.setType(entityClass.getSimpleName());
		repository.save(syncLogTable, log);

		// Setup mocks to return REST results with updated uuids for all entities
		setupRestMocks(entity.getUuid(), entity, false);

		// Execute push
		syncService.push();

		// Check for entities with new uuids
		E search = repository.querySingle(table, table.getProperty("uuid").eq(entity.getUuid()));
		Assert.assertNotNull(search);

		relatedObjects = getRelatedObjects(search);
		for (RelatedObject obj : relatedObjects) {
			ModelAdapter<? extends BaseOpenmrsObject> objTable = obj.getModelTable();

			BaseOpenmrsObject objSearch = repository.querySingle(objTable, objTable.getProperty("uuid").eq(obj.getEntity().getUuid()));
			Assert.assertNotNull(objSearch);
		}
	}

	@Test
	public void push_shouldUpdateRelatedObjectsFK() throws Exception {
		// Create server entity with local related records
		E entity = generator.generate(true);
		entity.setUuid(UUID.randomUUID().toString());

		// Save patient to the db
		repository.save(table, entity);

		// Check that patient and related records are present
		E savedEntity = repository.querySingle(table, table.getProperty("uuid").eq(entity.getUuid()));
		asserter.assertModel(entity, savedEntity);

		// Create sync log for record and save to the db
		SyncLog log =  new SyncLog();
		log.setAction(SyncAction.NEW);
		log.setKey(savedEntity.getUuid());
		log.setType(entityClass.getSimpleName());
		repository.save(syncLogTable, log);

		// Setup mocks to return REST results with updated uuids for all entities
		setupRestMocks(entity.getUuid(), entity, true);

		// Execute push
		syncService.push();

		// Check for entities with old uuids, should not be any results
		E search = repository.querySingle(table, table.getProperty("uuid").eq(entity.getUuid()));
		Assert.assertNotNull(search);

		List<RelatedObject> relatedObjects = getRelatedObjects(savedEntity);
		for (RelatedObject obj : relatedObjects) {
			ModelAdapter<? extends BaseOpenmrsObject> objTable = obj.getModelTable();

			BaseOpenmrsObject objSearch = repository.querySingle(objTable, objTable.getProperty("uuid").eq(obj.getEntity().getUuid()));
			Assert.assertNull(objSearch);
		}

		// Check for entities with new uuids
		search = repository.querySingle(table, table.getProperty("uuid").eq(entity.getUuid()));
		Assert.assertNotNull(search);

		relatedObjects = getRelatedObjects(search);
		for (RelatedObject obj : relatedObjects) {
			ModelAdapter<? extends BaseOpenmrsObject> objTable = obj.getModelTable();

			BaseOpenmrsObject objSearch = repository.querySingle(objTable, objTable.getProperty("uuid").eq(obj.getEntity().getUuid()));
			Assert.assertNotNull(objSearch);
		}
	}

	/**
	 * Gets a usable instance of the actual class of the generic type E defined by the implementing sub-class.
	 * @return The class object for the entity.
	 */
	@SuppressWarnings("unchecked")
	protected Class<E> getEntityClass() {
		if (entityClass == null) {
			ParameterizedType parameterizedType = (ParameterizedType)getClass().getGenericSuperclass();

			entityClass = (Class<E>)parameterizedType.getActualTypeArguments()[0];
		}

		return entityClass;
	}
}
