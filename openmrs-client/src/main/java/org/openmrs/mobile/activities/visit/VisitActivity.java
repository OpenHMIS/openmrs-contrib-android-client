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

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.core.view.GravityCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseActivity;
import org.openmrs.mobile.activities.addeditvisit.AddEditVisitActivity;
import org.openmrs.mobile.activities.auditdata.AuditDataActivity;
import org.openmrs.mobile.activities.capturevitals.CaptureVitalsActivity;
import org.openmrs.mobile.activities.imagegallery.ImageGalleryActivity;
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
import org.openmrs.mobile.bundle.CustomDialogBundle;
import org.openmrs.mobile.event.PatientRefreshEvent;
import org.openmrs.mobile.models.Resource;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.FontsUtil;
import org.openmrs.mobile.utilities.StringUtils;
import org.openmrs.mobile.utilities.TabUtil;
import org.openmrs.mobile.utilities.ToastUtil;

import java.util.ArrayList;
import java.util.List;

public class VisitActivity extends ACBaseActivity
		implements VisitDetailsFragment.OnFragmentInteractionListener, VisitContract.View,
		VisitPhotoFragment.OnFragmentInteractionListener {
		
	private static final int TAB_COUNT = 3;
	private static final int VISIT_DETAILS_TAB_POSITION = 0;
	private static final int VISIT_TASKS_TAB_POSITION = 1;
	private static final int VISIT_IMAGES_TAB_POSITION = 2;

	private static final int END_VISIT_RESULT = 1;
	public VisitContract.VisitDashboardPage.Presenter visitDetailsMainPresenter;
	private PatientHeaderContract.Presenter patientHeaderPresenter;
	private String patientUuid;
	private String visitUuid;
	private Intent intent;
	private OpenMRS instance = OpenMRS.getInstance();
	private SharedPreferences sharedPreferences = instance.getPreferences();
	private FloatingActionButton captureVitalsButton, endVisitButton, editVisitButton, auditData, refreshVisitButton;
	private FloatingActionMenu visitDetailsMenu;

	private VisitContract.Presenter presenter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View visitActivityView = getLayoutInflater().inflate(R.layout.activity_visit, frameLayout);

		presenter = new VisitPresenter(this);

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
			if (StringUtils.isNullOrEmpty(visitUuid)) {
				logger.e("Visit UUID NULL on the Visit Activity");
				showToast(ApplicationConstants.entityName.VISITS +
						ApplicationConstants.toastMessages.fetchErrorMessage, ToastUtil.ToastType.ERROR);
				return;
			}
			presenter.getVisit(visitUuid);

			handleViewPager(patientUuid, visitUuid);

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

		refreshVisitButton = (FloatingActionButton)findViewById(R.id.refresh_visit);
		captureVitalsButton = (FloatingActionButton)findViewById(R.id.capture_vitals);
		auditData = (FloatingActionButton)findViewById(R.id.auditDataForm);
		endVisitButton = (FloatingActionButton)findViewById(R.id.end_visit);
		editVisitButton = (FloatingActionButton)findViewById(R.id.edit_visit);
		visitDetailsMenu = (FloatingActionMenu)findViewById(R.id.visitDetailsMenu);
		addCustomAnimation(visitDetailsMenu);
		visitDetailsMenu.setClosedOnTouchOutside(true);

		// Font config
		FontsUtil.setFont((ViewGroup)this.findViewById(android.R.id.content));

		initializeListeners(endVisitButton, editVisitButton, captureVitalsButton, auditData, refreshVisitButton);
	}

	private void addCustomAnimation(FloatingActionMenu visitDetailsMenu) {
		AnimatorSet set = new AnimatorSet();

		ObjectAnimator scaleOutX = ObjectAnimator
				.ofFloat(visitDetailsMenu.getMenuIconView(), "scaleX", 1.0f, 0.2f);
		ObjectAnimator scaleOutY = ObjectAnimator
				.ofFloat(visitDetailsMenu.getMenuIconView(), "scaleY", 1.0f, 0.2f);

		ObjectAnimator scaleInX = ObjectAnimator
				.ofFloat(visitDetailsMenu.getMenuIconView(), "scaleX", 0.2f, 1.0f);
		ObjectAnimator scaleInY = ObjectAnimator
				.ofFloat(visitDetailsMenu.getMenuIconView(), "scaleY", 0.2f, 1.0f);

		scaleOutX.setDuration(300);
		scaleOutY.setDuration(300);

		scaleInX.setDuration(300);
		scaleInY.setDuration(300);

		scaleInX.addListener(new AnimatorListenerAdapter() {

			@Override
			public void onAnimationStart(Animator animation) {
				visitDetailsMenu.getMenuIconView()
						.setImageResource(visitDetailsMenu.isOpened() ? R.drawable.ic_close : R.drawable.ic_menu);
			}
		});

		set.play(scaleOutX).with(scaleOutY);
		set.play(scaleInX).with(scaleInY).after(scaleOutX);
		set.setInterpolator(new OvershootInterpolator(2));

		visitDetailsMenu.setIconToggleAnimatorSet(set);
	}

	private void handleViewPager(String patientUuid, String visitUuid) {
		// Set the view pager up
		VisitPageAdapter visitPageAdapter = new VisitPageAdapter(getSupportFragmentManager(), patientUuid, visitUuid);
		ViewPager pager = findViewById(R.id.visitDetailsPager);
		pager.setAdapter(visitPageAdapter);

		// Attach the ViewPager to the TabLayout
		TabLayout tabLayout = findViewById(R.id.visitDetailsTabLayout);
		tabLayout.setupWithViewPager(pager);
	}

	private void attachPresenterToFragment(Fragment fragment) {

		Bundle patientBundle = getIntent().getExtras();
		patientUuid = String.valueOf(patientBundle.get(ApplicationConstants.BundleKeys.PATIENT_UUID_BUNDLE));
		visitUuid = String.valueOf(patientBundle.get(ApplicationConstants.BundleKeys.VISIT_UUID_BUNDLE));
		if (fragment instanceof VisitTasksFragment) {
			visitDetailsMainPresenter = new VisitTasksPresenter(patientUuid, visitUuid, ((VisitTasksFragment) fragment));
		} else if (fragment instanceof VisitPhotoFragment) {
			visitDetailsMainPresenter =
					new VisitPhotoPresenter(((VisitPhotoFragment)fragment), patientUuid, visitUuid);
		} else if (fragment instanceof VisitDetailsFragment) {
			visitDetailsMainPresenter = new VisitDetailsPresenter(patientUuid, visitUuid, ((VisitDetailsFragment) fragment));
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
	protected void onPause() {
		super.onPause();
		OpenMRS.getInstance().getEventBus().unregister(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		OpenMRS.getInstance().getEventBus().register(this);
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onDataRefreshEvent(PatientRefreshEvent event) {
		if (event.getPatientUuid() != null && !Resource.isLocalUuid(event.getPatientUuid())) {
			this.patientUuid = event.getPatientUuid();
		}
	}

	@Override
	public void onBackPressed() {
		if (!isLoading()) {
			if (drawer.isDrawerOpen(GravityCompat.START)) {
				drawer.closeDrawer(GravityCompat.START);
			}

			goToDashboard();
		} else {
			createToast(getString(R.string.pending_save));
		}
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
			visitActionButtons.setOnClickListener(view -> handleFabClick(visitActionButtons.getId()));
		}
	}

	private void handleFabClick(int selectedId) {

		visitDetailsMenu.close(true);

		switch (selectedId) {
			case R.id.edit_visit:
				intent = new Intent(this, AddEditVisitActivity.class);
				intent.putExtra(ApplicationConstants.BundleKeys.PATIENT_UUID_BUNDLE, patientUuid);
				intent.putExtra(ApplicationConstants.BundleKeys.VISIT_UUID_BUNDLE, visitUuid);
				startActivity(intent);
				break;
			case R.id.end_visit:
				intent = new Intent(this, AddEditVisitActivity.class);
				intent.putExtra(ApplicationConstants.BundleKeys.PATIENT_UUID_BUNDLE, patientUuid);
				intent.putExtra(ApplicationConstants.BundleKeys.VISIT_UUID_BUNDLE, visitUuid);
				intent.putExtra(ApplicationConstants.BundleKeys.END_VISIT, true);
				startActivityForResult(intent, END_VISIT_RESULT);
				break;
			case R.id.capture_vitals:
				intent = new Intent(this, CaptureVitalsActivity.class);
				intent.putExtra(ApplicationConstants.BundleKeys.PATIENT_UUID_BUNDLE, patientUuid);
				intent.putExtra(ApplicationConstants.BundleKeys.VISIT_UUID_BUNDLE, visitUuid);
				startActivity(intent);
				break;

			case R.id.auditDataForm:
				intent = new Intent(this, AuditDataActivity.class);
				intent.putExtra(ApplicationConstants.BundleKeys.PATIENT_UUID_BUNDLE, patientUuid);
				intent.putExtra(ApplicationConstants.BundleKeys.VISIT_UUID_BUNDLE, visitUuid);
				startActivity(intent);
				break;

			case R.id.refresh_visit:
				visitDetailsMainPresenter.dataRefreshWasRequested();
				break;
		}
	}

	@Override
	public void finish() {
		super.finish();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (resultCode == RESULT_OK) {
			if (requestCode == END_VISIT_RESULT) {
				finish();
			} else if (requestCode == ApplicationConstants.Activity.RequestCodes.VIEW_IMAGE_GALLERY) {
				boolean shouldRefreshVisitPhotos = intent
						.getBooleanExtra(ApplicationConstants.BundleKeys.EXTRA_SHOULD_REFRESH, false);
				if (visitDetailsMainPresenter instanceof VisitPhotoPresenter && shouldRefreshVisitPhotos) {
					((VisitPhotoPresenter) visitDetailsMainPresenter).refreshPhotosWhenVisible();
				}
			}
		}
		super.onActivityResult(requestCode, resultCode, intent);
	}

	@Override
	public void onRestart() {

		//VisitDetailsFragment.refreshVitalsDetails();

		super.onRestart();
	}

	private void goToDashboard() {
		finish();
		Intent intent = new Intent(getApplicationContext(), PatientDashboardActivity.class);
		intent.putExtra(ApplicationConstants.BundleKeys.PATIENT_UUID_BUNDLE, patientUuid);
		//fix for now
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}

	@Override
	public void viewVisitPhotos(String photoUuidToView, List<String> visitPhotoUuids) {
		Intent intent = new Intent(this, ImageGalleryActivity.class);
		intent.putExtra(ApplicationConstants.BundleKeys.EXTRA_VISIT_PHOTO_UUID, photoUuidToView);
		intent.putStringArrayListExtra(ApplicationConstants.BundleKeys.EXTRA_VISIT_PHOTO_UUIDS, (ArrayList) visitPhotoUuids);
		startActivityForResult(intent, ApplicationConstants.Activity.RequestCodes.VIEW_IMAGE_GALLERY);
	}

	@Override
	public void fragmentProcessing(boolean isLoading) {
		setLoading(isLoading);
	}

	@Override
	public void addAuditData(String patientUuid, String visitUuid) {
		intent = new Intent(this, AuditDataActivity.class);
		intent.putExtra(ApplicationConstants.BundleKeys.PATIENT_UUID_BUNDLE, patientUuid);
		intent.putExtra(ApplicationConstants.BundleKeys.VISIT_UUID_BUNDLE, visitUuid);
		startActivity(intent);
	}

	@Override
	public void addVisitVitals(String patientUuid, String visitUuid) {
		intent = new Intent(this, CaptureVitalsActivity.class);
		intent.putExtra(ApplicationConstants.BundleKeys.PATIENT_UUID_BUNDLE, patientUuid);
		intent.putExtra(ApplicationConstants.BundleKeys.VISIT_UUID_BUNDLE, visitUuid);
		startActivity(intent);
	}

	@Override
	public void showDialog(CustomDialogBundle bundle, String dialogTag) {
		createAndShowDialog(bundle, dialogTag);
	}

	@Override
	public void setPresenter(VisitContract.Presenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public void runOnUIThread(Runnable runnable) {
		super.runOnUiThread(runnable);
	}

	@Override
	public void showToast(String message, ToastUtil.ToastType toastType) {
		ToastUtil.showShortToast(this, toastType, message);
	}

	@Override
	public void getVisitCompleted(Visit visit) {
		boolean isVisitClosed = visit.getStopDatetime() != null;
		if (isVisitClosed) {
			try {
				captureVitalsButton.setVisibility(View.GONE);
				endVisitButton.setVisibility(View.GONE);
			} catch (Exception e) {
				// The activity must be detached or something wrong such that these buttons aren't in the right state
				// But if this is the case, it's not a problem for the app and these buttons aren't even there
				logger.e(e.getMessage(), e);
			}
		}
	}

	private class VisitPageAdapter extends FragmentPagerAdapter {

		private String patientUuid;
		private String visitUuid;

		VisitPageAdapter(FragmentManager fm, String patientUuid, String visitUuid) {
			super(fm);
			this.patientUuid = patientUuid;
			this.visitUuid = visitUuid;
		}

		@Override
		public Fragment getItem(int position) {
			switch (position) {
				case VISIT_DETAILS_TAB_POSITION:
					VisitDetailsFragment visitDetailsFragment = VisitDetailsFragment.newInstance();
					new VisitDetailsPresenter(patientUuid, visitUuid, visitDetailsFragment);
					return visitDetailsFragment;
				case VISIT_TASKS_TAB_POSITION:
					VisitTasksFragment visitTasksFragment = VisitTasksFragment.newInstance();
					new VisitTasksPresenter(patientUuid, visitUuid, visitTasksFragment);
					return visitTasksFragment;
				case VISIT_IMAGES_TAB_POSITION:
					VisitPhotoFragment visitPhotoFragment = VisitPhotoFragment.newInstance();
					new VisitPhotoPresenter(visitPhotoFragment, patientUuid, visitUuid);
					return visitPhotoFragment;
			}
			return null;
		}

		@Nullable
		@Override
		public CharSequence getPageTitle(int position) {
			switch (position) {
				case VISIT_DETAILS_TAB_POSITION:
					return getString(R.string.visit_scroll_tab_details_label);
				case VISIT_TASKS_TAB_POSITION:
					return getString(R.string.visit_scroll_tab_visit_tasks_label);
				case VISIT_IMAGES_TAB_POSITION:
					return getString(R.string.visit_scroll_tab_visit_images_label);
				default:
					return null;
			}
		}

		@Override
		public int getCount() {
			return TAB_COUNT;
		}
	}
}