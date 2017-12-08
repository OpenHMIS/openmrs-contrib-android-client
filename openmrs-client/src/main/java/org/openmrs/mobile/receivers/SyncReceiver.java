package org.openmrs.mobile.receivers;

import javax.inject.Inject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.openmrs.mobile.application.OpenMRS;

public class SyncReceiver extends BroadcastReceiver {
	@Inject
	public SyncReceiver() {}

	@Override
	public void onReceive(Context context, Intent intent) {
		OpenMRS openMRS = (OpenMRS) context.getApplicationContext();
		if (openMRS.getNetworkUtils().isConnectedOrConnecting()) {
			openMRS.requestDataSync();
		}
	}
}
