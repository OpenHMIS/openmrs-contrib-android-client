package org.openmrs.mobile.data;

import org.openmrs.mobile.models.Concept;
import org.openmrs.mobile.models.ConceptClass;
import org.openmrs.mobile.models.ConceptName;
import org.openmrs.mobile.models.Datatype;
import org.openmrs.mobile.models.Encounter;
import org.openmrs.mobile.models.EncounterType;
import org.openmrs.mobile.models.Location;
import org.openmrs.mobile.models.Observation;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.PatientIdentifier;
import org.openmrs.mobile.models.PatientIdentifierType;
import org.openmrs.mobile.models.Person;
import org.openmrs.mobile.models.PersonAddress;
import org.openmrs.mobile.models.PersonAttribute;
import org.openmrs.mobile.models.PersonAttributeType;
import org.openmrs.mobile.models.PersonName;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.models.VisitAttribute;
import org.openmrs.mobile.models.VisitAttributeType;
import org.openmrs.mobile.models.VisitType;
import org.openmrs.mobile.utilities.DateUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ModelGenerators {
	public static final ConceptGenerator CONCEPT = new ConceptGenerator();
	public static final EncounterGenerator ENCOUNTER = new EncounterGenerator();
	public static final LocationGenerator LOCATION = new LocationGenerator();
	public static final ObservationGenerator OBSERVATION = new ObservationGenerator();
	public static final PatientGenerator PATIENT = new PatientGenerator();
	public static final PersonGenerator PERSON = new PersonGenerator();
	public static final VisitGenerator VISIT = new VisitGenerator();

	private static final SimpleDateFormat format = new SimpleDateFormat(DateUtils.OPEN_MRS_REQUEST_PATIENT_FORMAT);

	public interface ModelGenerator<E> {
		E generate(boolean createRelations);
	}

	public static class ConceptGenerator implements ModelGenerator<Concept> {
		private Datatype datatype;
		private ConceptClass conceptClass;
		private ConceptName conceptName;

		public Datatype getDatatype() {
			return datatype;
		}

		public void setDatatype(Datatype datatype) {
			this.datatype = datatype;
		}

		public ConceptClass getConceptClass() {
			return conceptClass;
		}

		public void setConceptClass(ConceptClass conceptClass) {
			this.conceptClass = conceptClass;
		}

		public ConceptName getConceptName() {
			return conceptName;
		}

		public void setConceptName(ConceptName conceptName) {
			this.conceptName = conceptName;
		}

		@Override
		public Concept generate(boolean createRelations) {
			Concept concept = new Concept();

			concept.setDescription("Description");
			concept.setValue("Value");

			if (datatype == null) {
				datatype = new Datatype();
				datatype.setUuid(CoreTestData.Constants.Datatype.TEXT_UUID);
			}
			concept.setDatatype(datatype);
			if (conceptClass == null) {
				conceptClass = new ConceptClass();
				conceptClass.setUuid(CoreTestData.Constants.ConceptClass.TEST_UUID);
			}
			concept.setConceptClass(conceptClass);
			if (conceptName == null) {
				conceptName = new ConceptName();
				conceptName.setName("Name");
			}
			concept.setName(conceptName);

			datatype = null;
			conceptClass = null;
			conceptName = null;

			return concept;
		}
	}

	public static class EncounterGenerator implements ModelGenerator<Encounter> {
		private EncounterType encounterType;
		private Location location;
		private Patient patient;
		private Visit visit;

		public EncounterType getEncounterType() {
			return encounterType;
		}

		public void setEncounterType(EncounterType encounterType) {
			this.encounterType = encounterType;
		}

		public Location getLocation() {
			return location;
		}

		public void setLocation(Location location) {
			this.location = location;
		}

		public Patient getPatient() {
			return patient;
		}

		public void setPatient(Patient patient) {
			this.patient = patient;
		}

		public Visit getVisit() {
			return visit;
		}

		public void setVisit(Visit visit) {
			this.visit = visit;
		}

		@Override
		public Encounter generate(boolean createRelations) {
			Encounter encounter = new Encounter();

			encounter.setEncounterDatetime(new Date(2010, 3, 4));

			if (encounterType == null) {
				encounterType = new EncounterType();
				encounterType.setUuid(CoreTestData.Constants.EncounterType.ADMISSION_UUID);
			}
			encounter.setEncounterType(encounterType);
			if (location == null) {
				location = LOCATION.generate(false);
			}
			encounter.setLocation(location);
			if (patient == null) {
				patient = PATIENT.generate(false);
			}
			encounter.setPatient(patient);
			if (visit == null) {
				visit = VISIT.generate(false);
			}
			encounter.setVisit(visit);

			encounterType = null;
			location = null;
			patient = null;
			visit = null;

			return encounter;
		}
	}

	public static class LocationGenerator implements ModelGenerator<Location> {
		@Override
		public Location generate(boolean createRelations) {
			Location entity = new Location();

			entity.setName("Location 1");
			entity.setAddress1("Address 1");
			entity.setAddress2("Address 2");
			entity.setCityVillage("City Village");
			entity.setCountry("Country");
			entity.setPostalCode("12345");
			entity.setStateProvince("State Province");

			return entity;
		}
	}

	public static class ObservationGenerator implements ModelGenerator<Observation> {
		private Concept concept;
		private Person person;
		private Encounter encounter;
		private Observation obsGroup;

		public Concept getConcept() {
			return concept;
		}

		public void setConcept(Concept concept) {
			this.concept = concept;
		}

		public Person getPerson() {
			return person;
		}

		public void setPerson(Person person) {
			this.person = person;
		}

		public Encounter getEncounter() {
			return encounter;
		}

		public void setEncounter(Encounter encounter) {
			this.encounter = encounter;
		}

		public Observation getObsGroup() {
			return obsGroup;
		}

		public void setObsGroup(Observation obsGroup) {
			this.obsGroup = obsGroup;
		}

		@Override
		public Observation generate(boolean createRelations) {
			Observation observation = new Observation();

			Date date = new Date(2010, 4, 5);
			observation.setObsDatetime(format.format(date));
			observation.setAccessionNumber("1");
			observation.setValueCodedName("Value Coded Name");
			observation.setComment("Comment");
			observation.setLocation("Location");
			observation.setFormFieldPath("Form Field Path");
			observation.setFormFieldNamespace("Form Field Namespace");

			if (concept == null) {
				concept = CONCEPT.generate(false);
			}
			observation.setConcept(concept);
			if (person == null) {
				person = PERSON.generate(false);
			}
			observation.setPerson(person);
			if (encounter == null) {
				encounter = ENCOUNTER.generate(false);
			}
			observation.setEncounter(encounter);
			if (obsGroup != null) {
				observation.setObsGroup(obsGroup);
			}

			concept = null;
			person = null;
			encounter = null;
			obsGroup = null;

			return observation;
		}
	}

	public static class PatientGenerator implements ModelGenerator<Patient> {
		@Override
		public Patient generate(boolean createRelations) {
			Patient entity = new Patient();

			entity.setVoided(false);
			entity.setResourceVersion("1");

			if (createRelations) {
				entity.setPerson(PERSON.generate(true));

				PatientIdentifierType idType = new PatientIdentifierType();
				idType.setUuid(CoreTestData.Constants.PatientIdentifierType.FILE_NUMBER_UUID);

				PatientIdentifier identifier = new PatientIdentifier();
				identifier.setIdentifier("12345");
				identifier.setIdentifierType(idType);
				identifier.setPatient(entity);

				entity.getIdentifiers().add(identifier);
			}

			return entity;
		}
	}

	public static class PersonGenerator implements ModelGenerator<Person> {
		@Override
		public Person generate(boolean createRelations) {
			Person person = new Person();
			person.setGender("MALE");
			person.setAge(9);

			Date birthdate = new Date(2001, 2, 3);
			person.setBirthdate(format.format(birthdate));

			Date deathdate = new Date(2010, 4, 5);
			person.setDeathDate(format.format(deathdate));

			person.getNames().add(generatePersonName(person, 1));
			person.getNames().add(generatePersonName(person, 2));
			person.getNames().add(generatePersonName(person, 3));

			person.getAddresses().add(generatePersonAddress(person, 1));
			person.getAddresses().add(generatePersonAddress(person, 2));
			person.getAddresses().add(generatePersonAddress(person, 3));

			PersonAttributeType type = new PersonAttributeType();
			type.setUuid(CoreTestData.Constants.PersonAttributeType.PHONE_NUMBER_UUID);
			person.getAttributes().add(generatePersonAttribute(person, type, 1));

			type = new PersonAttributeType();
			type.setUuid(CoreTestData.Constants.PersonAttributeType.WARD_UUID);
			person.getAttributes().add(generatePersonAttribute(person, type, 2));

			return person;
		}

		public PersonName generatePersonName(Person person, int index) {
			PersonName name = new PersonName();

			name.setGivenName("Given Name " + index);
			name.setMiddleName("Middle Name " + index);
			name.setFamilyName("Family Name " + index);

			name.setPerson(person);

			return name;
		}

		public PersonAddress generatePersonAddress(Person person, int index) {
			PersonAddress address = new PersonAddress();

			address.setPreferred(index == 1);
			address.setAddress1("Address1 " + index);
			address.setAddress2("Address2 " + index);
			address.setCityVillage("City Village " + index);
			address.setStateProvince("State Province " + index);
			address.setCountry("Country " + index);
			address.setPostalCode("Postal Code " + index);

			address.setPerson(person);

			return address;
		}

		public PersonAttribute generatePersonAttribute(Person person, PersonAttributeType type, int index) {
			PersonAttribute attribute = new PersonAttribute();

			attribute.setStringValue("Something " + index);

			attribute.setAttributeType(type);
			attribute.setPerson(person);

			return attribute;
		}
	}

	public static class VisitGenerator implements ModelGenerator<Visit> {
		private Patient patient;
		private Location location;

		public Patient getPatient() {
			return patient;
		}

		public void setPatient(Patient patient) {
			this.patient = patient;
		}

		public Location getLocation() {
			return location;
		}

		public void setLocation(Location location) {
			this.location = location;
		}

		@Override
		public Visit generate(boolean createRelations) {
			Visit visit = new Visit();

			VisitType type = new VisitType();
			type.setUuid(CoreTestData.Constants.VisitType.INPATIENT_MEDICINE_UUID);

			visit.setVisitType(type);
			visit.setStartDatetime(new Date(2017, 2, 3, 10, 30, 0));
			visit.setStopDatetime(new Date(2017, 2, 3, 13, 00, 0));

			if (patient == null) {
				patient = PATIENT.generate(false);
			}
			visit.setPatient(patient);
			if (location == null) {
				location = LOCATION.generate(false);
			}
			visit.setLocation(location);

			if (createRelations) {
				EncounterType encounterType = new EncounterType();
				encounterType.setUuid(CoreTestData.Constants.EncounterType.ADMISSION_UUID);
				ENCOUNTER.setEncounterType(encounterType);
				ENCOUNTER.setVisit(visit);
				Encounter encounter = ENCOUNTER.generate(true);
				visit.getEncounters().add(encounter);

				VisitAttributeType attributeType = new VisitAttributeType();
				attributeType.setUuid(CoreTestData.Constants.VisitAttributeType.BED_NUMBER_UUID);
				visit.getAttributes().add(generateVisitAttribute(visit, attributeType, 0));
			}

			patient = null;
			location = null;

			return visit;
		}

		public VisitAttribute generateVisitAttribute(Visit visit, VisitAttributeType type, int index) {
			VisitAttribute attribute = new VisitAttribute();

			attribute.setAttributeType(type);
			attribute.setValueReference(String.valueOf(index));

			attribute.setVisit(visit);

			return attribute;
		}
	}
}
