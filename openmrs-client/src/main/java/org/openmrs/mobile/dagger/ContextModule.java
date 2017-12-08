package org.openmrs.mobile.dagger;

import android.content.Context;

import org.openmrs.mobile.application.OpenMRS;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ContextModule {

	@Provides
	@Singleton
	public Context providesContext() {
		return OpenMRS.getInstance();
	}
}
