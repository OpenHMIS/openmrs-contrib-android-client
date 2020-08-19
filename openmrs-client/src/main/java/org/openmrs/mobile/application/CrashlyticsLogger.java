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

package org.openmrs.mobile.application;

import android.util.Log;

import com.google.firebase.crashlytics.FirebaseCrashlytics;

public class CrashlyticsLogger implements Logger {

	private static final String TAG = "Banda Health";
	private FirebaseCrashlytics crashlytics = FirebaseCrashlytics.getInstance();

	private String constructLogString(int logLevel, String tag, String message) {
		String messageLogLevelPrefix;
		switch (logLevel) {
			case Log.DEBUG:
				messageLogLevelPrefix = "D/";
				break;
			case Log.VERBOSE:
				messageLogLevelPrefix = "V/";
				break;
			case Log.WARN:
				messageLogLevelPrefix = "W/";
				break;
			case Log.ERROR:
				messageLogLevelPrefix = "E/";
				break;
			default:
				messageLogLevelPrefix = "I/";
				break;
		}
		return messageLogLevelPrefix + tag + ": " + message;
	}

	@Override
	public void v(String msg) {
		v(TAG, msg);
	}

	@Override
	public void v(String tag, String msg) {
		try {
			crashlytics.log(constructLogString(Log.VERBOSE, tag, msg));
		} catch(IllegalStateException ex) {
			// crashlytics not initialized.
		}
	}

	@Override
	public void v(String msg, Throwable throwable) {
		v(TAG, msg, throwable);
	}

	@Override
	public void v(String tag, String msg, Throwable throwable) {
		try {
			crashlytics.log(constructLogString(Log.VERBOSE, tag, msg));
			crashlytics.recordException(throwable);
		} catch(IllegalStateException ex) {
			// crashlytics not initialized.
		}
	}

	@Override
	public void d(String msg) {
		d(TAG, msg);
	}

	@Override
	public void d(String tag, String msg) {
		try {
			crashlytics.log(constructLogString(Log.DEBUG, tag, msg));
		} catch(IllegalStateException ex) {
			// crashlytics not initialized.
		}
	}

	@Override
	public void d(String msg, Throwable throwable) {
		d(TAG, msg, throwable);
	}

	@Override
	public void d(String tag, String msg, Throwable throwable) {
		try {
			crashlytics.log(constructLogString(Log.DEBUG, tag, msg));
			crashlytics.recordException(throwable);
		} catch(IllegalStateException ex) {
			// crashlytics not initialized.
		}
	}

	@Override
	public void i(String msg) {
		i(TAG, msg);
	}

	@Override
	public void i(String tag, String msg) {
		try {
			crashlytics.log(constructLogString(Log.INFO, tag, msg));
		} catch(IllegalStateException ex) {
			// crashlytics not initialized.
		}
	}

	@Override
	public void i(String msg, Throwable throwable) {
		i(TAG, msg, throwable);
	}

	@Override
	public void i(String tag, String msg, Throwable throwable) {
		try {
			crashlytics.log(constructLogString(Log.INFO, tag, msg));
			crashlytics.recordException(throwable);
		} catch(IllegalStateException ex) {
			// crashlytics not initialized.
		}
	}

	@Override
	public void w(String msg) {
		w(TAG, msg);
	}

	@Override
	public void w(String tag, String msg) {
		try {
			crashlytics.log(constructLogString(Log.WARN, tag, msg));
		} catch(IllegalStateException ex) {
			// crashlytics not initialized.
		}
	}

	@Override
	public void w(String msg, Throwable throwable) {
		w(TAG, msg, throwable);
	}

	@Override
	public void w(String tag, String msg, Throwable throwable) {
		try {
			crashlytics.log(constructLogString(Log.WARN, tag, msg));
			crashlytics.recordException(throwable);
		} catch(IllegalStateException ex) {
			// crashlytics not initialized.
		}
	}

	@Override
	public void e(String msg) {
		e(TAG, msg);
	}

	@Override
	public void e(String tag, String msg) {
		try {
			crashlytics.log(constructLogString(Log.ERROR, tag, msg));
			// Even though no exception was thrown, let's log this as a non-fatal crash to make sure we see errors in the app
			crashlytics.recordException(new Throwable(msg));
		} catch(IllegalStateException ex) {
			// crashlytics not initialized.
		}
	}

	@Override
	public void e(String msg, Throwable throwable) {
		e(TAG, msg, throwable);
	}

	@Override
	public void e(String tag, String msg, Throwable throwable) {
		try {
			crashlytics.log(constructLogString(Log.ERROR, tag, msg));
			crashlytics.recordException(throwable);
		} catch(IllegalStateException ex) {
			// crashlytics not initialized.
		}
	}

	@Override
	public void e(Throwable throwable) {
		try {
			crashlytics.recordException(throwable);
		} catch(IllegalStateException ex) {
			// crashlytics not initialized.
		}
	}

	@Override
	public void setUser(String user) {
		try {
			crashlytics.setUserId(user);
		} catch(IllegalStateException ex) {
			// crashlytics not initialized.
		}
	}
}
