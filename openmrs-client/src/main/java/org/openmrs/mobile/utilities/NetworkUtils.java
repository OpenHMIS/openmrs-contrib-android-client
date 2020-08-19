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
import androidx.annotation.Nullable;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.openmrs.mobile.application.Logger;
import org.openmrs.mobile.application.OpenMRS;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

public class NetworkUtils {

	public enum ConnectionQuality {
		POOR,
		MODERATE,
		GOOD,
		EXCELLENT,
		UNKNOWN
	}

	private OkHttpClient client;
	private Logger logger;

	// bandwidth in kbps
	private static final int SPEED_KBPS_POOR_BANDWIDTH = 150;
	private static final int SPEED_KBPS_AVERAGE_BANDWIDTH = 550;
	private static final int SPEED_KBPS_EXCELLENT_BANDWIDTH = 2000;

	private ConnectionQuality currentConnectionSpeed = ConnectionQuality.UNKNOWN;

	private boolean areMeasuringConnectivitySpeed = false;
	private final Double INITIAL_NETWORK_SPEED = 375D; // kbps (equivalent to slow HSDPA)
	private Double averageNetworkSpeed = INITIAL_NETWORK_SPEED; // kbps (equivalent to slow HSDPA)
	private final double SMOOTHING_FACTOR = 0.005;
	private final long DATA_SPEED_POLLING_FREQUENCY = 30000; // 30 seconds

	private TimerTask measureConnectivityTimerTask;
	private Timer networkConnectivityCheckTimer;

	@Inject
	public NetworkUtils(OkHttpClient client, Logger logger) {
		this.client = client;
		this.logger = logger;
	}

	public boolean isConnected() {
		try {
			NetworkInfo activeNetworkInfo = getNetworkInfo();
			return activeNetworkInfo != null && activeNetworkInfo.isConnected();
		} catch (Exception e) {
			logger.e(e);
		}
		return false;
	}

	public boolean checkIfServerOnline() {
		if (isConnected()) {
			try {
				HttpURLConnection urlc = (HttpURLConnection)(new URL(OpenMRS.getInstance().getServerUrl()).openConnection
						());
				urlc.setRequestProperty("User-Agent", "Test");
				urlc.setRequestProperty("Connection", "close");
				urlc.setConnectTimeout(1500);
				urlc.connect();
				return (urlc.getResponseCode() == 200);
			} catch (IOException e) {
				logger.e("Network Utils", "Error: ", e);
			}
		} else {
			logger.d("Network Utils", "No network present");
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
		if (!isConnected()) {
			return null;
		}
		return averageNetworkSpeed;
	}

	public void startSamplingConnectivity() {
		if (areMeasuringConnectivitySpeed) {
			return;
		}
		areMeasuringConnectivitySpeed = true;

		measureConnectivityTimerTask = new TimerTask() {
			@Override
			public void run() {
				if (averageNetworkSpeed.equals(INITIAL_NETWORK_SPEED) || currentConnectionSpeed == ConnectionQuality.POOR
						|| currentConnectionSpeed == ConnectionQuality.UNKNOWN) {
					fetchImageForNetworkTesting();
				}
			}
		};
		if (networkConnectivityCheckTimer == null) {
			networkConnectivityCheckTimer = new Timer();
		}
		measureConnectivityTimerTask.run();
		networkConnectivityCheckTimer.schedule(measureConnectivityTimerTask, DATA_SPEED_POLLING_FREQUENCY,
				DATA_SPEED_POLLING_FREQUENCY);
	}

	public void stopSamplingConnectivity() {
		areMeasuringConnectivitySpeed = false;
		if (measureConnectivityTimerTask != null) {
			measureConnectivityTimerTask.cancel();
		}
	}

	public void calculateConnectivitySpeed(long requestStartTime, long requestEndTime, long contentLength) {
		if (!areMeasuringConnectivitySpeed) {
			return;
		}

		// calculate how long the request took
		double timeTakenInNanos = Math.floor(requestEndTime - requestStartTime);  // time taken in nanoseconds
		double timeTakenInSecs = timeTakenInNanos / 1000000000D;  // divide by 1000000000 to get time in seconds
		// get the download speed by dividing the file size by time taken to download (Bps)
		// then divide by 1024 to get to KBps, then multiply by 8 (number of bits in a byte) to get Kbps
		final int speed = (int) Math.round(contentLength / timeTakenInSecs / 1024D * 8D);
		calculateNewAverageNetworkSpeed(speed);

		if (averageNetworkSpeed <= SPEED_KBPS_POOR_BANDWIDTH){
			currentConnectionSpeed = ConnectionQuality.POOR;
		} else if (averageNetworkSpeed <= SPEED_KBPS_AVERAGE_BANDWIDTH) {
			currentConnectionSpeed = ConnectionQuality.MODERATE;
		} else if (averageNetworkSpeed <= SPEED_KBPS_EXCELLENT_BANDWIDTH) {
			currentConnectionSpeed = ConnectionQuality.GOOD;
		} else {
			currentConnectionSpeed = ConnectionQuality.EXCELLENT;
		}

		// Uncomment for some statistics
//		logger.d("Time taken in secs: " + timeTakenInSecs);
		logger.i("Average connection speed: " + averageNetworkSpeed);
//		logger.d("Download Speed: " + speed);
//		logger.d("File size: " + contentLength);
	}

	private void calculateNewAverageNetworkSpeed(double networkSpeed) {
		if (averageNetworkSpeed == null) {
			averageNetworkSpeed = networkSpeed;
		} else {
			averageNetworkSpeed = SMOOTHING_FACTOR * networkSpeed + (1 - SMOOTHING_FACTOR) * averageNetworkSpeed;
		}
	}

	private void fetchImageForNetworkTesting() {
		try {
			// Spin this up in a new thread so we don't hold the app up
			new Thread(() -> {
				// Fetch an image from online
				Request imageRequest = new Request.Builder()
//						.url("https://drive.google.com/uc?id=18T9gRM4VXqojAfBs_MEKYexpvtqyXLf0&export=download") // 100K
						.url("https://drive.google.com/uc?id=1PIMBHloabfXH988qTv5qSt3WxrKkcVTn&export=download") // 10K
						.build();
				long requestStartTime = System.nanoTime();
				client.newCall(imageRequest).enqueue(new Callback() {

					@Override
					public void onFailure(Call call, IOException e) {
						logger.e(e);
					}

					@Override
					public void onResponse(Call call, Response response) throws IOException {
						if (!response.isSuccessful()) {
							throw new IOException(
									"The request to fetch an image to determine network speed returned an unexpected code: "
											+ response);
						}

						calculateConnectivitySpeed(requestStartTime, System.nanoTime(), response.body().contentLength());
						response.body().close();
					}
				});
			}).start();
		} catch (Exception e) {
			logger.e(e);
		}
	}
}
