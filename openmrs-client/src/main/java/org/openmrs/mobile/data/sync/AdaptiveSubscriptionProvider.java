package org.openmrs.mobile.data.sync;

import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.From;
import com.raizlabs.android.dbflow.sql.language.Join;
import com.raizlabs.android.dbflow.sql.language.NameAlias;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.language.property.Property;
import com.raizlabs.android.dbflow.structure.ModelAdapter;

import org.openmrs.mobile.data.db.DbService;
import org.openmrs.mobile.data.db.Repository;
import org.openmrs.mobile.data.rest.RestService;
import org.openmrs.mobile.models.BaseOpenmrsAuditableObject;
import org.openmrs.mobile.models.PullSubscription;
import org.openmrs.mobile.models.RecordInfo;
import org.openmrs.mobile.models.RecordInfo_Table;
import org.openmrs.mobile.models.Results;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Base class for subscription providers that attempt an incremental pull  but can switch to a table pull if too
 * many records have changed.
 * @param <E> The entity class
 * @param <DS> The db service class
 * @param <RS> The rest service class
 */
public abstract class AdaptiveSubscriptionProvider<E extends BaseOpenmrsAuditableObject,
		DS extends DbService<E>, RS extends RestService<E>> extends BaseSubscriptionProvider {
	protected DS dbService;
	protected DbService<RecordInfo> recordInfoDbService;
	protected RS restService;
	protected Repository repository;

	private Class<E> entityClass;

	@Inject
	public AdaptiveSubscriptionProvider(DS dbService, DbService<RecordInfo> recordInfoDbService, RS restService,
			Repository repository) {
		this.dbService = dbService;
		this.recordInfoDbService = recordInfoDbService;
		this.restService = restService;
		this.repository = repository;
	}

	/**
	 * Executes the pull synchronization process. If no local records are found then a table pull will be done; otherwise,
	 * an incremental pull is started.
	 * @param subscription The {@link PullSubscription} that will be processed
	 */
	@Override
	public void pull(PullSubscription subscription) {
		long recordCount = getRecordCountDb();

		if (recordCount == 0) {
			// If table is empty then do a table pull
			pullTable(subscription);
		} else {
			pullIncremental(subscription);
		}
	}

	/**
	 * Pulls the complete set of data from the rest service and saves all data to the local database. No deletions are
	 * performed.
	 * @param subscription The {@link PullSubscription} being processed
	 */
	protected void pullTable(PullSubscription subscription) {
		// Get all records via rest
		List<E> table = getAllRest();
		if (table == null || table.size() == 0) {
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
		if (tableInfo == null || tableInfo.size() == 0) {
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
			if (updates != null && updates.size() > 0) {
				processIncremental(updates);
			}

			if (inserts != null && inserts.size() > 0) {
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

		From<E> from = SQLite.select(entityUuidProperty)
				.from(getEntityClass())
				.innerJoin(RecordInfo.class)
				.on(entityUuidProperty.withTable().eq(RecordInfo_Table.uuid.withTable()));
		from.where(RecordInfo_Table.dateChanged.greaterThan(since));

		return repository.queryCustom(String.class, from);
	}

	/**
	 * Calculates the inserts that need to be performed, returning the list of uuid's for entities that are new.
	 * @return A list containing the uuid's for entities that are new
	 */
	protected List<String> calculateIncrementalInserts() {
		Property entityUuidProperty = getModelTable(getEntityClass()).getProperty("uuid");

		From<RecordInfo> from = SQLite.select(RecordInfo_Table.uuid)
				.from(RecordInfo.class)
				.leftOuterJoin(getEntityClass())
				.on(RecordInfo_Table.uuid.withTable().eq(entityUuidProperty.withTable()));
		from.where(entityUuidProperty.withTable().isNull());

		return repository.queryCustom(String.class, from);
	}

	/**
	 * Deletes the local entities that were not found in the rest results.
	 */
	protected void deleteIncremental() {
		From<E> from = SQLite.delete(getEntityClass()).as("E")
				.join(RecordInfo.class, Join.JoinType.LEFT_OUTER).as("R")
				.on(
						getModelTable(getEntityClass()).getProperty("uuid").withTable(NameAlias.of("E"))
								.eq(RecordInfo_Table.uuid.withTable(NameAlias.of("R"))));
		from.where(RecordInfo_Table.uuid.withTable(NameAlias.of("R")).isNull());

		repository.deleteAll(from);
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
		return getCallListValue(restService.getAll(null, null));
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
		return getCallValue(restService.getByUuid(uuid, null));
	}

	/**
	 * Gets the {@link RecordInfo} from the rest service.
	 * @return The record info
	 */
	protected List<RecordInfo> getRecordInfoRest() {
		return getCallListValue(restService.getRecordInfo(null));
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
