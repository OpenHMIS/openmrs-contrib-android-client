package org.openmrs.mobile.data.rest;

import org.openmrs.mobile.models.BaseOpenmrsMetadata;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface GenericMetadataRestService<E extends BaseOpenmrsMetadata>
        extends GenericObjectRestService<E> {
    @GET("/{restPath}/")
    Call<List<E>> getByNameFragment(@Path("restPath") String restPath,
                                    @Query("q") String name);
}
