package org.openmrs.mobile.data.db.impl;

import android.support.annotation.NonNull;

import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.Method;
import com.raizlabs.android.dbflow.structure.ModelAdapter;

import org.openmrs.mobile.data.PagingInfo;
import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.data.db.BaseDbService;
import org.openmrs.mobile.data.db.DbService;
import org.openmrs.mobile.data.db.Repository;
import org.openmrs.mobile.models.Encounter;
import org.openmrs.mobile.models.Encounter_Table;
import org.openmrs.mobile.models.Observation;
import org.openmrs.mobile.models.Observation_Table;
import org.openmrs.mobile.models.Resource;

import java.util.List;

import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

public class EncounterDbService extends BaseDbService<Encounter> implements DbService<Encounter> {
	private static Observation_Table observationTable;

	static {
		observationTable = (Observation_Table)FlowManager.getInstanceAdapter(Observation.class);
	}
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

	public void deleteLocalRelatedObjects(@NonNull Encounter encounter) {
		checkNotNull(encounter);

		repository.deleteAll(observationTable, Observation_Table.encounter_uuid.eq(encounter.getUuid()),
				new Method("LENGTH", Observation_Table.uuid).lessThanOrEq(Resource.LOCAL_UUID_LENGTH));
	}
}