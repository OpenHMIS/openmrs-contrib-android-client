package org.openmrs.mobile.activities.patientheader;

import org.openmrs.mobile.activities.BasePresenter;
import org.openmrs.mobile.data.DataService;
import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.data.impl.PatientDataService;
import org.openmrs.mobile.models.Patient;

public class PatientHeaderPresenter extends BasePresenter implements PatientHeaderContract.Presenter {

	private PatientDataService patientDataService;
	private PatientHeaderContract.View patientHeaderView;
	private String patientUuid;

	public PatientHeaderPresenter(PatientHeaderContract.View view, String patientUuid) {
		this.patientHeaderView = view;
		this.patientHeaderView.setPresenter(this);
		this.patientUuid = patientUuid;

		this.patientDataService = dataAccess().patient();
	}

	@Override
	public void getPatient() {
		patientHeaderView.holdHeader(true);
		patientDataService.getByUuid(patientUuid, QueryOptions.FULL_REP,
				new DataService.GetCallback<Patient>() {
			@Override
			public void onCompleted(Patient patient) {
				if (patient != null) {
					patientHeaderView.holdHeader(false);
					patientHeaderView.updatePatientHeader(patient);
				}
			}

			@Override
			public void onError(Throwable t) {
				patientHeaderView.holdHeader(false);
				t.printStackTrace();
			}
		});
	}

	@Override
	public void subscribe() {
		getPatient();
	}
}
