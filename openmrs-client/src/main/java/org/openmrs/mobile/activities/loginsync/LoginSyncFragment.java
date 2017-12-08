package org.openmrs.mobile.activities.loginsync;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseFragment;
import org.openmrs.mobile.activities.patientlist.PatientListActivity;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.FontsUtil;
import org.openmrs.mobile.utilities.ToastUtil;

public class LoginSyncFragment extends ACBaseFragment<LoginSyncContract.Presenter> implements LoginSyncContract.View {

	private View rootView;
	private ProgressBar pullProgressBar, pushProgressBar;
	private TextView pullProgressText, pullDurationText, pushProgressText, pushDurationText;

	public static LoginSyncFragment newInstance() {
		return new LoginSyncFragment();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_login_sync, container, false);

		initViewFields();

		// Font config
		FontsUtil.setFont((ViewGroup)this.getActivity().findViewById(android.R.id.content));

		return rootView;
	}

	@Override
	public void onStart() {
		super.onStart();
		mPresenter.startMeasuringConnectivity();
	}

	@Override
	public void onStop() {
		mPresenter.stopMeasuringConnectivity();
		super.onStop();
	}

	@Override
	public void onPause(){
		super.onPause();
		mPresenter.stopMeasuringConnectivity();
	}

	@Override
	public void onResume(){
		mPresenter.startMeasuringConnectivity();
		super.onResume();

	}

	private void initViewFields() {
		pushProgressBar = (ProgressBar) rootView.findViewById(R.id.pushProgressBar);
		pushProgressText = (TextView) rootView.findViewById(R.id.pushProgressText);
		pushDurationText = (TextView) rootView.findViewById(R.id.pushDurationText);

		pullProgressBar = (ProgressBar) rootView.findViewById(R.id.pullProgressBar);
		pullProgressText = (TextView) rootView.findViewById(R.id.pullProgressText);
		pullDurationText = (TextView) rootView.findViewById(R.id.pullDurationText);

		pushProgressText.setText(getString(R.string.initial_sync_push_progress_text));
		pushDurationText.setText(ApplicationConstants.EMPTY_STRING);
		pullProgressText.setText(getString(R.string.initial_sync_pull_progress_text));
		pullDurationText.setText(ApplicationConstants.EMPTY_STRING);
	}

	private void updateSyncPushProgress(double percentComplete, String progressText) {
		pushProgressBar.setProgress((int) Math.floor(percentComplete));
		pushProgressText.setText(progressText);

		if (percentComplete == 100) {
			pushDurationText.setVisibility(View.INVISIBLE);
			pullDurationText.setText(pushDurationText.getText());
			pullDurationText.setVisibility(View.VISIBLE);
		}
	}

	private void updateSyncPullProgress(double percentComplete, String progressText) {
		pullProgressBar.setProgress((int) Math.floor(percentComplete));
		pullProgressText.setText(progressText);

		if (percentComplete == 100) {
			pullDurationText.setVisibility(View.INVISIBLE);
		}
	}

	public void navigateToNextActivity() {
		Intent intent = new Intent(this.getContext(), PatientListActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		this.getContext().startActivity(intent);
		getActivity().finish();
	}

	public void notifyConnectionLost() {
		ToastUtil.notify(getString(R.string.sync_off_for_now_because_network_not_available));
	}

	public void updateSyncPullProgressForStartingSubscription(double percentComplete, String subscriptionName) {
		String progressText = getString(R.string.subscription_remote_pull_starting, subscriptionName);
		updateSyncPullProgress(percentComplete, progressText);
	}

	public void updateSyncPullProgressForCompletingSubscription(double percentComplete, String subscriptionName) {
		String progressText = getString(R.string.subscription_remote_pull_complete, subscriptionName);
		updateSyncPullProgress(percentComplete, progressText);
	}

	public void updateSyncPullProgressForStartingEntity(double percentComplete, String subscriptionName, String entityName) {
		String progressText = getString(R.string.entity_remote_pull_starting, subscriptionName, entityName);
		updateSyncPullProgress(percentComplete, progressText);
	}

	public void updateSyncPullProgressForCompletingEntity(double percentComplete, String subscriptionName,
			String entityName) {
		String progressText = getString(R.string.entity_remote_pull_complete, subscriptionName, entityName);
		updateSyncPullProgress(percentComplete, progressText);
	}

	public void notifySyncPushConnectionIsSlow() {
		setSyncDurationTextViewText(pushDurationText, getString(R.string.download_upload_speed_is_slow));
	}

	public void notifySyncPushConnectionIsFast() {
		setSyncDurationTextViewText(pushDurationText, getString(R.string.download_upload_speed_is_fast));
	}

	public void notifySyncPullConnectionIsSlow() {
		setSyncDurationTextViewText(pullDurationText, getString(R.string.download_upload_speed_is_slow));
	}

	public void notifySyncPullConnectionIsFast() {
		setSyncDurationTextViewText(pullDurationText, getString(R.string.download_upload_speed_is_fast));
	}

	private void setSyncDurationTextViewText(TextView durationTextToUpdate, String text) {
		durationTextToUpdate.setVisibility(View.VISIBLE);
		durationTextToUpdate.setText(text);
	}

	public void notifySyncPullComplete() {
		pullProgressText.setText(getString(R.string.download_complete));
		pullProgressBar.setProgress(100);
	}

	public void notifySyncPushComplete() {
		pullProgressText.setText(getString(R.string.initial_sync_pull_progress_text_after_upload_complete));
		pushProgressText.setText(getString(R.string.upload_complete));
		pushDurationText.setVisibility(View.INVISIBLE);
		pushProgressBar.setProgress(100);

	}

	public void updateSyncPushProgressForStartingRecord(double percentComplete, Integer recordNumber, Integer totalNumber) {
		String progressText = getString(R.string.record_remote_push_starting, recordNumber.toString(),
				totalNumber.toString());
		updateSyncPullProgress(percentComplete, progressText);
	}

	public void updateSyncPushProgressForCompletingRecord(double percentComplete, Integer recordNumber,
			Integer totalNumber) {
		String progressText = getString(R.string.record_remote_push_complete, recordNumber.toString(),
				totalNumber.toString());
		updateSyncPullProgress(percentComplete, progressText);
	}

	@Override
	public void updateSyncPullProgressForStartingTrim(double percentComplete) {
		String progressText = getString(R.string.trim_starting);
		updateSyncPullProgress(percentComplete, progressText);
	}

	@Override
	public void updateSyncPullProgressForCompletingTrim(double percentComplete) {
		String progressText = getString(R.string.trim_complete);
		updateSyncPullProgress(percentComplete, progressText);
	}
}
