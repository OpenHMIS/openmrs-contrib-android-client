package org.openmrs.mobile.data.db.impl;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.structure.ModelAdapter;

import org.openmrs.mobile.data.PagingInfo;
import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.data.db.BaseDbService;
import org.openmrs.mobile.data.db.DbService;
import org.openmrs.mobile.data.db.Repository;
import org.openmrs.mobile.models.Encounter;
import org.openmrs.mobile.models.Observation;
import org.openmrs.mobile.models.Observation_Table;

import java.util.List;

import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

public class ObsDbService extends BaseDbService<Observation> implements DbService<Observation> {
	@Inject
	public ObsDbService(Repository repository) {
		super(repository);
	}

	@Override
	protected ModelAdapter<Observation> getEntityTable() {
		return (Observation_Table)FlowManager.getInstanceAdapter(Observation.class);
	}

	public List<Observation> getVisitDocumentsObsByPatientAndConceptList(String patientUuid, QueryOptions options) {
		return null;
	}

	public List<Observation> getByEncounter(@NonNull Encounter encounter, @Nullable QueryOptions options,
			@Nullable PagingInfo pagingInfo) {
		checkNotNull(encounter);

		return executeQuery(options, pagingInfo,
				(f) -> f.where(Observation_Table.encounter_uuid.eq(encounter.getUuid())));
	}
}
