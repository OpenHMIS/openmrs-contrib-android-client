package org.openmrs.mobile.data.rest;

public class RestConstants {
	public static final String REST_PATH = "{restPath}/";

	public static final String GET_BY_UUID = REST_PATH + "{uuid}";
	public static final String GET_ALL = REST_PATH;
	public static final String CREATE = REST_PATH;
	public static final String UPDATE = GET_BY_UUID;
	public static final String PURGE = GET_BY_UUID;
	public static final String LOCATION_PATH = REST_PATH + "?tag=IM Location";
	public static final String CONCEPT_SEARCH_PATH = REST_PATH + "?s=diagnosisByTerm";


	public class Representations {
		public static final String FULL = "full";
		public static final String DEFAULT = "default";
		public static final String REF = "ref";

		public static final String RECORD_INFO = "custom:(uuid,dateCreated,dateChanged)";

		public static final String DIAGNOSIS_CONCEPT = "custom:(uuid,display,name:(uuid,display,name),conceptClass:"
				+ "(uuid,display),retired,mappings:(uuid,display,conceptReferenceTerm:(uuid,display,code)),dateCreated,"
				+ "dateChanged)";
		public static final String PATIENT_LIST_PATIENTS = "custom:(uuid,patient:(uuid,dateChanged),patientList:(uuid,"
				+ "display))";
		public static final String VISIT_ENCOUNTER = "custom:(uuid,display,encounterDatetime,patient:ref,location:ref,"
				+ "form:ref,encounterType:ref,obs,creator:ref,dateCreated,changedBy:ref,dateChanged)";
	}
}
