package org.openmrs.mobile.data.db.impl;

import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.structure.ModelAdapter;

import org.openmrs.mobile.data.db.BaseDbService;
import org.openmrs.mobile.data.db.DbService;
import org.openmrs.mobile.data.db.Repository;
import org.openmrs.mobile.models.VisitPhoto;
import org.openmrs.mobile.models.VisitPhoto_Table;

import java.util.List;

import javax.inject.Inject;

public class VisitPhotoDbService extends BaseDbService<VisitPhoto> implements DbService<VisitPhoto> {
	@Inject
	public VisitPhotoDbService(Repository repository) {
		super(repository);
	}

	@Override
	protected ModelAdapter<VisitPhoto> getEntityTable() {
		return (VisitPhoto_Table)FlowManager.getInstanceAdapter(VisitPhoto.class);
	}

	public List<VisitPhoto> getByVisit(String uuid) {
		return repository.query(getEntityTable(), VisitPhoto_Table.visit_uuid.eq(uuid));
	}

	public List<VisitPhoto> getByPatient(String uuid) {
		return repository.query(getEntityTable(), VisitPhoto_Table.patient_uuid.eq(uuid));
	}
}

