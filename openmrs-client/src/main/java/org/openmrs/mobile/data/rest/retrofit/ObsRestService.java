package org.openmrs.mobile.data.rest.retrofit;

import org.openmrs.mobile.data.rest.RestConstants;
import org.openmrs.mobile.models.Observation;
import org.openmrs.mobile.models.RecordInfo;
import org.openmrs.mobile.models.Results;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ObsRestService {
	@GET(RestConstants.GET_ALL)
	Call<Results<Observation>> getAll(@Path(value = "restPath", encoded = true) String restPath,
			@Query("v") String representation,
			@Query("includeAll") Boolean includeAll,
			@Query("limit") Integer limit,
			@Query("startIndex") Integer startIndex);

	@GET(RestConstants.GET_BY_UUID)
	Call<Observation> getByUuid(@Path(value = "restPath", encoded = true) String restPath,
			@Path("uuid") String uuid,
			@Query("v") String representation,
			@Query("includeAll") Boolean includeAll);

	@GET(RestConstants.OBS_SEARCH_PATH)
	Call<Results<Observation>> getVisitPhotoObservations(
			@Path(value = "restPath", encoded = true) String restPath,
			@Query("visit") String visitUuid,
			@Query("conceptList") String conceptList,
			@Query("v") String representation);

	@POST(RestConstants.UPDATE)
	Call<Observation> update(@Path(value = "restPath", encoded = true) String restPath,
			@Path("uuid") String uuid, @Body Observation entity);

	@POST(RestConstants.CREATE)
	Call<Observation> create(@Path(value = "restPath", encoded = true) String restPath,
			@Body Observation entity);

	@DELETE(RestConstants.GET_BY_UUID)
	Call<Observation> purge(@Path(value = "restPath", encoded = true) String restPath,
			@Path("uuid") String uuid);

	@GET(RestConstants.OBS_SEARCH_PATH)
	Call<Results<RecordInfo>> getRecordInfoObservationsByVisit(
			@Path(value = "restPath", encoded = true) String restPath,
			@Query("visit") String visitUuid,
			@Query("conceptList") String conceptList,
			@Query("v") String representation);

}
