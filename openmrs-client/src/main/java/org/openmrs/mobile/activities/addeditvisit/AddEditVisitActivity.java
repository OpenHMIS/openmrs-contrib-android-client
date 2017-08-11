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
package org.openmrs.mobile.activities.addeditvisit;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseActivity;
import org.openmrs.mobile.activities.patientheader.PatientHeaderFragment;
import org.openmrs.mobile.activities.patientheader.PatientHeaderPresenter;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.StringUtils;
import org.openmrs.mobile.utilities.TabUtil;
import org.openmrs.mobile.utilities.ToastUtil;

public class AddEditVisitActivity extends ACBaseActivity {

	public AddEditVisitContract.Presenter addEditVisitPresenter;
	private String patientUuid, visitUuid, providerUuid, visitStopDate;
	private Bundle extras;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getLayoutInflater().inflate(R.layout.activity_add_edit_visit, frameLayout);

		Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
		toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white);
		toolbar.setTitle(ApplicationConstants.EMPTY_STRING);
		setSupportActionBar(toolbar);
		if (getSupportActionBar() != null) {
			getSupportActionBar().setElevation(0);
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			getSupportActionBar().setDisplayShowHomeEnabled(true);
		}
		this.extras = getIntent().getExtras();
		if (extras != null) {
			this.patientUuid =
					extras.getString(ApplicationConstants.BundleKeys.PATIENT_UUID_BUNDLE, ApplicationConstants
							.EMPTY_STRING);
			this.visitUuid =
					extras.getString(ApplicationConstants.BundleKeys.VISIT_UUID_BUNDLE, ApplicationConstants.EMPTY_STRING);
			this.providerUuid = extras.getString(ApplicationConstants.BundleKeys.PROVIDER_UUID_BUNDLE,
					ApplicationConstants.EMPTY_STRING);
			this.visitStopDate = extras.getString(ApplicationConstants.BundleKeys.VISIT_CLOSED_DATE, ApplicationConstants
					.EMPTY_STRING);
			if (StringUtils.notEmpty(patientUuid)) {
				AddEditVisitFragment addEditVisitFragment =
						(AddEditVisitFragment)getSupportFragmentManager().findFragmentById(R.id.addeditVisitContentFrame);
				if (addEditVisitFragment == null) {
					addEditVisitFragment = AddEditVisitFragment.newInstance();
				}

				if (!addEditVisitFragment.isActive()) {
					addFragmentToActivity(getSupportFragmentManager(), addEditVisitFragment, R.id.addeditVisitContentFrame);
				}

				addEditVisitPresenter = new AddEditVisitPresenter(addEditVisitFragment, patientUuid, visitUuid,
						extras.getBoolean(ApplicationConstants.BundleKeys.END_VISIT, false));

				// patient header
				PatientHeaderFragment headerFragment = (PatientHeaderFragment)getSupportFragmentManager()
						.findFragmentById(R.id.patientHeader);
				if (headerFragment == null) {
					headerFragment = PatientHeaderFragment.newInstance();
				}

				if (!headerFragment.isActive()) {
					addFragmentToActivity(getSupportFragmentManager(), headerFragment, R.id.patientHeader);
				}

				new PatientHeaderPresenter(headerFragment, patientUuid);

			} else {
				ToastUtil.error(getString(R.string.no_patient_selected));
			}
		}
	}

	@Override
	public void onConfigurationChanged(final Configuration config) {
		super.onConfigurationChanged(config);
		TabUtil.setHasEmbeddedTabs(getSupportActionBar(), getWindowManager(),
				TabUtil.MIN_SCREEN_WIDTH_FOR_VISITDETAILSACTIVITY);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(ApplicationConstants.BundleKeys.PATIENT_UUID_BUNDLE, patientUuid);
		outState.putString(ApplicationConstants.BundleKeys.VISIT_UUID_BUNDLE, visitUuid);
		outState.putString(ApplicationConstants.BundleKeys.PROVIDER_UUID_BUNDLE, providerUuid);
		outState.putString(ApplicationConstants.BundleKeys.VISIT_CLOSED_DATE, visitStopDate);
	}

	@Override
	public void onBackPressed() {
		if (drawer.isDrawerOpen(GravityCompat.START)) {
			drawer.closeDrawer(GravityCompat.START);
		}
		super.onBackPressed();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
			case android.R.id.home:
				onBackPressed();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
}
