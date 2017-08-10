package org.openmrs.mobile.dagger;

import javax.inject.Singleton;

import dagger.Component;
import org.openmrs.mobile.application.OpenMRS;

@Singleton
@Component(modules = { ApplicationModule.class, DbModule.class })
public interface ApplicationComponent {
	void inject(OpenMRS openMRS);
}
