package org.openmrs.mobile.data.db.impl;

import org.openmrs.mobile.data.ModelAsserters;
import org.openmrs.mobile.data.db.BaseDbService;
import org.openmrs.mobile.data.db.BaseMetadataDbServiceTest;
import org.openmrs.mobile.models.Location;

public class LocationDbServiceTest extends BaseMetadataDbServiceTest<Location> {
	@Override
	protected BaseDbService<Location> getDbService() {
		return null;
	}

	@Override
	protected ModelAsserters.MetadataAsserter<Location> getAsserter() {
		return ModelAsserters.LOCATION;
	}
}
