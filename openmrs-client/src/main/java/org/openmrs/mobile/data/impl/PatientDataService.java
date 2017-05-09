package org.openmrs.mobile.data.impl;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.openmrs.mobile.data.BaseDataService;
import org.openmrs.mobile.data.PagingInfo;
import org.openmrs.mobile.data.rest.PatientRestService;
import org.openmrs.mobile.data.rest.RestConstants;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.Results;
import org.openmrs.mobile.utilities.ApplicationConstants;

import retrofit2.Call;

public class PatientDataService extends BaseDataService<Patient, PatientRestService> {

    @Override
    protected Class<PatientRestService> getRestServiceClass() {
        return PatientRestService.class;
    }

    @Override
    protected String getRestPath() {
        return ApplicationConstants.API.REST_ENDPOINT_V1;
    }

    @Override
    protected String getEntityName() {
        return "patient";
    }

    @Override
    public void getAll(boolean includeInactive, @Nullable PagingInfo pagingInfo, @NonNull GetMultipleCallback<Patient> callback) {
        // The patient rest service does not support getting all patients
        return;
    }

    // Begin Retrofit Workaround

    @Override
    protected Call<Patient> _restGetByUuid(String restPath, String uuid, String representation) {
        return restService.getByUuid(restPath, uuid, representation);
    }

    @Override
    protected Call<Results<Patient>> _restGetAll(String restPath, PagingInfo pagingInfo, String representation) {
        throw new UnsupportedOperationException("The patients rest service does not support a get all method.");
    }

    @Override
    protected Call<Patient> _restCreate(String restPath, Patient entity) {
        return restService.create(restPath, entity);
    }

    @Override
    protected Call<Patient> _restUpdate(String restPath, Patient entity) {
        return restService.update(restPath, entity.getUuid(), entity);
    }

    @Override
    protected Call<Patient> _restPurge(String restPath, String uuid) {
        return restService.purge(restPath, uuid);
    }

    // End Retrofit Workaround

    public void getByName(String name, PagingInfo pagingInfo, GetMultipleCallback<Patient> callback) {
        executeMultipleCallback(callback, null, () -> {
            if (isPagingValid(pagingInfo)) {
                return restService.getByName(buildRestRequestPath(), name, RestConstants.Representations.FULL,
                        pagingInfo.getLimit(), pagingInfo.getStartIndex());
            } else {
                return restService.getByName(buildRestRequestPath(), name, RestConstants.Representations.FULL);
            }
        });
    }
}
