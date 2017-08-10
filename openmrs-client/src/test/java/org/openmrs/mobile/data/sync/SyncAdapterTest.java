package org.openmrs.mobile.data.sync;

import android.accounts.Account;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.openmrs.mobile.BuildConfig;
import org.openmrs.mobile.sync.SyncAdapter;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class SyncAdapterTest {
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();

	@Mock
	SyncService syncService;

	@Mock
	Context context;

	@Mock
	ContentProviderClient contentProviderClient;

	@Mock
	SyncResult syncResult;

	@Test
	public void sync_callToPerformSyncCallsSyncServiceToSync() {
		Account account = new Account("test", "test");
		SyncAdapter syncAdapter = new SyncAdapter(context, syncService);
		syncAdapter.onPerformSync(account, Bundle.EMPTY, "test", contentProviderClient, syncResult);

		Mockito.verify(syncService, Mockito.times(1)).sync();
	}
}
