package org.openmrs.mobile.activities.findpatientrecord;

import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openmrs.mobile.ConsoleLogger;
import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseActivity;
import org.openmrs.mobile.adapters.ListPatients;
import org.openmrs.mobile.sampledata.Patient;

import java.util.ArrayList;
import java.util.List;


public class FindPatientRecordActivity extends ACBaseActivity {
    private List<Patient> patientList;
    private RecyclerView patientsView;
    private Patient patient = new Patient();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_find_patient_record, frameLayout);
        setTitle(R.string.nav_find_patient);
        patientsView = (RecyclerView) findViewById(R.id.patients_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        patientsView.setLayoutManager(linearLayoutManager);
        patientsView.setHasFixedSize(true);
        try {
            initializeData();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        initializeAdapter();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_find_patient_record, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //adapter.getFilter().filter(newText);

                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private void initializeData() throws JSONException {
        patientList = new ArrayList<>();
        JSONArray patients = patient.getPatients();
        for (int i = 0; i < patients.length(); i++) {
            JSONObject patientData = new JSONObject(patients.get(i).toString());
            patientList.add(patient.newPatient(patientData.get("given_name").toString(),
                    patientData.get("middle_name").toString(),
                    patientData.get("family_name").toString(), patientData.get("id").toString(),
                    Integer.parseInt(patientData.get("age").toString()),
                    patientData.get("gender").toString().charAt(0), Integer.parseInt(patientData.get("active_visit").toString())));
        }
    }

    private void initializeAdapter() {
        ListPatients listPatients = new ListPatients(getApplicationContext(), patientList);
        patientsView.setAdapter(listPatients);
    }

}
