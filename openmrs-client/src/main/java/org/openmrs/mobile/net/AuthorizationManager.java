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

package org.openmrs.mobile.net;

import javax.inject.Inject;

import android.content.Intent;

import org.joda.time.DateTime;
import org.openmrs.mobile.activities.login.LoginActivity;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.utilities.ApplicationConstants;

public class AuthorizationManager {

	private DateTime lastUserInteraction;
	private static final long DISCONNECT_TIMEOUT_HOURS = 4;
	public static final long DISCONNECT_TIMEOUT_MILLIS = DISCONNECT_TIMEOUT_HOURS
			* 60 * 60 * 1000; // 1 hr = 60 minutes * 60 seconds * 1000 ms

	protected OpenMRS openMRS;

	@Inject
	public AuthorizationManager(OpenMRS openMRS) {
		lastUserInteraction = DateTime.now();
		this.openMRS = openMRS;
	}

	public boolean isUserNameOrServerEmpty() {
		boolean result = false;
		if (openMRS.getUsername().equals(ApplicationConstants.EMPTY_STRING) ||
				(openMRS.getServerUrl().equals(ApplicationConstants.EMPTY_STRING))) {
			result = true;
		}
		return result;
	}

	public void invalidateUser() {
		openMRS.setSessionToken(ApplicationConstants.EMPTY_STRING);
	}

	public boolean isUserLoggedIn() {
		return !ApplicationConstants.EMPTY_STRING.equals(openMRS.getSessionToken());
	}

	public void moveToLoginActivity() {
		Intent intent = new Intent(openMRS.getApplicationContext(), LoginActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		openMRS.getApplicationContext().startActivity(intent);
	}

	public boolean hasUserSessionExpiredDueToInactivity() {
		long durationSinceLastInteraction = DateTime.now().getMillis() - lastUserInteraction.getMillis();
		return durationSinceLastInteraction >= DISCONNECT_TIMEOUT_MILLIS;
	}

	public void trackUserInteraction() {
		lastUserInteraction = DateTime.now();
	}
}
