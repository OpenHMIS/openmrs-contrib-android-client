package org.openmrs.mobile.data.impl;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.openmrs.mobile.data.BaseDataService;
import org.openmrs.mobile.data.PagingInfo;
import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.data.db.impl.EncounterDbService;
import org.openmrs.mobile.data.db.impl.ObsDbService;
import org.openmrs.mobile.data.rest.impl.EncounterRestServiceImpl;
import org.openmrs.mobile.models.Encounter;
import org.openmrs.mobile.models.Observation;
import org.openmrs.mobile.models.SyncAction;

import java.util.List;

import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

public class EncounterDataService extends BaseDataService<Encounter, EncounterDbService, EncounterRestServiceImpl> {

	private ObsDbService observationDbService;

	@Inject
	public EncounterDataService(ObsDbService observationDbService) {
		this.observationDbService = observationDbService;
	}

	public void getByVisit(@NonNull String visitUuid, @Nullable QueryOptions options, @Nullable PagingInfo pagingInfo,
			@NonNull GetCallback<List<Encounter>> callback) {
		checkNotNull(visitUuid);
		checkNotNull(callback);

		executeMultipleCallback(callback, options, pagingInfo,
				() -> dbService.getByVisit(visitUuid, options, pagingInfo),
				() -> restService.getByVisit(visitUuid, options, pagingInfo));
	}

	@Override
	public void create(@NonNull Encounter entity, @NonNull GetCallback<Encounter> callback) {
		checkNotNull(entity);
		checkNotNull(callback);

		QueryOptions options = QueryOptions.REMOTE;
		executeSingleCallback(callback, options,
				() -> {
					entity.processRelationships();
					Encounter result = dbService.save(entity);
					syncLogService.save(result, SyncAction.NEW);
					return result;
				},
				() -> restService.create(entity),
				(Encounter result) -> {
					deleteVoidedObservationsFromEncounter(entity);
					return result;
				},
				(e) -> dbService.save(e));
	}

	@Override
	public void update(@NonNull Encounter entity, @NonNull GetCallback<Encounter> callback) {
		checkNotNull(entity);
		checkNotNull(callback);

		QueryOptions options = QueryOptions.REMOTE;
		executeSingleCallback(callback, options,
				() -> {
					entity.processRelationships();
					Encounter result = dbService.save(entity);
					syncLogService.save(result, SyncAction.UPDATED);
					return result;
				},
				() -> restService.update(entity),
				(Encounter result) -> {
					deleteVoidedObservationsFromEncounter(entity);
					return result;
				},
				(e) -> dbService.save(e));
	}

	private void deleteVoidedObservationsFromEncounter(Encounter entity) {
		if (!entity.getObs().isEmpty()) {
			for (Observation observation : entity.getObs()) {
				if (observation.getVoided() == true) {
					observationDbService.delete(observation);
				}
			}
		}
	}
}
