package org.openmrs.mobile.data.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.openmrs.mobile.dagger.DaggerSyncComponent;
import org.openmrs.mobile.data.db.DbService;
import org.openmrs.mobile.models.PullSubscription;
import org.openmrs.mobile.models.SyncLog;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

public class SyncService {
	private static final Object SYNC_LOCK = new Object();

	@Inject
	SyncProvider syncProvider;

	@Inject
	DbService<SyncLog> syncLogDbService;

	@Inject
	DbService<PullSubscription> subscriptionDbService;

	private Map<String, SubscriptionProvider> subscriptionProviders = new HashMap<String, SubscriptionProvider>();

	public SyncService() {
		DaggerSyncComponent.create().inject(this);
	}

	public void sync() {
		// Synchronize access so that only one thread is synchronizing at a time
		synchronized (SYNC_LOCK) {
			// Push changes to server
			push();

			// Pull subscription changes
			pull();
		}
	}

	protected void push() {
		List<SyncLog> records = syncLogDbService.getAll(null, null);

		for (SyncLog record : records) {
			syncProvider.sync(record);

			syncLogDbService.delete(record);
		}
	}

	/**
	 * Processes all defined subscription providers, ensuring that they are only processed when the minimum interval has
	 * elapsed.
	 */
	protected void pull() {
		// Get subscriptions
		List<PullSubscription> subscriptions = subscriptionDbService.getAll(null, null);
		for (PullSubscription sub : subscriptions) {
			// Check if subscription should be processed, given the minimum interval
			Integer seconds = null;
			if (sub.getLastSync() != null && sub.getMinimumInterval() != null) {
				Period p = new Period(DateTime.now(), new DateTime(sub.getLastSync()));
				seconds = p.getSeconds();
			}

			if (seconds == null || seconds < sub.getMinimumInterval()) {
				// Try to get the cached subscription provider
				SubscriptionProvider provider = subscriptionProviders.get(sub.getSubscriptionClass());
				if (provider == null) {
					// Load the provider from the class name defined as the subscription class
					try {
						Class<?> cls = Class.forName(sub.getSubscriptionClass());
						provider = (SubscriptionProvider) cls.newInstance();

						subscriptionProviders.put(sub.getSubscriptionClass(), provider);
					} catch (Exception e) {
						provider = null;
						Log.e("Sync", "Could not load class '" + sub.getSubscriptionClass() + "'");
					}
				}

				// If the provider was instantiated then execute it
				if (provider != null) {
					provider.initialize(sub);
					provider.pull(sub);

					// Update the last synced time to the current date time
					sub.setLastSync(new Date());
				}
			}
		}
	}
}
