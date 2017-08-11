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

import android.os.Bundle;
import android.view.Menu;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseActivity;

public class LoginActivity extends ACBaseActivity {

	public LoginContract.Presenter presenter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		// Create fragment
		LoginFragment loginFragment =
				(LoginFragment)getSupportFragmentManager().findFragmentById(R.id.loginContentFrame);
		if (loginFragment == null) {
			loginFragment = LoginFragment.newInstance();
		}
		if (!loginFragment.isActive()) {
			addFragmentToActivity(getSupportFragmentManager(),
					loginFragment, R.id.loginContentFrame);
		}

		presenter = new LoginPresenter(loginFragment, openMRS);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		return true;
	}
}
