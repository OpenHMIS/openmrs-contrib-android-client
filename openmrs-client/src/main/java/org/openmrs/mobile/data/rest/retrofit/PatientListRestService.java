package org.openmrs.mobile.data.rest.retrofit;

import org.openmrs.mobile.data.rest.RestConstants;
import org.openmrs.mobile.models.PatientList;
import org.openmrs.mobile.models.RecordInfo;
import org.openmrs.mobile.models.Results;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PatientListRestService {
	@GET(RestConstants.GET_BY_UUID)
	Call<PatientList> getByUuid(@Path(value = "restPath", encoded = true) String restPath,
			@Path("uuid") String uuid,
			@Query("v") String representation,
			@Query("includeAll") Boolean includeAll);

	@GET(RestConstants.GET_ALL)
	Call<Results<PatientList>> getAll(@Path(value = "restPath", encoded = true) String restPath,
			@Query("v") String representation,
			@Query("includeAll") Boolean includeAll,
			@Query("limit") Integer limit,
			@Query("startIndex") Integer startIndex);

	@POST(RestConstants.CREATE)
	Call<PatientList> create(@Path(value = "restPath", encoded = true) String restPath, PatientList entity);

	@POST(RestConstants.UPDATE)
	Call<PatientList> update(@Path(value = "restPath", encoded = true) String restPath,
			@Path("uuid") String uuid, PatientList entity);

	@DELETE(RestConstants.PURGE)
	Call<PatientList> purge(@Path(value = "restPath", encoded = true) String restPath,
			@Path("uuid") String uuid);

	@GET(RestConstants.REST_PATH)
	Call<Results<PatientList>> getByNameFragment(@Path(value = "restPath", encoded = true) String restPath,
			@Query("q") String name,
			@Query("v") String representation,
			@Query("includeAll") Boolean includeAll,
			@Query("limit") Integer limit,
			@Query("startIndex") Integer startIndex);

	@GET(RestConstants.GET_ALL)
	Call<Results<RecordInfo>> getRecordInfo(@Path(value = "restPath", encoded = true) String restPath,
			@Query("v") String representation,
			@Query("includeAll") Boolean includeAll);
}
