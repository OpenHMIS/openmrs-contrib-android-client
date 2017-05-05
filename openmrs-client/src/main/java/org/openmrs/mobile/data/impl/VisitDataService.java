package org.openmrs.mobile.data.impl;

import org.openmrs.mobile.data.BaseEntityDataService;
import org.openmrs.mobile.data.EntityDataService;
import org.openmrs.mobile.data.PagingInfo;
import org.openmrs.mobile.data.rest.RestConstants;
import org.openmrs.mobile.data.rest.VisitRestService;
import org.openmrs.mobile.models.Results;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.utilities.ApplicationConstants;

import retrofit2.Call;

public class VisitDataService extends BaseEntityDataService<Visit, VisitRestService> implements EntityDataService<Visit> {
    @Override
    protected Class<VisitRestService> getRestServiceClass() {
        return VisitRestService.class;
    }

    @Override
    protected String getRestPath() {
        return ApplicationConstants.API.REST_ENDPOINT_V1;
    }

    @Override
    protected String getEntityName() {
        return "visit";
    }

    // Begin Retrofit Workaround

    @Override
    protected Call<Visit> _restGetByUuid(String restPath, String uuid, String representation) {
        return restService.getByUuid(restPath, uuid, RestConstants.Representations.FULL);
    }

    @Override
    protected Call<Results<Visit>> _restGetAll(String restPath, PagingInfo pagingInfo, String representation) {
        if (isPagingValid(pagingInfo)) {
            return restService.getAll(restPath, representation, pagingInfo.getLimit(), pagingInfo.getStartIndex());
        } else {
            return restService.getAll(restPath, representation);
        }
    }

    @Override
    protected Call<Visit> _restCreate(String restPath, Visit entity) {
        return restService.create(restPath, entity);
    }

    @Override
    protected Call<Visit> _restUpdate(String restPath, Visit entity) {
        return restService.update(restPath, entity.getUuid(), entity);
    }

    @Override
    protected Call<Visit> _restPurge(String restPath, String uuid) {
        return restService.purge(restPath, uuid);
    }

    @Override
    protected Call<Results<Visit>> _restGetByPatient(String restPath, PagingInfo pagingInfo, String patientUuid,
                                                     String representation) {
        if (isPagingValid(pagingInfo)) {
            return restService.getByPatient(restPath, patientUuid, representation,
                    pagingInfo.getLimit(), pagingInfo.getStartIndex());
        } else {
            return restService.getByPatient(restPath, patientUuid, representation);
        }
    }

    // End Retrofit Workaround
}

