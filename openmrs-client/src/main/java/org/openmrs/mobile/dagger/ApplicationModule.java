package org.openmrs.mobile.dagger;

import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.data.db.impl.PatientListContextDbService;
import org.openmrs.mobile.net.AuthorizationManager;
import org.openmrs.mobile.net.NetworkManager;
import org.openmrs.mobile.sync.SyncManager;

import javax.inject.Singleton;

import android.content.Context;
import dagger.Module;
import dagger.Provides;

@Module
public class ApplicationModule {
	protected OpenMRS openMRS;
	protected ReceiverComponent receiverComponent;
	protected SyncComponent syncComponent;

	public ApplicationModule(OpenMRS openMRS) {
		this.openMRS = openMRS;
		this.receiverComponent = DaggerReceiverComponent.create();
		this.syncComponent = DaggerSyncComponent.builder().syncModule(new SyncModule(openMRS)).build();
	}

	@Provides
	@Singleton
	public Context providesContext() {
		return providesOpenMRS();
	}

	@Provides
	@Singleton
	public OpenMRS providesOpenMRS() {
		return openMRS;
	}

	@Provides
	@Singleton
	public SyncManager provideSyncManager(PatientListContextDbService patientListContextDbService) {
		return new SyncManager(providesOpenMRS(), receiverComponent.syncReceiver(), syncComponent.syncService(),
				patientListContextDbService);
	}

	@Provides
	@Singleton
	public AuthorizationManager provideAuthorizationManager() {
		return new AuthorizationManager(openMRS);
	}

	@Provides
	@Singleton
	public NetworkManager provideNetworkManager() {
		return new NetworkManager(openMRS, DaggerReceiverComponent.create());
	}
}
