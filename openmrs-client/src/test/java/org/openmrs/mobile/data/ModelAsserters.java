package org.openmrs.mobile.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import junit.framework.Assert;

import org.openmrs.mobile.models.BaseOpenmrsAuditableObject;
import org.openmrs.mobile.models.BaseOpenmrsEntity;
import org.openmrs.mobile.models.BaseOpenmrsMetadata;
import org.openmrs.mobile.models.BaseOpenmrsObject;
import org.openmrs.mobile.models.Concept;
import org.openmrs.mobile.models.ConceptName;
import org.openmrs.mobile.models.Encounter;
import org.openmrs.mobile.models.Location;
import org.openmrs.mobile.models.Observation;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.PatientIdentifier;
import org.openmrs.mobile.models.Person;
import org.openmrs.mobile.models.PersonAddress;
import org.openmrs.mobile.models.PersonAttribute;
import org.openmrs.mobile.models.PersonName;
import org.openmrs.mobile.models.User;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.models.VisitAttribute;
import org.openmrs.mobile.models.VisitType;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class ModelAsserters {
	public static final ConceptAsserter CONCEPT = new ConceptAsserter();
	public static final ConceptNameAsserter CONCEPT_NAME = new ConceptNameAsserter();
	public static final LocationAsserter LOCATION = new LocationAsserter();
	public static final EncounterAsserter ENCOUNTER = new EncounterAsserter();
	public static final ObservationAsserter OBSERVATION = new ObservationAsserter();
	public static final PatientAsserter PATIENT = new PatientAsserter();
	public static final PatientIdentifierAsserter PATIENT_IDENTIFIER = new PatientIdentifierAsserter();
	public static final PersonAsserter PERSON = new PersonAsserter();
	public static final PersonAddressAsserter PERSON_ADDRESS = new PersonAddressAsserter();
	public static final PersonAttributeAsserter PERSON_ATTRIBUTE = new PersonAttributeAsserter();
	public static final PersonNameAsserter PERSON_NAME = new PersonNameAsserter();
	public static final UserAsserter USER = new UserAsserter();
	public static final UuidAsserter UUID = new UuidAsserter();
	public static final VisitAsserter VISIT = new VisitAsserter();
	public static final VisitAttributeAsserter VISIT_ATTRIBUTE = new VisitAttributeAsserter();
	public static final VistTypeAsserter VISIT_TYPE = new VistTypeAsserter();

	public static void assertListContainsUuid(List<? extends BaseOpenmrsObject> list, String uuid) {
		Assert.assertNotNull(list);
		Assert.assertNotNull(uuid);
		Assert.assertFalse("List is empty", list.size() == 0);
		Assert.assertFalse("Uuid is empty", uuid.isEmpty());

		for (BaseOpenmrsObject obj : list) {
			if (obj.getUuid().equalsIgnoreCase(uuid)) {
				return;
			}
		}

		Assert.fail("Entity with uuid '" + uuid + "' not found in list.");
	}

	public interface ModelAsserter<E> {
		void assertModel(E expected, E actual);
	}

	public static abstract class ObjectAsserter<E extends BaseOpenmrsObject> implements ModelAsserter<E> {
		public void assertModel(@NonNull E expected, @NonNull E actual) {
			checkNotNull(expected);
			checkNotNull(actual);

			Assert.assertEquals(expected.getUuid(), actual.getUuid());
			Assert.assertEquals(expected.getDisplay(), actual.getDisplay());
		}

		protected <M> void assertSubModel(@Nullable M expected, @Nullable M actual, ModelAsserter<M> asserter) {
			if (expected == null) {
				Assert.assertNull(actual);
			} else {
				asserter.assertModel(expected, actual);
			}
		}

		protected <M extends BaseOpenmrsObject> void assertSubList(@Nullable List<M> expected, @Nullable List<M> actual) {
			if (expected == null) {
				Assert.assertNull(actual);
			} else {
				Assert.assertNotNull(actual);
				Assert.assertEquals(expected.size(), actual.size());

				for (int i = 0; i < expected.size(); i++) {
					Assert.assertNotNull(expected.get(i));
					Assert.assertNotNull(actual.get(i));

					Assert.assertEquals(expected.get(i).getUuid(), actual.get(i).getUuid());
				}
			}
		}

		protected <M> void assertSubList(@Nullable List<M> expected, @Nullable List<M> actual, ModelAsserter<M> asserter) {
			if (expected == null) {
				Assert.assertNull(actual);
			} else {
				Assert.assertNotNull(actual);
				Assert.assertEquals(expected.size(), actual.size());

				for (int i = 0; i < expected.size(); i++) {
					asserter.assertModel(expected.get(i), actual.get(i));
				}
			}
		}
	}

	public static abstract class AuditableAsserter<E extends BaseOpenmrsAuditableObject> extends ObjectAsserter<E> {
		@Override
		public void assertModel(@NonNull E expected, @NonNull E actual) {
			super.assertModel(expected, actual);

			Assert.assertEquals(expected.getDateCreated(), actual.getDateCreated());
			Assert.assertEquals(expected.getDateChanged(), actual.getDateChanged());

			assertSubModel(expected.getCreator(), actual.getCreator(), UUID);
			assertSubModel(expected.getChangedBy(), actual.getChangedBy(), UUID);
		}
	}

	public static abstract class MetadataAsserter<E extends BaseOpenmrsMetadata> extends AuditableAsserter<E> {
		@Override
		public void assertModel(@NonNull E expected, @NonNull E actual) {
			super.assertModel(expected, actual);

			Assert.assertEquals(expected.getName(), actual.getName());
			Assert.assertEquals(expected.getDescription(), actual.getDescription());
		}
	}

	public static abstract class EntityAsserter<E extends BaseOpenmrsEntity> extends AuditableAsserter<E> {
		@Override
		public void assertModel(@NonNull E expected, @NonNull E actual) {
			super.assertModel(expected, actual);

			Assert.assertEquals(expected.getVoided(), actual.getVoided());
			Assert.assertEquals(expected.getVoidReason(), actual.getVoidReason());
			Assert.assertEquals(expected.getDateVoided(), actual.getDateVoided());

			assertSubModel(expected.getVoidedBy(), actual.getVoidedBy(), UUID);
		}
	}

	public static class UuidAsserter extends ObjectAsserter<BaseOpenmrsObject> {
		@Override
		public void assertModel(@NonNull BaseOpenmrsObject expected, @NonNull BaseOpenmrsObject actual) {
			checkNotNull(expected);
			checkNotNull(actual);

			Assert.assertEquals(expected.getUuid(), actual.getUuid());
		}
	}

	public static class UserAsserter extends ObjectAsserter<User> {
		@Override
		public void assertModel(@NonNull User expected, @NonNull User actual) {
			super.assertModel(expected, actual);

			Assert.assertEquals(expected.getSystemId(), actual.getSystemId());
			Assert.assertEquals(expected.getUsername(), actual.getUsername());

			assertSubModel(expected.getPerson(), actual.getPerson(), UUID);
		}
	}

	public static class ConceptAsserter extends AuditableAsserter<Concept> {
		@Override
		public void assertModel(@NonNull Concept expected, @NonNull Concept actual) {
			super.assertModel(expected, actual);

			Assert.assertEquals(expected.getDescription(), actual.getDescription());
			Assert.assertEquals(expected.getValue(), actual.getValue());

			assertSubModel(expected.getDatatype(), actual.getDatatype(), UUID);
			assertSubModel(expected.getConceptClass(), actual.getConceptClass(), UUID);
			assertSubModel(expected.getName(), actual.getName(), CONCEPT_NAME);

			assertSubList(expected.getAnswers(), actual.getAnswers());
			assertSubList(expected.getMappings(), actual.getMappings());
		}
	}

	public static class ConceptNameAsserter extends MetadataAsserter<ConceptName> {
		@Override
		public void assertModel(@NonNull ConceptName expected, @NonNull ConceptName actual) {
			super.assertModel(expected, actual);
		}
	}

	public static class LocationAsserter extends MetadataAsserter<Location> {
		@Override
		public void assertModel(@NonNull Location expected, @NonNull Location actual) {
			super.assertModel(expected, actual);

			Assert.assertEquals(expected.getAddress1(), actual.getAddress1());
			Assert.assertEquals(expected.getAddress2(), actual.getAddress2());
			Assert.assertEquals(expected.getCityVillage(), actual.getCityVillage());
			Assert.assertEquals(expected.getCountry(), actual.getCountry());
			Assert.assertEquals(expected.getPostalCode(), actual.getPostalCode());
			Assert.assertEquals(expected.getStateProvince(), actual.getStateProvince());

			if (expected.getParentLocation() == null) {
				Assert.assertNull(actual.getParentLocation());
			} else {
				assertModel(expected.getParentLocation(), actual.getParentLocation());
			}
		}
	}

	public static class EncounterAsserter extends AuditableAsserter<Encounter> {
		@Override
		public void assertModel(@NonNull Encounter expected, @NonNull Encounter actual) {
			super.assertModel(expected, actual);

			Assert.assertEquals(expected.getEncounterDatetime(), actual.getEncounterDatetime());
			Assert.assertEquals(expected.getVoided(), actual.getVoided());
			Assert.assertEquals(expected.getProvider(), actual.getProvider());
			Assert.assertEquals(expected.getResourceVersion(), actual.getResourceVersion());

			assertSubModel(expected.getPatient(), actual.getPatient(), UUID);
			assertSubModel(expected.getLocation(), actual.getLocation(), UUID);
			assertSubModel(expected.getForm(), actual.getForm(), UUID);
			assertSubModel(expected.getEncounterType(), actual.getEncounterType(), UUID);
			assertSubModel(expected.getVisit(), actual.getVisit(), UUID);

			assertSubList(expected.getObs(), actual.getObs(), OBSERVATION);
			assertSubList(expected.getEncounterProviders(), actual.getEncounterProviders());
		}
	}

	public static class ObservationAsserter extends EntityAsserter<Observation> {
		@Override
		public void assertModel(@NonNull Observation expected, @NonNull Observation actual) {
			super.assertModel(expected, actual);

			Assert.assertEquals(expected.getObsDatetime(), actual.getObsDatetime());
			Assert.assertEquals(expected.getAccessionNumber(), actual.getAccessionNumber());
			Assert.assertEquals(expected.getValueCodedName(), actual.getValueCodedName());
			Assert.assertEquals(expected.getComment(), actual.getComment());
			Assert.assertEquals(expected.getLocation(), actual.getLocation());
			Assert.assertEquals(expected.getFormFieldPath(), actual.getFormFieldPath());
			Assert.assertEquals(expected.getFormFieldNamespace(), actual.getFormFieldNamespace());
			Assert.assertEquals(expected.getResourceVersion(), actual.getResourceVersion());

			assertSubModel(expected.getPerson(), actual.getPerson(), UUID);
			assertSubModel(expected.getConcept(), actual.getConcept(), UUID);
			assertSubModel(expected.getEncounter(), actual.getEncounter(), UUID);
			assertSubModel(expected.getObsGroup(), actual.getObsGroup(), UUID);
			assertSubModel(expected.getProvider(), actual.getProvider(), UUID);
		}
	}

	public static class PatientAsserter extends AuditableAsserter<Patient> {
		@Override
		public void assertModel(@NonNull Patient expected, @NonNull Patient actual) {
			super.assertModel(expected, actual);

			Assert.assertEquals(expected.getVoided(), actual.getVoided());
			Assert.assertEquals(expected.getResourceVersion(), actual.getResourceVersion());

			assertSubModel(expected.getPerson(), actual.getPerson(), PERSON);

			assertSubList(expected.getIdentifiers(), actual.getIdentifiers(), PATIENT_IDENTIFIER);
		}
	}

	public static class PersonAsserter extends EntityAsserter<Person> {
		@Override
		public void assertModel(@NonNull Person expected, @NonNull Person actual) {
			super.assertModel(expected, actual);

			Assert.assertEquals(expected.getGender(), actual.getGender());
			Assert.assertEquals(expected.getBirthdate(), actual.getBirthdate());
			Assert.assertEquals(expected.getBirthdateEstimated(), actual.getBirthdateEstimated());
			Assert.assertEquals(expected.getDeathDate(), actual.getDeathDate());
			Assert.assertEquals(expected.getAge(), actual.getAge());

			assertSubList(expected.getNames(), actual.getNames(), PERSON_NAME);
			assertSubList(expected.getAddresses(), actual.getAddresses(), PERSON_ADDRESS);
			assertSubList(expected.getAttributes(), actual.getAttributes(), PERSON_ATTRIBUTE);
		}
	}

	public static class PersonNameAsserter extends EntityAsserter<PersonName> {
		@Override
		public void assertModel(@NonNull PersonName expected, @NonNull PersonName actual) {
			super.assertModel(expected, actual);

			Assert.assertEquals(expected.getGivenName(), actual.getGivenName());
			Assert.assertEquals(expected.getMiddleName(), actual.getMiddleName());
			Assert.assertEquals(expected.getFamilyName(), actual.getFamilyName());

			assertSubModel(expected.getPerson(), actual.getPerson(), UUID);
		}
	}

	public static class PersonAddressAsserter extends EntityAsserter<PersonAddress> {
		@Override
		public void assertModel(@NonNull PersonAddress expected, @NonNull PersonAddress actual) {
			super.assertModel(expected, actual);

			Assert.assertEquals(expected.getPreferred(), actual.getPreferred());
			Assert.assertEquals(expected.getAddress1(), actual.getAddress1());
			Assert.assertEquals(expected.getAddress2(), actual.getAddress2());
			Assert.assertEquals(expected.getCityVillage(), actual.getCityVillage());
			Assert.assertEquals(expected.getStateProvince(), actual.getStateProvince());
			Assert.assertEquals(expected.getCountry(), actual.getCountry());
			Assert.assertEquals(expected.getPostalCode(), actual.getPostalCode());

			assertSubModel(expected.getPerson(), actual.getPerson(), UUID);
		}
	}

	public static class PersonAttributeAsserter extends ObjectAsserter<PersonAttribute> {
		@Override
		public void assertModel(@NonNull PersonAttribute expected, @NonNull PersonAttribute actual) {
			super.assertModel(expected, actual);

			Assert.assertEquals(expected.getStringValue(), actual.getStringValue());

			assertSubModel(expected.getAttributeType(), actual.getAttributeType(), UUID);
			assertSubModel(expected.getConceptValue(), actual.getConceptValue(), UUID);
			assertSubModel(expected.getPerson(), actual.getPerson(), UUID);
		}
	}

	public static class PatientIdentifierAsserter extends EntityAsserter<PatientIdentifier> {
		@Override
		public void assertModel(@NonNull PatientIdentifier expected, @NonNull PatientIdentifier actual) {
			super.assertModel(expected, actual);

			Assert.assertEquals(expected.getIdentifier(), actual.getIdentifier());

			assertSubModel(expected.getLocation(), actual.getLocation(), UUID);
			assertSubModel(expected.getPatient(), actual.getPatient(), UUID);
			assertSubModel(expected.getIdentifierType(), actual.getIdentifierType(), UUID);
		}
	}

	public static class VisitAsserter extends EntityAsserter<Visit> {
		@Override
		public void assertModel(@NonNull Visit expected, @NonNull Visit actual) {
			super.assertModel(expected, actual);

			Assert.assertEquals(expected.getStartDatetime(), actual.getStartDatetime());
			Assert.assertEquals(expected.getStopDatetime(), actual.getStopDatetime());

			assertSubModel(expected.getPatient(), actual.getPatient(), UUID);
			assertSubModel(expected.getLocation(), actual.getLocation(), UUID);
			assertSubModel(expected.getVisitType(), actual.getVisitType(), UUID);

			assertSubList(expected.getEncounters(), actual.getEncounters(), ENCOUNTER);
			assertSubList(expected.getAttributes(), actual.getAttributes(), VISIT_ATTRIBUTE);
		}
	}

	public static class VistTypeAsserter extends MetadataAsserter<VisitType> {
		@Override
		public void assertModel(@NonNull VisitType expected, @NonNull VisitType actual) {
			super.assertModel(expected, actual);
		}
	}

	public static class VisitAttributeAsserter extends EntityAsserter<VisitAttribute> {
		@Override
		public void assertModel(@NonNull VisitAttribute expected, @NonNull VisitAttribute actual) {
			super.assertModel(expected, actual);

			Assert.assertEquals(expected.getValueReference(), actual.getValueReference());

			assertSubModel(expected.getVisit(), actual.getVisit(), UUID);
			assertSubModel(expected.getAttributeType(), actual.getAttributeType(), UUID);
		}
	}
}
