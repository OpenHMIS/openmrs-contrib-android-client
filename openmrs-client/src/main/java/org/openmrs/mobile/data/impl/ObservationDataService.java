package org.openmrs.mobile.data.impl;

import org.openmrs.mobile.data.BaseEntityDataService;
import org.openmrs.mobile.data.EntityDataService;
import org.openmrs.mobile.data.PagingInfo;
import org.openmrs.mobile.data.rest.ObservationRestService;
import org.openmrs.mobile.data.rest.RestConstants;
import org.openmrs.mobile.models.Observation;
import org.openmrs.mobile.models.Results;
import org.openmrs.mobile.utilities.ApplicationConstants;

import retrofit2.Call;


public class ObservationDataService extends BaseEntityDataService<Observation, ObservationRestService> implements EntityDataService<Observation> {

    @Override
    protected Call<Results<Observation>> _restGetByPatient(String restPath, PagingInfo pagingInfo, String patientUuid, String representation) {
        if (isPagingValid(pagingInfo)) {
            return restService.getByPatient(restPath, patientUuid, representation,
                    pagingInfo.getLimit(), pagingInfo.getStartIndex());
        } else {
            return restService.getByPatient(restPath, patientUuid, representation);
        }
    }

    @Override
    protected Call<Results<Observation>> _restGetByEncounter(String restPath, PagingInfo pagingInfo, String pncounterUuid, String representation) {
        if (isPagingValid(pagingInfo)) {
            return restService.getByEncounter(restPath, pncounterUuid, representation,
                    pagingInfo.getLimit(), pagingInfo.getStartIndex());
        } else {
            return restService.getByEncounter(restPath, pncounterUuid, representation);
        }
    }

    @Override
    protected Class<ObservationRestService> getRestServiceClass() {
        return ObservationRestService.class;
    }

    @Override
    protected String getRestPath() {
        return ApplicationConstants.API.REST_ENDPOINT_V1;
    }

    @Override
    protected String getEntityName() {
        return "obs";
    }

    @Override
    protected Call<Observation> _restGetByUuid(String restPath, String uuid, String representation) {
        return restService.getByUuid(restPath, uuid, RestConstants.Representations.FULL);
    }

    @Override
    protected Call<Results<Observation>> _restGetAll(String restPath, PagingInfo pagingInfo, String representation) {
        if (isPagingValid(pagingInfo)) {
            return restService.getAll(restPath, representation, pagingInfo.getLimit(), pagingInfo.getStartIndex());
        } else {
            return restService.getAll(restPath, representation);
        }
    }

    @Override
    protected Call<Observation> _restCreate(String restPath, Observation entity) {
        return restService.create(restPath, entity);
    }

    @Override
    protected Call<Observation> _restUpdate(String restPath, Observation entity) {
        return restService.update(restPath, entity.getUuid(), entity);
    }

    @Override
    protected Call<Observation> _restPurge(String restPath, String uuid) {
        return restService.purge(restPath, uuid);
    }

}
