package org.openmrs.mobile.dagger;

import org.openmrs.mobile.data.db.impl.SyncLogDbService;
import org.openmrs.mobile.data.sync.SyncLogService;
import org.openmrs.mobile.data.sync.impl.SyncLogServiceImpl;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class SyncLogModule {
	@Provides
	@Singleton
	public SyncLogService providesSyncLogService(SyncLogDbService dbService) {
		return new SyncLogServiceImpl(dbService);
	}
}
