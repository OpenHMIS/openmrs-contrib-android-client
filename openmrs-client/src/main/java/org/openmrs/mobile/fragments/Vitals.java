package org.openmrs.mobile.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.openmrs.mobile.R;
import org.openmrs.mobile.sampledata.Patient;
import org.openmrs.mobile.utilities.ApplicationConstants;


public class Vitals extends Fragment {

    private Patient patient;
    private TextView vitalHeight;
    private TextView vitalWeight;
    private TextView vitalBmi;
    private TextView vitalTemperature;
    private TextView vitalPulse;
    private TextView vitalRespiratoryRate;
    private TextView vitalBloodPressure;
    private TextView vitalBloodOxygenSaturation;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        patient = (Patient) getArguments().getSerializable(ApplicationConstants.Tags.PATIENT_ID);
        View view = inflater.inflate(R.layout.fragment_vitals, container, false);
        vitalHeight = (TextView) view.findViewById(R.id.vital_height);
        vitalWeight = (TextView) view.findViewById(R.id.vital_weight);
        vitalBmi = (TextView) view.findViewById(R.id.vital_bmi);
        vitalTemperature = (TextView) view.findViewById(R.id.vital_temperature);
        vitalPulse = (TextView) view.findViewById(R.id.vital_pulse);
        vitalRespiratoryRate = (TextView) view.findViewById(R.id.vital_respiratory_rate);
        vitalBloodPressure = (TextView) view.findViewById(R.id.vital_blood_pressure);
        vitalBloodOxygenSaturation = (TextView) view.findViewById(R.id.vital_bos);

        vitalHeight.setText("150");
        vitalWeight.setText("80");
        vitalBmi.setText("35.6");
        vitalTemperature.setText("37C");
        vitalPulse.setText("72/min");
        vitalRespiratoryRate.setText("72/min");
        vitalBloodPressure.setText("72/45");
        vitalBloodOxygenSaturation.setText("80%");

        return view;
    }


}
