package org.openmrs.mobile.data.rest.retrofit;

import org.openmrs.mobile.data.rest.RestConstants;
import org.openmrs.mobile.models.RecordInfo;
import org.openmrs.mobile.models.Results;
import org.openmrs.mobile.models.VisitAttributeType;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface VisitAttributeTypeRestService {
	@GET(RestConstants.GET_ALL)
	Call<Results<VisitAttributeType>> getAll(@Path(value = "restPath", encoded = true) String restPath,
			@Query("v") String representation,
			@Query("includeAll") Boolean includeAll,
			@Query("limit") Integer limit,
			@Query("startIndex") Integer startIndex);

	@GET(RestConstants.GET_BY_UUID)
	Call<VisitAttributeType> getByUuid(@Path(value = "restPath", encoded = true) String restPath,
			@Path("uuid") String uuid,
			@Query("v") String representation,
			@Query("includeAll") Boolean includeAll);

	@POST(RestConstants.CREATE)
	Call<VisitAttributeType> create(@Path(value = "restPath", encoded = true) String restPath, VisitAttributeType entity);

	@POST(RestConstants.UPDATE)
	Call<VisitAttributeType> update(@Path(value = "restPath", encoded = true) String restPath,
			@Path("uuid") String uuid, VisitAttributeType entity);

	@DELETE(RestConstants.PURGE)
	Call<VisitAttributeType> purge(@Path(value = "restPath", encoded = true) String restPath,
			@Path("uuid") String uuid);

	@GET(RestConstants.GET_ALL)
	Call<Results<RecordInfo>> getRecordInfo(@Path(value = "restPath", encoded = true) String restPath,
			@Query("v") String representation,
			@Query("includeAll") Boolean includeAll);
}
