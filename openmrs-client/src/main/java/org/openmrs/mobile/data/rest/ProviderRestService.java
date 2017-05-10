package org.openmrs.mobile.data.rest;

import org.openmrs.mobile.models.Provider;
import org.openmrs.mobile.models.Results;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ProviderRestService {

	@GET(RestConstants.GET_ALL)
	Call<Results<Provider>> getAll(@Path(value = "restPath", encoded = true) String restPath,
			@Query("v") String representation);

	@GET(RestConstants.GET_BY_UUID)
	Call<Provider> getByUuid(@Path(value = "restPath", encoded = true) String restPath,
			@Path("uuid") String uuid,
			@Query("v") String representation);
}
