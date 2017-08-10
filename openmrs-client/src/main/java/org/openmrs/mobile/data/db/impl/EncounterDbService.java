package org.openmrs.mobile.data.db.impl;

import android.support.annotation.NonNull;

import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.structure.ModelAdapter;

import org.openmrs.mobile.data.PagingInfo;
import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.data.db.BaseDbService;
import org.openmrs.mobile.data.db.DbService;
import org.openmrs.mobile.data.db.Repository;
import org.openmrs.mobile.models.Encounter;
import org.openmrs.mobile.models.Encounter_Table;

import java.util.List;

import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

public class EncounterDbService extends BaseDbService<Encounter> implements DbService<Encounter> {
	@Inject
	public EncounterDbService(Repository repository) {
		super(repository);
	}

	@Override
	protected ModelAdapter<Encounter> getEntityTable() {
		return (Encounter_Table)FlowManager.getInstanceAdapter(Encounter.class);
	}

	public List<Encounter> getByEncounter(Encounter encounter, QueryOptions options, PagingInfo pagingInfo) {
		return null;
	}

	public List<Encounter> getByVisit(@NonNull String visitUuid, QueryOptions options, PagingInfo pagingInfo) {
		checkNotNull(visitUuid);

		return executeQuery(options, pagingInfo,
				(f) -> f.where(Encounter_Table.visit_uuid.eq(visitUuid)));
	}
}