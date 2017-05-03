package org.openmrs.mobile.data.rest;

import org.openmrs.mobile.models.Observation;
import org.openmrs.mobile.models.Results;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ObservationRestService {

    @GET(RestConstants.GET_BY_UUID)
    Call<Observation> getByUuid(@Path(value = "restPath", encoded = true) String restPath,
                                @Path("uuid") String uuid,
                                @Query("v") String representation);

    @GET(RestConstants.GET_ALL)
    Call<Results<Observation>> getAll(@Path(value = "restPath", encoded = true) String restPath,
                                      @Query("v") String representation);

    @GET(RestConstants.GET_ALL)
    Call<Results<Observation>> getAll(@Path(value = "restPath", encoded = true) String restPath,
                                      @Query("v") String representation,
                                      @Query("limit") int limit,
                                      @Query("startIndex") int startIndex);

    @POST(RestConstants.CREATE)
    Call<Observation> create(@Path(value = "restPath", encoded = true) String restPath, Observation entity);

    @POST(RestConstants.UPDATE)
    Call<Observation> update(@Path(value = "restPath", encoded = true) String restPath,
                             @Path("uuid") String uuid, @Body Observation entity);

    @DELETE(RestConstants.PURGE)
    Call<Observation> purge(@Path(value = "restPath", encoded = true) String restPath,
                            @Path("uuid") String uuid);

    @GET(RestConstants.REST_PATH)
    Call<Results<Observation>> getByPatient(@Path(value = "restPath", encoded = true) String restPath,
                                            @Query("patient") String patientUuid,
                                            @Query("v") String representation);

    @GET(RestConstants.REST_PATH)
    Call<Results<Observation>> getByPatient(@Path(value = "restPath", encoded = true) String restPath,
                                            @Query("patient") String patientUuid,
                                            @Query("v") String representation,
                                            @Query("limit") int limit,
                                            @Query("startIndex") int startIndex);

    @GET(RestConstants.REST_PATH)
    Call<Results<Observation>> getByEncounter(@Path(value = "restPath", encoded = true) String restPath,
                                              @Query("encounter") String encounterUuid,
                                              @Query("v") String representation);

    @GET(RestConstants.REST_PATH)
    Call<Results<Observation>> getByEncounter(@Path(value = "restPath", encoded = true) String restPath,
                                              @Query("encounter") String encounterUuid,
                                              @Query("v") String representation,
                                              @Query("limit") int limit,
                                              @Query("startIndex") int startIndex);

}
