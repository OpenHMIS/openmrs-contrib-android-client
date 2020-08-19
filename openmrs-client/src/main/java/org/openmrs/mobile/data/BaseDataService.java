package org.openmrs.mobile.data;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.common.base.Supplier;

import org.openmrs.mobile.application.Logger;
import org.openmrs.mobile.data.cache.CacheService;
import org.openmrs.mobile.data.db.BaseDbService;
import org.openmrs.mobile.data.rest.BaseRestService;
import org.openmrs.mobile.data.sync.SyncLogService;
import org.openmrs.mobile.models.BaseOpenmrsObject;
import org.openmrs.mobile.models.Resource;
import org.openmrs.mobile.models.Results;
import org.openmrs.mobile.models.SyncAction;
import org.openmrs.mobile.utilities.Consumer;
import org.openmrs.mobile.utilities.DataUtil;
import org.openmrs.mobile.utilities.Dealer;
import org.openmrs.mobile.utilities.Function;
import org.openmrs.mobile.utilities.NetworkUtils;
import org.openmrs.mobile.utilities.StringUtils;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.util.List;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class BaseDataService<E extends BaseOpenmrsObject, DS extends BaseDbService<E>,
		RS extends BaseRestService<E, ?>> implements DataService<E> {
	public static final String TAG = "Data Service";

	/**
	 * The database service for this entity.
	 */
	@Inject
	protected DS dbService;

	/**
	 * The REST service for this entity.
	 */
	@Inject
	protected RS restService;

	/**
	 * The global caching service
	 */
	@Inject
	protected CacheService cacheService;

	/**
	 * The utilities to handle network checks
	 */
	@Inject
	protected NetworkUtils networkUtils;

	/**
	 * The logging utility for the app
	 */
	@Inject
	protected Logger logger;

	@Inject
	protected SyncLogService syncLogService;

	private Class<E> entityClass;

	private Dealer<E> defaultDBDealer = new Dealer<E>() {
		@Override
		public void reject() {
			logger.e("DB Data was rejected");
		}

		@Override
		public E get() {
			throw new UnsupportedOperationException("defaultDBDealer doesn't support this method");
		}
	};

	@Override
	public void getByUuid(@NonNull String uuid, @Nullable QueryOptions options, @NonNull GetCallback<E> callback) {
		checkNotNull(uuid);
		checkNotNull(callback);

		executeSingleCallback(callback, options,
				new Dealer<E>() {
					@Override
					public void reject() {
						dbService.delete(uuid);
					}

					@Override
					public E get() {
						return dbService.getByUuid(uuid, options);
					}
				},
				() -> restService.getByUuid(uuid, options));
	}

	@Override
	public E getLocalByUuid(@NonNull String uuid, @Nullable QueryOptions options) {
		checkNotNull(uuid);

		return dbService.getByUuid(uuid, options);
	}

	@Override
	public void getAll(@Nullable QueryOptions options, @Nullable PagingInfo pagingInfo,
	                   @NonNull GetCallback<List<E>> callback) {
		checkNotNull(callback);

		executeMultipleCallback(callback, options, pagingInfo,
				() -> dbService.getAll(options, pagingInfo),
				() -> restService.getAll(options, pagingInfo));
	}

	@Override
	public void create(@NonNull E entity, @NonNull GetCallback<E> callback) {
		checkNotNull(entity);
		checkNotNull(callback);

		QueryOptions options = QueryOptions.REMOTE;
		executeSingleCallback(callback, options,
				new Dealer<E>() {
					@Override
					public void reject() {
						defaultDBDealer.reject();
					}

					@Override
					public E get() {
						entity.processRelationships();
						E result = dbService.save(entity);
						syncLogService.save(result, SyncAction.NEW);
						return result;
					}
				},
				() -> restService.create(entity));
	}

	@Override
	public void update(@NonNull E entity, @NonNull GetCallback<E> callback) {
		checkNotNull(entity);
		checkNotNull(callback);

		QueryOptions options = QueryOptions.REMOTE;
		executeSingleCallback(callback, options,
				new Dealer<E>() {
					@Override
					public void reject() {
						defaultDBDealer.reject();
					}

					@Override
					public E get() {
						entity.processRelationships();
						E result = dbService.save(entity);
						syncLogService.save(result, SyncAction.UPDATED);
						return result;
					}
				},
				() -> restService.update(entity));
	}

	@Override
	public void purge(@NonNull E entity, @NonNull VoidCallback callback) {
		checkNotNull(entity);
		checkNotNull(callback);

		executeVoidCallback(entity, callback, QueryOptions.REMOTE,
				() -> dbService.delete(entity.getUuid()),
				() -> restService.purge(entity.getUuid()));
	}

	/**
	 * Executes a data operation with no return result.
	 *
	 * @param callback      The operation callback
	 * @param dbOperation   The database operation to perform
	 * @param restOperation The REST operation to perform
	 */
	protected void executeVoidCallback(@Nullable E entity, @NonNull VoidCallback callback,
	                                   @Nullable QueryOptions options, @NonNull Runnable dbOperation,
	                                   @NonNull Supplier<Call<E>> restOperation) {
		checkNotNull(callback);
		checkNotNull(dbOperation);
		checkNotNull(restOperation);

		// Set up wrappers around callback and functions to work with performCallback method which assumes that something
		// will be returned
		GetCallback<E> callbackWrapper = new GetCallback<E>() {
			@Override
			public void onCompleted(E e) {
				callback.onCompleted();
			}

			@Override
			public void onError(Throwable t) {
				callback.onError(t);
			}
		};
		Dealer<E> dbDealer = new Dealer<E>() {
			@Override
			public void reject() {
				defaultDBDealer.reject();
			}

			@Override
			public E get() {
				dbOperation.run();

				// Return the entity so the calling function knows that the operation completed successfully
				return entity;
			}
		};
		Consumer<E> dbOperationWrapper = e -> dbOperation.run();

		performCallback(callbackWrapper, options, dbDealer, restOperation,
				(E result) -> result, dbOperationWrapper);
	}

	/**
	 * Executes a data operation which returns a single result. Valid results returned from the REST query will be saved
	 * to the
	 * db. Invalid results will cause an error to be thrown.
	 *
	 * @param callback  The operation callback
	 * @param dbSupplier  The database query operation to perform
	 * @param restQuery The REST query operation to perform
	 */
	protected void executeSingleCallback(@NonNull GetCallback<E> callback, @Nullable QueryOptions options,
	                                     @NonNull Supplier<E> dbSupplier, @NonNull Supplier<Call<E>> restQuery) {
		executeSingleCallback(callback, options, () -> dbSupplier.get(), restQuery, (e) -> dbService.save(e));
	}

	/**
	 * Executes a data operation which returns a single result. The result returned from the REST request will be
	 * processed
	 * by the specified dbOperation.-
	 *
	 * @param callback    The operation callback
	 * @param dbSupplier    The database query operation to perform
	 * @param restQuery   The REST query operation to perform
	 * @param dbOperation The database operation to perform when results are returned from the REST query
	 */
	protected void executeSingleCallback(@NonNull GetCallback<E> callback, @Nullable QueryOptions options,
	                                     @NonNull Supplier<E> dbSupplier, @NonNull Supplier<Call<E>> restQuery,
	                                     @NonNull Consumer<E> dbOperation) {
		checkNotNull(callback);
		checkNotNull(dbSupplier);
		checkNotNull(restQuery);
		checkNotNull(dbOperation);

		performCallback(callback, options,
				new Dealer<E>() {
					@Override
					public void reject() {
						defaultDBDealer.reject();
					}

					@Override
					public E get() {
						return dbSupplier.get();
					}
				}, restQuery, (E result) -> result, dbOperation);
	}

	/**
	 * Executes a data operation which returns a single result. The result returned from the REST request will be
	 * converted by the specified responseConverter function and then processed by the dbOperation.
	 *
	 * @param callback          The operation callback
	 * @param options           The query options
	 * @param dbSupplier          The database query operation to perform
	 * @param restQuery         The REST query operation to perform
	 * @param responseConverter The response converter function
	 * @param dbOperation       The database operation to perform when the results are returned from the REST query and
	 *                          converted by the response converter
	 * @param <T>               The entity type
	 * @param <R>               The type of the object returned by the REST query
	 */
	protected <T, R> void executeSingleCallback(@NonNull GetCallback<T> callback, @Nullable QueryOptions options,
	                                            @NonNull Supplier<T> dbSupplier, @NonNull Supplier<Call<R>> restQuery,
	                                            @NonNull Function<R, T> responseConverter,
	                                            @NonNull Consumer<T> dbOperation) {
		checkNotNull(callback);
		checkNotNull(dbSupplier);
		checkNotNull(restQuery);
		checkNotNull(responseConverter);
		checkNotNull(dbOperation);

		performCallback(callback, options,
				new Dealer<T>() {
					@Override
					public void reject() {
						defaultDBDealer.reject();
					}

					@Override
					public T get() {
						return dbSupplier.get();
					}
				}, restQuery, responseConverter, dbOperation);
	}

	/**
	 * Executes a data operation which can return multiple results. Valid results returned from the REST query will be
	 * saved to
	 * the DB. If any results are invalid, the entire set is rejected and not saved to the DB.
	 *
	 * @param callback   The operation callback
	 * @param pagingInfo The optional paging information
	 * @param dbSupplier The database query operation to perform
	 * @param restQuery  The REST query operation to perform
	 */
	protected void executeMultipleCallback(@NonNull GetCallback<List<E>> callback,
	                                       @Nullable QueryOptions options, @Nullable PagingInfo pagingInfo,
	                                       @NonNull Supplier<List<E>> dbSupplier,
	                                       @NonNull Supplier<Call<Results<E>>> restQuery) {
		executeMultipleCallback(callback, options, pagingInfo,
				new Dealer<List<E>>() {
					@Override
					public void reject() {
						defaultDBDealer.reject();
					}

					@Override
					public List<E> get() {
						return dbSupplier.get();
					}
				}, restQuery, (e) -> dbService.saveAll(e));
	}

	/**
	 * Executes a data operation which can return multiple results. The results returned from the REST request will be
	 * processed by the specified dbOperation.
	 *
	 * @param callback    The operation callback
	 * @param pagingInfo  The optional paging information
	 * @param dbSupplier    The database query operation to perform
	 * @param restQuery   The REST query operation to perform
	 * @param dbOperation The database operation to perform when results are returned from the REST query
	 */
	protected void executeMultipleCallback(@NonNull GetCallback<List<E>> callback, @Nullable QueryOptions options,
	                                       @Nullable PagingInfo pagingInfo, @NonNull Supplier<List<E>> dbSupplier,
	                                       @NonNull Supplier<Call<Results<E>>> restQuery,
	                                       @NonNull Consumer<List<E>> dbOperation) {
		checkNotNull(callback);
		checkNotNull(dbSupplier);
		checkNotNull(restQuery);
		checkNotNull(dbOperation);

		performCallback(callback, options,
				new Dealer<List<E>>() {
					@Override
					public void reject() {
						defaultDBDealer.reject();
					}

					@Override
					public List<E> get() {
						return dbSupplier.get();
					}
				},
				restQuery,
				(Results<E> results) -> {
					if (pagingInfo != null) {
						pagingInfo.setTotalRecordCount(results.getLength());
					}

					return results.getResults();
				},
				dbOperation);
	}

	/**
	 * Executes the data operation (via REST or DB, depending on the network state) and performs the correct callback
	 * based on the operation result.
	 */
	private <T, R> void performCallback(GetCallback<T> callback, QueryOptions options, Dealer<T> dbDealer,
	                                    Supplier<Call<R>> restSupplier, Function<R, T> responseConverter,
	                                    Consumer<T> dbSave) {
		if (!getCachedResult(callback, options)) {
			switch (QueryOptions.getRequestStrategy(options)) {
				case LOCAL_ONLY:
					performOfflineCallback(callback, options, dbDealer, restSupplier, responseConverter, dbSave);
					break;
				case LOCAL_THEN_REMOTE:
					performOfflineCallback(callback, options, dbDealer, restSupplier, responseConverter, dbSave);
					break;
				case REMOTE_THEN_LOCAL:
					performOnlineCallback(callback, options, dbDealer, restSupplier, responseConverter, dbSave);
					break;
			}
		}
	}

	/**
	 * Executes the specified data operation on the local db and performs the appropriate callback based on the operation
	 * result. If the call fails and the Request Strategy is to LOCAL_THEN_REMOTE, try REST
	 */
	private <T, R> void performOfflineCallback(GetCallback<T> callback, QueryOptions options, Dealer<T> dbDealer,
	                                           Supplier<Call<R>> restSupplier, Function<R, T> responseConverter,
	                                           Consumer<T> dbOperation) {
		// Perform the db task on another thread
		new Thread(() -> {
			try {
				// Try to get the entity from the db. If nothing is found just return null without any error
				T result = dbDealer.get();

				if (!DataUtil.isValid(result)) {
					dbDealer.reject();
					result = null;
				}

				if ((result == null || (result instanceof List<?> && ((List<?>) result).size() == 0)) &&
						networkUtils.isConnected() &&
						QueryOptions.getRequestStrategy(options) == RequestStrategy.LOCAL_THEN_REMOTE) {
					// This call will spin up another thread
					performOnlineCallback(callback, options, dbDealer, restSupplier, responseConverter, dbOperation);
				} else {
					T finalResult = result;
					setCachedResult(options, finalResult);
					// Execute callback on the current UI Thread
					new Handler(Looper.getMainLooper()).post(() -> callback.onCompleted(finalResult));
				}
			} catch (Exception ex) {
				// An exception occurred while trying to get the entity from the db

				// Execute callback on the current UI Thread
				new Handler(Looper.getMainLooper()).post(() -> callback.onError(ex));
			}
		}).start();
	}

	/**
	 * Executes the specified data operation, first trying REST and then the local db if the REST request fails. The
	 * result of the REST operation, if successful, is processed by the specified dbOperation. If the REST operation
	 * fails
	 * because of a network issue (as determined by the error response code), then the local db is used instead. If the
	 * local db operation returns null then an exception is thrown to indicate that the call failed.
	 */
	private <T, R> void performOnlineCallback(GetCallback<T> callback, QueryOptions options, Dealer<T> dbDealer,
	                                          Supplier<Call<R>> restSupplier, Function<R, T> responseConverter,
	                                          Consumer<T> dbOperation) {
		// Perform the rest task
		restSupplier.get().enqueue(new Callback<R>() {
			@Override
			public void onResponse(Call<R> call, Response<R> response) {
				// The request may have failed but still get here
				boolean wasResponseSuccessful = false;
				Throwable localError = null;
				T tempResult = null;
				if (response.isSuccessful()) {
					// If it was successful, try getting the data and making sure the data is good
					try {
						tempResult = responseConverter.apply(response.body());
						// Sometimes data doesn't come back from the server correctly. Log it so we can see what's going on
						logger.d(StringUtils.toJson(tempResult));
						// If the result isn't valid, throw an error
						if (!DataUtil.isValid(tempResult)) {
							DataOperationException incompleteDataError = new DataOperationException("The data returned from the " +
									"server was incomplete.");
							logger.e(incompleteDataError);
							throw incompleteDataError;
						}
						wasResponseSuccessful = true;
					} catch (Exception ex) {
						// An exception occurred while mapping the data, so assume the data fetch was unsuccessful
						localError = ex;
						wasResponseSuccessful = false;
					}
				}
				if (wasResponseSuccessful) {
					T result = tempResult;
					// This is likely running on the UI thread so spawn another thread to do the db stuff
					new Thread(() -> {
						// Save the resulting model to the db
						try {
							if (result instanceof Resource) {
								((Resource) result).processRelationships();
							}
							if (result instanceof List<?> && ((List<?>) result).size() > 0 && ((List<?>) result).get(0) instanceof Resource) {
								List<Resource> castResults = (List<Resource>) result;
								for (Resource castResult : castResults) {
									castResult.processRelationships();
								}
							}

							dbOperation.accept(result);

							setCachedResult(options, result);

							new Handler(Looper.getMainLooper()).post(() -> callback.onCompleted(result));
						} catch (Exception ex) {
							// An exception occurred while trying to save the entity in the db
							new Handler(Looper.getMainLooper()).post(() -> callback.onError(ex));
						}
					}).start();
				} else {
					// Something failed. If the issue was a connectivity issue then try to get the entity from the db)
					if (localError == null) {
						localError = new DataOperationException(response.code() + ": " + response.message());
					}
					if ((response.code() >= 502 && response.code() <= 504) || response.code() == 500) {
						logger.w(TAG, "REST response error (" + response.code() + ": " + response.message() + "");
						handleOnlineCallbackError(callback, options, dbDealer, localError);
					} else {
						// Some other type of error occurred so just notify the caller about the error
						callback.onError(localError);
					}
				}
			}

			@Override
			public void onFailure(Call<R> call, Throwable t) {
				DataOperationException dataOperationException = new DataOperationException(t);
				if (t instanceof IOException) {
					// Likely a connectivity issue so try to get from the db instead
					logger.w(TAG, "REST response error; trying local db", t);
					handleOnlineCallbackError(callback, options, dbDealer, dataOperationException);
				} else {
					// Some other type of exception occurred so just notify the caller about the exception
					logger.e(TAG, "REST request exception", t);
					callback.onError(dataOperationException);
				}
			}
		});
	}

	/**
	 * Combine the logic in one place for handling errors in the online response call of the data
	 *
	 * @param callback
	 * @param options
	 * @param dbDealer
	 * @param <T>
	 */
	private <T> void handleOnlineCallbackError(@NonNull GetCallback<T> callback, @NonNull QueryOptions options,
	                                           @NonNull Dealer<T> dbDealer, @NonNull Throwable exception) {
		checkNotNull(callback);
		checkNotNull(options);
		checkNotNull(dbDealer);
		checkNotNull(exception);
		if (QueryOptions.getRequestStrategy(options) != RequestStrategy.LOCAL_THEN_REMOTE) {
			new Thread(() -> {
				try {
					T tempResult = dbDealer.get();

					if (!DataUtil.isValid(tempResult)) {
						dbDealer.reject();
						tempResult = null;
					}

					T result = tempResult;
					if (result != null) {
						// Found it! Return this entity and ignore the rest connection error
						setCachedResult(options, result);
						new Handler(Looper.getMainLooper()).post(() -> callback.onCompleted(result));
					} else {
						// Could not find the entity so notify of the error
						new Handler(Looper.getMainLooper()).post(() -> callback.onError(exception));
					}
				} catch (Exception ex) {
					// An exception occurred while trying to get the entity from the db
					new Handler(Looper.getMainLooper()).post(() ->
							callback.onError(new DataOperationException(ex)));
				}
			}).start();
		} else {
			logger.w(TAG, "LOCAL_THEN_REMOTE request strategy means we can't try the local db");
			callback.onError(exception);
		}
	}

	protected <T> boolean getCachedResult(GetCallback<T> callback, QueryOptions options) {
		String cacheKey = QueryOptions.getCacheKey(options);
		if (StringUtils.notEmpty(cacheKey)) {
			Object cachedResult = cacheService.get(cacheKey);
			if (cachedResult != null) {
				callback.onCompleted((T) cachedResult);

				return true;
			}
		}

		return false;
	}

	protected void setCachedResult(QueryOptions options, Object value) {
		String cacheKey = QueryOptions.getCacheKey(options);
		if (StringUtils.notEmpty(cacheKey) && value != null) {
			cacheService.set(cacheKey, value);
		}
	}

	/**
	 * Gets a usable instance of the actual class of the generic type E defined by the implementing sub-class.
	 *
	 * @return The class object for the entity.
	 */
	@SuppressWarnings("unchecked")
	protected Class<E> getEntityClass() {
		if (entityClass == null) {
			ParameterizedType parameterizedType = (ParameterizedType) getClass().getGenericSuperclass();

			entityClass = (Class<E>) parameterizedType.getActualTypeArguments()[0];
		}

		return entityClass;
	}
}
