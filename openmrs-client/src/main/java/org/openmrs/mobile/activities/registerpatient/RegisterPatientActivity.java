package org.openmrs.mobile.activities.registerpatient;

import android.os.Bundle;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseActivity;


public class RegisterPatientActivity extends ACBaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_find_patient_record, frameLayout);
        setTitle(R.string.nav_register_patient);
    }


}
