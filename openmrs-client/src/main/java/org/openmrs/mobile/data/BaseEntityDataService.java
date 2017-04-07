package org.openmrs.mobile.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.openmrs.mobile.data.rest.GenericEntityRestService;
import org.openmrs.mobile.models.BaseOpenmrsEntity;
import org.openmrs.mobile.models.Patient;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class BaseEntityDataService<E extends BaseOpenmrsEntity> extends BaseDataService<E>
        implements EntityDataService<E> {

    protected GenericEntityRestService<E> entityRestService;

    protected BaseEntityDataService() {
        super();

        entityRestService = (GenericEntityRestService<E>)restService;
    }

    @Override
    public void getByPatient(@NonNull Patient patient, boolean includeInactive,
                             @Nullable PagingInfo pagingInfo,
                             @NonNull GetMultipleCallback<E> callback) {
        checkNotNull(patient);
        checkNotNull(callback);

        executeMultipleCallback(callback, pagingInfo,
                () -> entityRestService.getByPatient(buildRestRequestPath(), patient.getUuid()));
    }
}
