package org.openmrs.mobile.data;

import org.openmrs.mobile.models.BaseOpenmrsObject;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

/**
 * Represents classes that provide data services to load entity data.
 * @param <E> The entity class
 */
public interface DataService<E extends BaseOpenmrsObject> {
    /**
     * Callback interface for operations with no return value.
     */
    interface VoidCallback {
        /**
         * Called if the operation completes successfully.
         */
        void onCompleted();

        /**
         * Called if the operation fails.
         * @param t The exception information
         */
        void onError(Throwable t);
    }


    /**
     * Callback interface for operations that return a single entity.
     * @param <E> The entity class
     */
    interface GetSingleCallback<E> {
        /**
         * Called if the operation completes successfully.
         * @param entity The returned entity
         */
        void onCompleted(E entity);

        /**
         * Called if the operation fails.
         * @param t The exception information
         */
        void onError(Throwable t);
    }

    /**
     * Callback interface for operations that return multiple entities.
     * @param <E> The entity class
     */
    interface GetMultipleCallback<E> {
        /**
         * Called if the operation completes successfully.
         * @param entities The returned entities
         */
        void onCompleted(List<E> entities);

        /**
         * Called if the operation fails.
         * @param t The exception information
         */
        void onError(Throwable t);
    }

    /**
     * Gets a single entity with the specified UUID.
     * @param uuid The entity UUID
     * @param callback
     */
    void getByUUID(@NonNull String uuid, @NonNull GetSingleCallback<E> callback);

    /**
     *
     * @param includeInactive
     * @param pagingInfo
     * @param callback
     */
    void getAll(boolean includeInactive, @Nullable PagingInfo pagingInfo,
                @NonNull GetMultipleCallback<E> callback);

    void search(@NonNull E template, @Nullable PagingInfo pagingInfo,
                @NonNull GetMultipleCallback<E> callback);

    void save(@NonNull E entity, @NonNull GetSingleCallback<E> callback);
    void deactivate(@NonNull E entity, @NonNull GetSingleCallback<E> callback);
    void activate(@NonNull E entity, @NonNull GetSingleCallback<E> callback);
    void purge(@NonNull E entity, @NonNull VoidCallback callback);
}

