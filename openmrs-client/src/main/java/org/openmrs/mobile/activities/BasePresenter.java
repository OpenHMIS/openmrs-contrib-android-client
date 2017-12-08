/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.mobile.activities;

import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.dagger.DaggerDataAccessComponent;
import org.openmrs.mobile.dagger.DaggerSyncComponent;
import org.openmrs.mobile.dagger.DataAccessComponent;
import org.openmrs.mobile.dagger.SyncComponent;
import org.openmrs.mobile.dagger.SyncModule;
import org.openmrs.mobile.data.db.impl.PullSubscriptionDbService;

public abstract class BasePresenter implements BasePresenterContract {
	private DataAccessComponent dataAccess;
	// TODO: refactor this to do better DI with Dagger 2 and a single component
	private SyncComponent syncComponent;

	public BasePresenter() {
		dataAccess = DaggerDataAccessComponent.create();
		syncComponent = DaggerSyncComponent.builder().syncModule(new SyncModule(OpenMRS.getInstance())).build();
	}

	@Override
	public void unsubscribe() {	}

	protected DataAccessComponent dataAccess() {
		return dataAccess;
	}

	protected PullSubscriptionDbService pullSubscriptionDbService() {
		return syncComponent.pullSubscriptionDbService();
	}
}
