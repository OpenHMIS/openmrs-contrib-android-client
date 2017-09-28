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

package org.openmrs.mobile.activities.visit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import net.yanzm.mth.MaterialTabHost;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseActivity;
import org.openmrs.mobile.activities.addeditvisit.AddEditVisitActivity;
import org.openmrs.mobile.activities.auditdata.AuditDataActivity;
import org.openmrs.mobile.activities.capturevitals.CaptureVitalsActivity;
import org.openmrs.mobile.activities.patientdashboard.PatientDashboardActivity;
import org.openmrs.mobile.activities.patientheader.PatientHeaderContract;
import org.openmrs.mobile.activities.patientheader.PatientHeaderFragment;
import org.openmrs.mobile.activities.patientheader.PatientHeaderPresenter;
import org.openmrs.mobile.activities.visit.detail.VisitDetailsFragment;
import org.openmrs.mobile.activities.visit.detail.VisitDetailsPresenter;
import org.openmrs.mobile.activities.visit.visitphoto.VisitPhotoFragment;
import org.openmrs.mobile.activities.visit.visitphoto.VisitPhotoPresenter;
import org.openmrs.mobile.activities.visit.visittasks.VisitTasksFragment;
import org.openmrs.mobile.activities.visit.visittasks.VisitTasksPresenter;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.TabUtil;

import java.util.ArrayList;

public class VisitActivity extends ACBaseActivity {
	private static final int END_VISIT_RESULT = 1;
	public VisitContract.VisitDetailsMainPresenter visitDetailsMainPresenter;
	private PatientHeaderContract.Presenter patientHeaderPresenter;
	private String patientUuid;
	private String visitUuid;
	private String providerUuid, visitClosedDate;
	private Intent intent;
	private OpenMRS instance = OpenMRS.getInstance();
	private SharedPreferences sharedPreferences = instance.getPreferences();
	private FloatingActionButton captureVitalsButton, endVisitButton, editVisitButton, auditData;
	private FloatingActionMenu visitDetailsMenu;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getLayoutInflater().inflate(R.layout.activity_visit_details, frameLayout);

		Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
		toolbar.setTitle(R.string.nav_visit_details);
		toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white);
		setSupportActionBar(toolbar);
		if (getSupportActionBar() != null) {
			getSupportActionBar().setElevation(0);
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			getSupportActionBar().setDisplayShowHomeEnabled(true);
		}

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			patientUuid = extras.getString(ApplicationConstants.BundleKeys.PATIENT_UUID_BUNDLE);
			visitUuid = extras.getString(ApplicationConstants.BundleKeys.VISIT_UUID_BUNDLE);
			providerUuid = OpenMRS.getInstance().getCurrentProviderUUID();
			visitClosedDate = extras.getString(ApplicationConstants.BundleKeys.VISIT_CLOSED_DATE);
			initViewPager(new VisitPageAdapter(getSupportFragmentManager(), patientUuid, visitUuid, providerUuid,
					visitClosedDate));

			// patient header
			if (patientHeaderPresenter == null) {
				PatientHeaderFragment headerFragment = (PatientHeaderFragment)getSupportFragmentManager()
						.findFragmentById(R.id.patientHeader);
				if (headerFragment == null) {
					headerFragment = PatientHeaderFragment.newInstance();
				}

				if (!headerFragment.isActive()) {
					addFragmentToActivity(getSupportFragmentManager(), headerFragment, R.id.patientHeader);
				}

				patientHeaderPresenter = new PatientHeaderPresenter(headerFragment, patientUuid);
			}

		}

		captureVitalsButton = (FloatingActionButton)findViewById(R.id.capture_vitals);
		auditData = (FloatingActionButton)findViewById(R.id.auditDataForm);
		endVisitButton = (FloatingActionButton)findViewById(R.id.end_visit);
		editVisitButton = (FloatingActionButton)findViewById(R.id.edit_visit);
		visitDetailsMenu = (FloatingActionMenu)findViewById(R.id.visitDetailsMenu);
		visitDetailsMenu.setClosedOnTouchOutside(true);

		if (visitClosedDate != null && !visitClosedDate.isEmpty()) {
			captureVitalsButton.setVisibility(View.GONE);
			endVisitButton.setVisibility(View.GONE);
		}

		initializeListeners(endVisitButton, editVisitButton, captureVitalsButton, auditData);
	}

	private void initViewPager(VisitPageAdapter visitPageAdapter) {
		MaterialTabHost tabHost = (MaterialTabHost)findViewById(R.id.visitDetailsTabHost);
		tabHost.setType(MaterialTabHost.Type.FullScreenWidth);
		for (int i = 0; i < visitPageAdapter.getCount(); i++) {
			tabHost.addTab(getTabNames().get(i).toUpperCase());
		}
		final ViewPager viewPager = (ViewPager)findViewById(R.id.visitDetailsPager);
		viewPager.setOffscreenPageLimit(visitPageAdapter.getCount() - 1);
		viewPager.setAdapter(visitPageAdapter);
		viewPager.addOnPageChangeListener(tabHost);
		tabHost.setOnTabChangeListener(new MaterialTabHost.OnTabChangeListener() {
			@Override
			public void onTabSelected(int position) {
				viewPager.setCurrentItem(position);
			}
		});
	}

	private ArrayList<String> getTabNames() {
		ArrayList<String> tabNames = new ArrayList<>();
		tabNames.add(getString(R.string.visit_scroll_tab_details_label));
		tabNames.add(getString(R.string.visi_scroll_tab_visit_tasks_label));
		tabNames.add(getString(R.string.visit_scroll_tab_visit_images_label));
		return tabNames;
	}

	private void attachPresenterToFragment(Fragment fragment) {

		Bundle patientBundle = getIntent().getExtras();
		patientUuid = String.valueOf(patientBundle.get(ApplicationConstants.BundleKeys.PATIENT_UUID_BUNDLE));
		visitUuid = String.valueOf(patientBundle.get(ApplicationConstants.BundleKeys.VISIT_UUID_BUNDLE));
		if (fragment instanceof VisitTasksFragment) {
			visitDetailsMainPresenter = new VisitTasksPresenter(patientUuid, visitUuid, ((VisitTasksFragment)fragment));
		} else if (fragment instanceof VisitPhotoFragment) {
			visitDetailsMainPresenter =
					new VisitPhotoPresenter(((VisitPhotoFragment)fragment), patientUuid, visitUuid, providerUuid);
		} else if (fragment instanceof VisitDetailsFragment) {
			visitDetailsMainPresenter = new VisitDetailsPresenter(patientUuid, visitUuid, providerUuid, visitClosedDate, (
					(VisitDetailsFragment)fragment));
		}
	}

	@Override
	public void onConfigurationChanged(final Configuration config) {
		super.onConfigurationChanged(config);
		TabUtil.setHasEmbeddedTabs(getSupportActionBar(), getWindowManager(),
				TabUtil.MIN_SCREEN_WIDTH_FOR_VISITDETAILSACTIVITY);
	}

	@Override
	public void onAttachFragment(Fragment fragment) {
		attachPresenterToFragment(fragment);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		return true;
	}

	@Override
	public void onBackPressed() {
		if (drawer.isDrawerOpen(GravityCompat.START)) {
			drawer.closeDrawer(GravityCompat.START);
		}
		super.onBackPressed();
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

	private void initializeListeners(FloatingActionButton... params) {
		for (FloatingActionButton visitActionButtons : params) {
			visitActionButtons.setOnClickListener(
					view -> startSelectedVisitActivity(visitActionButtons.getId()));
		}
	}

	private void startSelectedVisitActivity(int selectedId) {

		visitDetailsMenu.close(true);

		switch (selectedId) {
			case R.id.edit_visit:
				intent = new Intent(getApplicationContext(), AddEditVisitActivity.class);
				intent.putExtra(ApplicationConstants.BundleKeys.PATIENT_UUID_BUNDLE, patientUuid);
				intent.putExtra(ApplicationConstants.BundleKeys.VISIT_UUID_BUNDLE, visitUuid);
				intent.putExtra(ApplicationConstants.BundleKeys.PROVIDER_UUID_BUNDLE, providerUuid);
				intent.putExtra(ApplicationConstants.BundleKeys.VISIT_CLOSED_DATE, visitClosedDate);
				startActivity(intent);
				break;
			case R.id.end_visit:
				intent = new Intent(getApplicationContext(), AddEditVisitActivity.class);
				intent.putExtra(ApplicationConstants.BundleKeys.PATIENT_UUID_BUNDLE, patientUuid);
				intent.putExtra(ApplicationConstants.BundleKeys.VISIT_UUID_BUNDLE, visitUuid);
				intent.putExtra(ApplicationConstants.BundleKeys.PROVIDER_UUID_BUNDLE, providerUuid);
				intent.putExtra(ApplicationConstants.BundleKeys.VISIT_CLOSED_DATE, visitClosedDate);
				intent.putExtra(ApplicationConstants.BundleKeys.END_VISIT, true);
				startActivityForResult(intent, END_VISIT_RESULT);
				break;
			case R.id.capture_vitals:
				intent = new Intent(getApplicationContext(), CaptureVitalsActivity.class);
				intent.putExtra(ApplicationConstants.BundleKeys.PATIENT_UUID_BUNDLE, patientUuid);
				intent.putExtra(ApplicationConstants.BundleKeys.VISIT_UUID_BUNDLE, visitUuid);
				intent.putExtra(ApplicationConstants.BundleKeys.PROVIDER_UUID_BUNDLE, providerUuid);
				intent.putExtra(ApplicationConstants.BundleKeys.VISIT_CLOSED_DATE, visitClosedDate);
				startActivity(intent);
				break;

			case R.id.auditDataForm:
				intent = new Intent(getApplicationContext(), AuditDataActivity.class);
				intent.putExtra(ApplicationConstants.BundleKeys.PATIENT_UUID_BUNDLE, patientUuid);
				intent.putExtra(ApplicationConstants.BundleKeys.VISIT_UUID_BUNDLE, visitUuid);
				intent.putExtra(ApplicationConstants.BundleKeys.PROVIDER_UUID_BUNDLE, providerUuid);
				intent.putExtra(ApplicationConstants.BundleKeys.VISIT_CLOSED_DATE, visitClosedDate);
				startActivity(intent);
				break;
		}
	}

	@Override
	public void finish() {
		super.finish();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == END_VISIT_RESULT) {
			if (resultCode == RESULT_OK) {
				finish();
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onRestart() {

		//VisitDetailsFragment.refreshVitalsDetails();

		super.onRestart();
	}
}