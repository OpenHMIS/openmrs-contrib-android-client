package org.openmrs.mobile.data.sync.impl;

import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.structure.ModelAdapter;

import org.mockito.Mock;
import org.mockito.Spy;
import org.openmrs.mobile.data.ModelAsserters;
import org.openmrs.mobile.data.ModelGenerators;
import org.openmrs.mobile.data.RelatedObject;
import org.openmrs.mobile.data.db.impl.PatientDbService;
import org.openmrs.mobile.data.db.impl.PersonDbService;
import org.openmrs.mobile.data.db.impl.SyncLogDbService;
import org.openmrs.mobile.data.db.impl.VisitDbService;
import org.openmrs.mobile.data.db.impl.VisitPhotoDbService;
import org.openmrs.mobile.data.db.impl.VisitTaskDbService;
import org.openmrs.mobile.data.rest.impl.PatientRestServiceImpl;
import org.openmrs.mobile.data.sync.PushProviderUuidTest;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.PatientIdentifier;
import org.openmrs.mobile.models.PatientIdentifier_Table;
import org.openmrs.mobile.models.Patient_Table;
import org.openmrs.mobile.models.Person;
import org.openmrs.mobile.models.PersonAddress;
import org.openmrs.mobile.models.PersonAddress_Table;
import org.openmrs.mobile.models.PersonAttribute;
import org.openmrs.mobile.models.PersonAttribute_Table;
import org.openmrs.mobile.models.PersonName;
import org.openmrs.mobile.models.PersonName_Table;
import org.openmrs.mobile.models.Person_Table;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PatientPushProviderUuidTest extends PushProviderUuidTest<Patient> {
	@Mock PatientPushProvider patientPushProvider;
	@Mock PatientRestServiceImpl patientRestService;

	@Override
	public void before() {
		super.before();

		patientPushProvider = new PatientPushProvider(new PatientDbService(repository), patientRestService,
				new PersonDbService(repository), new VisitDbService(repository),
				new VisitTaskDbService(repository),
				new VisitPhotoDbService(repository));
	}

	@Override
	protected ModelAsserters.ModelAsserter<Patient> getAsserter() {
		return ModelAsserters.PATIENT;
	}

	@Override
	protected ModelGenerators.ModelGenerator<Patient> getGenerator() {
		return ModelGenerators.PATIENT;
	}

	@Override
	protected ModelAdapter<Patient> getTable() {
		return (Patient_Table)FlowManager.getInstanceAdapter(Patient.class);
	}

	@Override
	protected void setupRestMocks(String newUuid, Patient entity, boolean createServerUuids) {
		if (createServerUuids) {
			List<RelatedObject> relatedObjects = getRelatedObjects(entity);
			entity.setUuid(newUuid);
			for (RelatedObject obj : relatedObjects) {
				obj.getEntity().setUuid(UUID.randomUUID().toString());
			}
		}

		Call<Patient> call = mock(Call.class);
		Response<Patient> response = Response.success(entity);

		when(daggerProviderHelper.getSyncProvider(any())).thenReturn(patientPushProvider);
		when(patientRestService.create(any())).thenReturn(call);
		try {
			when(call.execute()).thenReturn(response);
		} catch (IOException e) { }
	}

	@Override
	protected List<RelatedObject> getRelatedObjects(Patient entity) {
		List<RelatedObject> results = new ArrayList<>();

		if (entity.getPerson() != null) {
			ModelAdapter<Person> personTable = (Person_Table)FlowManager.getInstanceAdapter(Person.class);
			results.add(new RelatedObject(Person.class, personTable, entity.getPerson()));

			ModelAdapter<PersonName> personNameTable = (PersonName_Table)FlowManager.getInstanceAdapter(PersonName.class);
			for (PersonName name : entity.getPerson().getNames()) {
				results.add(new RelatedObject(PersonName.class, personNameTable, name));
			}

			ModelAdapter<PersonAddress> personAddressTable = (PersonAddress_Table)FlowManager.getInstanceAdapter(
					PersonAddress.class);
			for (PersonAddress address : entity.getPerson().getAddresses()) {
				results.add(new RelatedObject(PersonAddress.class, personAddressTable, address));
			}

			ModelAdapter<PersonAttribute> personAttributeTable = (PersonAttribute_Table)FlowManager.getInstanceAdapter(
					PersonAttribute.class);
			for (PersonAttribute attribute : entity.getPerson().getAttributes()) {
				results.add(new RelatedObject(PersonAttribute.class, personAttributeTable, attribute));
			}
		}

		if (entity.getIdentifiers() != null) {
			ModelAdapter<PatientIdentifier> patientIdTable = (PatientIdentifier_Table)FlowManager.getInstanceAdapter(
					PatientIdentifier.class);
			for (PatientIdentifier id : entity.getIdentifiers()) {
				results.add(new RelatedObject(PatientIdentifier.class, patientIdTable, id));
			}
		}

		return results;
	}
}
