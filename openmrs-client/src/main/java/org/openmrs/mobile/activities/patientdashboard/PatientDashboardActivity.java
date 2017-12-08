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

package org.openmrs.mobile.activities.patientdashboard;

import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.view.Menu;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseActivity;
import org.openmrs.mobile.activities.patientheader.PatientHeaderFragment;
import org.openmrs.mobile.activities.patientheader.PatientHeaderPresenter;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.StringUtils;

public class PatientDashboardActivity extends ACBaseActivity {

	public PatientDashboardContract.Presenter mPresenter;
	private PatientHeaderFragment headerFragment;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getLayoutInflater().inflate(R.layout.activity_patient_dashboard, frameLayout);
		setTitle(R.string.title_patient_dashboard);
		// Create fragment
		PatientDashboardFragment patientDashboardFragment =
				(PatientDashboardFragment)getSupportFragmentManager().findFragmentById(R.id.contentFrame);
		if (patientDashboardFragment == null) {
			patientDashboardFragment = PatientDashboardFragment.newInstance();
		}
		if (!patientDashboardFragment.isActive()) {
			addFragmentToActivity(getSupportFragmentManager(), patientDashboardFragment, R.id.contentFrame);
		}

		Bundle extras = getIntent().getExtras();
		String patientUuid = "";
		if (extras != null) {
			patientUuid = extras.getString(ApplicationConstants.BundleKeys.PATIENT_UUID_BUNDLE);
			if (StringUtils.notEmpty(patientUuid)) {
				// patient header
				headerFragment = (PatientHeaderFragment)getSupportFragmentManager()
						.findFragmentById(R.id.patientHeader);
				if (headerFragment == null) {
					headerFragment = PatientHeaderFragment.newInstance();
				}

				if (!headerFragment.isActive()) {
					addFragmentToActivity(getSupportFragmentManager(), headerFragment, R.id.patientHeader);
				}

				new PatientHeaderPresenter(headerFragment, patientUuid);
				headerFragment.showLastSyncInformation();
			}
		}

		mPresenter = new PatientDashboardPresenter(patientDashboardFragment, openMRS, patientUuid);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		return true;
	}

	public void updateHeaderShadowLine(boolean visible) {
		headerFragment.updateShadowLine(visible);
	}

	@Override
	public void onBackPressed() {
		if (!mPresenter.isLoading()) {
			if (drawer.isDrawerOpen(GravityCompat.START)) {
				drawer.closeDrawer(GravityCompat.START);
			} else {
				super.onBackPressed();
			}
		} else {
			createToast(getString(R.string.pending_save));
		}
	}

	@Override
	protected void onRestart() {
		super.onRestart();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}
}