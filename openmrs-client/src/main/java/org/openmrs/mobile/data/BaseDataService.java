package org.openmrs.mobile.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.common.base.Supplier;

import org.openmrs.mobile.data.rest.GenericObjectRestService;
import org.openmrs.mobile.data.rest.RestServiceBuilder;
import org.openmrs.mobile.models.BaseOpenmrsObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class BaseDataService<E extends BaseOpenmrsObject> implements DataService<E> {
    protected GenericObjectRestService<E> restService;

    protected BaseDataService() {
        restService = RestServiceBuilder.createService(getRestServiceClass());
    }

    protected abstract Class<? extends GenericObjectRestService<E>> getRestServiceClass();

    protected abstract String getRestPath();

    protected abstract String getEntityName();


    @Override
    public void getByUUID(@NonNull String uuid, @NonNull GetSingleCallback<E> callback) {
        checkNotNull(uuid);
        checkNotNull(callback);

        executeSingleCallback(callback, () -> restService.getByUuid(buildRestRequestPath(), uuid));

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

        executeMultipleCallback(callback, pagingInfo, () -> restService.getAll(buildRestRequestPath()));
    }

    @Override
    public void search(@NonNull E template, @Nullable PagingInfo pagingInfo, @NonNull GetMultipleCallback<E> callback) {
        checkNotNull(template);
        checkNotNull(callback);
    }

    @Override
    public void save(@NonNull E entity, @NonNull GetSingleCallback<E> callback) {
        checkNotNull(entity);
        checkNotNull(callback);

        // Figure out if entity is new

        executeSingleCallback(callback, () -> restService.create(buildRestRequestPath(), entity));
    }

    @Override
    public void deactivate(@NonNull E entity, @NonNull GetSingleCallback<E> callback) {
        checkNotNull(entity);
        checkNotNull(callback);

        entity.setActive(false);

        executeSingleCallback(callback,
                () -> restService.update(buildRestRequestPath(), entity.getUuid(), entity));
    }

    @Override
    public void activate(@NonNull E entity, @NonNull GetSingleCallback<E> callback) {
        checkNotNull(entity);
        checkNotNull(callback);

        entity.setActive(true);

        executeSingleCallback(callback,
                () -> restService.update(buildRestRequestPath(), entity.getUuid(), entity));
    }

    @Override
    public void purge(@NonNull E entity, @NonNull VoidCallback callback) {
        checkNotNull(entity);
        checkNotNull(callback);

        executeVoidCallback(callback, () -> restService.purge(buildRestRequestPath(), entity.getUuid()));
    }

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
                                           Supplier<Call<List<E>>> supplier) {
        supplier.get().enqueue(new Callback<List<E>>() {
            @Override
            public void onResponse(Call<List<E>> call, Response<List<E>> response) {
                callback.onCompleted(response.body());
            }

            @Override
            public void onFailure(Call<List<E>> call, Throwable t) {
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
}

