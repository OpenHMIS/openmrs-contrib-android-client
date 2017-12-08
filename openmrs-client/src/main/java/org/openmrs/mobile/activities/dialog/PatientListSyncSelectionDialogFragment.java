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

package org.openmrs.mobile.activities.dialog;

import static org.openmrs.mobile.R.dimen.nav_title_margin;

import java.util.List;

import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.syncselection.SyncSelectionModelRecycleViewAdapter;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.models.PatientList;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.FontsUtil;

public class PatientListSyncSelectionDialogFragment extends DialogFragment implements
		PatientListSyncSelectionDialogContract.View {

	private static final int TYPED_DIMENSION_VALUE = 10;

	protected LayoutInflater layoutInflater;
	protected LinearLayout fieldsLayout;
	protected RecyclerView syncSelectionModelRecyclerView;
	protected TextView titleTextView;
	private Button rightButton;
	private View.OnClickListener rightButtonOnClickListener;

	private PatientListSyncSelectionDialogContract.Presenter presenter;
	private SyncSelectionModelRecycleViewAdapter adapter;

	public static PatientListSyncSelectionDialogFragment newInstance() {
		return new PatientListSyncSelectionDialogFragment();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
		this.setRetainInstance(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		this.layoutInflater = inflater;
		View dialogLayout = layoutInflater.inflate(R.layout.fragment_dialog_layout, null, false);
		this.fieldsLayout = (LinearLayout) dialogLayout.findViewById(R.id.dialogForm);

		buildDialog(dialogLayout);

		FontsUtil.setFont((ViewGroup)dialogLayout);

		presenter.dialogCreated();

		return dialogLayout;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (isDialogAvailable()) {
			this.setBorderless();
			this.setOnBackListener();
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (isDialogAvailable()) {
			this.setBorderless();
			this.setOnBackListener();
		}
	}

	public void show(FragmentManager manager) {
		String tag = ApplicationConstants.DialogTAG.SELECT_PATIENT_LISTS_TO_SYNC_DIALOG_TAG;
		if (manager.findFragmentByTag(tag) == null) {
			manager.beginTransaction().add(this, tag).commitAllowingStateLoss();
		}
	}

	@Override
	public void onDestroyView() {
		if (getDialog() != null && getRetainInstance()) {
			getDialog().setDismissMessage(null);
		}
		super.onDestroyView();

	}

	public final void setOnBackListener() {
		getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				dismiss();
				return false;
			}
		});
	}

	public final void setBorderless() {
		getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

		int marginWidth = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, TYPED_DIMENSION_VALUE,
				OpenMRS.getInstance().getResources().getDisplayMetrics());

		DisplayMetrics display = this.getResources().getDisplayMetrics();
		int width = display.widthPixels;

		WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
		params.width = width - 2 * marginWidth;

		getDialog().getWindow().setAttributes(params);
	}

	private void buildDialog(View dialogView) {
		titleTextView = addTitleBar(getString(R.string.select_patient_lists_to_sync));
		rightButton = setRightButton(dialogView);
		syncSelectionModelRecyclerView = addRecycleView();
	}

	private RecyclerView addRecycleView() {
		LinearLayout field = (LinearLayout) layoutInflater.inflate(R.layout.openmrs_recycle_view, null);
		RecyclerView recyclerView = (RecyclerView) field.findViewById(R.id.recyclerView);
		recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

		adapter = new SyncSelectionModelRecycleViewAdapter((PatientList patientList, boolean isSelected) -> {
				presenter.toggleSyncSelection(patientList, isSelected);
		});
		recyclerView.setAdapter(adapter);

		recyclerView.setNestedScrollingEnabled(false);

		ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) recyclerView.getLayoutParams();
		int navTitleMarginPixels = getResources().getDimensionPixelSize(nav_title_margin);
		marginLayoutParams.setMargins(navTitleMarginPixels, navTitleMarginPixels, navTitleMarginPixels,
				marginLayoutParams.bottomMargin);
		recyclerView.setLayoutParams(marginLayoutParams);

		fieldsLayout.addView(field);
		recyclerView.setHasFixedSize(true);

		return recyclerView;
	}

	private TextView addTitleBar(String title) {
		LinearLayout field = (LinearLayout) layoutInflater.inflate(R.layout.openmrs_title_view_field, null);
		TextView textView = (TextView)field.findViewById(R.id.openmrsTitleView);
		textView.setText(title);
		textView.setSingleLine(true);
		fieldsLayout.addView(field, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT));
		return textView;
	}

	private void setViewVisible(View view, boolean visible) {
		if (visible) {
			view.setVisibility(View.VISIBLE);
		} else {
			view.setVisibility(View.GONE);
		}
	}

	public Button setRightButton(View dialogLayout) {
		Button rightButton = (Button) dialogLayout.findViewById(R.id.dialogFormButtonsSubmitButton);
		rightButton.setText(getString(R.string.save_patient_list_sync_selections));
		rightButton.setOnClickListener(v -> {
			presenter.saveUsersSyncSelections();
			if (rightButtonOnClickListener != null) {
				rightButtonOnClickListener.onClick(v);
			}
		});
		setViewVisible(rightButton, true);
		return rightButton;
	}

	private boolean isDialogAvailable() {
		return null != this && null != this.getDialog();
	}

	@Override
	public void setPresenter(PatientListSyncSelectionDialogContract.Presenter presenter) {
		this.presenter = presenter;
	}

	public void dismissDialog() {
		dismiss();
	}

	@Override
	public void displayPatientLists(List<PatientList> patientLists, List<PatientList> syncingPatientLists) {
		adapter.setItems(patientLists, syncingPatientLists);
	}

	public void setRightButtonOnClickListener(View.OnClickListener onClickListener) {
		rightButtonOnClickListener = onClickListener;
	}
}
