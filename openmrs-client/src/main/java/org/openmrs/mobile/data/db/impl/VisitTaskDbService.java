package org.openmrs.mobile.data.db.impl;

import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.structure.ModelAdapter;

import org.openmrs.mobile.data.PagingInfo;
import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.data.db.BaseEntityDbService;
import org.openmrs.mobile.data.db.EntityDbService;
import org.openmrs.mobile.data.db.Repository;
import org.openmrs.mobile.models.VisitTask;
import org.openmrs.mobile.models.VisitTaskStatus;
import org.openmrs.mobile.models.VisitTask_Table;

import java.util.List;

import javax.inject.Inject;

public class VisitTaskDbService extends BaseEntityDbService<VisitTask> implements EntityDbService<VisitTask> {
	@Inject
	public VisitTaskDbService(Repository repository) {
		super(repository);
	}

	@Override
	protected ModelAdapter<VisitTask> getEntityTable() {
		return (VisitTask_Table)FlowManager.getInstanceAdapter(VisitTask.class);
	}

	public List<VisitTask> getAll(String status, String patientUuid, String visitUuid, QueryOptions options,
			PagingInfo pagingInfo) {
		List<VisitTask> results;
		VisitTaskStatus taskStatus;

		try {
			taskStatus = Enum.valueOf(VisitTaskStatus.class, status);

			results = executeQuery(options, pagingInfo,
					(f) -> f.where(VisitTask_Table.status.eq(taskStatus))
							.and(VisitTask_Table.patient_uuid.eq(patientUuid))
							.and(VisitTask_Table.visit_uuid.eq(visitUuid))
			);
		} catch (Exception ex) {
			results = null;
		}

		return results;
	}
}

