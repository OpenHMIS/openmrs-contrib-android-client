package org.openmrs.mobile.data.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.IntentFilter;
import android.os.Bundle;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.openmrs.mobile.BuildConfig;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.data.dagger.DaggerTestReceiverComponent;
import org.openmrs.mobile.data.dagger.TestReceiverComponent;
import org.openmrs.mobile.data.db.impl.PatientListContextDbService;
import org.openmrs.mobile.receivers.SyncReceiver;
import org.openmrs.mobile.sync.SyncManager;
import org.openmrs.mobile.utilities.NetworkUtils;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class SyncManagerTest {
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();

	@Mock
	AccountManager accountManager;

	@Mock
	OpenMRS openMRS;

	@Mock
	NetworkUtils networkUtils;

	@Mock
	AlarmManager alarmManager;

	@Mock
	SyncService syncService;

	@Mock
	PatientListContextDbService patientListContextDbService;

	private SyncManager syncManager;
	private TestReceiverComponent testReceiverComponent;

	@Before
	public void setUp() {
		Mockito.when(accountManager.addAccountExplicitly(Mockito.any(Account.class), Mockito.anyString(),
				Mockito.any(Bundle.class))).thenReturn(true);
		Mockito.when(openMRS.getSystemService(openMRS.ACCOUNT_SERVICE)).thenReturn(accountManager);
		Mockito.when(openMRS.getSystemService(openMRS.ALARM_SERVICE)).thenReturn(alarmManager);

		testReceiverComponent = DaggerTestReceiverComponent.create();
		syncManager = new SyncManager(openMRS, testReceiverComponent.syncReceiver(), syncService,
				patientListContextDbService);
	}

	@Test
	public void syncManager_syncInitializationCreatesRepeatingAlarmForSync() {
		syncManager.initializeDataSync();

		Mockito.verify(alarmManager, Mockito.times(1)).setInexactRepeating(Mockito.anyInt(), Mockito.anyLong(),
				Mockito.anyLong(), Mockito.any(PendingIntent.class));
	}

	@Test
	public void syncManager_registeringReceiversRegistersSyncReceiver() {
		SyncReceiver syncReceiver = testReceiverComponent.syncReceiver();

		syncManager.initializeDataSync();

		Mockito.verify(openMRS, Mockito.times(1)).registerReceiver(Mockito.eq(syncReceiver),
				Mockito.any(IntentFilter.class));
	}
}
