package org.openmrs.mobile.data.db.impl;

import android.support.annotation.NonNull;

import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.structure.ModelAdapter;

import org.openmrs.mobile.data.PagingInfo;
import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.data.db.BaseDbService;
import org.openmrs.mobile.data.db.DbService;
import org.openmrs.mobile.data.db.Repository;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.models.VisitPhoto;
import org.openmrs.mobile.models.VisitPhoto_Table;

import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

public class VisitPhotoDbService extends BaseDbService<VisitPhoto> implements DbService<VisitPhoto> {
	@Inject
	public VisitPhotoDbService(Repository repository) {
		super(repository);
	}

	@Override
	protected ModelAdapter<VisitPhoto> getEntityTable() {
		return (VisitPhoto_Table)FlowManager.getInstanceAdapter(VisitPhoto.class);
	}

	public VisitPhoto getPhotoByObservation(@NonNull String obsUuid) {
		checkNotNull(obsUuid);

		return repository.querySingle(entityTable, VisitPhoto_Table.observation_uuid.eq(obsUuid));
	}

	public List<VisitPhoto> getByVisit(@NonNull Visit visit) {
		checkNotNull(visit);

		return repository.query(getEntityTable(), VisitPhoto_Table.visit_uuid.eq(visit.getUuid()));
	}

	public List<VisitPhoto> getByPatient(@NonNull String uuid) {
		checkNotNull(uuid);

		return executeQuery(null, null, (f) -> f.where(VisitPhoto_Table.patient_uuid.eq(uuid)));
	}
}

