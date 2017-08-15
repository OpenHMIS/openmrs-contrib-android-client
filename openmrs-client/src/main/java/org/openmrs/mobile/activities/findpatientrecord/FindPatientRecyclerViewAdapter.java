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

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.patientdashboard.PatientDashboardActivity;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.PersonGenderEnum;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.DateUtils;

import java.util.List;

class FindPatientRecyclerViewAdapter extends RecyclerView.Adapter<FindPatientRecyclerViewAdapter.FetchedPatientHolder> {

	private Activity mContext;
	private List<Patient> patients;
	private FindPatientRecordContract.View view;

	FindPatientRecyclerViewAdapter(Activity context,
			List<Patient> patients, FindPatientRecordContract.View view) {
		this.mContext = context;
		this.patients = patients;
		this.view = view;
	}

	@Override
	public FetchedPatientHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fetched_patients_row, parent, false);
		return new FetchedPatientHolder(itemView);
	}

	@Override
	public void onBindViewHolder(FetchedPatientHolder holder, int position) {
		final Patient patient = patients.get(position);

		try {
			String patientIdentifier = String.format(mContext.getResources().getString(R.string.patient_identifier),
					patient.getIdentifier().getIdentifier());
			holder.mIdentifier.setText(patientIdentifier);
		} catch (Exception e) {
			holder.mIdentifier.setText("");
		}

		try {
			holder.mDisplayName.setText(patient.getPerson().getName().getNameString());
		} catch (Exception e) {
			holder.mDisplayName.setText("");
		}

		try {
			holder.mGender.setImageResource(
					patient.getPerson().getGender().equalsIgnoreCase(PersonGenderEnum.FEMALE.toString()) ?
							R.drawable.female : R.drawable.male);

		} catch (Exception e) {
			holder.mGender.setImageResource(0);
		}

		try {
			holder.mBirthDate.setText(DateUtils.convertTime1(patient.getPerson().getBirthdate(), DateUtils.DATE_FORMAT));
		} catch (Exception e) {
			holder.mBirthDate.setText(" ");
		}

		try {
			holder.fetchedPatientAge
					.setText(DateUtils.calculateAge(patient.getPerson().getBirthdate()));
		} catch (Exception e) {
			holder.fetchedPatientAge.setText("");
		}

		holder.mRowLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, PatientDashboardActivity.class);
				intent.putExtra(ApplicationConstants.BundleKeys.PATIENT_UUID_BUNDLE, patient.getUuid());
				mContext.startActivity(intent);
			}
		});
	}

	@Override
	public int getItemCount() {
		return patients.size();
	}

	class FetchedPatientHolder extends RecyclerView.ViewHolder {
		private LinearLayout mRowLayout;
		private TextView mIdentifier;
		private ImageView mGender;
		private TextView mDisplayName, fetchedPatientAge, mBirthDate, patientSeperator;

		FetchedPatientHolder(View itemView) {
			super(itemView);
			mRowLayout = (LinearLayout)itemView;
			mIdentifier = (TextView)itemView.findViewById(R.id.fetchedPatientIdentifier);
			mDisplayName = (TextView)itemView.findViewById(R.id.fetchedPatientDisplayName);
			mGender = (ImageView)itemView.findViewById(R.id.fetchedPatientGender);
			mBirthDate = (TextView)itemView.findViewById(R.id.fetchedPatientBirthDate);
			fetchedPatientAge = (TextView)itemView.findViewById(R.id.fetchedPatientAge);
			patientSeperator = (TextView)itemView.findViewById(R.id.patientSeperator);
		}
	}
}