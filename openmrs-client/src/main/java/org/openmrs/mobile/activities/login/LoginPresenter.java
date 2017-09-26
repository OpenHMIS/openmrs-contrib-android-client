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
import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.data.RequestStrategy;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.openmrs.mobile.utilities.ApplicationConstants.ErrorCodes.AUTH_FAILED;
import static org.openmrs.mobile.utilities.ApplicationConstants.ErrorCodes.INVALID_USERNAME_PASSWORD;
import static org.openmrs.mobile.utilities.ApplicationConstants.ErrorCodes.LOGOUT_DUE_TO_INACTIVITY;
import static org.openmrs.mobile.utilities.ApplicationConstants.ErrorCodes.NO_INTERNET;
import static org.openmrs.mobile.utilities.ApplicationConstants.ErrorCodes.OFFLINE_LOGIN;
import static org.openmrs.mobile.utilities.ApplicationConstants.ErrorCodes.OFFLINE_LOGIN_UNSUPPORTED;
import static org.openmrs.mobile.utilities.ApplicationConstants.ErrorCodes.SERVER_ERROR;

public class LoginPresenter extends BasePresenter implements LoginContract.Presenter {

	private LoginContract.View loginView;
	private OpenMRS openMRS;
	private boolean wipeRequired;
	private AuthorizationManager authorizationManager;
	private SessionDataService loginDataService;
	private LocationDataService locationDataService;
	private UserDataService userService;
	private boolean isFirstAccessOfNewUrl;

	private int startIndex = 0;//Old API, works with indexes not pages
	private int limit = 100;

	public LoginPresenter(LoginContract.View view, OpenMRS openMRS) {
		this.loginView = view;
		this.loginView.setPresenter(this);
		this.openMRS = openMRS;
		this.authorizationManager = openMRS.getAuthorizationManager();

		this.locationDataService = dataAccess().location();
		this.loginDataService = dataAccess().session();
		// the user service requires to be authenticated before it can be used to retrieve user information.
		// it cannot be initialized here since no user/password has been set in the OpenMRS instance
		//this.userService = dataAccess().user();
	}

	@Override
	public void subscribe() {
		//intentionally left blank
	}

	@Override
	public void login(String username, String password, String url, String oldUrl) {
		loginView.hideSoftKeys();
		String storedUserName = openMRS.getUsername();
		String storedServerUrl = openMRS.getServerUrl();

		boolean userNameIsNotStored = storedUserName.equals(ApplicationConstants.EMPTY_STRING);
		boolean serverUrlIsNotStored = storedServerUrl.equals(ApplicationConstants.EMPTY_STRING);
		boolean enteredUserNameMatchesWhatIsStored = storedUserName.equals(username);
		boolean oldUrlMatchesWhatIsStored = storedServerUrl.equals(oldUrl);
		boolean oldUrlIsEmpty = oldUrl.equals(ApplicationConstants.EMPTY_STRING);

		if (wipeRequired) {
			loginView.showWarningDialog();
		} else if ((userNameIsNotStored || enteredUserNameMatchesWhatIsStored)
				&& (serverUrlIsNotStored || oldUrlMatchesWhatIsStored || oldUrlIsEmpty)) {
			if (!oldUrlMatchesWhatIsStored || userNameIsNotStored || serverUrlIsNotStored) {
				isFirstAccessOfNewUrl = true;
			}
			authenticateUser(username, password, url, wipeRequired);
		} else {
			loginView.showWarningDialog();
		}
	}

	@Override
	public void authenticateUser(final String username, final String password, final String url,
			final boolean wipeDatabase) {
		loginView.setProgressBarVisibility(true);
		RestServiceBuilder.setloginUrl(url);

		if (openMRS.getNetworkUtils().isOnline()) {
			wipeRequired = wipeDatabase;

			DataService.GetCallback<Session> loginUserCallback = new DataService.GetCallback<Session>() {
				@Override
				public void onCompleted(Session session) {
					if (session != null && session.isAuthenticated()) {
						if (wipeRequired) {
							openMRS.deleteDatabase(AppDatabase.NAME);
							isFirstAccessOfNewUrl = true;
							setData(session.getSessionId(), url, username, password);

							wipeRequired = false;
						}

						if (authorizationManager.isUserNameOrServerEmpty()) {
							setData(session.getSessionId(), url, username, password);
						} else {
							openMRS.setSessionToken(session.getSessionId());
						}

						setLogin(true, url);
						RestServiceBuilder.applyDefaultBaseUrl();
						openMRS.setLoginUserUuid(session.getUser().getUuid());

						fetchFullUserInformation(session.getUser().getUuid());
						loginView.userAuthenticated(isFirstAccessOfNewUrl);
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
			if (openMRS.isUserLoggedOnline() && url.equals(openMRS.getLastLoginServerUrl())) {
				RestServiceBuilder.applyDefaultBaseUrl();
				loginView.setProgressBarVisibility(false);
				if (openMRS.getUsername().equals(username) && openMRS.getPassword().equals(password)) {
					openMRS.setSessionToken(openMRS.getLastSessionToken());
					loginView.showMessage(OFFLINE_LOGIN);
					loginView.userAuthenticated(isFirstAccessOfNewUrl);
					loginView.finishLoginActivity();
				} else {
					loginView.showMessage(AUTH_FAILED);
				}
			} else if (openMRS.getNetworkUtils().hasNetwork()) {
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
				if (user != null) {
					userService.save(user);
					Map<String, String> userInfo = new HashMap<>();
					userInfo.put(ApplicationConstants.UserKeys.USER_PERSON_NAME, user.getPerson().getDisplay());
					userInfo.put(ApplicationConstants.UserKeys.USER_UUID, user.getPerson().getUuid());
					openMRS.setCurrentUserInformation(userInfo);
					openMRS.setLoginUserUuid(ApplicationConstants.EMPTY_STRING);
				}
			}

			@Override
			public void onError(Throwable t) {
				loginView.showMessage(SERVER_ERROR);
			}
		};

		QueryOptions options = new QueryOptions.Builder()
				.includeInactive(true)
				.customRepresentation(RestConstants.Representations.FULL)
				.requestStrategy(RequestStrategy.REMOTE_THEN_LOCAL)
				.build();

		// initialize here after setting the username and password
		userService = dataAccess().user();
		userService.getByUuid(uuid, options, fetchUserCallback);
	}

	@Override
	public void saveLocationsInPreferences(List<HashMap<String, String>> locationList, int selectedItemPosition) {
		openMRS.setLocation(locationList.get(selectedItemPosition).get("uuid"));
		openMRS.setParentLocationUuid(locationList.get(selectedItemPosition).get("parentlocationuuid"));
		openMRS.saveLocations(new Gson().toJson(locationList));

	}

	@Override
	public void loadLocations(String url) {
		loginView.setProgressBarVisibility(true);
		RestServiceBuilder.setBaseUrl(url);
		DataService.GetCallback<List<Location>> locationDataServiceCallback =
				new DataService.GetCallback<List<Location>>() {
					@Override
					public void onCompleted(List<Location> locations) {
						openMRS.setServerUrl(url);
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
		openMRS.setSessionToken(sessionToken);
		openMRS.setServerUrl(url);
		openMRS.setUsername(username);
		openMRS.setPassword(password);
	}

	private void setLogin(boolean isLogin, String serverUrl) {
		openMRS.setUserLoggedOnline(isLogin);
		openMRS.setLastLoginServerUrl(serverUrl);
	}

	public void userWasLoggedOutDueToInactivity() {
		loginView.showMessage(LOGOUT_DUE_TO_INACTIVITY);
	}
}
