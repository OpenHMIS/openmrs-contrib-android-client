package org.openmrs.mobile.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;

import org.openmrs.mobile.data.sync.SyncService;

import javax.inject.Inject;

public class SyncAdapter extends AbstractThreadedSyncAdapter {

	// Injected via constructor injection
    private SyncService syncService;

	@Inject
    public SyncAdapter(Context context, SyncService syncService) {
        super(context, true);

	    this.syncService = syncService;
    }

    /*@Inject
    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSync, SyncService syncService) {
        super(context, autoInitialize, allowParallelSync);
	    this.syncService = syncService;
    }*/

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
                              ContentProviderClient provider, SyncResult syncResult) {
	    syncService.sync();
    }
}
