package org.openmrs.mobile.data.db.impl;

import android.support.annotation.NonNull;

import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.structure.ModelAdapter;

import org.openmrs.mobile.data.db.BaseEntityDbService;
import org.openmrs.mobile.data.db.EntityDbService;
import org.openmrs.mobile.data.db.Repository;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.models.Visit_Table;

import java.util.Date;

import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

public class VisitDbService extends BaseEntityDbService<Visit> implements EntityDbService<Visit> {
	@Inject
	public VisitDbService(Repository repository) {
		super(repository);
	}

	@Override
	protected ModelAdapter<Visit> getEntityTable() {
		return (Visit_Table)FlowManager.getInstanceAdapter(Visit.class);
	}

	public Visit endVisit(@NonNull Visit visit) {
		checkNotNull(visit);

		if (visit.getStopDatetime() == null) {
			visit.setStopDatetime(new Date());

			visit = save(visit);
		}

		return visit;
	}
}
