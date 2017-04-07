package org.openmrs.mobile.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.openmrs.mobile.models.BaseOpenmrsEntity;
import org.openmrs.mobile.models.Patient;

import java.util.List;

public interface EntityDataService<E extends BaseOpenmrsEntity> extends DataService<E> {
    void getByPatient(@NonNull Patient patient, boolean includeInactive,
                      @Nullable PagingInfo pagingInfo,
                      @NonNull GetMultipleCallback<E> callback);
}
