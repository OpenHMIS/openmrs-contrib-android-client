package org.openmrs.mobile.data.db.impl;

import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.structure.ModelAdapter;

import org.openmrs.mobile.data.PagingInfo;
import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.data.db.BaseDbService;
import org.openmrs.mobile.data.db.DbService;
import org.openmrs.mobile.data.db.Repository;
import org.openmrs.mobile.models.PatientListContext;
import org.openmrs.mobile.models.PatientListContext_Table;

import java.util.List;

import javax.inject.Inject;

public class PatientListContextDbService extends BaseDbService<PatientListContext>
		implements DbService<PatientListContext> {
	@Inject
	public PatientListContextDbService(Repository repository) {
		super(repository);
	}

	@Override
	protected ModelAdapter<PatientListContext> getEntityTable() {
		return (PatientListContext_Table)FlowManager.getInstanceAdapter(PatientListContext.class);
	}

	public List<PatientListContext> getListPatients(String patientListUuid, QueryOptions options, PagingInfo pagingInfo) {
		return executeQuery(options, pagingInfo,
				(f) -> f.where(PatientListContext_Table.patientList_uuid.eq(patientListUuid)));
	}
}


