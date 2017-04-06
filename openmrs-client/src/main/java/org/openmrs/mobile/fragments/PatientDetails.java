package org.openmrs.mobile.fragments;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.patientdashboard.TabsPageView;
import org.openmrs.mobile.sampledata.Patient;
import org.openmrs.mobile.utilities.ApplicationConstants;


public class PatientDetails extends Fragment {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Patient patient;


    public PatientDetails() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        patient = (Patient) getArguments().getSerializable(ApplicationConstants.Tags.PATIENT_ID);
        View view = layoutInflater.inflate(R.layout.fragment_patient_details, container, false);
        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        setupTabIcons();


        return view;
    }


    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_tab_diagnoses);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_tab_vitals);

    }

    private void setupViewPager(ViewPager viewPager) {
        TabsPageView adapter = new TabsPageView(getChildFragmentManager());

        Bundle bundle = new Bundle();
        bundle.putSerializable(ApplicationConstants.Tags.PATIENT_ID, patient);

        Fragment diagnoses = new Diagnoses();
        diagnoses.setArguments(bundle);
        adapter.addFragment(diagnoses, getResources().getString(R.string.label_diagnoses));

        Fragment vitals = new Vitals();
        vitals.setArguments(bundle);
        adapter.addFragment(vitals, getResources().getString(R.string.label_vitals));


        viewPager.setAdapter(adapter);
    }


}