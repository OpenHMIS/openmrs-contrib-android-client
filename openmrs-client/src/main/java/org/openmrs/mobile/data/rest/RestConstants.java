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
		public static final String RECORD_INFO = "custom:(uuid,dateChanged}";
	}
}
