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

package org.openmrs.mobile.activities.patientlists;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseFragment;
import org.openmrs.mobile.utilities.FontsUtil;

public class PatientListsFragment extends ACBaseFragment<PatientListsContract.Presenter> implements PatientListsContract.View {
	
	private View mRootView;
	
	
	public static PatientListsFragment newInstance() {
		return new PatientListsFragment();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.fragment_patient_lists, container, false);
		
		// Font config
		FontsUtil.setFont((ViewGroup) this.getActivity().findViewById(android.R.id.content));
		
		return mRootView;
	}
	
	
}
