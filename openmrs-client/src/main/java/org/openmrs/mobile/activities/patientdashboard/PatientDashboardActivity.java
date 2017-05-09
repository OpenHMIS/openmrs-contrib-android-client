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

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.view.Menu;
import android.view.View;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseActivity;

public class PatientDashboardActivity extends ACBaseActivity {

    public PatientDashboardContract.Presenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_patient_dashboard, frameLayout);
        setTitle(R.string.title_patient_details);
        // Create fragment
        PatientDashboardFragment patientDashboardFragment = (PatientDashboardFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (patientDashboardFragment == null) {
            patientDashboardFragment = PatientDashboardFragment.newInstance();
        }
        if (!patientDashboardFragment.isActive()) {
            addFragmentToActivity(getSupportFragmentManager(), patientDashboardFragment, R.id.contentFrame);
        }

        mPresenter = new PatientDashboardPresenter(patientDashboardFragment);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }



}
