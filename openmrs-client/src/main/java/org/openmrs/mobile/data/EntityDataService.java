package org.openmrs.mobile.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.openmrs.mobile.models.BaseOpenmrsEntity;
import org.openmrs.mobile.models.Encounter;
import org.openmrs.mobile.models.Observation;
import org.openmrs.mobile.models.Patient;

/**
 * Represents classes the provide data services for {@link BaseOpenmrsEntity} objects.
 * @param <E> The entity class
 */
public interface EntityDataService<E extends BaseOpenmrsEntity> extends DataService<E> {
    /**
     * Gets entities associated with the specified patient.
     * @param patient The patient to search for
     * @param includeInactive {@code true} to include inactive entities; otherwise, {@code false}
     * @param pagingInfo The paging information or null to exclude paging
     * @param callback
     */
    void getByPatient(@NonNull Patient patient, boolean includeInactive,
                      @Nullable PagingInfo pagingInfo,
                      @NonNull GetMultipleCallback<E> callback);
}
