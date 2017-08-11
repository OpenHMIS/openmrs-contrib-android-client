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

package org.openmrs.mobile.activities.login;

import org.openmrs.mobile.activities.BasePresenterContract;
import org.openmrs.mobile.activities.BaseView;
import org.openmrs.mobile.models.Location;

import java.util.HashMap;
import java.util.List;

public interface LoginContract {

	interface View extends BaseView<Presenter> {

		void hideSoftKeys();

		void showWarningDialog();

		void userAuthenticated();

		void finishLoginActivity();

		void setProgressBarVisibility(boolean visible);

		void updateLoginFormLocations(List<Location> locations, String url);

		void showMessage(String message);

		void showMessage(int errorCode);
	}

	interface Presenter extends BasePresenterContract {

		void login(String username, String password, String url, String oldUrl);

		void loadLocations(String url);

		void authenticateUser(final String username, final String password, final String url, boolean wipeDatabase);

		void saveLocationsInPreferences(List<HashMap<String, String>> locationList, int selectedItemPosition);

		void userWasLoggedOutDueToInactivity();
	}

}