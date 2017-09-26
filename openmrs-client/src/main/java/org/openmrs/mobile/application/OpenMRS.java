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

import android.app.Application;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;

import com.raizlabs.android.dbflow.config.FlowManager;

import net.sqlcipher.database.SQLiteDatabase;

import org.greenrobot.eventbus.EventBus;
import org.openmrs.mobile.dagger.ApplicationComponent;
import org.openmrs.mobile.dagger.ApplicationModule;
import org.openmrs.mobile.dagger.DaggerApplicationComponent;
import org.openmrs.mobile.dagger.SyncModule;
import org.openmrs.mobile.net.AuthorizationManager;
import org.openmrs.mobile.data.DatabaseHelper;
import org.openmrs.mobile.net.NetworkManager;
import org.openmrs.mobile.security.SecretKeyGenerator;
import org.openmrs.mobile.sync.SyncManager;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.NetworkUtils;
import org.openmrs.mobile.utilities.StringUtils;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class OpenMRS extends Application {
	private static final String OPENMRS_DIR_NAME = "OpenMRS";
	private static final String OPENMRS_DIR_PATH = File.separator + OPENMRS_DIR_NAME;
	private static String mExternalDirectoryPath;

	private static OpenMRS instance;
	private static boolean ENCRYPTED = true;
	private OpenMRSLogger logger;

	public static OpenMRS getInstance() {
		return instance;
	}

	private AuthorizationManager authorizationManager;
	private SyncManager syncManager;
	private NetworkUtils networkUtils;
	private DatabaseHelper databaseHelper;
	private NetworkManager networkManager;
	private EventBus eventBus;

	@Override
	public void onCreate() {
		super.onCreate();

		generateKey();
		initializeDB();

		injectDependencies();

		// This method only called when app starts up from scratch, so invalidate the last user so they don't get
		// automatically logged in
		authorizationManager.invalidateUser();

		//initializeSQLCipher();
		instance = this;

		if (mExternalDirectoryPath == null) {
			//mExternalDirectoryPath = this.getExternalFilesDir(null).toString();
			String state = Environment.getExternalStorageState();
			if (Environment.MEDIA_MOUNTED.equals(state)) {
				mExternalDirectoryPath = getExternalFilesDir(null).toString();
			} else {
				mExternalDirectoryPath = getFilesDir().toString();
			}
		}

		logger = new OpenMRSLogger();

		syncManager.initializeDataSync();
		networkManager.initializeNetworkReceiver();
	}

	private void injectDependencies() {
		ApplicationComponent applicationComponent = DaggerApplicationComponent.builder()
				.applicationModule(new ApplicationModule(this))
				.syncModule(new SyncModule(this))
				.build();
		syncManager = applicationComponent.syncManager();
		networkUtils = applicationComponent.networkUtils();
		authorizationManager = applicationComponent.authorizationManager();
		databaseHelper = applicationComponent.databaseHelper();
		networkManager = applicationComponent.networkManager();
		eventBus = applicationComponent.eventBus();
	}

	protected void initializeDB() {
		/*FlowConfig config = new FlowConfig.Builder(this)
				.openDatabasesOnInit(true)
				.build();*/


		FlowManager.init(this);
	}

	public SharedPreferences getPreferences() {
		return getSharedPreferences(ApplicationConstants.Preferences.SHARED_PREFERENCES_NAME, MODE_PRIVATE);
	}

	public boolean isUserLoggedOnline() {
		SharedPreferences prefs = getPreferences();
		return prefs.getBoolean(ApplicationConstants.UserKeys.LOGIN, false);
	}

	public void setUserLoggedOnline(boolean firstLogin) {
		SharedPreferences.Editor editor = getPreferences().edit();
		editor.putBoolean(ApplicationConstants.UserKeys.LOGIN, firstLogin);
		editor.commit();
	}

	public String getUsername() {
		SharedPreferences prefs = getPreferences();
		return prefs.getString(ApplicationConstants.UserKeys.USER_NAME, ApplicationConstants.EMPTY_STRING);
	}

	public void setUsername(String username) {
		SharedPreferences.Editor editor = getPreferences().edit();
		editor.putString(ApplicationConstants.UserKeys.USER_NAME, username);
		editor.commit();
	}

	public String getPassword() {
		SharedPreferences prefs = getPreferences();
		return prefs.getString(ApplicationConstants.UserKeys.PASSWORD, ApplicationConstants.EMPTY_STRING);
	}

	public void setPassword(String password) {
		SharedPreferences.Editor editor = getPreferences().edit();
		editor.putString(ApplicationConstants.UserKeys.PASSWORD, password);
		editor.commit();
	}

	public String getServerUrl() {
		SharedPreferences prefs = getPreferences();
		String url = prefs.getString(ApplicationConstants.SERVER_URL, ApplicationConstants.DEFAULT_OPEN_MRS_URL);

		if (!url.endsWith("/")) {
			url += "/";
		}
		return url;
	}

	public void setServerUrl(String serverUrl) {
		SharedPreferences.Editor editor = getPreferences().edit();
		editor.putString(ApplicationConstants.SERVER_URL, serverUrl);
		editor.commit();
	}

	public String getLastLoginServerUrl() {
		SharedPreferences prefs = getPreferences();
		return prefs.getString(ApplicationConstants.LAST_LOGIN_SERVER_URL, ApplicationConstants.EMPTY_STRING);
	}

	public void setLastLoginServerUrl(String url) {
		SharedPreferences.Editor editor = getPreferences().edit();
		editor.putString(ApplicationConstants.LAST_LOGIN_SERVER_URL, url);
		editor.commit();
	}

	public String getSessionToken() {
		SharedPreferences prefs = getPreferences();
		return prefs.getString(ApplicationConstants.SESSION_TOKEN, ApplicationConstants.EMPTY_STRING);
	}

	public void setSessionToken(String serverUrl) {
		SharedPreferences.Editor editor = getPreferences().edit();
		editor.putString(ApplicationConstants.SESSION_TOKEN, serverUrl);
		editor.commit();
	}

	public String getLastSessionToken() {
		SharedPreferences prefs = getPreferences();
		return prefs.getString(ApplicationConstants.LAST_SESSION_TOKEN, ApplicationConstants.EMPTY_STRING);
	}

	public String getAuthorizationToken() {
		SharedPreferences prefs = getPreferences();
		return prefs.getString(ApplicationConstants.AUTHORIZATION_TOKEN, ApplicationConstants.EMPTY_STRING);
	}

	public void setAuthorizationToken(String authorization) {
		SharedPreferences.Editor editor = getPreferences().edit();
		editor.putString(ApplicationConstants.AUTHORIZATION_TOKEN, authorization);
		editor.commit();
	}

	public String getLocation() {
		SharedPreferences prefs = getPreferences();
		return prefs.getString(ApplicationConstants.LOCATION, ApplicationConstants.EMPTY_STRING);
	}

	public void setLocation(String location) {
		SharedPreferences.Editor editor = getPreferences().edit();
		editor.putString(ApplicationConstants.LOCATION, location);
		editor.commit();
	}

	public String getParentLocationUuid() {
		SharedPreferences prefs = getPreferences();
		return prefs.getString(ApplicationConstants.PARENT_LOCATION, ApplicationConstants.EMPTY_STRING);
	}

	public void setParentLocationUuid(String uuid) {
		SharedPreferences.Editor editor = getPreferences().edit();
		editor.putString(ApplicationConstants.PARENT_LOCATION, uuid);
		editor.commit();
	}

	public void saveLocations(String locations) {
		SharedPreferences.Editor editor = getPreferences().edit();
		editor.putString(ApplicationConstants.LOGIN_LOCATIONS, locations);
		editor.commit();
	}

	public String getLocations() {
		SharedPreferences sharedPreferences = instance.getPreferences();
		return sharedPreferences.getString(ApplicationConstants.LOGIN_LOCATIONS, ApplicationConstants
				.EMPTY_STRING);
	}

	public void setPatientUuid(String patientUuid) {
		SharedPreferences.Editor editor = instance.getPreferences().edit();
		editor.putString(ApplicationConstants.BundleKeys.PATIENT_UUID_BUNDLE, patientUuid);
		editor.commit();
	}

	public String getPatientUuid() {
		SharedPreferences sharedPreferences = instance.getPreferences();
		return sharedPreferences.getString(ApplicationConstants.BundleKeys.PATIENT_UUID_BUNDLE, ApplicationConstants
				.EMPTY_STRING);
	}

	public String getVisitUuid() {
		SharedPreferences sharedPreferences = instance.getPreferences();
		return sharedPreferences.getString(ApplicationConstants.BundleKeys.VISIT_UUID_BUNDLE, ApplicationConstants
				.EMPTY_STRING);
	}

	public void setVisitUuid(String visitUuid) {
		SharedPreferences.Editor editor = instance.getPreferences().edit();
		editor.putString(ApplicationConstants.BundleKeys.VISIT_UUID_BUNDLE, visitUuid);
		editor.commit();
	}

	public String getVisitTypeUUID() {
		SharedPreferences prefs = getPreferences();
		return prefs.getString(ApplicationConstants.VISIT_TYPE_UUID, ApplicationConstants.EMPTY_STRING);
	}

	public String getCurrentProviderUUID() {
		SharedPreferences prefs = getPreferences();
		return prefs.getString(ApplicationConstants.BundleKeys.PROVIDER_UUID_BUNDLE, ApplicationConstants.EMPTY_STRING);
	}

	public String getCurrentUserUuid() {
		SharedPreferences prefs = getPreferences();
		return prefs.getString(ApplicationConstants.UserKeys.USER_UUID, ApplicationConstants.EMPTY_STRING);
	}

	public void setCurrentUserUuid(String uuid) {
		SharedPreferences.Editor editor = getPreferences().edit();
		editor.putString(ApplicationConstants.UserKeys.USER_UUID, uuid);
		editor.commit();
	}

	public void setLoginUserUuid(String uuid) {
		SharedPreferences.Editor editor = instance.getPreferences().edit();
		editor.putString(ApplicationConstants.UserKeys.LOGIN_USER_UUID, uuid);
		editor.commit();
	}

	public String getLoginUserUuid() {
		SharedPreferences sharedPreferences = instance.getPreferences();
		return sharedPreferences.getString(ApplicationConstants.UserKeys.LOGIN_USER_UUID, ApplicationConstants.EMPTY_STRING);
	}

	public String getSearchQuery() {
		SharedPreferences prefs = getPreferences();
		return prefs.getString(ApplicationConstants.BundleKeys.PATIENT_QUERY_BUNDLE, ApplicationConstants.EMPTY_STRING);
	}

	public void setVisitTypeUUID(String visitTypeUUID) {
		SharedPreferences.Editor editor = getPreferences().edit();
		editor.putString(ApplicationConstants.VISIT_TYPE_UUID, visitTypeUUID);
		editor.commit();
	}

	private void generateKey() {
		// create database key only if not exist
		if (ApplicationConstants.EMPTY_STRING.equals(getSecretKey())) {
			SharedPreferences.Editor editor = getPreferences().edit();
			String key = SecretKeyGenerator.generateKey();
			editor.putString(ApplicationConstants.SECRET_KEY, key);
			editor.commit();
		}
	}

	public String getSecretKey() {
		SharedPreferences prefs = getPreferences();
		return prefs.getString(ApplicationConstants.SECRET_KEY, ApplicationConstants.EMPTY_STRING);
	}

	public boolean getSyncState() {
		final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		return prefs.getBoolean("sync", true);
	}

	public void setSyncState(boolean enabled) {
		final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean("sync", enabled);
		editor.apply();
	}

	public void setDefaultFormLoadID(String xFormName, String xFormID) {
		SharedPreferences.Editor editor = getPreferences().edit();
		editor.putString(xFormName, xFormID);
		editor.commit();
	}

	public String getDefaultFormLoadID(String xFormName) {
		SharedPreferences prefs = getPreferences();
		return prefs.getString(xFormName, ApplicationConstants.EMPTY_STRING);
	}

	public Date getLastTrimDate() {
		SharedPreferences preferences = getPreferences();
		String dateString = preferences.getString(ApplicationConstants.Preferences.LAST_TRIM_DATE, null);

		Date result = null;
		if (StringUtils.notEmpty(dateString)) {
			try {
				result = DateFormat.getDateTimeInstance().parse(dateString);

			} catch (ParseException e) {
				logger.w("Could not parse last trim date '" + dateString + "'");
				result = null;
			}
		}

		return result;
	}

	public void setLastTrimDate(Date date) {
		SharedPreferences.Editor preferences = getPreferences().edit();

		String value = null;
		if (date != null) {
			value = DateFormat.getDateTimeInstance().format(date);
		}

		preferences.putString(ApplicationConstants.Preferences.LAST_TRIM_DATE, value);
		preferences.apply();
	}

	public void setCurrentUserInformation(Map<String, String> userInformation) {
		SharedPreferences.Editor editor = getPreferences().edit();
		for (Map.Entry<String, String> entry : userInformation.entrySet()) {
			editor.putString(entry.getKey(), entry.getValue());
		}
		editor.commit();
	}

	public Map<String, String> getCurrentLoggedInUserInfo() {
		SharedPreferences prefs = getPreferences();
		Map<String, String> infoMap = new HashMap<String, String>();
		infoMap.put(ApplicationConstants.UserKeys.USER_PERSON_NAME,
				prefs.getString(ApplicationConstants.UserKeys.USER_PERSON_NAME, ApplicationConstants.EMPTY_STRING));
		infoMap.put(ApplicationConstants.UserKeys.USER_UUID,
				prefs.getString(ApplicationConstants.UserKeys.USER_UUID, ApplicationConstants.EMPTY_STRING));
		return infoMap;
	}

	private void clearCurrentLoggedInUserInfo() {
		SharedPreferences prefs = OpenMRS.getInstance().getPreferences();
		SharedPreferences.Editor editor = prefs.edit();
		editor.remove(ApplicationConstants.UserKeys.USER_PERSON_NAME);
		editor.remove(ApplicationConstants.UserKeys.USER_UUID);
	}

	public OpenMRSLogger getOpenMRSLogger() {
		return logger;
	}

	public String getOpenMRSDir() {
		return mExternalDirectoryPath + OPENMRS_DIR_PATH;
	}

	public boolean isRunningHoneycombVersionOrHigher() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
	}

	public boolean isRunningJellyBeanVersionOrHigher() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
	}

	public boolean isRunningKitKatVersionOrHigher() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
	}

	private void initializeSQLCipher() {
		SQLiteDatabase.loadLibs(this);
	}

	public void clearUserPreferencesData() {
		SharedPreferences prefs = OpenMRS.getInstance().getPreferences();
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(ApplicationConstants.LAST_SESSION_TOKEN,
				prefs.getString(ApplicationConstants.SESSION_TOKEN, ApplicationConstants.EMPTY_STRING));
		editor.remove(ApplicationConstants.SESSION_TOKEN);
		editor.remove(ApplicationConstants.AUTHORIZATION_TOKEN);
		editor.remove(ApplicationConstants.BundleKeys.PATIENT_QUERY_BUNDLE);
		editor.remove(ApplicationConstants.LOGIN_LOCATIONS);
		editor.remove(ApplicationConstants.BundleKeys.PATIENT_UUID_BUNDLE);
		editor.remove(ApplicationConstants.BundleKeys.VISIT_UUID_BUNDLE);
		clearCurrentLoggedInUserInfo();
		editor.commit();
	}

	public void requestDataSync() {
		syncManager.requestSync();
	}

	public NetworkUtils getNetworkUtils() {
		return networkUtils;
	}

	public AuthorizationManager getAuthorizationManager() {
		return authorizationManager;
	}
  
	public DatabaseHelper getDatabaseHelper() {
		return databaseHelper;
	}

	public EventBus getEventBus() {
		return eventBus;
	}

	public SyncManager getSyncManager() {
		return syncManager;
	}
}
