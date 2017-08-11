package org.openmrs.mobile.sync;

import javax.inject.Inject;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.dagger.ReceiverComponent;
import org.openmrs.mobile.utilities.TimeConstants;

public class SyncManager {
	// Sync constants
	// The authority for the sync adapter's content provider
	private final String SYNC_ADAPTER_AUTHORITY = "org.openmrs.mobile.provider";
	// An account type, in the form of a domain name
	private final String SYNC_ADAPTER_ACCOUNT_TYPE = "org.openmrs.mobile.datasync";
	// The account name (this isn't used by the app, but is needed for syncing)
	private final String SYNC_ADAPTER_ACCOUNT = "syncAdapterAccount";
	// Sync interval constants
	private final long SYNC_INTERVAL_IN_MINUTES = 5L;
	private final long SYNC_INTERVAL_IN_MILLISECONDS = SYNC_INTERVAL_IN_MINUTES * TimeConstants.MILLIS_PER_MINUTE;

	private static final String ALARM_SYNC_INTENT = "alarmSyncIntent";

	private Account account;
	private static OpenMRS openMRS;
	private ReceiverComponent receiverComponent;

	@Inject
	public SyncManager(OpenMRS openMRS, ReceiverComponent receiverComponent) {
		this.openMRS = openMRS;
		this.receiverComponent = receiverComponent;
	}

	private void registerReceivers() {
		// Register connectivity receiver
		BroadcastReceiver syncReceiver = receiverComponent.syncReceiver();
		IntentFilter syncFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
		syncFilter.addAction(ALARM_SYNC_INTENT);
		openMRS.registerReceiver(syncReceiver, syncFilter);
	}

	public void initializeDataSync() {
		createAlarmForSyncing();
		account = createSyncAccount();
		registerReceivers();
	}

	private Account createSyncAccount() {
		if (account != null) {
			return account;
		}
		// Create the account type and default account
		Account newAccount = new Account(SYNC_ADAPTER_ACCOUNT, SYNC_ADAPTER_ACCOUNT_TYPE);
		// Get an instance of the Android account manager
		AccountManager accountManager = (AccountManager) openMRS.getSystemService(openMRS.ACCOUNT_SERVICE);

		// Below needed for syncing, but is for dummy account
		if (accountManager.addAccountExplicitly(newAccount, null, null)) {
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call context.setIsSyncable(account, SYNC_ADAPTER_AUTHORITY, 1)
             * here.
             */
			// Intentionally left blank
		} else {
            /*
             * The account exists or some other error occurred. Log this, report it,
             * or handle it internally.
             */
			// Intentionally left blank
		}
		return newAccount;
	}

	private void createAlarmForSyncing() {
		Intent startIntent = new Intent(ALARM_SYNC_INTENT);
		int dummyRequestCode = 0;
		int dummyFlag = 0;
		PendingIntent appIntent = PendingIntent.getBroadcast(openMRS, dummyRequestCode, startIntent, dummyFlag);
		AlarmManager alarmManager = (AlarmManager) openMRS.getSystemService(openMRS.ALARM_SERVICE);
		int initialIntervalInMillis = 0;
		alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, initialIntervalInMillis,
				SYNC_INTERVAL_IN_MILLISECONDS, appIntent);
	}

	public void requestSync() {
		ContentResolver.requestSync(account, SYNC_ADAPTER_AUTHORITY, Bundle.EMPTY);
	}
}
