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

package org.openmrs.mobile.activities.findpatientrecord;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseActivity;
import org.openmrs.mobile.activities.addeditpatient.AddEditPatientActivity;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.StringUtils;

import java.util.Timer;
import java.util.TimerTask;

public class FindPatientRecordActivity extends ACBaseActivity {

	private final long DELAY = 1000;
	public FindPatientRecordContract.Presenter findPatientPresenter;
	private FindPatientRecordFragment findPatientRecordFragment;
	private String query = "";
	private OpenMRS instance = OpenMRS.getInstance();
	private EditText searchPatientsView;
	private Timer timer;

	private boolean userClickedSearchButton = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getLayoutInflater().inflate(R.layout.activity_find_patient_record, frameLayout);

		// Create fragment
		findPatientRecordFragment =
				(FindPatientRecordFragment)getSupportFragmentManager().findFragmentById(R.id
						.findPatientContentFrame);
		if (findPatientRecordFragment == null) {
			findPatientRecordFragment = FindPatientRecordFragment.newInstance();
		}
		if (!findPatientRecordFragment.isActive()) {
			addFragmentToActivity(getSupportFragmentManager(), findPatientRecordFragment, R.id.findPatientContentFrame);
		}

		if (savedInstanceState != null) {
			query = savedInstanceState.getString(ApplicationConstants.BundleKeys.PATIENT_QUERY_BUNDLE, "");
			findPatientPresenter = new FindPatientRecordPresenter(findPatientRecordFragment, query);
		} else {
			findPatientPresenter = new FindPatientRecordPresenter(findPatientRecordFragment);
		}

		//Add menu autoclose
		FloatingActionMenu findPatientMenu = (FloatingActionMenu)findViewById(R.id.findPatientMenu);
		findPatientMenu.setClosedOnTouchOutside(true);

		//adding
		FloatingActionButton floatingActionButton = (FloatingActionButton)findViewById(R.id.add_Patient);
		floatingActionButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				findPatientMenu.close(true);
				Intent intent = new Intent(FindPatientRecordActivity.this, AddEditPatientActivity.class);
				startActivity(intent);
			}
		});

		if (savedInstanceState != null) {
			query = savedInstanceState.getString(ApplicationConstants.BundleKeys.PATIENT_QUERY_BUNDLE, "");
			findPatientPresenter = new FindPatientRecordPresenter(findPatientRecordFragment, query);
		} else {
			findPatientPresenter = new FindPatientRecordPresenter(findPatientRecordFragment);
		}

		query = getSearchQuery();

	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.menu_find_patient_record, menu);
		MenuItem mFindPatientMenuItem = menu.findItem(R.id.action_search);

		searchPatientsView = (EditText)mFindPatientMenuItem.getActionView().findViewById(R.id.searchPatient);

		setTitle(R.string.nav_find_patient);

		searchPatientsView.setText(query);
		searchPatientsView.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (timer != null) {
					timer.cancel();
				}
			}

			@Override
			public void afterTextChanged(Editable s) {
				query = s.toString();
				if (query.length() >= 3) {
					timer = new Timer();
					timer.schedule(new TimerTask() {
						@Override
						public void run() {
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									findPatientPresenter.findPatient(query);
									setSearchQuery(query);
								}
							});
						}
					}, DELAY);
				} else {
					setSearchQuery(query);
					findPatientRecordFragment.setNumberOfPatientsView(0);
					findPatientRecordFragment.setNoPatientsVisibility(true);
				}
			}
		});

		FindPatientRecordActivity self = this;
		MenuItemCompat.setOnActionExpandListener(mFindPatientMenuItem, new MenuItemCompat.OnActionExpandListener() {

			@Override
			public boolean onMenuItemActionExpand(MenuItem item) {
				if (userClickedSearchButton) {
					searchPatientsView.requestFocusFromTouch();
					showSoftKeyboard(self);
				}
				return true;
			}

			@Override
			public boolean onMenuItemActionCollapse(MenuItem item) {
				return true;
			}
		});

		return true;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		MenuItem actionSearchMenuItem = menu.findItem(R.id.action_search);
		if (StringUtils.notEmpty(query)) {
			userClickedSearchButton = false;
			actionSearchMenuItem.expandActionView();
		}
		userClickedSearchButton = true;
		return true;
	}

	private String getSearchQuery() {
		return instance.getPreferences().getString(
				ApplicationConstants.BundleKeys.PATIENT_QUERY_BUNDLE,
				ApplicationConstants.EMPTY_STRING);
	}

	private void setSearchQuery(String query) {
		SharedPreferences.Editor editor = instance.getPreferences().edit();
		editor.putString(ApplicationConstants.BundleKeys.PATIENT_QUERY_BUNDLE, query);
		editor.commit();
	}

	@Override
	public void onBackPressed() {
		if (drawer.isDrawerOpen(GravityCompat.START)) {
			drawer.closeDrawer(GravityCompat.START);
		}
	}
}
