package org.openmrs.mobile.data.impl;

import org.openmrs.mobile.data.BaseMetadataDataService;
import org.openmrs.mobile.data.db.impl.LocationDbService;
import org.openmrs.mobile.data.rest.impl.LocationRestServiceImpl;
import org.openmrs.mobile.models.Location;

import java.util.List;

import javax.inject.Inject;

public class LocationDataService extends BaseMetadataDataService<Location, LocationDbService, LocationRestServiceImpl> {
	@Inject
	public LocationDataService() { }

	public void getLoginLocations(String url, GetCallback<List<Location>> callback) {
		executeMultipleCallback(callback, null, null,
				() -> dbService.getAll(null, null),
				() -> restService.getLoginLocations(url)
		);

	}
}
