package org.openmrs.mobile.data.impl;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.openmrs.mobile.data.BaseDataService;
import org.openmrs.mobile.data.PagingInfo;
import org.openmrs.mobile.data.rest.EncounterRestService;
import org.openmrs.mobile.data.rest.EncounterRestService;
import org.openmrs.mobile.data.rest.RestConstants;
import org.openmrs.mobile.models.Encounter;
import org.openmrs.mobile.models.Encounter;
import org.openmrs.mobile.models.Results;
import org.openmrs.mobile.utilities.ApplicationConstants;

import retrofit2.Call;


public class EncounterDataService extends BaseDataService<Encounter, EncounterRestService> {
    @Override
    protected Class<EncounterRestService> getRestServiceClass() {
        return EncounterRestService.class;
    }

    @Override
    protected String getRestPath() {
        return ApplicationConstants.API.REST_ENDPOINT_V1;
    }

    @Override
    protected String getEntityName() {
        return "encounter";
    }

    @Override
    public void getAll(boolean includeInactive, @Nullable PagingInfo pagingInfo, @NonNull GetMultipleCallback<Encounter> callback) {
        // The encounter rest service does not support getting all encounters
        return;
    }

    // Begin Retrofit Workaround

    @Override
    protected Call<Encounter> _restGetByUuid(String restPath, String uuid, String representation) {
        return restService.getByUuid(restPath, uuid, representation);
    }

    @Override
    protected Call<Results<Encounter>> _restGetAll(String restPath, PagingInfo pagingInfo, String representation) {
        throw new UnsupportedOperationException("The encounters rest service does not support a get all method.");
    }

    @Override
    protected Call<Encounter> _restCreate(String restPath, Encounter entity) {
        return restService.create(restPath, entity);
    }

    @Override
    protected Call<Encounter> _restUpdate(String restPath, Encounter entity) {
        return restService.update(restPath, entity.getUuid(), entity);
    }

    @Override
    protected Call<Encounter> _restPurge(String restPath, String uuid) {
        return restService.purge(restPath, uuid);
    }

    // End Retrofit Workaround


}
