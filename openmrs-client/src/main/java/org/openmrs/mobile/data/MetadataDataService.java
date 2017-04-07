package org.openmrs.mobile.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.openmrs.mobile.models.BaseOpenmrsMetadata;

import java.util.List;

public interface MetadataDataService<E extends BaseOpenmrsMetadata> extends DataService<E> {
    void getByNameFragment(@NonNull String name, boolean includeInactive,
                           @Nullable PagingInfo pagingInfo,
                           @NonNull GetMultipleCallback<E> callback);
}
