package org.openmrs.mobile.data.rest.impl;

import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.data.rest.BaseMetadataRestService;
import org.openmrs.mobile.data.rest.RestConstants;
import org.openmrs.mobile.data.rest.retrofit.LocationRestService;
import org.openmrs.mobile.data.rest.retrofit.RestServiceBuilder;
import org.openmrs.mobile.models.Location;
import org.openmrs.mobile.models.Results;
import org.openmrs.mobile.utilities.ApplicationConstants;

import javax.inject.Inject;

import retrofit2.Call;

public class LocationRestServiceImpl extends BaseMetadataRestService<Location, LocationRestService> {
	@Inject
	public LocationRestServiceImpl() { }

	@Override
	protected String getRestPath() {
		return ApplicationConstants.API.REST_ENDPOINT_V1;
	}

	@Override
	protected String getEntityName() {
		return "location";
	}

	public Call<Results<Location>> getLoginLocations() {
		restService = RestServiceBuilder.createService(LocationRestService.class, "", "");

		return restService.getLoginLocations(buildRestRequestPath(),
				QueryOptions.getRepresentation(QueryOptions.FULL_REP));
	}
}
