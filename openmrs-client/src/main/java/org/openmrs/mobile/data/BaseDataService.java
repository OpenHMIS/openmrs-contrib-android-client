package org.openmrs.mobile.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.common.base.Supplier;

import org.openmrs.mobile.data.rest.RestConstants;
import org.openmrs.mobile.data.rest.RestServiceBuilder;
import org.openmrs.mobile.models.BaseOpenmrsObject;
import org.openmrs.mobile.models.Results;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class BaseDataService<E extends BaseOpenmrsObject, S> implements DataService<E> {
    protected S restService;

    protected BaseDataService() {
        restService = RestServiceBuilder.createService(getRestServiceClass());
    }

    /**
     * Gets the rest service class defined by the implementing class.
     * @return The rest service class
     */
    protected abstract Class<S> getRestServiceClass();

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

    protected abstract Call<E> _restGetByUuid(String restPath, String uuid, String representation);

    protected abstract Call<Results<E>> _restGetAll(String restPath, PagingInfo pagingInfo, String representation);

    protected abstract Call<E> _restCreate(String restPath, E entity);

    protected abstract Call<E> _restUpdate(String restPath, E entity);

    protected abstract Call<E> _restPurge(String restPath, String uuid);

    @Override
    public void getByUUID(@NonNull String uuid, @NonNull GetSingleCallback<E> callback) {
        checkNotNull(uuid);
        checkNotNull(callback);

        executeSingleCallback(callback, () -> _restGetByUuid(buildRestRequestPath(), uuid, RestConstants.Representations.FULL));

        // Build local query
        // Build REST query

        // Check local for entity
        // If found, check for updates via rest
        // If not found, get from rest

        // Add to local
    }

    @Override
    public void getAll(boolean includeInactive, @Nullable PagingInfo pagingInfo,
                       @NonNull GetMultipleCallback<E> callback) {
        checkNotNull(callback);

        executeMultipleCallback(callback, pagingInfo, () -> _restGetAll(buildRestRequestPath(),
                pagingInfo, RestConstants.Representations.FULL));
    }

    @Override
    public void search(@NonNull E template, @Nullable PagingInfo pagingInfo, @NonNull GetMultipleCallback<E> callback) {
        checkNotNull(template);
        checkNotNull(callback);
    }

    @Override
    public void create(@NonNull E entity, @NonNull GetSingleCallback<E> callback) {
        checkNotNull(entity);
        checkNotNull(callback);

        executeSingleCallback(callback, () -> _restCreate(buildRestRequestPath(), entity));
    }

    @Override
    public void update(@NonNull E entity, @NonNull GetSingleCallback<E> callback) {
        checkNotNull(entity);
        checkNotNull(callback);

        executeSingleCallback(callback, () -> _restUpdate(buildRestRequestPath(), entity));
    }

    @Override
    public void purge(@NonNull E entity, @NonNull VoidCallback callback) {
        checkNotNull(entity);
        checkNotNull(callback);

        executeVoidCallback(callback, () -> _restPurge(buildRestRequestPath(), entity.getUuid()));
    }

    /**
     * Helper method to build the rest request path.
     * @return The rest request path
     */
    protected String buildRestRequestPath() {
        return getRestPath() + "/" + getEntityName();
    }

    protected void executeSingleCallback(GetSingleCallback<E> callback, Supplier<Call<E>> supplier) {
        supplier.get().enqueue(new Callback<E>() {
            @Override
            public void onResponse(Call<E> call, Response<E> response) {
                callback.onCompleted(response.body());
            }

            @Override
            public void onFailure(Call<E> call, Throwable t) {
                callback.onError(t);
            }
        });
    }

    protected void executeMultipleCallback(GetMultipleCallback<E> callback, PagingInfo pagingInfo,
                                           Supplier<Call<Results<E>>> supplier) {
        supplier.get().enqueue(new Callback<Results<E>>() {
            @Override
            public void onResponse(Call<Results<E>> call, Response<Results<E>> response) {
                callback.onCompleted(response.body().getResults());
            }

            @Override
            public void onFailure(Call<Results<E>> call, Throwable t) {
                callback.onError(t);
            }
        });
    }

    protected void executeVoidCallback(VoidCallback callback, Supplier<Call<E>> supplier) {
        supplier.get().enqueue(new Callback<E>() {
            @Override
            public void onResponse(Call<E> call, Response<E> response) {
                callback.onCompleted();
            }

            @Override
            public void onFailure(Call<E> call, Throwable t) {
                callback.onError(t);
            }
        });
    }

    protected boolean isPagingValid(PagingInfo pagingInfo) {
        if (pagingInfo == null || pagingInfo.getPage() == 0) {
            return false;
        } else {
            return true;
        }
    }
}

