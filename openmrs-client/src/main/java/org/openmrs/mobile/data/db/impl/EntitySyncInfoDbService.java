package org.openmrs.mobile.data.db.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Inject;

import java.util.Date;

import android.support.annotation.NonNull;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.structure.ModelAdapter;
import org.openmrs.mobile.data.db.BaseDbService;
import org.openmrs.mobile.data.db.Repository;
import org.openmrs.mobile.models.BaseOpenmrsObject;
import org.openmrs.mobile.models.EntitySyncInfo;
import org.openmrs.mobile.models.EntitySyncInfo_Table;
import org.openmrs.mobile.models.Patient;

public class EntitySyncInfoDbService extends BaseDbService<EntitySyncInfo> {
	@Inject
	public EntitySyncInfoDbService(Repository repository) {
		super(repository);
	}

	@Override
	protected ModelAdapter<EntitySyncInfo> getEntityTable() {
		return (EntitySyncInfo_Table)FlowManager.getInstanceAdapter(EntitySyncInfo.class);
	}

	public <T extends BaseOpenmrsObject> EntitySyncInfo getLastSyncInfo(@NonNull T entity) {
		checkNotNull(entity);

		return getLastSyncInfo(entity.getClass(), entity.getUuid());
	}

	public <T extends BaseOpenmrsObject> void saveLastSyncInfo(@NonNull T entity, @NonNull Date lastSync) {
		checkNotNull(entity);
		checkNotNull(lastSync);

		saveLastSyncInfo(entity.getClass(), entity.getUuid(), lastSync);
	}

	public <T extends BaseOpenmrsObject> EntitySyncInfo getLastSyncInfo(@NonNull Class<T> tClass,
			@NonNull String entityUuid) {
		checkNotNull(tClass);
		checkNotNull(entityUuid);

		return repository.querySingle(getEntityTable(),
				EntitySyncInfo_Table.entityClass.eq(tClass.getSimpleName()),
				EntitySyncInfo_Table.entityUuid.eq(entityUuid));
	}

	public <T extends BaseOpenmrsObject> void saveLastSyncInfo(@NonNull Class<T> tClass, @NonNull String entityUuid,
			@NonNull Date lastSync) {
		checkNotNull(tClass);
		checkNotNull(entityUuid);
		checkNotNull(lastSync);

		EntitySyncInfo entitySyncInfo = getLastSyncInfo(tClass, entityUuid);
		if (entitySyncInfo == null) {
			entitySyncInfo = new EntitySyncInfo();
			entitySyncInfo.setEntityClass(tClass.getSimpleName());
			entitySyncInfo.setEntityUuid(entityUuid);
		}

		entitySyncInfo.setLastSync(lastSync);

		repository.save(getEntityTable(), entitySyncInfo);
	}
}
