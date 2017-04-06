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

import android.content.Context;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.BasePresenter;
import org.openmrs.mobile.api.RestApi;
import org.openmrs.mobile.api.RestServiceBuilder;
import org.openmrs.mobile.api.UserService;
import org.openmrs.mobile.api.retrofit.VisitApi;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.application.OpenMRSLogger;
import org.openmrs.mobile.dao.LocationDAO;
import org.openmrs.mobile.databases.OpenMRSSQLiteOpenHelper;
import org.openmrs.mobile.listeners.retrofit.GetVisitTypeCallbackListener;
import org.openmrs.mobile.models.Location;
import org.openmrs.mobile.models.Results;
import org.openmrs.mobile.models.Session;
import org.openmrs.mobile.models.VisitType;
import org.openmrs.mobile.net.AuthorizationManager;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.NetworkUtils;
import org.openmrs.mobile.utilities.StringUtils;
import org.openmrs.mobile.utilities.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.android.schedulers.AndroidSchedulers;

public class LoginPresenter extends BasePresenter implements LoginContract.Presenter {

    private OpenMRS mOpenMRS;
    private LoginActivity loginActivity;
    private RestApi restApi;
    private VisitApi visitApi;
    private LocationDAO locationDAO;
    private boolean mWipeRequired;
    private OpenMRSLogger mLogger;
    private AuthorizationManager authorizationManager;
    private UserService userService;


    public LoginPresenter(LoginActivity loginActivity, OpenMRS openMRS) {
        this.loginActivity = loginActivity;
        this.mOpenMRS = openMRS;
        this.restApi = RestServiceBuilder.createService(RestApi.class);
        this.locationDAO = new LocationDAO();
        this.mLogger = openMRS.getOpenMRSLogger();
        this.authorizationManager = new AuthorizationManager();
        this.visitApi = new VisitApi();
        this.userService = new UserService();
    }

    @Override
    public void subscribe() {

    }

    @Override
    public void authenticateUser(String username, String password, String url) {
        authenticateUser(username, password, url, mWipeRequired);
    }

    @Override
    public void authenticateUser(String username, String password, String url, boolean wipeDatabase) {
        loginActivity.showLoadingAnimation();
        if (NetworkUtils.isOnline()) {
            mWipeRequired = wipeDatabase;
            RestApi restApi = RestServiceBuilder.createService(RestApi.class, username, password);
            Call<Session> call = restApi.getSession();
            call.enqueue(new Callback<Session>() {
                @Override
                public void onResponse(Call<Session> call, Response<Session> response) {
                    if (response.isSuccessful()) {
                        mLogger.d(response.body().toString());
                        Session session = response.body();
                        if (session.isAuthenticated()) {
                            if (wipeDatabase) {
                                mOpenMRS.deleteDatabase(OpenMRSSQLiteOpenHelper.DATABASE_NAME);
                                setData(session.getSessionId(), url, username, password);
                                mWipeRequired = false;
                            }
                            if (authorizationManager.isUserNameOrServerEmpty()) {
                                setData(session.getSessionId(), url, username, password);
                            } else {
                                mOpenMRS.setSessionToken(session.getSessionId());
                            }

                            visitApi.getVisitType(new GetVisitTypeCallbackListener() {
                                @Override
                                public void onGetVisitTypeResponse(VisitType visitType) {
                                    OpenMRS.getInstance().setVisitTypeUUID(visitType.getUuid());
                                }

                                @Override
                                public void onResponse() {
                                    // This method is intentionally empty
                                }

                                @Override
                                public void onErrorResponse(String errorMessage) {
                                    loginActivity.showToast("Failed to fetch visit type",
                                            ToastUtil.ToastType.ERROR);
                                }
                            });
                            setLogin(true, url);
                            userService.updateUserInformation(username);

                            loginActivity.userAuthenticated();
                            loginActivity.finishLoginActivity();
                        } else {
                            loginActivity.hideLoadingAnimation();
                            loginActivity.showInvalidLoginOrPasswordSnackbar();
                        }
                    } else {
                        loginActivity.hideLoadingAnimation();
                        loginActivity.showToast(response.message(), ToastUtil.ToastType.ERROR);
                    }
                }

                @Override
                public void onFailure(Call<Session> call, Throwable t) {
                    loginActivity.hideLoadingAnimation();
                    loginActivity.showToast(t.getMessage(), ToastUtil.ToastType.ERROR);
                }
            });
        } else {
            if (mOpenMRS.isUserLoggedOnline() && url.equals(mOpenMRS.getLastLoginServerUrl())) {
                if (mOpenMRS.getUsername().equals(username) && mOpenMRS.getPassword().equals(password)) {
                    mOpenMRS.setSessionToken(mOpenMRS.getLastSessionToken());
                    loginActivity.showToast("LoggedIn in offline mode.", ToastUtil.ToastType.NOTICE);
                    loginActivity.userAuthenticated();
                    loginActivity.finishLoginActivity();
                } else {
                    loginActivity.hideLoadingAnimation();
                    loginActivity.showToast(R.string.auth_failed_dialog_message,
                            ToastUtil.ToastType.ERROR);
                }
            } else if (NetworkUtils.hasNetwork()) {
                loginActivity.showToast(R.string.offline_mode_unsupported_in_first_login,
                        ToastUtil.ToastType.ERROR);
                loginActivity.hideLoadingAnimation();
            } else {
                loginActivity.showToast(R.string.no_internet_conn_dialog_message,
                        ToastUtil.ToastType.ERROR);
                loginActivity.hideLoadingAnimation();
            }
        }
    }

    @Override
    public void login(String username, String password, String url, String oldUrl) {
        if (validateLoginFields(username, password, url)) {
            loginActivity.hideSoftKeys();
            if ((!mOpenMRS.getUsername().equals(ApplicationConstants.EMPTY_STRING) &&
                    !mOpenMRS.getUsername().equals(username)) ||
                    ((!mOpenMRS.getServerUrl().equals(ApplicationConstants.EMPTY_STRING) &&
                            !mOpenMRS.getServerUrl().equals(oldUrl))) ||
                    mWipeRequired) {
                loginActivity.showWarningDialog();
            } else {
                authenticateUser(username, password, url);
            }
        }
    }

    @Override
    public void saveLocationsToDatabase(List<Location> locationList, String selectedLocation) {

    }

    @Override
    public void loadLocations(String url) {
        loginActivity.showLocationLoadingAnimation();
        if (NetworkUtils.hasNetwork()) {
            String locationEndPoint = url + ApplicationConstants.API.REST_ENDPOINT + "location";
            Call<Results<Location>> call =
                    restApi.getLocations(locationEndPoint, "Login Location", "full");
            call.enqueue(new Callback<Results<Location>>() {
                @Override
                public void onResponse(Call<Results<Location>> call, Response<Results<Location>> response) {
                    if (response.isSuccessful()) {
                        RestServiceBuilder.changeBaseUrl(url.trim());
                        mOpenMRS.setServerUrl(url);
                        loginActivity.initLoginForm(response.body().getResults(), url);
                        loginActivity.startFormListService();
                        loginActivity.setLocationErrorOccurred(false);
                    } else {
                        loginActivity.showInvalidURLSnackbar("Failed to fetch server's locations");
                        loginActivity.setLocationErrorOccurred(true);
                        loginActivity.initLoginForm(new ArrayList<Location>(), url);
                    }
                    loginActivity.hideUrlLoadingAnimation();
                }

                @Override
                public void onFailure(Call<Results<Location>> call, Throwable t) {
                    loginActivity.hideUrlLoadingAnimation();
                    loginActivity.showInvalidURLSnackbar(t.getMessage());
                    loginActivity.initLoginForm(new ArrayList<Location>(), url);
                    loginActivity.setLocationErrorOccurred(true);
                }
            });
        } else {
            addSubscription(locationDAO.getLocations()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(locations -> {
                        if (locations.size() > 0) {
                            loginActivity.initLoginForm(locations, url);
                            loginActivity.setLocationErrorOccurred(false);
                        } else {
                            loginActivity.showToast("Network not available.", ToastUtil.ToastType.ERROR);
                            loginActivity.setLocationErrorOccurred(true);
                        }
                        loginActivity.hideLoadingAnimation();
                    }));
        }
    }

    private boolean validateLoginFields(String username, String password, String url) {
        return StringUtils.notEmpty(username) || StringUtils.notEmpty(password) || StringUtils.notEmpty(url);
    }

    private void setData(String sessionToken,String url, String username, String password) {
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
