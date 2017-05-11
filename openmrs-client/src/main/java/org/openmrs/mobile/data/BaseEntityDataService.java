package org.openmrs.mobile.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.openmrs.mobile.data.rest.RestConstants;
import org.openmrs.mobile.models.BaseOpenmrsEntity;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.Results;

import retrofit2.Call;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class BaseEntityDataService<E extends BaseOpenmrsEntity, S> extends BaseDataService<E, S>
		implements EntityDataService<E> {

	protected abstract Call<Results<E>> _restGetByPatient(String restPath, PagingInfo pagingInfo, String patientUuid,
			boolean includeInactive,
			String representation);

	@Override
	public void getByPatient(@NonNull Patient patient, boolean includeInactive,
			@Nullable PagingInfo pagingInfo,
			@NonNull GetMultipleCallback<E> callback) {
		checkNotNull(patient);
		checkNotNull(callback);

		executeMultipleCallback(callback, pagingInfo,
				() -> _restGetByPatient(buildRestRequestPath(), pagingInfo, patient.getUuid(), includeInactive,
						RestConstants.Representations.FULL));
	}
}
