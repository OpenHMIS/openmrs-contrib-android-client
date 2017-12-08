package org.openmrs.mobile.data.db;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.raizlabs.android.dbflow.sql.language.From;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.language.Where;
import com.raizlabs.android.dbflow.sql.queriable.ModelQueriable;
import com.raizlabs.android.dbflow.structure.ModelAdapter;

import org.openmrs.mobile.data.DataOperationException;
import org.openmrs.mobile.data.PagingInfo;
import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.models.BaseOpenmrsObject;
import org.openmrs.mobile.utilities.Function;

import java.lang.reflect.ParameterizedType;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class BaseDbService<E extends BaseOpenmrsObject> implements DbService<E> {
	private Class<E> entityClass;

	protected Repository repository;
	protected ModelAdapter<E> entityTable;

	public BaseDbService(Repository repository) {
		this.repository = repository;
		this.entityTable = getEntityTable();
	}

	protected abstract ModelAdapter<E> getEntityTable();

	protected void postLoad(@NonNull E entity) { }

	protected void preSave(@NonNull E entity) { }

	protected void postSave(@NonNull E entity) { }

	protected void preSaveAll(@NonNull List<E> entities) { }

	protected void postSaveAll(@NonNull List<E> entities) { }

	protected void preDelete(@NonNull E entity) { }

	protected void preDelete(@NonNull String uuid) { }

	protected void postDelete(@NonNull E entity) { }

	protected void postDelete(@NonNull String uuid) { }

	protected void preDeleteAll() { }

	protected void postDeleteAll() { }

	@Override
	public long getCount(QueryOptions options) {
		if (entityTable == null) {
			return 0;
		}

		return repository.count(entityTable);
	}

	@Override
	public List<E> getAll(@Nullable QueryOptions options, @Nullable PagingInfo pagingInfo) {
		return executeQuery(options, pagingInfo, getEntityClass());
	}

	@Override
	public E getByUuid(@NonNull String uuid, @Nullable QueryOptions options) {
		checkNotNull(uuid);
		if (entityTable == null) {
			return null;
		}

		E result = repository.querySingle(entityTable, entityTable.getProperty("uuid").eq(uuid));

		if (result != null) {
			postLoad(result);
		}

		return result;
	}

	@Override
	public List<E> saveAll(@NonNull List<E> entities) {
		checkNotNull(entities);
		if (entityTable == null) {
			return null;
		}

		preSaveAll(entities);

		repository.saveAll(entityTable, entities);

		postSaveAll(entities);

		return entities;
	}

	@Override
	public E save(@NonNull E entity) {
		checkNotNull(entity);
		if (entityTable == null) {
			return null;
		}

		preSave(entity);

		if (repository.save(entityTable, entity)) {
			postSave(entity);

			return entity;
		} else {
			throw new DataOperationException("Entity save failed.");
		}
	}

	@Override
	public E update(@NonNull String originalUuid, @NonNull E entity) {
		checkNotNull(originalUuid);
		checkNotNull(entity);
		if (entityTable == null) {
			return null;
		}

		preSave(entity);

		if (repository.update(entityTable, originalUuid, entity)) {
			postSave(entity);

			return entity;
		} else {
			throw new DataOperationException("Entity update failed.");
		}
	}

	@Override
	public void delete(@NonNull E entity) {
		checkNotNull(entity);
		if (entityTable == null) {
			return;
		}

		preDelete(entity);

		if (repository.delete(entityTable, entity)) {
			postDelete(entity);
		}
	}

	@Override
	public void delete(@NonNull String uuid) {
		checkNotNull(uuid);
		if (entityTable == null) {
			return;
		}

		preDelete(uuid);

		repository.deleteAll(entityTable, entityTable.getProperty("uuid").eq(uuid));

		postDelete(uuid);
	}

	@Override
	public void deleteAll() {
		if (entityTable == null) {
			return;
		}

		preDeleteAll();

		repository.deleteAll(entityTable);

		postDeleteAll();
	}

	protected List<E> executeQuery(@Nullable QueryOptions options, @Nullable PagingInfo pagingInfo,
			@Nullable Function<From<E>, ModelQueriable<E>> where) {
		return executeQuery(options, pagingInfo, getEntityClass(), where);
	}

	protected <M> List<M> executeQuery(@Nullable QueryOptions options, @Nullable PagingInfo pagingInfo,
			@NonNull Class<M> cls) {
		checkNotNull(cls);

		return executeQuery(options, pagingInfo, cls, null);
	}

	protected <M> List<M> executeQuery(@Nullable QueryOptions options, @Nullable PagingInfo pagingInfo,
			@NonNull Class<M> cls, @Nullable Function<From<M>, ModelQueriable<M>> where) {
		checkNotNull(cls);
		if (entityTable == null) {
			return null;
		}

		// Set up basic select query
		ModelQueriable<M> query = SQLite.select().from(cls);

		// Add Where clauses, if defined
		if (where != null) {
			query = where.apply((From<M>) query);
		}

		// Add paging logic, if defined
		if (PagingInfo.isValid(pagingInfo)) {
			// Check if paging total should be loaded
			if (pagingInfo.shouldLoadRecordCount()) {
				// Loading total record count
				ModelQueriable<M> pagingTotalQuery = SQLite.selectCountOf().from(cls);

				if (where != null) {
					pagingTotalQuery = where.apply((From<M>) pagingTotalQuery);
				}

				pagingInfo.setTotalRecordCount((int) repository.count(pagingTotalQuery));
			}

			// Set up paging logic
			query = (query instanceof From<?> ? (From<M>) query : (Where<M>) query).limit(pagingInfo.getPageSize());
			if (pagingInfo.getPage() > 1) {
				query = (query instanceof From<?> ? (From<M>) query : (Where<M>) query)
						.offset(pagingInfo.getStartIndex() - 1);
			}
		}

		// Return the results
		List<M> results = repository.query(query);

		// Ensure entity class is loaded
		getEntityClass();

		// Call post-load hook
		Boolean isEntity = null;
		if (results != null && !results.isEmpty()) {
			for (M item : results) {
				if (Boolean.TRUE.equals(isEntity) || entityClass.isInstance(item)) {
					postLoad((E)item);

					isEntity = true;
				} else {
					isEntity = false;
				}
			}
		}
		return results;
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

