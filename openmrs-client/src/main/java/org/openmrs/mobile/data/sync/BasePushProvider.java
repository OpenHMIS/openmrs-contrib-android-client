package org.openmrs.mobile.data.sync;

import org.openmrs.mobile.data.DataOperationException;
import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.data.db.DbService;
import org.openmrs.mobile.data.db.Repository;
import org.openmrs.mobile.data.db.impl.SyncLogDbService;
import org.openmrs.mobile.data.rest.RestHelper;
import org.openmrs.mobile.data.rest.RestService;
import org.openmrs.mobile.models.BaseOpenmrsAuditableObject;
import org.openmrs.mobile.models.SyncLog;
import org.openmrs.mobile.utilities.StringUtils;

import retrofit2.Call;

/**
 * Base class for push providers
 */
public abstract class BasePushProvider<E extends BaseOpenmrsAuditableObject,
		DS extends DbService<E>, RS extends RestService<E>> implements PushProvider {
	protected DS dbService;
	protected RS restService;

	public BasePushProvider(DS dbService, RS restService) {
		this.dbService = dbService;
		this.restService = restService;
	}

	protected abstract void deleteLocalRelatedRecords(E originalEntity, E restEntity);

	@Override
	public void push(SyncLog syncLog) {
		if (syncLog.getKey() == null) {
			throw new DataOperationException("Entity UUID must be set");
		}

		E entity = dbService.getByUuid(syncLog.getKey(), QueryOptions.FULL_REP);
		if (entity == null) {
			throw new DataOperationException("Entity not found");
		}

		// Prepare rest call
		Call<E> call = null;
		switch (syncLog.getAction()) {
			case NEW:
				call = create(entity);
				break;
			case UPDATED:
				call = update(entity);
				break;
			case DELETED:
				call = purge(entity);
				break;
		}

		// Perform rest call
		E restEntity = RestHelper.getCallValue(call);
		if (restEntity != null) {
			if (StringUtils.isNullOrEmpty(entity.getUuid())) {
				// The local uuid can be removed when sent to the REST service so add the original uuid back if blank
				// Note: this entity won't be saved to the db and so this uuid will not be used except to delete/update
				// old records

				// This won't work for underlying entities which have been created.
				// A good example is creating a patient -> person -> names
				//entity.setUuid(syncLog.getKey());
				entity = dbService.getByUuid(syncLog.getKey(), QueryOptions.FULL_REP);
			}

			// Delete any related records with local uuids, records with the server-generated uuids will be saved when
			// saving the entity below
			deleteLocalRelatedRecords(entity, restEntity);

			// Check if uuid has changed
			if (!entity.getUuid().equalsIgnoreCase(restEntity.getUuid())) {
				// This will update the uuid for the current entity and also save any related objects
				dbService.update(entity.getUuid(), restEntity);
			}
		}
	}

	protected Call<E> create(E entity) {
		return restService.create(entity);
	}

	protected Call<E> update(E entity) {
		return restService.update(entity);
	}

	protected Call<E> purge(E entity) {
		return restService.purge(entity.getUuid());
	}
}
