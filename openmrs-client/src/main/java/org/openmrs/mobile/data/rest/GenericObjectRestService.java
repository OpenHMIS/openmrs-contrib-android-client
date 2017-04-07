package org.openmrs.mobile.data.rest;

import org.openmrs.mobile.models.BaseOpenmrsObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface GenericObjectRestService<E extends BaseOpenmrsObject> {
    @GET("/{restPath}/{uuid}")
    Call<E> getByUuid(@Path("restPath") String restPath,
                      @Path("uuid") String uuid);

    @GET("/{restPath}")
    Call<List<E>> getAll(@Path("restPath") String restPath);

    @POST("/{restPath}")
    Call<E> create(@Path("restPath") String restPath, E entity);

    @POST("/{restPath}/{uuid}")
    Call<E> update(@Path("restPath") String restPath,
                   @Path("uuid") String uuid, E entity);

    @DELETE("/{restPath}/{uuid}")
    Call<E> purge(@Path("restPath") String restPath,
                   @Path("uuid") String uuid);
}

