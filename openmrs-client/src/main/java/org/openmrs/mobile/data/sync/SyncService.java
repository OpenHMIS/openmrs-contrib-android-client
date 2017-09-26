package org.openmrs.mobile.data.sync;

import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.data.DataOperationException;
import org.openmrs.mobile.data.db.impl.PullSubscriptionDbService;
import org.openmrs.mobile.data.db.impl.SyncLogDbService;
import org.openmrs.mobile.data.sync.impl.PatientTrimProvider;
import org.openmrs.mobile.event.SyncEvent;
import org.openmrs.mobile.event.SyncPullEvent;
import org.openmrs.mobile.event.SyncPushEvent;
import org.openmrs.mobile.models.PullSubscription;
import org.openmrs.mobile.models.SyncLog;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.NetworkUtils;
import org.openmrs.mobile.utilities.StringUtils;
import org.openmrs.mobile.utilities.TimeConstants;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

public class SyncService {
	private static final String TAG = SyncService.class.getSimpleName();
	private static final long TRIM_INTERVAL = TimeConstants.SECONDS_PER_DAY;

	private static final Object SYNC_LOCK = new Object();

	private OpenMRS openmrs;
	private SyncLogDbService syncLogDbService;
	private PullSubscriptionDbService subscriptionDbService;
	private DaggerProviderHelper providerHelper;
	private EventBus eventBus;
	private PatientTrimProvider patientTrimProvider;

	private NetworkUtils networkUtils;
	private Map<String, SubscriptionProvider> subscriptionProviders = new HashMap<String, SubscriptionProvider>();
	private Map<String, PushProvider> pushSyncProviders = new HashMap<>();

	@Inject
	public SyncService(OpenMRS openmrs, SyncLogDbService syncLogDbService, PullSubscriptionDbService subscriptionDbService,
			DaggerProviderHelper providerHelper, NetworkUtils networkUtils, EventBus eventBus,
			PatientTrimProvider patientTrimProvider) {
		this.openmrs = openmrs;
		this.syncLogDbService = syncLogDbService;
		this.subscriptionDbService = subscriptionDbService;
		this.providerHelper = providerHelper;
		this.networkUtils = networkUtils;
		this.eventBus = eventBus;
		this.patientTrimProvider = patientTrimProvider;
	}

	public void sync() {
		// Synchronize access so that only one thread is synchronizing at a time
		synchronized (SYNC_LOCK) {
			// Push changes to server
			push();

			// Pull subscription changes
			pull();

			// Trim patient data that is not subscribed
			trim();
		}
	}

	/**
	 * Retrieves all SyncLog entries, and pushes to the server via REST. Once a record has successfully been updated on
	 * the server, the corresponding synclog entry is deleted.
	 */
	protected void push() {
		// Get synclog records that need to be pushed
		List<SyncLog> records = syncLogDbService.getOrderedList();
		eventBus.post(new SyncPushEvent(ApplicationConstants.EventMessages.Sync.Push.TOTAL_RECORDS,
				ApplicationConstants.EventMessages.Sync.SyncType.RECORD, records.size()));

		for (SyncLog record : records) {
			eventBus.post(new SyncPushEvent(ApplicationConstants.EventMessages.Sync.Push.RECORD_REMOTE_PUSH_STARTING,
					record.getType(), null));
			PushProvider pushProvider = pushSyncProviders.get(record.getType());
			if (pushProvider == null) {
				pushProvider = providerHelper.getSyncProvider(record.getType());

				pushSyncProviders.put(record.getType(), pushProvider);
			}

			if (pushProvider != null) {
				try {

					if (StringUtils.notNull(openmrs.getPatientUuid()) &&
							openmrs.getPatientUuid().equalsIgnoreCase(record.getKey())) {
						Log.i(TAG, "Skip. The Patient with uuid '" + record.getKey() + "' is currently being viewed");
						continue;
					}

					if (StringUtils.notNull(openmrs.getVisitUuid()) &&
							openmrs.getVisitUuid().equalsIgnoreCase(record.getKey())) {
						Log.i(TAG, "Skip. The Visit with uuid '" + record.getKey() + "' is currently being viewed");
						continue;
					}

					pushProvider.push(record);

					syncLogDbService.delete(record);
				} catch (DataOperationException doe) {
					Log.w(TAG, "Data exception occurred while processing push provider '" +
							pushProvider.getClass().getSimpleName() + ":" +
							(StringUtils.isBlank(record.getKey()) ? "(null)" :
									record.getKey()) + "'", doe);
				} catch (Exception ex) {
					Log.e(TAG, "An exception occurred while processing push provider '" +
							pushProvider.getClass().getSimpleName() + ":" +
							(StringUtils.isBlank(record.getKey()) ? "(null)" :
									record.getKey()) + "'", ex);
				} finally {
					// Check to see if we're still online, if not, then stop the sync
					if (!networkUtils.hasNetwork()) {
						break;
					}
				}
			} else {
				Log.e(TAG, "Could not find provider for sync type '" + record.getType() + "'");
			}

			eventBus.post(new SyncPushEvent(ApplicationConstants.EventMessages.Sync.Push.RECORD_REMOTE_PUSH_COMPLETE,
					record.getType(), null));
		}
	}

	/**
	 * Processes all defined subscription providers, ensuring that they are only processed when the minimum interval has
	 * elapsed.
	 */
	protected void pull() {
		// Get subscriptions
		List<PullSubscription> subscriptions = subscriptionDbService.getAll(null, null);
		eventBus.post(new SyncPullEvent(ApplicationConstants.EventMessages.Sync.Pull.TOTAL_SUBSCRIPTIONS,
				ApplicationConstants.EventMessages.Sync.SyncType.SUBSCRIPTION, subscriptions.size()));
		for (PullSubscription sub : subscriptions) {
			eventBus.post(new SyncPullEvent(ApplicationConstants.EventMessages.Sync.Pull.SUBSCRIPTION_REMOTE_PULL_STARTING,
					sub.getSubscriptionClass(), null));
			// Check if subscription should be processed, given the minimum interval
			Integer seconds = null;
			if (sub.getLastSync() != null && sub.getMinimumInterval() != null) {
				Period p = new Period(new DateTime(sub.getLastSync()), DateTime.now());
				seconds = p.getSeconds();
			}

			if (seconds == null || sub.getMinimumInterval() == null || seconds > sub.getMinimumInterval()) {
				// Try to get the cached subscription provider
				SubscriptionProvider provider = subscriptionProviders.get(sub.getSubscriptionClass());
				if (provider == null) {
					provider = providerHelper.getSubscriptionProvider(sub.getSubscriptionClass());

					subscriptionProviders.put(sub.getSubscriptionClass(), provider);
				}

				// If the provider was instantiated then execute it
				if (provider != null) {
					try {
						// Get the date before starting the pull process so that server changes while the provider is
						// processing don't get lost
						Date lastSync = new Date();

						provider.initialize(sub);
						provider.pull(sub);

						sub.setLastSync(lastSync);
						subscriptionDbService.save(sub);
					} catch (DataOperationException doe) {
						Log.w(TAG, "Data exception occurred while processing subscription provider '" +
								sub.getSubscriptionClass() + ":" +
								(StringUtils.isBlank(sub.getSubscriptionKey()) ? "(null)" :
										sub.getSubscriptionKey()) + "'", doe);

					} catch (Exception ex) {
						Log.e(TAG, "An exception occurred while processing subscription provider '" +
								sub.getSubscriptionClass() + ":" +
								(StringUtils.isBlank(sub.getSubscriptionKey()) ? "(null)" :
										sub.getSubscriptionKey()) + "'", ex);
					} finally {
						// Check to see if we're still online, if not, then stop the sync
						if (!networkUtils.hasNetwork()) {
							eventBus.post(new SyncEvent(ApplicationConstants.EventMessages.Sync.CANT_SYNC_NO_NETWORK,
									null, null));
							break;
						}
					}
				}
			}

			eventBus.post(new SyncPullEvent(ApplicationConstants.EventMessages.Sync.Pull.SUBSCRIPTION_REMOTE_PULL_COMPLETE,
					sub.getSubscriptionClass(), null));
		}
	}

	/**
	 * Trims patient data for patients that are not subscribed.
	 */
	protected void trim() {
		eventBus.post(new SyncPullEvent(ApplicationConstants.EventMessages.Sync.Pull.TRIM_STARTING,
				ApplicationConstants.EventMessages.Sync.SyncType.TRIM, null));

		// Get the number of seconds since the trim was last executed
		Date lastTrimDate = openmrs.getLastTrimDate();
		Integer seconds = null;
		if (lastTrimDate != null) {
			Period p = new Period(new DateTime(lastTrimDate), DateTime.now());
			seconds = p.getSeconds();
		}

		if (seconds == null || seconds > TRIM_INTERVAL) {
			try {
				patientTrimProvider.trim();
			} catch (Exception ex) {
				Log.e(TAG, "An exception occurred while trimming the patient data.", ex);
			} finally {
				openmrs.setLastTrimDate(new Date());
			}
		}

		eventBus.post(new SyncPullEvent(ApplicationConstants.EventMessages.Sync.Pull.TRIM_COMPLETE,
				ApplicationConstants.EventMessages.Sync.SyncType.TRIM, null));
	}
}

