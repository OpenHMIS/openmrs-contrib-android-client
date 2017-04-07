package org.openmrs.mobile.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.openmrs.mobile.data.rest.GenericMetadataRestService;
import org.openmrs.mobile.models.BaseOpenmrsMetadata;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class BaseMetadataDataService<E extends BaseOpenmrsMetadata> extends BaseDataService<E>
        implements MetadataDataService<E> {
    protected GenericMetadataRestService<E> metadataRestService;

    protected BaseMetadataDataService() {
        super();

        metadataRestService = (GenericMetadataRestService<E>)restService;
    }

    @Override
    public void getByNameFragment(@NonNull String name, boolean includeInactive,
                                  @Nullable PagingInfo pagingInfo,
                                  @NonNull GetMultipleCallback<E> callback) {
        checkNotNull(name);
        checkNotNull(callback);

        executeMultipleCallback(callback, pagingInfo,
                () -> metadataRestService.getByNameFragment(buildRestRequestPath(), name));
    }
}
