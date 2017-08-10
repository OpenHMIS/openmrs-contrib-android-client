/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.mobile.data.rest.retrofit;

import org.openmrs.mobile.data.rest.RestConstants;
import org.openmrs.mobile.models.Concept;
import org.openmrs.mobile.models.RecordInfo;
import org.openmrs.mobile.models.Results;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ConceptRestService {
	@GET(RestConstants.GET_BY_UUID)
	Call<Concept> getByUuid(@Path(value = "restPath", encoded = true) String restPath,
			@Path("uuid") String uuid,
			@Query("v") String representation,
			@Query("includeAll") Boolean includeAll);

	@GET(RestConstants.REST_PATH)
	Call<Results<Concept>> getByConceptName(@Path(value = "restPath", encoded = true) String restPath,
			@Query("name") String name,
			@Query("v") String representation);

	@GET(RestConstants.CONCEPT_SEARCH_PATH)
	Call<Results<Concept>> findConcept(@Path(value = "restPath", encoded = true) String restPath,
			@Query("term") String name,
			@Query("v") String representation,
			@Query("limit") Integer limit,
			@Query("startIndex") Integer startIndex);

	@GET(RestConstants.REST_PATH)
	Call<Results<Concept>> getSetConcepts(@Path(value = "restPath", encoded = true) String restPath,
			@Query("set") String setUuid,
			@Query("includeSetConcepts") Boolean includeSetConcepts,
			@Query("v") String representation);

	@GET(RestConstants.REST_PATH)
	Call<Results<RecordInfo>> getSetConceptRecordInfo(@Path(value = "restPath", encoded = true) String restPath,
			@Query("set") String setUuid,
			@Query("includeSetConcepts") Boolean includeSetConcepts,
			@Query("v") String representation);
}
