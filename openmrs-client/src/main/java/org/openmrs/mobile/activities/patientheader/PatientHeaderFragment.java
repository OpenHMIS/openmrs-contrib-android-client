package org.openmrs.mobile.activities.patientheader;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseFragment;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.event.DataRefreshEvent;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.DateUtils;
import org.openmrs.mobile.utilities.StringUtils;

public class PatientHeaderFragment extends ACBaseFragment<PatientHeaderContract.Presenter>
		implements PatientHeaderContract.View {

	private TextView patientDisplayName, patientAge, fileNumber, patientDob, patientAddress, patientPhonenumber,
			patientSyncAge;
	ImageView patientGender;
	private View shadowLine;
	private RelativeLayout hideHeader, headerScreen, lastSyncInformation;

	private boolean shouldShowLastSyncInformation = false;

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

		patientSyncAge = (TextView)fragmentView.findViewById(R.id.patientSyncAge);

		hideHeader = (RelativeLayout)fragmentView.findViewById(R.id.hideHeader);
		headerScreen = (RelativeLayout)fragmentView.findViewById(R.id.headerScreen);
		lastSyncInformation = (RelativeLayout)fragmentView.findViewById(R.id.patientSyncAgeLayout);
		if (shouldShowLastSyncInformation) {
			showLastSyncInformation();
		}

		shadowLine = fragmentView.findViewById(R.id.shadowLine);

		return fragmentView;
	}

	@Override
	public void updatePatientHeader(Patient patient) {
		if (patient == null || patient.getPerson() == null) {
			return;
		}
		patientDisplayName.setText(patient.getPerson().getName() != null ?
				patient.getPerson().getName().getNameString() : patient.getPerson().getDisplay());
		patientGender.setImageResource(patient.getPerson().getGender().equalsIgnoreCase("f") ? R.drawable.female : R
				.drawable.male);
		fileNumber.setText(patient.getIdentifier() != null ?
				patient.getIdentifier().getIdentifier() : ApplicationConstants.EMPTY_STRING);
		patientAge.setText(DateUtils.calculateAge(patient.getPerson().getBirthdate()));
		patientDob.setText(
				DateUtils.convertTime1(patient.getPerson().getBirthdate(), DateUtils.DATE_FORMAT));
	}

	@Override
	public void onStart() {
		super.onStart();
		OpenMRS.getInstance().getEventBus().register(this);
	}

	@Override
	public void onStop() {
		OpenMRS.getInstance().getEventBus().unregister(this);
		super.onStop();
	}

	@Override
	public void holdHeader(boolean visibility) {
		hideHeader.setVisibility(visibility ? View.VISIBLE : View.GONE);
		headerScreen.setVisibility(visibility ? View.GONE : View.VISIBLE);
	}

	@Override
	public void updatePatientSyncAge(String lastSyncCalendarTimeDifference) {
		if (StringUtils.isNullOrEmpty(lastSyncCalendarTimeDifference)) {
			patientSyncAge.setText(getString(R.string.patient_dashboard_sync_age_display_not_available));
		} else {
			patientSyncAge.setText(getString(R.string.patient_dashboard_sync_age_display, lastSyncCalendarTimeDifference));
		}
	}

	public void updateShadowLine(boolean visible) {
		shadowLine.setVisibility(visible ? View.VISIBLE : View.GONE);

	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onDataRefreshEvent(DataRefreshEvent event) {
		presenter.dataRefreshEventOccurred(event);
	}

	public void showLastSyncInformation() {
		shouldShowLastSyncInformation = true;
		if (lastSyncInformation != null) {
			lastSyncInformation.setVisibility(View.VISIBLE);
		}
	}

}
