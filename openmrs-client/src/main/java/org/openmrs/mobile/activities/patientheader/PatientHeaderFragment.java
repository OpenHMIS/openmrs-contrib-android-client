package org.openmrs.mobile.activities.patientheader;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseFragment;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.utilities.DateUtils;

public class PatientHeaderFragment extends ACBaseFragment<PatientHeaderContract.Presenter>
		implements PatientHeaderContract.View {

	private TextView patientDisplayName, patientAge, fileNumber, patientDob, patientAddress, patientPhonenumber;
	ImageView patientGender;
	private View shadowLine;
	private RelativeLayout hideHeader, headerScreen;

	public static PatientHeaderFragment newInstance() {
		return new PatientHeaderFragment();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

		View fragmentView = inflater.inflate(R.layout.fragment_patient_header, container, false);
		patientDisplayName = (TextView)fragmentView.findViewById(R.id.fetchedPatientDisplayName);
		fileNumber = (TextView)fragmentView.findViewById(R.id.fileNumber);

		patientGender = (ImageView)fragmentView.findViewById(R.id.fetchedPatientGender);
		patientAge = (TextView)fragmentView.findViewById(R.id.fetchedPatientAge);
		patientDob = (TextView)fragmentView.findViewById(R.id.fetchedPatientBirthDate);

		patientAddress = (TextView)fragmentView.findViewById(R.id.patientAddress);
		patientPhonenumber = (TextView)fragmentView.findViewById(R.id.patientPhonenumber);

		hideHeader = (RelativeLayout)fragmentView.findViewById(R.id.hideHeader);
		headerScreen = (RelativeLayout)fragmentView.findViewById(R.id.headerScreen);

		shadowLine = fragmentView.findViewById(R.id.shadowLine);

		return fragmentView;
	}

	@Override
	public void updatePatientHeader(Patient patient) {
		patientDisplayName.setText(patient.getPerson().getName().getNameString());
		patientGender.setImageResource(patient.getPerson().getGender().equalsIgnoreCase("f") ? R.drawable.female : R
				.drawable.male);
		fileNumber.setText(patient.getIdentifier().getIdentifier());
		patientAge.setText(DateUtils.calculateAge(patient.getPerson().getBirthdate()));
		patientDob.setText(
				DateUtils.convertTime1(patient.getPerson().getBirthdate(), DateUtils.DATE_FORMAT));
	}

	@Override
	public void holdHeader(boolean visibility) {
		hideHeader.setVisibility(visibility ? View.VISIBLE : View.GONE);
		headerScreen.setVisibility(visibility ? View.GONE : View.VISIBLE);
	}

	public void updateShadowLine(boolean visible) {
		shadowLine.setVisibility(visible ? View.VISIBLE : View.GONE);

	}

}
