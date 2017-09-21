package org.openmrs.mobile.data.db.impl;

import org.openmrs.mobile.data.ModelAsserters;
import org.openmrs.mobile.data.ModelGenerators;
import org.openmrs.mobile.data.db.BaseDbService;
import org.openmrs.mobile.data.db.BaseMetadataDbServiceTest;
import org.openmrs.mobile.models.Location;

import java.util.UUID;

public class LocationDbServiceTest extends BaseMetadataDbServiceTest<Location> {
	@Override
	protected BaseDbService<Location> getDbService() {
		return new LocationDbService(repository);
	}

	@Override
	protected ModelAsserters.MetadataAsserter<Location> getAsserter() {
		return ModelAsserters.LOCATION;
	}

	@Override
	protected ModelGenerators.ModelGenerator<Location> getGenerator() {
		return ModelGenerators.LOCATION;
	}
}
