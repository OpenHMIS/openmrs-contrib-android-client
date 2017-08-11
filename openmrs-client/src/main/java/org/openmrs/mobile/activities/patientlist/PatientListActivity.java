/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and
 * limitations under the License.
 *
 * Copyright (C) OpenHMIS.  All Rights Reserved.
 */
package org.openmrs.mobile.activities.patientlist;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseActivity;

/**
 * Patient List activity
 */
public class PatientListActivity extends ACBaseActivity {

	private PatientListContract.Presenter patientListPresenter;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getLayoutInflater().inflate(R.layout.activity_patient_list, frameLayout);
		setTitle(R.string.nav_patient_list);

		// create fragment
		PatientListFragment patientListFragment =
				(PatientListFragment)getSupportFragmentManager().findFragmentById(R.id.patientListContentFrame);
		if (patientListFragment == null) {
			patientListFragment = PatientListFragment.newInstance();
		}

		if (!patientListFragment.isActive()) {
			addFragmentToActivity(getSupportFragmentManager(),
					patientListFragment, R.id.patientListContentFrame);
		}

		if (authorizationManager.isUserLoggedIn()) {
			patientListPresenter = new PatientListPresenter(patientListFragment);
		}
	}

	@Override
	public void onBackPressed() {
		if (drawer.isDrawerOpen(GravityCompat.START)) {
			drawer.closeDrawer(GravityCompat.START);
		}
	}
}
