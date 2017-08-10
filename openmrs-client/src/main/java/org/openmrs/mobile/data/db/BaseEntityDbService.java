package org.openmrs.mobile.data.db;

import org.openmrs.mobile.data.PagingInfo;
import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.models.BaseOpenmrsEntity;
import org.openmrs.mobile.models.Patient;

import java.util.List;

public abstract class BaseEntityDbService<E extends BaseOpenmrsEntity> extends BaseDbService<E>
		implements EntityDbService<E> {
	public BaseEntityDbService(Repository repository) {
		super(repository);
	}

	@Override
	public List<E> getByPatient(Patient patient, QueryOptions options, PagingInfo pagingInfo) {
		return executeQuery(options, pagingInfo, (w) -> w.where(getEntityTable().getProperty("patient").eq(patient)));
	}
}

