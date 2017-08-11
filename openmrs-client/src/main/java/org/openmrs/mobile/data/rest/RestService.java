package org.openmrs.mobile.data.rest;

import org.openmrs.mobile.data.PagingInfo;
import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.models.BaseOpenmrsObject;
import org.openmrs.mobile.models.RecordInfo;
import org.openmrs.mobile.models.Results;

import retrofit2.Call;

public interface RestService<E extends BaseOpenmrsObject> {
	Call<E> getByUuid(String uuid, QueryOptions options);

	Call<Results<E>> getAll(QueryOptions options, PagingInfo pagingInfo);

	Call<Results<RecordInfo>> getRecordInfo(QueryOptions options);

	Call<E> create(E entity);

	Call<E> update(E entity);

	Call<E> purge(String uuid);
}
