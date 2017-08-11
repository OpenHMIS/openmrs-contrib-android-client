package org.openmrs.mobile.data.rest;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import org.openmrs.mobile.data.PagingInfo;
import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.data.rest.retrofit.RestServiceBuilder;
import org.openmrs.mobile.models.BaseOpenmrsObject;
import org.openmrs.mobile.models.RecordInfo;
import org.openmrs.mobile.models.Results;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;

import retrofit2.Call;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class BaseRestService<E extends BaseOpenmrsObject, RS> implements RestService<E> {
	public static final String GET_BY_UUID_METHOD_NAME = "getByUuid";
	public static final String GET_ALL_METHOD_NAME = "getAll";
	public static final String GET_RECORD_INFO_METHOD_NAME = "getRecordInfo";
	public static final String CREATE_METHOD_NAME = "create";
	public static final String UPDATE_METHOD_NAME = "update";
	public static final String PURGE_METHOD_NAME = "purge";

	/**
	 * The REST service for this entity.
	 */
	protected RS restService;

	protected Class<E> entityClass;

	protected Class<RS> restServiceClass;

	/**
	 * The getByUuid method in the restService class
	 */
	private Method getByUuidMethod;

	/**
	 * The getAll method in the restService class
	 */
	private Method getAllMethod;

	/**
	 * The getRecordInfo in the restService class
	 */
	private Method getRecordInfoMethod;

	/**
	 * The create method in the restService class
	 */
	private Method createMethod;

	/**
	 * The update method in the restService class
	 */
	private Method updateMethod;

	/**
	 * The update method in the restService class
	 */
	private Method purgeMethod;

	protected BaseRestService() {
		loadGenericClasses();

		restService = RestServiceBuilder.createService(restServiceClass);

		initializeRestMethods();
	}

	/**
	 * Gets the rest path for the specific entity.
	 * @return The rest path
	 */
	protected abstract String getRestPath();

	/**
	 * Gets the entity name used for rest calls for the specific entity.
	 * @return The entity name
	 */
	protected abstract String getEntityName();

	@Override
	@SuppressWarnings("unchecked")
	public Call<E> getByUuid(@NonNull String uuid, @Nullable QueryOptions options) {
		checkNotNull(uuid);

		if (getByUuidMethod == null) {
			Log.w("Rest Service", "Attempt to call 'getByUuid' REST method but REST service method could not be found for "
					+ "entity '" + entityClass.getName() + "'");

			return null;
		}

		Call<E> call = null;

		try {
			Object result = getByUuidMethod.invoke(restService, buildRestRequestPath(), uuid,
					QueryOptions.getRepresentation(options), QueryOptions.getIncludeInactive(options));

			if (result != null) {
				call = (Call<E>)result;
			}
		} catch (Exception nex) {
			Log.e("Rest Service", "Exception executing REST getByUuid method", nex);

			call = null;
		}

		return call;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Call<Results<E>> getAll(@Nullable QueryOptions options, @Nullable PagingInfo pagingInfo) {
		if (getAllMethod == null) {
			Log.w("Rest Service", "Attempt to call 'getAll' REST method but REST service method could not be found for "
					+ "entity '" + entityClass.getName() + "'");

			return null;
		}

		Call<Results<E>> call = null;

		try {
			Object result = getAllMethod.invoke(restService, buildRestRequestPath(),
					QueryOptions.getRepresentation(options), QueryOptions.getIncludeInactive(options),
					PagingInfo.getLimit(pagingInfo), PagingInfo.getStartIndex(pagingInfo));

			if (result != null) {
				call = (Call<Results<E>>)result;
			}
		} catch (Exception nex) {
			Log.e("Rest Service", "Exception executing REST getAll method", nex);

			call = null;
		}

		return call;
	}

	@Override
	public Call<Results<RecordInfo>> getRecordInfo(QueryOptions options) {
		if (getRecordInfoMethod == null) {
			Log.w("Rest Service", "Attempt to call 'getRecordInfo' REST method but REST service method could not be found for "
					+ "entity '" + entityClass.getName() + "'");

			return null;
		}

		Call<Results<RecordInfo>> call = null;

		try {
			Object result = getRecordInfoMethod.invoke(restService, buildRestRequestPath(),
					RestConstants.Representations.RECORD_INFO, QueryOptions.getIncludeInactive(options));

			if (result != null) {
				call = (Call<Results<RecordInfo>>)result;
			}
		} catch (Exception nex) {
			Log.e("Rest Service", "Exception executing REST getRecordInfo method", nex);

			call = null;
		}

		return call;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Call<E> create(@NonNull E entity) {
		checkNotNull(entity);

		if (createMethod == null) {
			Log.w("Rest Service", "Attempt to call create REST method but REST service method could not be found for "
					+ "entity '" + entityClass.getName() + "'");

			return null;
		}

		Call<E> call = null;

		try {
			Object result = createMethod.invoke(restService, buildRestRequestPath(), entity);

			if (result != null) {
				call = (Call<E>)result;
			}
		} catch (Exception nex) {
			Log.e("Rest Service", "Exception executing REST create method", nex);

			call = null;
		}

		return call;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Call<E> update(@NonNull E entity) {
		checkNotNull(entity);

		if (updateMethod == null) {
			Log.w("Rest Service", "Attempt to call 'update' REST method but REST service method could not be found for "
					+ "entity '" + entityClass.getName() + "'");

			return null;
		}

		Call<E> call = null;

		try {
			Object result = updateMethod.invoke(restService, buildRestRequestPath(), entity.getUuid(), entity);

			if (result != null) {
				call = (Call<E>)result;
			}
		} catch (Exception nex) {
			Log.e("Rest Service", "Exception executing REST update method", nex);

			call = null;
		}

		return call;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Call<E> purge(@NonNull String uuid) {
		checkNotNull(uuid);

		if (purgeMethod == null) {
			Log.w("Rest Service", "Attempt to call 'purge' REST method but REST service method could not be found for "
					+ "entity '" + entityClass.getName() + "'");

			return null;
		}

		Call<E> call = null;

		try {
			Object result = purgeMethod.invoke(restService, buildRestRequestPath(), uuid);

			if (result != null) {
				call = (Call<E>)result;
			}
		} catch (Exception nex) {
			Log.e("Rest Service", "Exception executing REST purge method", nex);

			call = null;
		}

		return call;
	}

	/**
	 * Helper method to build the rest request path.
	 * @return The rest request path
	 */
	protected String buildRestRequestPath() {
		return getRestPath() + "/" + getEntityName();
	}

	/**
	 * Loads the rest methods from the retrofit entity interface
	 */
	private void initializeRestMethods() {
		Method[] methods = restService.getClass().getMethods();

		getByUuidMethod = findMethod(methods, GET_BY_UUID_METHOD_NAME);
		getAllMethod = findMethod(methods, GET_ALL_METHOD_NAME);
		getRecordInfoMethod = findMethod(methods, GET_RECORD_INFO_METHOD_NAME);
		createMethod = findMethod(methods, CREATE_METHOD_NAME);
		updateMethod = findMethod(methods, UPDATE_METHOD_NAME);
		purgeMethod = findMethod(methods, PURGE_METHOD_NAME);
	}

	protected Method findMethod(Method[] methods, String name) {
		for (Method method : methods) {
			if (method.getName().equals(name)) {
				return method;
			}
		}

		return null;
	}

	/**
	 * Loads usable instances of the actual classes of the generic types defined by the implementing sub-class.
	 */
	@SuppressWarnings("unchecked")
	protected void loadGenericClasses() {
		if (entityClass == null) {
			ParameterizedType parameterizedType = (ParameterizedType)getClass().getGenericSuperclass();

			entityClass = (Class<E>)parameterizedType.getActualTypeArguments()[0];
		}

		if (restServiceClass == null) {
			ParameterizedType parameterizedType = (ParameterizedType)getClass().getGenericSuperclass();

			restServiceClass = (Class<RS>)parameterizedType.getActualTypeArguments()[1];
		}
	}
}

