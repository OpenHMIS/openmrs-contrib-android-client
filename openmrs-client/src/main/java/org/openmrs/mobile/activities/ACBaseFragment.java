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

import android.support.v4.app.Fragment;
import android.view.Menu;

import org.openmrs.mobile.R;
import org.openmrs.mobile.utilities.ApplicationConstants;

public abstract class ACBaseFragment<T extends BasePresenterContract> extends Fragment implements BaseView<T> {

	protected T mPresenter;

	@Override
	public void setPresenter(T presenter) {
		mPresenter = presenter;
	}

	public boolean isActive() {
		return isAdded();
	}

	@Override
	public void onResume() {
		super.onResume();
		if (mPresenter != null) {
			mPresenter.subscribe();
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		if (mPresenter != null) {
			mPresenter.unsubscribe();
		}
	}

	public void createSnackbar(String message) {
		((ACBaseActivity)getActivity()).createSnackbar(message);
	}

	public void showError(int errorCode) {

		String message = "";

		switch (errorCode) {
			case ApplicationConstants.ErrorCodes.INVALID_URL:
				message = getString(R.string.invalid_url_dialog_message);
				break;
			case ApplicationConstants.ErrorCodes.INVALID_USERNAME_PASSWORD:
				message = getString(R.string.invalid_login_or_password_message);
				break;
			case ApplicationConstants.ErrorCodes.SERVER_ERROR:
				message = getString(R.string.server_error_dialog_message);
				break;
			case ApplicationConstants.ErrorCodes.OFFLINE_LOGIN:
				message = getString(R.string.logged_in_in_offline_mode);
				break;
			case ApplicationConstants.ErrorCodes.AUTH_FAILED:
				message = getString(R.string.auth_failed_dialog_message);
				break;
			case ApplicationConstants.ErrorCodes.OFFLINE_LOGIN_UNSUPPORTED:
				message = getString(R.string.auth_failed_dialog_message);
				break;
			case ApplicationConstants.ErrorCodes.NO_INTERNET:
				message = getString(R.string.no_internet_conn_dialog_message);
				break;
			case ApplicationConstants.ErrorCodes.USER_NOT_FOUND:
				message = getString(R.string.err_fetching_user_data);
				break;
			case ApplicationConstants.ErrorCodes.LOGOUT_DUE_TO_INACTIVITY:
				message = getString(R.string.logout_due_to_inactivity);
				break;
		}

		createSnackbar(message);
	}

	public void showError(String message) {
		createSnackbar(message);
	}
}
