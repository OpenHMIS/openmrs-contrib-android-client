package org.openmrs.mobile.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.openmrs.mobile.data.db.BaseMetadataDbService;
import org.openmrs.mobile.data.rest.BaseMetadataRestService;
import org.openmrs.mobile.models.BaseOpenmrsMetadata;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class BaseMetadataDataService<E extends BaseOpenmrsMetadata, DS extends BaseMetadataDbService<E>,
		RS extends BaseMetadataRestService<E, ?>> extends BaseDataService<E, DS, RS> implements MetadataDataService<E> {
	@Override
	public void getByNameFragment(@NonNull String name, @Nullable QueryOptions options, @Nullable PagingInfo pagingInfo,
			@NonNull GetCallback<List<E>> callback) {
		checkNotNull(name);
		checkNotNull(callback);

		executeMultipleCallback(callback, options, pagingInfo,
				() -> dbService.getByNameFragment(name, options, pagingInfo),
				() -> restService.getByNameFragment(name, options, pagingInfo));
	}
}
