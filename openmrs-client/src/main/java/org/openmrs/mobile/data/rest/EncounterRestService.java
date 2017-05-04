package org.openmrs.mobile.data.rest;

import org.openmrs.mobile.models.Encounter;
import org.openmrs.mobile.models.Results;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface EncounterRestService {

    @GET(RestConstants.GET_BY_UUID)
    Call<Encounter> getByUuid(@Path(value = "restPath", encoded = true) String restPath,
                              @Path("uuid") String uuid,
                              @Query("v") String representation);

    @GET(RestConstants.GET_ALL)
    Call<Results<Encounter>> getAll(@Path(value = "restPath", encoded = true) String restPath,
                                    @Query("v") String representation);

    @GET(RestConstants.GET_ALL)
    Call<Results<Encounter>> getAll(@Path(value = "restPath", encoded = true) String restPath,
                                    @Query("v") String representation,
                                    @Query("limit") int limit,
                                    @Query("startIndex") int startIndex);

    @POST(RestConstants.CREATE)
    Call<Encounter> create(@Path(value = "restPath", encoded = true) String restPath, @Body Encounter entity);

    @POST(RestConstants.UPDATE)
    Call<Encounter> update(@Path(value = "restPath", encoded = true) String restPath,
                           @Path("uuid") String uuid, @Body Encounter entity);

    @DELETE(RestConstants.PURGE)
    Call<Encounter> purge(@Path(value = "restPath", encoded = true) String restPath,
                          @Path("uuid") String uuid);

    @GET(RestConstants.REST_PATH)
    Call<Results<Encounter>> getByPatient(@Path(value = "restPath", encoded = true) String restPath,
                                          @Query("encounter") String encounterUuid,
                                          @Query("v") String representation);

    @GET(RestConstants.REST_PATH)
    Call<Results<Encounter>> getByPatient(@Path(value = "restPath", encoded = true) String restPath,
                                          @Query("encounter") String encounterUuid,
                                          @Query("v") String representation,
                                          @Query("limit") int limit,
                                          @Query("startIndex") int startIndex);


    @GET(RestConstants.REST_PATH)
    Call<Results<Encounter>> getByObservation(@Path(value = "restPath", encoded = true) String restPath,
                                              @Query("encounter") String encounterUuid,
                                              @Query("v") String representation);

    @GET(RestConstants.REST_PATH)
    Call<Results<Encounter>> getByObservation(@Path(value = "restPath", encoded = true) String restPath,
                                              @Query("encounter") String encounterUuid,
                                              @Query("v") String representation,
                                              @Query("limit") int limit,
                                              @Query("startIndex") int startIndex);
}
