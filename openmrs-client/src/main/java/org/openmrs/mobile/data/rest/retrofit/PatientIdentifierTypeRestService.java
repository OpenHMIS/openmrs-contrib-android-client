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
import org.openmrs.mobile.models.PatientIdentifierType;
import org.openmrs.mobile.models.RecordInfo;
import org.openmrs.mobile.models.Results;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PatientIdentifierTypeRestService {
	@POST(RestConstants.CREATE)
	Call<PatientIdentifierType> create(@Path(value = "restPath", encoded = true) String restPath,
			@Body PatientIdentifierType entity);

	@POST(RestConstants.UPDATE)
	Call<PatientIdentifierType> update(@Path(value = "restPath", encoded = true) String restPath,
			@Path("uuid") String uuid, @Body PatientIdentifierType entity);

	@DELETE(RestConstants.PURGE)
	Call<PatientIdentifierType> purge(@Path(value = "restPath", encoded = true) String restPath,
			@Path("uuid") String uuid);

	@GET(RestConstants.GET_ALL)
	Call<Results<PatientIdentifierType>> getAll(@Path(value = "restPath", encoded = true) String restPath,
			@Query("v") String representation,
			@Query("includeAll") Boolean includeAll,
			@Query("limit") Integer limit,
			@Query("startIndex") Integer startIndex);

	@GET(RestConstants.REST_PATH)
	Call<Results<PatientIdentifierType>> getByNameFragment(@Path(value = "restPath", encoded = true) String restPath,
			@Query("q") String name,
			@Query("v") String representation,
			@Query("includeAll") Boolean includeAll,
			@Query("limit") Integer limit,
			@Query("startIndex") Integer startIndex);

	@GET(RestConstants.GET_BY_UUID)
	Call<PatientIdentifierType> getByUuid(@Path(value = "restPath", encoded = true) String restPath,
			@Path("uuid") String uuid,
			@Query("v") String representation,
			@Query("includeAll") Boolean includeAll);

	@GET(RestConstants.GET_ALL)
	Call<Results<RecordInfo>> getRecordInfo(@Path(value = "restPath", encoded = true) String restPath,
			@Query("v") String representation,
			@Query("includeAll") Boolean includeAll);
}

