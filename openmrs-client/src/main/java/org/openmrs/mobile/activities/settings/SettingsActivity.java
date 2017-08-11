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

package org.openmrs.mobile.activities.settings;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.view.Menu;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseActivity;

public class SettingsActivity extends ACBaseActivity {

	public SettingsContract.Presenter mPresenter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getLayoutInflater().inflate(R.layout.activity_settings, frameLayout);
		setTitle(R.string.nav_settings);
		// Create fragment
		SettingsFragment settingsFragment =
				(SettingsFragment)getSupportFragmentManager().findFragmentById(R.id.contentFrame);
		if (settingsFragment == null) {
			settingsFragment = SettingsFragment.newInstance();
		}
		if (!settingsFragment.isActive()) {
			addFragmentToActivity(getSupportFragmentManager(), settingsFragment, R.id.contentFrame);
		}

		mPresenter = new SettingsPresenter(settingsFragment, openMRS);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
	}

	@Override
	public void onBackPressed() {
		if (drawer.isDrawerOpen(GravityCompat.START)) {
			drawer.closeDrawer(GravityCompat.START);
		} else {
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_HOME);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		return true;
	}

}
