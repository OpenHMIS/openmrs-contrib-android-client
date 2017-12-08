package org.openmrs.mobile.data.sync.impl;

import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.structure.ModelAdapter;

import org.mockito.Mock;
import org.openmrs.mobile.data.ModelAsserters;
import org.openmrs.mobile.data.ModelGenerators;
import org.openmrs.mobile.data.RelatedObject;
import org.openmrs.mobile.data.db.impl.EncounterDbService;
import org.openmrs.mobile.data.rest.impl.EncounterRestServiceImpl;
import org.openmrs.mobile.data.sync.PushProviderUuidTest;
import org.openmrs.mobile.models.Encounter;
import org.openmrs.mobile.models.Encounter_Table;
import org.openmrs.mobile.models.Observation;
import org.openmrs.mobile.models.Observation_Table;
import org.openmrs.mobile.models.Visit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Response;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EncounterPushProviderUuidTest extends PushProviderUuidTest<Encounter> {
	@Mock EncounterPushProvider encounterPushProvider;
	@Mock EncounterRestServiceImpl encounterRestService;

	@Override
	public void before() {
		super.before();

		encounterPushProvider = new EncounterPushProvider(new EncounterDbService(repository), encounterRestService);
	}

	@Override
	protected ModelAsserters.ModelAsserter<Encounter> getAsserter() {
		return ModelAsserters.ENCOUNTER;
	}

	@Override
	protected ModelGenerators.ModelGenerator<Encounter> getGenerator() {
		return ModelGenerators.ENCOUNTER;
	}

	@Override
	protected ModelAdapter<Encounter> getTable() {
		return (Encounter_Table)FlowManager.getInstanceAdapter(Encounter.class);
	}

	@Override
	protected void setupRestMocks(String newUuid, Encounter entity, boolean createServerUuids) {
		if (createServerUuids) {
			List<RelatedObject> relatedObjects = getRelatedObjects(entity);
			entity.setUuid(newUuid);
			for (RelatedObject obj : relatedObjects) {
				obj.getEntity().setUuid(UUID.randomUUID().toString());
			}
		}

		Call<Encounter> call = mock(Call.class);
		Response<Encounter> response = Response.success(entity);

		when(daggerProviderHelper.getSyncProvider(any())).thenReturn(encounterPushProvider);
		when(encounterRestService.create(any())).thenReturn(call);
		try {
			when(call.execute()).thenReturn(response);
		} catch (IOException e) { }
	}

	@Override
	protected List<RelatedObject> getRelatedObjects(Encounter entity) {
		List<RelatedObject> results = new ArrayList<>();

		if (entity.getObs() != null) {
			ModelAdapter<Observation> observationTable = (Observation_Table)FlowManager.getInstanceAdapter(
					Observation.class);
			for (Observation obs : entity.getObs()) {
				results.add(new RelatedObject(Observation.class, observationTable, obs));
			}
		}

		return results;
	}
}
