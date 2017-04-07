package org.openmrs.mobile.data.rest;

import org.openmrs.mobile.models.BaseOpenmrsEntity;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface GenericEntityRestService<E extends BaseOpenmrsEntity>
        extends GenericObjectRestService<E> {
    @GET("/{restPath}/")
    Call<List<E>> getByPatient(@Path("restPath") String restPath,
                               @Query("patient") String patientUuid);
}

