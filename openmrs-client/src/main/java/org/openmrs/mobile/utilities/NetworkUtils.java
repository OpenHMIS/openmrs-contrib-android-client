/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.mobile.utilities;

import javax.inject.Inject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;
import android.util.Log;

import org.openmrs.mobile.application.OpenMRS;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetworkUtils {

	public static final double UNKNOWN_CONNECTION_SPEED = -1;

	@Inject
	public NetworkUtils() {
	}

	public boolean isConnectedOrConnecting() {

		NetworkInfo activeNetworkInfo = getNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
	}

	public boolean checkIfServerOnline() {
		if (isConnectedOrConnecting()) {
			try {
				HttpURLConnection urlc = (HttpURLConnection)(new URL(OpenMRS.getInstance().getServerUrl()).openConnection
						());
				urlc.setRequestProperty("User-Agent", "Test");
				urlc.setRequestProperty("Connection", "close");
				urlc.setConnectTimeout(1500);
				urlc.connect();
				return (urlc.getResponseCode() == 200);
			} catch (IOException e) {
				Log.e("Error", "Error: ", e);
			}
		} else {
			Log.d("error", "No network present");
		}
		return false;
	}

	private NetworkInfo getNetworkInfo() {
		ConnectivityManager connectivityManager =
				(ConnectivityManager) OpenMRS.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
		return connectivityManager.getActiveNetworkInfo();
	}

	/**
	 * Get the connection speed based on the current network connection
	 * @return An estimate of the connection speed based on typical range estimates in KB/s
	 */
	public @Nullable Double getCurrentConnectionSpeed() {
		if (!isConnectedOrConnecting()) {
			return null;
		}

		NetworkInfo networkInfo = getNetworkInfo();
		int type = networkInfo.getType();
		int subType = networkInfo.getSubtype();

		if (type == ConnectivityManager.TYPE_WIFI) {
			// ~ 20+ Mbps
			return 2500D;
		} else if (type == ConnectivityManager.TYPE_MOBILE) {
			switch(subType){
				case TelephonyManager.NETWORK_TYPE_1xRTT:
					// ~ 50-100 kbps
					return 7D;
				case TelephonyManager.NETWORK_TYPE_CDMA:
					// ~ 14-64 kbps
					return 2D;
				case TelephonyManager.NETWORK_TYPE_EDGE:
					// ~ 50-100 kbps
					return 7D;
				case TelephonyManager.NETWORK_TYPE_EVDO_0:
					// ~ 400-1000 kbps
					return 60D;
				case TelephonyManager.NETWORK_TYPE_EVDO_A:
					// ~ 600-1400 kbps
					return 90D;
				case TelephonyManager.NETWORK_TYPE_GPRS:
					// ~ 100 kbps
					return 12D;
				case TelephonyManager.NETWORK_TYPE_HSDPA:
					// ~ 2-14 Mbps
					return 375D;
				case TelephonyManager.NETWORK_TYPE_HSPA:
					// ~ 700-1700 kbps
					return 100D;
				case TelephonyManager.NETWORK_TYPE_HSUPA:
					// ~ 1-23 Mbps
					return 500D;
				case TelephonyManager.NETWORK_TYPE_UMTS:
					// ~ 400-7000 kbps
					return 100D;
				case TelephonyManager.NETWORK_TYPE_EHRPD:
					// ~ 1-2 Mbps
					return 125D;
				case TelephonyManager.NETWORK_TYPE_EVDO_B:
					// ~ 5 Mbps
					return 625D;
				case TelephonyManager.NETWORK_TYPE_HSPAP:
					// ~ 10-20 Mbps
					return 1250D;
				case TelephonyManager.NETWORK_TYPE_IDEN:
					// ~25 kbps
					return 3D;
				case TelephonyManager.NETWORK_TYPE_LTE:
					// ~ 10+ Mbps
					return 1250D;
				// Unknown
				case TelephonyManager.NETWORK_TYPE_UNKNOWN:
				default:
					return UNKNOWN_CONNECTION_SPEED;
			}
		} else {
			return UNKNOWN_CONNECTION_SPEED;
		}
	}
}
