package org.openmrs.mobile.dagger;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import org.greenrobot.eventbus.EventBus;
import org.openmrs.mobile.application.OpenMRS;

@Module
public class SyncModule {
	protected OpenMRS openMRS;

	public SyncModule(OpenMRS openMRS) {
		this.openMRS = openMRS;
	}

	@Provides
	@Singleton
	public OpenMRS providesOpenMRS() {
		return openMRS;
	}

	@Provides
	@Singleton
	public EventBus providesEventBus() {
		return EventBus.getDefault();
	}
}
