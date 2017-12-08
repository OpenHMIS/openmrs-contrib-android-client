package org.openmrs.mobile.data.rest.retrofit;

import org.openmrs.mobile.data.rest.RestConstants;
import org.openmrs.mobile.models.Encounter;
import org.openmrs.mobile.models.RecordInfo;
import org.openmrs.mobile.models.Results;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface EncounterRestService {
	@GET(RestConstants.GET_ALL)
	Call<Results<Encounter>> getAll(@Path(value = "restPath", encoded = true) String restPath,
			@Query("v") String representation,
			@Query("includeAll") Boolean includeAll,
			@Query("limit") Integer limit,
			@Query("startIndex") Integer startIndex);

	@GET(RestConstants.GET_BY_UUID)
	Call<Encounter> getByUuid(@Path(value = "restPath", encoded = true) String restPath,
			@Path("uuid") String uuid,
			@Query("v") String representation,
			@Query("includeAll") Boolean includeAll);

	@POST(RestConstants.CREATE)
	Call<Encounter> create(@Path(value = "restPath", encoded = true) String restPath,
			@Body Encounter entity);

	@POST(RestConstants.UPDATE)
	Call<Encounter> update(@Path(value = "restPath", encoded = true) String restPath,
			@Path("uuid") String uuid, @Body Encounter entity);

	@GET(RestConstants.GET_ALL)
	Call<Results<Encounter>> getByVisit(@Path(value = "restPath", encoded = true) String restPath,
			@Query("visit") String visitUuid,
			@Query("v") String representation,
			@Query("includeAll") Boolean includeAll,
			@Query("limit") Integer limit,
			@Query("startIndex") Integer startIndex);

	@GET(RestConstants.REST_PATH)
	Call<Results<RecordInfo>> getRecordInfoByVisit(@Path(value = "restPath", encoded = true) String restPath,
			@Query("visit") String visitUuid,
			@Query("v") String representation,
			@Query("includeAll") Boolean includeAll,
			@Query("limit") int limit,
			@Query("startIndex") int startIndex);
}
