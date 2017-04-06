package org.openmrs.mobile.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.patientdashboard.ListRecentVisits;
import org.openmrs.mobile.sampledata.Patient;
import org.openmrs.mobile.sampledata.Visit;
import org.openmrs.mobile.utilities.ApplicationConstants;


import java.util.ArrayList;
import java.util.List;


public class RecentVisits extends Fragment {

    private List<Visit> visitList;
    private RecyclerView visitsView;
    private Patient patient;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        patient = (Patient) getArguments().getSerializable(ApplicationConstants.Tags.PATIENT_ID);
        View view = inflater.inflate(R.layout.fragment_recent_visits, container, false);
        visitsView = (RecyclerView) view.findViewById(R.id.recent_visits_list);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
       visitsView.setLayoutManager(llm);
        visitsView.setHasFixedSize(true);
        initializeData();
        initializeAdapter();
        return view;
    }

    private void initializeData() {
        visitList = new ArrayList<>();
        visitList.add(new Visit("08.Feb.2017", "", patient.active_visit, "Inpatient"));
        visitList.add(new Visit("13.Dec.2016", "08.Feb.2017", 0, "Outpatient"));
        visitList.add(new Visit("31.Oct.2016", "", 0, "Outpatient"));
        visitList.add(new Visit("26.Oct.2016", "31.Oct.2016", 0, "Outpatient"));
        visitList.add(new Visit("11.Oct.2016", "19.Oct.2016", 0, "Inpatient"));
    }

    private void initializeAdapter() {
        ListRecentVisits listRecentVisits = new ListRecentVisits(getActivity(), visitList);
        visitsView.setAdapter(listRecentVisits);
    }
}
