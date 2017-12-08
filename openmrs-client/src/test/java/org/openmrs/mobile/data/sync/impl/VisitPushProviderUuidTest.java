package org.openmrs.mobile.data.sync.impl;

import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.structure.ModelAdapter;

import org.mockito.Mock;
import org.openmrs.mobile.data.ModelAsserters;
import org.openmrs.mobile.data.ModelGenerators;
import org.openmrs.mobile.data.RelatedObject;
import org.openmrs.mobile.data.CoreTestData;
import org.openmrs.mobile.data.db.impl.EncounterDbService;
import org.openmrs.mobile.data.db.impl.VisitDbService;
import org.openmrs.mobile.data.db.impl.VisitNoteDbService;
import org.openmrs.mobile.data.db.impl.VisitPhotoDbService;
import org.openmrs.mobile.data.db.impl.VisitTaskDbService;
import org.openmrs.mobile.data.rest.impl.VisitRestServiceImpl;
import org.openmrs.mobile.data.sync.PushProviderUuidTest;
import org.openmrs.mobile.models.Location;
import org.openmrs.mobile.models.Location_Table;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.models.VisitAttribute;
import org.openmrs.mobile.models.VisitAttribute_Table;
import org.openmrs.mobile.models.Visit_Table;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Response;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class VisitPushProviderUuidTest extends PushProviderUuidTest<Visit> {
	@Mock VisitPushProvider visitPushProvider;
	@Mock VisitRestServiceImpl visitRestService;

	@Override
	public void before() {
		super.before();

		visitPushProvider = new VisitPushProvider(
				new VisitDbService(repository), visitRestService,
				new VisitTaskDbService(repository), new EncounterDbService(repository),
				new VisitPhotoDbService(repository), new VisitNoteDbService(repository));
	}

	@Override
	protected ModelAsserters.ModelAsserter<Visit> getAsserter() {
		return ModelAsserters.VISIT;
	}

	@Override
	protected ModelGenerators.ModelGenerator<Visit> getGenerator() {
		Location_Table table = (Location_Table)FlowManager.getInstanceAdapter(Location.class);
		Location location = repository.querySingle(table,
				Location_Table.uuid.eq(CoreTestData.Constants.Location.REGISTRATION_DESK_UUID));

		ModelGenerators.VisitGenerator generator = ModelGenerators.VISIT;
		generator.setLocation(location);

		return generator;
	}

	@Override
	protected ModelAdapter<Visit> getTable() {
		return (Visit_Table)FlowManager.getInstanceAdapter(Visit.class);
	}

	@Override
	protected void setupRestMocks(String newUuid, Visit entity, boolean createServerUuids) {
		if (createServerUuids) {
			List<RelatedObject> relatedObjects = getRelatedObjects(entity);
			entity.setUuid(newUuid);
			for (RelatedObject obj : relatedObjects) {
				obj.getEntity().setUuid(UUID.randomUUID().toString());
			}
		}

		Call<Visit> call = mock(Call.class);
		Response<Visit> response = Response.success(entity);

		when(daggerProviderHelper.getSyncProvider(any())).thenReturn(visitPushProvider);
		when(visitRestService.create(any())).thenReturn(call);
		try {
			when(call.execute()).thenReturn(response);
		} catch (IOException e) { }
	}

	@Override
	protected List<RelatedObject> getRelatedObjects(Visit entity) {
		List<RelatedObject> results = new ArrayList<>();

		if (entity.getAttributes() != null) {
			ModelAdapter<VisitAttribute> visitAttributeTable = (VisitAttribute_Table)FlowManager.getInstanceAdapter(
					VisitAttribute.class);
			for (VisitAttribute attribute : entity.getAttributes()) {
				results.add(new RelatedObject(VisitAttribute.class, visitAttributeTable, attribute));
			}
		}

		return results;
	}
}
