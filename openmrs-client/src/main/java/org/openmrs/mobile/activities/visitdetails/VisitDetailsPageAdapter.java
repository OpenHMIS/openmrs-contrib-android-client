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

package org.openmrs.mobile.activities.visitdetails;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import org.openmrs.mobile.activities.visittasks.VisitTasksFragment;
import org.openmrs.mobile.activities.visittasks.VisitTasksPresenter;

public class VisitDetailsPageAdapter extends FragmentPagerAdapter {
	private static final int TAB_COUNT = 3;

	private static final int VISIT_DETAILS_TAB_POS = 0;
	private static final int VISIT_TASKS_TAB_POS = 1;
	private static final int VISIT_IMAGES_TAB_POS = 2;

	private SparseArray<Fragment> registeredFragments = new SparseArray<>();

	private String patientUuid;
	private String visitUuid;

	VisitDetailsPageAdapter(FragmentManager fm, String patientUuid, String visitUuid) {
		super(fm);
		this.patientUuid = patientUuid;
		this.visitUuid = visitUuid;
	}

	@Override
	public Fragment getItem(int i) {

		switch (i) {

			case VISIT_DETAILS_TAB_POS:
				VisitTasksFragment visitTasksFragment = VisitTasksFragment.newInstance();
				new VisitTasksPresenter(patientUuid,visitUuid, visitTasksFragment);
				return visitTasksFragment;
			case VISIT_TASKS_TAB_POS:
				VisitTasksFragment visitTasksFragment1 = VisitTasksFragment.newInstance();
				new VisitTasksPresenter(patientUuid,visitUuid, visitTasksFragment1);
				return visitTasksFragment1;
			case VISIT_IMAGES_TAB_POS:
				VisitTasksFragment visitTasksFragment2 = VisitTasksFragment.newInstance();
				new VisitTasksPresenter(patientUuid,visitUuid, visitTasksFragment2);
				return visitTasksFragment2;
			default:
				return null;
		}
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		Fragment fragment = (Fragment)super.instantiateItem(container, position);
		registeredFragments.put(position, fragment);
		return fragment;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		registeredFragments.remove(position);
		super.destroyItem(container, position, object);
	}

	@Override
	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}

	@Override
	public int getCount() {
		return TAB_COUNT;
	}
}
