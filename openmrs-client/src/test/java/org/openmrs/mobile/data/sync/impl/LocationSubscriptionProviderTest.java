package org.openmrs.mobile.data.sync.impl;

import org.mockito.InjectMocks;
import org.openmrs.mobile.data.db.impl.LocationDbService;
import org.openmrs.mobile.data.rest.impl.LocationRestServiceImpl;
import org.openmrs.mobile.data.sync.AdaptiveSubscriptionProviderTest;
import org.openmrs.mobile.models.Location;
import org.openmrs.mobile.models.PullSubscription;

public class LocationSubscriptionProviderTest
		extends AdaptiveSubscriptionProviderTest<Location, LocationDbService, LocationRestServiceImpl,
		LocationSubscriptionProvider> {
	@InjectMocks LocationSubscriptionProvider provider;

	@Override
	protected LocationSubscriptionProvider getProvider() {
		return provider;
	}

	@Override
	protected PullSubscription createSubscriptionRecord() {
		PullSubscription sub = new PullSubscription();

		sub.setMaximumIncrementalCount(50);

		return sub;
	}

	@Override
	protected Location createEntity() {
		Location location = new Location();

		location.setName("Location");

		return location;
	}
}
