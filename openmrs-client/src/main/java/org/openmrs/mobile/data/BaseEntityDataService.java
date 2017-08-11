package org.openmrs.mobile.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.openmrs.mobile.data.db.BaseEntityDbService;
import org.openmrs.mobile.data.rest.BaseEntityRestService;
import org.openmrs.mobile.models.BaseOpenmrsEntity;
import org.openmrs.mobile.models.Patient;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class BaseEntityDataService<E extends BaseOpenmrsEntity, DS extends BaseEntityDbService<E>,
		RS extends BaseEntityRestService<E, ?>> extends BaseDataService<E, DS, RS> implements EntityDataService<E> {
	@Override
	public void getByPatient(@NonNull Patient patient, @Nullable QueryOptions options, @Nullable PagingInfo pagingInfo,
			@NonNull GetCallback<List<E>> callback) {
		checkNotNull(patient);
		checkNotNull(callback);

		executeMultipleCallback(callback, options, pagingInfo,
				() -> dbService.getByPatient(patient, options, pagingInfo),
				() -> restService.getByPatient(patient.getUuid(), options, pagingInfo));
	}
}
