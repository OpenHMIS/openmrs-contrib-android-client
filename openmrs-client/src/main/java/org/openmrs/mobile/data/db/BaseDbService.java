package org.openmrs.mobile.data.db;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.raizlabs.android.dbflow.sql.language.From;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.ModelAdapter;

import org.openmrs.mobile.data.DataOperationException;
import org.openmrs.mobile.data.PagingInfo;
import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.models.BaseOpenmrsObject;
import org.openmrs.mobile.utilities.Consumer;

import java.lang.reflect.ParameterizedType;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class BaseDbService<E extends BaseOpenmrsObject> implements DbService<E> {
	private Class<E> entityClass;

	protected Repository repository;

	public BaseDbService(Repository repository) {
		this.repository = repository;
	}

	protected abstract ModelAdapter<E> getEntityTable();

	protected void postLoad(@NonNull E entity) { }

	protected void preSave(@NonNull E entity) { }

	protected void postSave(@NonNull E entity) { }

	protected void preDelete(@NonNull E entity) { }

	protected void preDelete(@NonNull String uuid) { }

	protected void postDelete(@NonNull E entity) { }

	protected void postDelete(@NonNull String uuid) { }

	protected void preDeleteAll() { }

	protected void postDeleteAll() { }

	@Override
	public long getCount(QueryOptions options) {
		ModelAdapter<E> table = getEntityTable();
		if (table == null) {
			return 0;
		}

		return repository.count(table);
	}

	@Override
	public List<E> getAll(@Nullable QueryOptions options, @Nullable PagingInfo pagingInfo) {
		return executeQuery(options, pagingInfo, getEntityClass());
	}

	@Override
	public E getByUuid(@NonNull String uuid, @Nullable QueryOptions options) {
		checkNotNull(uuid);
		ModelAdapter<E> table = getEntityTable();
		if (table == null) {
			return null;
		}

		E result = repository.querySingle(table, table.getProperty("uuid").eq(uuid));

		if (result != null) {
			postLoad(result);
		}

		return result;
	}

	@Override
	public List<E> saveAll(@NonNull List<E> entities) {
		checkNotNull(entities);
		ModelAdapter<E> table = getEntityTable();
		if (table == null) {
			return null;
		}

		for (E entity : entities) {
			if (entity != null) {
				preSave(entity);
			}
		}

		repository.saveAll(table, entities);

		for (E entity : entities) {
			if (entity != null) {
				postSave(entity);
			}
		}

		return entities;
	}

	@Override
	public E save(@NonNull E entity) {
		checkNotNull(entity);
		ModelAdapter<E> table = getEntityTable();
		if (table == null) {
			return null;
		}

		preSave(entity);

		if (repository.save(table, entity)) {
			postSave(entity);

			return entity;
		} else {
			throw new DataOperationException("Entity save failed.");
		}
	}

	@Override
	public void delete(@NonNull E entity) {
		checkNotNull(entity);
		ModelAdapter<E> table = getEntityTable();
		if (table == null) {
			return;
		}

		preDelete(entity);

		if (repository.delete(table, entity)) {
			postDelete(entity);
		}
	}

	@Override
	public void delete(@NonNull String uuid) {
		checkNotNull(uuid);
		ModelAdapter<E> table = getEntityTable();
		if (table == null) {
			return;
		}

		preDelete(uuid);

		repository.deleteAll(table, table.getProperty("uuid").eq(uuid));

		postDelete(uuid);
	}

	@Override
	public void deleteAll() {
		ModelAdapter<E> table = getEntityTable();
		if (table == null) {
			return;
		}

		preDeleteAll();

		repository.deleteAll(table);

		postDeleteAll();
	}

	protected List<E> executeQuery(@Nullable QueryOptions options, @Nullable PagingInfo pagingInfo,
			@Nullable Consumer<From<E>> where) {
		return executeQuery(options, pagingInfo, getEntityClass(), where);
	}

	protected <M> List<M> executeQuery(@Nullable QueryOptions options, @Nullable PagingInfo pagingInfo,
			@NonNull Class<M> cls) {
		checkNotNull(cls);

		return executeQuery(options, pagingInfo, cls, null);
	}

	protected <M> List<M> executeQuery(@Nullable QueryOptions options, @Nullable PagingInfo pagingInfo,
			@NonNull Class<M> cls, @Nullable Consumer<From<M>> where) {
		checkNotNull(cls);
		ModelAdapter<E> table = getEntityTable();
		if (table == null) {
			return null;
		}

		// Set up basic select query
		From<M> from = SQLite.select().from(cls);

		// Add paging logic, if defined
		if (PagingInfo.isValid(pagingInfo)) {
			// Check if paging total should be loaded
			if (pagingInfo.shouldLoadRecordCount()) {
				// Loading total record count
				From<M> pagingTotalQuery = SQLite.selectCountOf().from(cls);

				if (where != null) {
					where.accept(pagingTotalQuery);
				}

				pagingInfo.setTotalRecordCount((int)repository.count(pagingTotalQuery));
			}

			// Set up paging logic
			from.limit(pagingInfo.getPageSize())
					.offset((pagingInfo.getPage() - 1) * pagingInfo.getPageSize());
		}

		// Add Where clauses, if defined
		if (where != null) {
			where.accept(from);
		}

		// Return the results
		List<M> results = repository.query(from);

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

