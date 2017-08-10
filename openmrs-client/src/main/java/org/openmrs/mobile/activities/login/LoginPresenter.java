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

import com.google.gson.Gson;

import org.openmrs.mobile.activities.BasePresenter;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.data.DataService;
import org.openmrs.mobile.data.PagingInfo;
import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.data.db.AppDatabase;
import org.openmrs.mobile.data.impl.LocationDataService;
import org.openmrs.mobile.data.impl.SessionDataService;
import org.openmrs.mobile.data.impl.UserDataService;
import org.openmrs.mobile.data.rest.RestConstants;
import org.openmrs.mobile.data.rest.retrofit.RestServiceBuilder;
import org.openmrs.mobile.models.Location;
import org.openmrs.mobile.models.Session;
import org.openmrs.mobile.models.User;
import org.openmrs.mobile.net.AuthorizationManager;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.NetworkUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.openmrs.mobile.utilities.ApplicationConstants.ErrorCodes.AUTH_FAILED;
import static org.openmrs.mobile.utilities.ApplicationConstants.ErrorCodes.INVALID_USERNAME_PASSWORD;
import static org.openmrs.mobile.utilities.ApplicationConstants.ErrorCodes.NO_INTERNET;
import static org.openmrs.mobile.utilities.ApplicationConstants.ErrorCodes.OFFLINE_LOGIN;
import static org.openmrs.mobile.utilities.ApplicationConstants.ErrorCodes.OFFLINE_LOGIN_UNSUPPORTED;
import static org.openmrs.mobile.utilities.ApplicationConstants.ErrorCodes.SERVER_ERROR;
import static org.openmrs.mobile.utilities.ApplicationConstants.ErrorCodes.USER_NOT_FOUND;

public class LoginPresenter extends BasePresenter implements LoginContract.Presenter {

	private LoginContract.View loginView;
	private OpenMRS mOpenMRS;
	private boolean mWipeRequired;
	private AuthorizationManager authorizationManager;
	private SessionDataService loginDataService;
	private LocationDataService locationDataService;
	private UserDataService userService;

	private int startIndex = 0;//Old API, works with indexes not pages
	private int limit = 100;

	public LoginPresenter(LoginContract.View view, OpenMRS mOpenMRS) {
		this.loginView = view;
		this.loginView.setPresenter(this);
		this.mOpenMRS = mOpenMRS;
		this.authorizationManager = new AuthorizationManager();

		this.locationDataService = dataAccess().location();
		this.loginDataService = dataAccess().session();
		this.userService = dataAccess().user();
	}

	@Override
	public void subscribe() {
		//intentionally left blank
	}

	@Override
	public void login(String username, String password, String url, String oldUrl) {
		loginView.hideSoftKeys();
		if ((!mOpenMRS.getUsername().equals(ApplicationConstants.EMPTY_STRING) &&
				!mOpenMRS.getUsername().equals(username)) ||
				((!mOpenMRS.getServerUrl().equals(ApplicationConstants.EMPTY_STRING) &&
						!mOpenMRS.getServerUrl().equals(oldUrl))) ||
				mWipeRequired) {
			loginView.showWarningDialog();
		} else {
			authenticateUser(username, password, url, mWipeRequired);
		}
	}

	@Override
	public void authenticateUser(final String username, final String password, final String url,
			final boolean wipeDatabase) {
		loginView.setProgressBarVisibility(true);
		RestServiceBuilder.setloginUrl(url);

		if (mOpenMRS.getNetworkUtils().isOnline()) {
			mWipeRequired = wipeDatabase;
			DataService.GetCallback<List<User>> loginUsersFoundCallback =
					new DataService.GetCallback<List<User>>() {
						@Override
						public void onCompleted(List<User> users) {
							boolean matchFound = false;
							if (!users.isEmpty()) {
								for (User user : users) {
									if (user.getDisplay().toUpperCase().equals(username.toUpperCase())) {
										matchFound = true;
										fetchFullUserInformation(user.getUuid());
									}
								}
							}

							if (!matchFound) {
								loginView.showMessage(USER_NOT_FOUND);
							}
						}

						@Override
						public void onError(Throwable t) {
							loginView.showMessage(SERVER_ERROR);
						}
					};

			PagingInfo pagingInfo = new PagingInfo(startIndex, limit);
			DataService.GetCallback<Session> loginUserCallback = new DataService.GetCallback<Session>() {
				@Override
				public void onCompleted(Session session) {
					if (session != null && session.isAuthenticated()) {
						if (wipeDatabase) {
							mOpenMRS.deleteDatabase(AppDatabase.NAME);
							setData(session.getSessionId(), url, username, password);
							mWipeRequired = false;
						}

						if (authorizationManager.isUserNameOrServerEmpty()) {
							setData(session.getSessionId(), url, username, password);
						} else {
							mOpenMRS.setSessionToken(session.getSessionId());
						}

						setLogin(true, url);
						RestServiceBuilder.applyDefaultBaseUrl();
						//Instantiate the user service  here to use our new session
						//userService = new UserDataService();
						userService.getByUsername(username, QueryOptions.FULL_REP, pagingInfo,
								loginUsersFoundCallback);
						loginView.userAuthenticated();
						loginView.finishLoginActivity();

					} else {
						loginView.showMessage(INVALID_USERNAME_PASSWORD);
					}
					loginView.setProgressBarVisibility(false);

				}

				@Override
				public void onError(Throwable t) {
					t.printStackTrace();
					loginView.setProgressBarVisibility(false);
					loginView.showMessage(SERVER_ERROR);
				}
			};

			loginDataService.getSession(url, username, password, loginUserCallback);
		} else {
			if (mOpenMRS.isUserLoggedOnline() && url.equals(mOpenMRS.getLastLoginServerUrl())) {
				loginView.setProgressBarVisibility(false);
				if (mOpenMRS.getUsername().equals(username) && mOpenMRS.getPassword().equals(password)) {
					mOpenMRS.setSessionToken(mOpenMRS.getLastSessionToken());
					loginView.showMessage(OFFLINE_LOGIN);
					loginView.userAuthenticated();
					loginView.finishLoginActivity();
				} else {
					loginView.showMessage(AUTH_FAILED);
				}
			} else if (mOpenMRS.getNetworkUtils().hasNetwork()) {
				loginView.showMessage(OFFLINE_LOGIN_UNSUPPORTED);
				loginView.setProgressBarVisibility(false);

			} else {
				loginView.showMessage(NO_INTERNET);
				loginView.setProgressBarVisibility(false);

			}
		}
	}

	private void fetchFullUserInformation(String uuid) {
		DataService.GetCallback<User> fetchUserCallback = new DataService.GetCallback<User>() {
			@Override
			public void onCompleted(User user) {
				Map<String, String> userInfo = new HashMap<>();
				userInfo.put(ApplicationConstants.UserKeys.USER_PERSON_NAME, user.getPerson().getDisplay());
				userInfo.put(ApplicationConstants.UserKeys.USER_UUID, user.getPerson().getUuid());
				OpenMRS.getInstance().setCurrentUserInformation(userInfo);
			}

			@Override
			public void onError(Throwable t) {
				loginView.showMessage(SERVER_ERROR);
			}
		};

		userService.getByUuid(uuid, QueryOptions.INCLUDE_ALL_FULL_REP, fetchUserCallback);
	}

	@Override
	public void saveLocationsInPreferences(List<HashMap<String, String>> locationList, int selectedItemPosition) {
		mOpenMRS.setLocation(locationList.get(selectedItemPosition).get("uuid"));
		mOpenMRS.setParentLocationUuid(locationList.get(selectedItemPosition).get("parentlocationuuid"));
		mOpenMRS.saveLocations(new Gson().toJson(locationList));

	}

	@Override
	public void loadLocations(String url) {
		loginView.setProgressBarVisibility(true);
		RestServiceBuilder.setBaseUrl(url);
		DataService.GetCallback<List<Location>> locationDataServiceCallback = new DataService.GetCallback<List<Location>>
				() {
			@Override
			public void onCompleted(List<Location> locations) {
				mOpenMRS.setServerUrl(url);
				loginView.updateLoginFormLocations(locations, url);
			}

			@Override
			public void onError(Throwable t) {
				loginView.showMessage(SERVER_ERROR);
			}
		};

		try {
			locationDataService.getLoginLocations(url, locationDataServiceCallback);
		} catch (IllegalArgumentException ex) {
			loginView.setProgressBarVisibility(false);
			loginView.showMessage(SERVER_ERROR);
		}
	}

	private void setData(String sessionToken, String url, String username, String password) {
		mOpenMRS.setSessionToken(sessionToken);
		mOpenMRS.setServerUrl(url);
		mOpenMRS.setUsername(username);
		mOpenMRS.setPassword(password);
	}

	private void setLogin(boolean isLogin, String serverUrl) {
		mOpenMRS.setUserLoggedOnline(isLogin);
		mOpenMRS.setLastLoginServerUrl(serverUrl);
	}
}
