package org.openmrs.mobile.data.sync;

import org.openmrs.mobile.models.PullSubscription;

/**
 * Base class for all pull subscription providers.
 */
public abstract class BaseSubscriptionProvider implements SubscriptionProvider {
	/**
	 * Initialize the provider.
	 * @param subscription The {@link PullSubscription} that will be processed by the provider
	 */
	@Override
	public void initialize(PullSubscription subscription) { }

	/**
	 * Executes the pull synchronization process.
	 * @param subscription The {@link PullSubscription} that will be processed
	 */
	@Override
	public abstract void pull(PullSubscription subscription);
}
