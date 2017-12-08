package org.openmrs.mobile.data;

import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.openmrs.mobile.models.ConceptClass;
import org.openmrs.mobile.models.ConceptClass_Table;
import org.openmrs.mobile.models.Datatype;
import org.openmrs.mobile.models.Datatype_Table;
import org.openmrs.mobile.models.Encounter;
import org.openmrs.mobile.models.EncounterType;
import org.openmrs.mobile.models.EncounterType_Table;
import org.openmrs.mobile.models.Location;
import org.openmrs.mobile.models.Location_Table;
import org.openmrs.mobile.models.PatientIdentifierType;
import org.openmrs.mobile.models.PatientIdentifierType_Table;
import org.openmrs.mobile.models.PersonAttributeType;
import org.openmrs.mobile.models.PersonAttributeType_Table;
import org.openmrs.mobile.models.VisitAttributeType;
import org.openmrs.mobile.models.VisitAttributeType_Table;
import org.openmrs.mobile.models.VisitTask_Table;
import org.openmrs.mobile.models.VisitType;
import org.openmrs.mobile.models.VisitType_Table;

import java.util.ArrayList;
import java.util.List;

public class CoreTestData {
	private static List<ConceptClass> conceptClasses = new ArrayList<>(3);
	private static List<Datatype> datatypes = new ArrayList<>(3);
	private static List<EncounterType> encounterTypes = new ArrayList<>(3);
	private static List<Location> locations = new ArrayList<>(2);
	private static List<PersonAttributeType> personAttributeTypes = new ArrayList<>(3);
	private static List<PatientIdentifierType> patientIdentifierTypes = new ArrayList<>(2);
	private static List<VisitType> visitTypes = new ArrayList<>(1);
	private static List<VisitAttributeType> visitAttributeTypes = new ArrayList<>(1);

	public static ConceptClass_Table conceptClassTable =
			(ConceptClass_Table)FlowManager.getInstanceAdapter(ConceptClass.class);
	public static Datatype_Table datatypeTable = (Datatype_Table)FlowManager.getInstanceAdapter(Datatype.class);
	public static EncounterType_Table encounterTypeTable =
			(EncounterType_Table)FlowManager.getInstanceAdapter(EncounterType.class);
	public static Location_Table locationTable = (Location_Table)FlowManager.getInstanceAdapter(Location.class);
	public static PersonAttributeType_Table personAttributeTypeTable =
			(PersonAttributeType_Table)FlowManager.getInstanceAdapter(PersonAttributeType.class);
	public static PatientIdentifierType_Table patientIdentifierTypeTable =
			(PatientIdentifierType_Table)FlowManager.getInstanceAdapter(PatientIdentifierType.class);
	public static VisitType_Table visitTaskTable =
			(VisitType_Table)FlowManager.getInstanceAdapter(VisitType.class);
	public static VisitAttributeType_Table visitAttributeTypeTable =
			(VisitAttributeType_Table)FlowManager.getInstanceAdapter(VisitAttributeType.class);

	public static void load() {
		if (conceptClasses.size() == 0) {
			loadConceptClasses();
		}
		conceptClassTable.saveAll(conceptClasses);

		if (datatypes.size() == 0) {
			loadDatatypes();
		}
		datatypeTable.saveAll(datatypes);

		if (encounterTypes.size() == 0) {
			loadEncounters();
		}
		encounterTypeTable.saveAll(encounterTypes);

		if (locations.size() == 0) {
			loadLocations();
		}
		locationTable.saveAll(locations);

		if (personAttributeTypes.size() == 0) {
			loadPersonAttributeTypes();
		}
		personAttributeTypeTable.saveAll(personAttributeTypes);

		if (patientIdentifierTypes.size() == 0) {
			loadPatientIdentifierTypes();
		}
		patientIdentifierTypeTable.saveAll(patientIdentifierTypes);

		if (visitTypes.size() == 0) {
			loadVisitTypes();
		}
		visitTaskTable.saveAll(visitTypes);

		if (visitAttributeTypes.size() == 0) {
			loadVisitAttributeTypes();
		}
		visitAttributeTypeTable.saveAll(visitAttributeTypes);
	}

	public static void clear() {
		SQLite.delete(Datatype.class).execute();
		SQLite.delete(EncounterType.class).execute();
		SQLite.delete(Location.class).execute();
		SQLite.delete(PersonAttributeType.class).execute();
		SQLite.delete(PatientIdentifierType.class).execute();
		SQLite.delete(VisitType.class).execute();
		SQLite.delete(VisitAttributeType.class).execute();
	}

	private static void loadConceptClasses() {
		ConceptClass conceptClass = new ConceptClass();
		conceptClass.setUuid(Constants.ConceptClass.CONV_SET_UUID);
		conceptClass.setName("ConvSet");
		conceptClass.setDisplay("ConvSet");

		ConceptClass conceptClass2 = new ConceptClass();
		conceptClass2.setUuid(Constants.ConceptClass.DIAGNOSIS_UUID);
		conceptClass2.setName("Diagnosis");
		conceptClass2.setDisplay("Diagnosis");

		ConceptClass conceptClass3 = new ConceptClass();
		conceptClass3.setUuid(Constants.ConceptClass.TEST_UUID);
		conceptClass3.setName("Test");
		conceptClass3.setDisplay("Test");

		conceptClasses.add(conceptClass);
		conceptClasses.add(conceptClass2);
		conceptClasses.add(conceptClass3);
	}

	private static void loadDatatypes() {
		Datatype datatype = new Datatype();
		datatype.setUuid(Constants.Datatype.NUMERIC_UUID);
		datatype.setDisplay("Numeric");

		Datatype datatype2 = new Datatype();
		datatype2.setUuid(Constants.Datatype.CODED_UUID);
		datatype2.setDisplay("Coded");

		Datatype datatype3 = new Datatype();
		datatype3.setUuid(Constants.Datatype.TEXT_UUID);
		datatype3.setDisplay("Text");

		datatypes.add(datatype);
		datatypes.add(datatype2);
		datatypes.add(datatype3);
	}

	private static void loadEncounters() {
		EncounterType type = new EncounterType();
		type.setUuid(Constants.EncounterType.ADMISSION_UUID);
		type.setDisplay("Admission");
		type.setName("Admission");

		EncounterType type2 = new EncounterType();
		type2.setUuid(Constants.EncounterType.VISIT_NOTE_UUID);
		type2.setDisplay("Visit Note");
		type2.setName("Visit Note");

		EncounterType type3 = new EncounterType();
		type3.setUuid(Constants.EncounterType.VITALS_UUID);
		type3.setDisplay("Vitals");
		type3.setName("Vitals");

		encounterTypes.add(type);
		encounterTypes.add(type2);
		encounterTypes.add(type3);
	}

	private static void loadLocations() {
		Location location = new Location();
		location.setUuid(Constants.Location.INPATIENT_WARD_UUID);
		location.setName("Inpatient Ward");
		location.setDisplay("Inpatient Ward");

		Location location2 = new Location();
		location2.setUuid(Constants.Location.REGISTRATION_DESK_UUID);
		location2.setName("Registration Desk");
		location2.setDisplay("Registration Desk");

		locations.add(location);
		locations.add(location2);
	}

	private static void loadPersonAttributeTypes() {
		PersonAttributeType type1 = new PersonAttributeType();
		type1.setUuid(Constants.PersonAttributeType.PHONE_NUMBER_UUID);
		type1.setDisplay("Phone Number");
		type1.setName("Phone Number");
		type1.setDescription("The telephone number for the person");
		type1.setFormat("java.lang.String");
		type1.setSortWeight(5d);
		type1.setSearchable(false);

		PersonAttributeType type2 = new PersonAttributeType();
		type2.setUuid(Constants.PersonAttributeType.WARD_UUID);
		type2.setDisplay("Ward");
		type2.setName("Ward");
		type2.setFormat("java.lang.String");
		type2.setSortWeight(4d);
		type2.setSearchable(false);

		PersonAttributeType type3 = new PersonAttributeType();
		type3.setUuid(Constants.PersonAttributeType.CIVIL_STATUS_UUID);
		type3.setDisplay("Civil Status");
		type3.setName("Civil Status");
		type3.setDescription("Marriage status of this person");
		type3.setFormat("org.openmrs.Concept");
		type3.setSortWeight(0d);
		type3.setSearchable(false);

		personAttributeTypes.add(type1);
		personAttributeTypes.add(type2);
		personAttributeTypes.add(type3);
	}

	private static void loadPatientIdentifierTypes() {
		PatientIdentifierType type1 = new PatientIdentifierType();

		type1.setUuid(Constants.PatientIdentifierType.FILE_NUMBER_UUID);
		type1.setDisplay("File Number");
		type1.setName("File Number");
		type1.setDescription("Patient file number");
		type1.setRequired(true);
		type1.setCheckDigit(false);

		PatientIdentifierType type2 = new PatientIdentifierType();

		type2.setUuid(Constants.PatientIdentifierType.OPENMRS_ID_UUID);
		type2.setDisplay("OpenMRS ID");
		type2.setName("OpenMRS ID");
		type2.setDescription("OpenMRS patient identifier, with check-digit");
		type2.setRequired(false);
		type2.setCheckDigit(false);

		patientIdentifierTypes.add(type1);
		patientIdentifierTypes.add(type2);
	}

	private static void loadVisitTypes() {
		VisitType type = new VisitType();

		type.setUuid(Constants.VisitType.INPATIENT_MEDICINE_UUID);
		type.setDisplay("Inpatient Medicine");
		type.setName("Inpatient Medicine");
		type.setDescription("Internal medicine service");

		visitTypes.add(type);
	}

	private static void loadVisitAttributeTypes() {
		VisitAttributeType type = new VisitAttributeType();
		type.setUuid(Constants.VisitAttributeType.BED_NUMBER_UUID);
		type.setDisplay("Bed Number");
		type.setName("Bed Number");
		type.setDescription("IMed Patients Bed Numbers");

		visitAttributeTypes.add(type);
	}

	public static class Constants {
		public static class ConceptClass {
			public static String TEST_UUID = "8d4907b2-c2cc-11de-8d13-0010c6dffd0f";
			public static String DIAGNOSIS_UUID = "8d4918b0-c2cc-11de-8d13-0010c6dffd0f";
			public static String CONV_SET_UUID = "8d492594-c2cc-11de-8d13-0010c6dffd0f";
		}

		public static class Datatype {
			public static String NUMERIC_UUID = "8d4a4488-c2cc-11de-8d13-0010c6dffd0f";
			public static String CODED_UUID = "8d4a48b6-c2cc-11de-8d13-0010c6dffd0f";
			public static String TEXT_UUID = "8d4a4ab4-c2cc-11de-8d13-0010c6dffd0f";
		}

		public static class EncounterType {
			public static String ADMISSION_UUID = "e22e39fd-7db2-45e7-80f1-60fa0d5a4378";
			public static String VISIT_NOTE_UUID = "d7151f82-c1f3-4152-a605-2f9ea7414a79";
			public static String VITALS_UUID = "67a71486-1a54-468f-ac3e-7091a9a79584";
		}

		public static class Location {
			public static String INPATIENT_WARD_UUID = "b1a8b05e-3542-4037-bbd3-998ee9c40574";
			public static String REGISTRATION_DESK_UUID = "6351fcf4-e311-4a19-90f9-35667d99a8af";
		}

		public static class PersonAttributeType {
			public static String PHONE_NUMBER_UUID = "14d4f066-15f5-102d-96e4-000c29c2a5d7";
			public static String WARD_UUID = "18e3b47a-666d-4173-85a2-8044f8841f0c";
			public static String CIVIL_STATUS_UUID = "8d871f2a-c2cc-11de-8d13-0010c6dffd0f";
		}

		public static class PatientIdentifierType {
			public static String FILE_NUMBER_UUID = "cf54df63-db87-4dea-a14d-dfc44e6d65e5";
			public static String OPENMRS_ID_UUID = "05a29f94-c0ed-11e2-94be-8c13b969e334";
		}

		public static class VisitType {
			public static String INPATIENT_MEDICINE_UUID = "a933acf6-9313-4cf3-bb24-24b5c750fda6";
		}

		public static class VisitAttributeType {
			public static String BED_NUMBER_UUID = "36ab5534-276b-4853-b86a-b9fd09a26085";
		}
	}
}
