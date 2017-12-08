package org.openmrs.mobile.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.openmrs.mobile.application.OpenMRS;

import javax.inject.Inject;

/**
 * Monitor device connectivity. Detect network changes and handle accordingly.
 */
public class NetworkReceiver extends BroadcastReceiver {

	@Inject
	public NetworkReceiver() {
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		OpenMRS openMRS = (OpenMRS)context.getApplicationContext();
		ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context
				.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if (networkInfo == null) {
			return;
		}

		boolean connected = networkInfo.isConnected();
		openMRS.getOpenMRSLogger().d("CONNECTED " + connected);
	}
}
