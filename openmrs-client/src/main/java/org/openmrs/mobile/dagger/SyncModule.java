package org.openmrs.mobile.dagger;

import android.content.Context;
import org.openmrs.mobile.data.db.DbService;
import org.openmrs.mobile.data.db.impl.PatientDbService;
import org.openmrs.mobile.data.db.impl.PullSubscriptionDbService;
import org.openmrs.mobile.data.db.impl.SyncLogDbService;
import org.openmrs.mobile.data.sync.PushSyncProvider;
import org.openmrs.mobile.data.sync.SyncProvider;
import org.openmrs.mobile.data.sync.SyncService;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.PullSubscription;
import org.openmrs.mobile.models.SyncLog;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import org.openmrs.mobile.sync.SyncAdapter;

@Module
public class SyncModule {
	protected Context context;

	public SyncModule(Context context) {
		this.context = context;
	}

	@Provides
	public Context providesContext() {
		return this.context;
	}
}
