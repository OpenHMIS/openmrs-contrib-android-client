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
	public static final String OBS_SEARCH_PATH = REST_PATH + "?s=obsByConceptListVisit";

	public class Representations {
		public static final String FULL = "full";
		public static final String DEFAULT = "default";
		public static final String REF = "ref";

		public static final String RECORD_INFO = "custom:(uuid,dateCreated,dateChanged)";

		public static final String DIAGNOSIS_CONCEPT = "custom:(uuid,display,name:(uuid,display,name),conceptClass:"
				+ "(uuid,display),retired,mappings:(uuid,display,conceptReferenceTerm:(uuid,display,code)),dateCreated,"
				+ "dateChanged,value)";
		public static final String PATIENT_LIST_PATIENTS_CONTEXT =
				"custom:(uuid,patient:(uuid,dateChanged),patientList:(uuid,display),headerContent,bodyContent)";
		public static final String PATIENT_LIST_PATIENTS = "custom:(uuid,patient,visit,patientList:(uuid,display),"
				+ "headerContent,bodyContent)";
		private static final String OBSERVATION_FIELDS = "(uuid,display,comment,value,groupMembers,concept:(uuid,display),"
				+ "encounter,dateCreated,creator:(uuid,display,person:(uuid,display)))";
		public static final String VISIT_ENCOUNTER = "custom:(uuid,display,encounterDatetime,patient:ref,location:ref,"
				+ "form:ref,encounterType:ref,obs:" + OBSERVATION_FIELDS + ",creator:(uuid,display,person:(uuid,display)),"
				+ "dateCreated,changedBy:ref,dateChanged,voided,visit)";
		public static final String OBSERVATION = "custom:" + OBSERVATION_FIELDS;
		public static final String VISIT_TASKS = "custom:(uuid,status,name,closedOn)";
		public static final String VISIT = "custom:(uuid,display,patient,visitType,startDatetime,stopDatetime,encounters:"
				+ "(uuid,display,encounterDatetime,patient,location,encounterType,voided,obs:" + OBSERVATION_FIELDS + "),"
				+ "voided)";
	}
}
