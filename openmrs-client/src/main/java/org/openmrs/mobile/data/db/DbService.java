package org.openmrs.mobile.data.db;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.openmrs.mobile.data.PagingInfo;
import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.models.BaseOpenmrsObject;

import java.util.List;

public interface DbService<E extends BaseOpenmrsObject> {
	/**
	 * Gets the number of records in the table.
	 * @param options The query options
	 * @return The number of records in the table
	 */
	long getCount(@Nullable QueryOptions options);

	/**
	 * Gets all entity records.
	 * @param options The query options
	 * @param pagingInfo The paging information
	 * @return The entity records
	 */
	List<E> getAll(@Nullable QueryOptions options, @Nullable PagingInfo pagingInfo);

	/**
	 * Gets an entity with the specified uuid.
	 * @param uuid The entity uuid
	 * @param options The query options
	 * @return The entity with the specified uuid or {@code null} if not found
	 */
	E getByUuid(@NonNull String uuid, @Nullable QueryOptions options);

	/**
	 * Saves all specified entities
	 * @param entities The entities to save
	 * @return The saved entities
	 */
	List<E> saveAll(@NonNull List<E> entities);

	/**
	 * Saves the specified entity, performs and insert for new records and an update for existing records
	 * @param entity The entity to save
	 * @return The saved entity
	 */
	E save(@NonNull E entity);

	/**
	 * Updates the specified entity, allowing the uuid to be changed along with saving the entity fields. If the uuid is
	 * not updated then just a normal update is performed.
	 * @param originalUuid The original entity uuid
	 * @param entity The entity to save.
	 * @return The updated entity
	 */
	E update(@NonNull String originalUuid, @NonNull E entity);

	/**
	 * Deletes the specified entity
	 * @param entity The entity to delete
	 */
	void delete(@NonNull E entity);

	/**
	 * Delete the entity with the specified uuid
	 * @param uuid The uuid of the entity to delete
	 */
	void delete(@NonNull String uuid);

	/**
	 * Deletes all records from the entity table.
	 */
	void deleteAll();
}
