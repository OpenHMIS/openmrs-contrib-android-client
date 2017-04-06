package org.openmrs.mobile.activities.patientdashboard;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import org.json.JSONException;
import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseActivity;
import org.openmrs.mobile.fragments.RecentVisits;
import org.openmrs.mobile.sampledata.Patient;
import org.openmrs.mobile.utilities.ApplicationConstants;


public class PatientDashboardActivity extends ACBaseActivity {

    private FloatingActionMenu floatingActionMenu;
    private FragmentManager fragmentManager;
    private Patient patient;
    private String patientId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_patient_dashboard, frameLayout);
        setTitle(R.string.title_patient_details);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            patientId = extras.getString(ApplicationConstants.Tags.PATIENT_ID);
        }

        loadPatientData(patientId);

        loadFragment();


    }

    private void loadPatientData(String patientId) {
        this.patient = new Patient();
        try {
            this.patient = patient.getPatient(patientId);
            floatingActionMenu = (FloatingActionMenu) findViewById(R.id.floatingActionMenu);
            floatingActionMenu.setClosedOnTouchOutside(true);

            FloatingActionButton createOldVisit = (FloatingActionButton) findViewById(R.id.create_past_visit);
            FloatingActionButton startNewVisit = (FloatingActionButton) findViewById(R.id.start_visit);
            FloatingActionButton visitTasks = (FloatingActionButton) findViewById(R.id.visit_tasks);
            FloatingActionButton endVisit = (FloatingActionButton) findViewById(R.id.end_visit);
            FloatingActionButton visitNote = (FloatingActionButton) findViewById(R.id.visit_note);
            FloatingActionButton admitInpatient = (FloatingActionButton) findViewById(R.id.admit_inpatient);
            FloatingActionButton captureVitals = (FloatingActionButton) findViewById(R.id.capture_vitals);
            FloatingActionButton createPastVisit = (FloatingActionButton) findViewById(R.id.create_past_visit);

            TextView given_name = (TextView) findViewById(R.id.given_name);
            TextView middle_name = (TextView) findViewById(R.id.middle_name);
            TextView family_name = (TextView) findViewById(R.id.family_name);
            TextView age = (TextView) findViewById(R.id.age);
            TextView id = (TextView) findViewById(R.id.patient_id);
            ImageView gender = (ImageView) findViewById(R.id.genderIcon);
            ImageView active_visit = (ImageView) findViewById(R.id.activeVisitIcon);

            given_name.setText(patient.given_name);
            middle_name.setText(patient.middle_name);
            family_name.setText(patient.family_name);
            age.setText(String.valueOf(patient.age));
            id.setText(patient.id);
            gender.setImageResource(String.valueOf(patient.gender).toLowerCase().equals("m") ? R.drawable.ic_male : R.drawable.ic_female);
            if (patient.active_visit == 1) {
                active_visit.setVisibility(View.VISIBLE);
                visitTasks.setVisibility(View.VISIBLE);
                endVisit.setVisibility(View.VISIBLE);
                visitNote.setVisibility(View.VISIBLE);
                admitInpatient.setVisibility(View.VISIBLE);
                captureVitals.setVisibility(View.VISIBLE);
            } else {
                startNewVisit.setVisibility(View.VISIBLE);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void loadFragment() {
        fragmentManager = getSupportFragmentManager();
        try {
            Fragment fragment = RecentVisits.class.newInstance();
            Bundle bundle = new Bundle();
            bundle.putSerializable(ApplicationConstants.Tags.PATIENT_ID, patient);
            fragment.setArguments(bundle);
            fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
