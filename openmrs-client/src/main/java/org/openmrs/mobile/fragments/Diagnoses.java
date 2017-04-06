package org.openmrs.mobile.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.patientdashboard.ListDiagnoses;
import org.openmrs.mobile.sampledata.DiagnosesData;
import org.openmrs.mobile.sampledata.Patient;
import org.openmrs.mobile.utilities.ApplicationConstants;

import java.util.ArrayList;
import java.util.List;


public class Diagnoses extends Fragment {
    private List<DiagnosesData> diagnosesList;
    private RecyclerView diagnosesView;
    private Patient patient;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        patient = (Patient) getArguments().getSerializable(ApplicationConstants.Tags.PATIENT_ID);
        View view = inflater.inflate(R.layout.fragment_diagnoses, container, false);
        diagnosesView = (RecyclerView) view.findViewById(R.id.diagnoses_list);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        diagnosesView.setLayoutManager(llm);
        diagnosesView.setHasFixedSize(true);
        initializeData();
        initializeAdapter();
        return view;
    }

    private void initializeData() {
        diagnosesList = new ArrayList<>();
        diagnosesList.add(new DiagnosesData("Hypertensive heart disease"));
        diagnosesList.add(new DiagnosesData("Malaria"));
        diagnosesList.add(new DiagnosesData("Vitamin A deficiency"));
        diagnosesList.add(new DiagnosesData("Cough"));
        diagnosesList.add(new DiagnosesData("Flu"));
    }

    private void initializeAdapter() {
        ListDiagnoses listDiagnoses = new ListDiagnoses(diagnosesList);
        diagnosesView.setAdapter(listDiagnoses);
    }
}
