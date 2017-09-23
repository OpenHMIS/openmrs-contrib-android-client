package org.openmrs.mobile.data.sync;

import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.Join;
import com.raizlabs.android.dbflow.sql.language.NameAlias;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.language.property.Property;
import com.raizlabs.android.dbflow.sql.language.property.PropertyFactory;
import com.raizlabs.android.dbflow.sql.queriable.ModelQueriable;
import com.raizlabs.android.dbflow.structure.ModelAdapter;

import org.greenrobot.eventbus.EventBus;
import org.openmrs.mobile.data.PagingInfo;
import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.data.db.DbService;
import org.openmrs.mobile.data.db.Repository;
import org.openmrs.mobile.data.db.impl.RecordInfoDbService;
import org.openmrs.mobile.data.rest.RestHelper;
import org.openmrs.mobile.data.rest.RestService;
import org.openmrs.mobile.event.SyncPullEvent;
import org.openmrs.mobile.models.BaseOpenmrsAuditableObject;
import org.openmrs.mobile.models.PullSubscription;
import org.openmrs.mobile.models.RecordInfo;
import org.openmrs.mobile.models.RecordInfo_Table;
import org.openmrs.mobile.models.queryModel.EntityUuid;
import org.openmrs.mobile.utilities.ApplicationConstants;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Base class for subscription providers that attempt an incremental pull but can switch to a table pull if too
 * many records have changed.
 * @param <E>  The entity class
 * @param <DS> The db service class
 * @param <RS> The rest service class
 */
public abstract class AdaptiveSubscriptionProvider<E extends BaseOpenmrsAuditableObject,
		DS extends DbService<E>, RS extends RestService<E>> extends BaseSubscriptionProvider {
	protected DS dbService;
	protected RecordInfoDbService recordInfoDbService;
	protected RS restService;
	protected Repository repository;
	protected EventBus eventBus;

	private Class<E> entityClass;

	public AdaptiveSubscriptionProvider(DS dbService, RecordInfoDbService recordInfoDbService, RS restService,
			Repository repository, EventBus eventBus) {
		this.dbService = dbService;
		this.recordInfoDbService = recordInfoDbService;
		this.restService = restService;
		this.repository = repository;
		this.eventBus = eventBus;
	}

	/**
	 * Executes the pull synchronization process. If no local records are found then a table pull will be done; otherwise,
	 * an incremental pull is started.
	 * @param subscription The {@link PullSubscription} that will be processed
	 */
	@Override
	public void pull(PullSubscription subscription) {
		long recordCount = getRecordCountDb();

		eventBus.post(new SyncPullEvent(ApplicationConstants.EventMessages.Sync.Pull.ENTITY_REMOTE_PULL_STARTING,
				(entityClass == null ? null : entityClass.getName()), null));

		if (recordCount == 0) {
			// If table is empty then do a table pull
			pullTable(subscription);
		} else {
			pullIncremental(subscription);
		}

		eventBus.post(new SyncPullEvent(ApplicationConstants.EventMessages.Sync.Pull.ENTITY_REMOTE_PULL_COMPLETE,
				(entityClass == null ? null : entityClass.getName()), null));
	}

	/**
	 * Pulls the complete set of data from the rest service and saves all data to the local database. No deletions are
	 * performed.
	 * @param subscription The {@link PullSubscription} being processed
	 */
	protected void pullTable(PullSubscription subscription) {
		// Get all records via rest
		List<E> table = getAllRest();
		if (table == null || table.isEmpty()) {
			return;
		}

		// Filter the list for records updated since the last sync
		List<E> updated = new ArrayList<>();
		if (subscription.getLastSync() != null) {
			for (E record : table) {
				if (record.getDateChanged().compareTo(subscription.getLastSync()) > 0) {
					updated.add(record);
				}
			}
		} else {
			updated = table;
		}

		// Update/Create local records
		saveAllDb(updated);
	}

	/**
	 * Pulls just the {@link RecordInfo} from the rest service, deleting any local records no longer found and calculating
	 * the new or updated remote records that are needed. If the number of new + updated records is more than the
	 * {@link PullSubscription#getMaximumIncrementalCount()} then a table pull is performed to update the local records.
	 * @param subscription The {@link PullSubscription} being processed
	 */
	protected void pullIncremental(PullSubscription subscription) {
		// Get the record info (UUID, DateUpdated) for each record via REST
		List<RecordInfo> tableInfo = getRecordInfoRest();
		if (tableInfo == null || tableInfo.isEmpty()) {
			return;
		}

		List<String> updates;
		List<String> inserts;

		try {
			// Insert record info into temp table
			recordInfoDbService.saveAll(tableInfo);

			// Delete local records that are not in record info
			deleteIncremental();

			// Calculate local records to update and create
			updates = calculateIncrementalUpdates(subscription.getLastSync());
			inserts = calculateIncrementalInserts();
		} finally {
			// Ensure that the record info table is cleared
			recordInfoDbService.deleteAll();
		}

		// If update+new count is greater than max incremental update then do table pull
		if (subscription.getMaximumIncrementalCount() != null &&
				(updates.size() + inserts.size()) > subscription.getMaximumIncrementalCount()) {
			pullTable(subscription);
		} else {
			// Else pull each record via rest and save to the local db
			if (updates != null && !updates.isEmpty()) {
				processIncremental(updates);
			}

			if (inserts != null && !inserts.isEmpty()) {
				processIncremental(inserts);
			}
		}
	}

	/**
	 * Calculates the updates that need to be performed, returning the list of uuid's for entities that need to be updated.
	 * @param since The last time the {@link PullSubscription} was processed
	 * @return A list containing the uuid's for entities that need to be updated
	 */
	protected List<String> calculateIncrementalUpdates(Date since) {
		Property entityUuidProperty = getModelTable(getEntityClass()).getProperty("uuid");

		ModelQueriable<E> query = SQLite.select(entityUuidProperty.withTable())
				.from(getEntityClass())
				.innerJoin(RecordInfo.class)
				.on(entityUuidProperty.withTable().eq(RecordInfo_Table.uuid.withTable()))
				.where(RecordInfo_Table.dateCreated.withTable().greaterThan(since))
				.or(RecordInfo_Table.dateChanged.withTable().greaterThan(since));

		return EntityUuid.getUuids(repository.queryCustom(EntityUuid.class, query));
	}

	/**
	 * Calculates the inserts that need to be performed, returning the list of uuid's for entities that are new.
	 * @return A list containing the uuid's for entities that are new
	 */
	protected List<String> calculateIncrementalInserts() {
		Property entityUuidProperty = getModelTable(getEntityClass()).getProperty("uuid");

		ModelQueriable<RecordInfo> query = SQLite.select(RecordInfo_Table.uuid.withTable())
				.from(RecordInfo.class)
				.leftOuterJoin(getEntityClass())
				.on(RecordInfo_Table.uuid.withTable().eq(entityUuidProperty.withTable()))
				.where(entityUuidProperty.withTable().isNull());

		return EntityUuid.getUuids(repository.queryCustom(EntityUuid.class, query));
	}

	/**
	 * Deletes the local entities that were not found in the rest results.
	 */
	protected void deleteIncremental() {
		/**
		 * SQLite (or at least the wrapper) doesn't like joins to deletes, so you have to do a nested query
		 * https://stackoverflow.com/questions/24511153/how-delete-table-inner-join-with-other-table-in-sqlite
		 *
		 * The code below produces a query similar to the following:
		 *  DELETE
		 *  FROM entityClass
		 *  WHERE uuid IN (
		 *      SELECT E.uuid
		 *      FROM entityClass E
		 *      LEFT OUTER JOIN RecordInfo R
		 *          ON E.uuid = R.uuid
		 *      WHERE R.uuid IS NULL
		 *  )
		 */
		ModelQueriable<E> query = SQLite.delete(getEntityClass())
				.where(getModelTable(getEntityClass()).getProperty("uuid").in(
						PropertyFactory.from(
								SQLite.select(
										getModelTable(getEntityClass()).getProperty("uuid").withTable(NameAlias.of("E"))
								)
										.from(getEntityClass()).as("E")
										.join(RecordInfo.class, Join.JoinType.LEFT_OUTER).as("R")
										.on(
												getModelTable(getEntityClass())
														.getProperty("uuid").withTable(NameAlias.of("E"))
														.eq(RecordInfo_Table.uuid.withTable(NameAlias.of("R")))
										)
										.where(RecordInfo_Table.uuid.withTable(NameAlias.of("R")).isNull())
						)
				));

		repository.deleteAll(query);
	}

	/**
	 * Gets each entity from the rest service by uuid and then saves all records to the database
	 * @param records The uuid's of the entities to process
	 */
	protected void processIncremental(List<String> records) {
		List<E> entities = new ArrayList<>(records.size());

		// Get all required entities via rest
		for (String uuid : records) {
			entities.add(getByUuidRest(uuid));
		}

		// Save the entities to the db
		saveAllDb(entities);
	}

	/**
	 * Gets the record count for the entities in the local database.
	 * @return The record count of local entities
	 */
	protected long getRecordCountDb() {
		return dbService.getCount(null);
	}

	/**
	 * Gets all entities from the rest service.
	 * @return The entities
	 */
	protected List<E> getAllRest() {
		return RestHelper.getCallListValue(restService.getAll(QueryOptions.FULL_REP, PagingInfo.ALL));
	}

	/**
	 * Saves the specified entities in the local database.
	 * @param entities The entities to save
	 */
	protected void saveAllDb(List<E> entities) {
		dbService.saveAll(entities);
	}

	/**
	 * Gets an entity from the rest service using the specified uuid.
	 * @param uuid The uuid
	 * @return The entity
	 */
	protected E getByUuidRest(String uuid) {
		return RestHelper.getCallValue(restService.getByUuid(uuid, null));
	}

	/**
	 * Gets the {@link RecordInfo} from the rest service.
	 * @return The record info
	 */
	protected List<RecordInfo> getRecordInfoRest() {
		return RestHelper.getCallListValue(restService.getRecordInfo(null));
	}

	/**
	 * Gets the model table for the specified model class.
	 * @param cls The model class
	 * @param <T> The model class
	 * @return The model table
	 */
	@SuppressWarnings("unchecked")
	protected <T> ModelAdapter<T> getModelTable(Class<T> cls) {
		return (ModelAdapter<T>)FlowManager.getInstanceAdapter(cls);
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
