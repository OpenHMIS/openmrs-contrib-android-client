package org.openmrs.mobile.net;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;

import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.dagger.ReceiverComponent;

import javax.inject.Inject;

public class NetworkManager {

	private OpenMRS openMRS;
	private ReceiverComponent receiverComponent;

	@Inject
	public NetworkManager(OpenMRS openMRS, ReceiverComponent receiverComponent) {
		this.openMRS = openMRS;
		this.receiverComponent = receiverComponent;
	}

	private void registerReceiver() {
		BroadcastReceiver networkReceiver = receiverComponent.networkReceiver();
		IntentFilter networkFilter = new IntentFilter();
		networkFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		networkFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		networkFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
		openMRS.registerReceiver(networkReceiver, networkFilter);
	}

	private void unregisterReceiver() {
		openMRS.unregisterReceiver(receiverComponent.networkReceiver());
	}

	public void initializeNetworkReceiver() {
		registerReceiver();
	}
}
