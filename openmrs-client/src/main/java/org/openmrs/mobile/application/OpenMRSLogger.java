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

package org.openmrs.mobile.application;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.PrintWriter;

public class OpenMRSLogger implements Logger {

	private static final boolean IS_DEBUGGING_ON = true;
	private static final String LOG_FILENAME = "OpenMRS.log";
	private static final int MAX_SIZE = 64 * 1024; // 64kB;
	private static String mTAG = "OpenMRS";
	private static File mLogFile = null;
	private static File mFolder = null;
	private static boolean mSaveToFileEnable = true;
	private static int mErrorCountSaveToFile = 2;
	private static boolean mIsRotating;
	private static OpenMRS mOpenMRS = OpenMRS.getInstance();
	private static OpenMRSLogger logger = null;
	private Thread.UncaughtExceptionHandler androidDefaultUEH;

	public OpenMRSLogger() {
		logger = this;
		androidDefaultUEH = Thread.getDefaultUncaughtExceptionHandler();
		Thread.UncaughtExceptionHandler handler = new Thread.UncaughtExceptionHandler() {
			public void uncaughtException(Thread thread, Throwable ex) {
				logger.e("Uncaught exception is: ", ex);
				androidDefaultUEH.uncaughtException(thread, ex);
			}
		};
		Thread.setDefaultUncaughtExceptionHandler(handler);

		mFolder = new File(mOpenMRS.getOpenMRSDir());
		try {
			if (isFolderExist()) {
				mLogFile = new File(mOpenMRS.getOpenMRSDir() + File.separator + LOG_FILENAME);
				if (!mLogFile.createNewFile()) {
					rotateLogFile();
				}

				mLogFile.createNewFile();
			}
			logger.d("Start logging to file");
		} catch (IOException e) {
			logger.e("Error during create file", e);
		}
	}

	private static boolean isFolderExist() {
		boolean success = true;
		if (!mFolder.exists()) {
			success = mFolder.mkdir();
		}
		return success;
	}

	private static boolean isSaveToFileEnable() {
		return mSaveToFileEnable && mErrorCountSaveToFile > 0;
	}

	private static void setErrorCount() {
		mErrorCountSaveToFile--;
		if (mErrorCountSaveToFile <= 0) {
			mSaveToFileEnable = false;
			logger.e("logging to file disabled because of to much error during save");
		}
	}

	private static void saveToFile() {
		if (isFolderExist() && isSaveToFileEnable()) {
			String command = "logcat -d -v time -s " + mTAG;
			try {
				Process mLoggerProcess = Runtime.getRuntime().exec(command);
				BufferedReader in = new BufferedReader(new InputStreamReader(mLoggerProcess.getInputStream()));
				String line;

				FileWriter writer = new FileWriter(mLogFile, true);
				while ((line = in.readLine()) != null) {
					if (!line.startsWith("---------")) {
						writer.write(line + "\n");
					}
				}
				writer.flush();
				writer.close();

				mLoggerProcess = Runtime.getRuntime().exec("logcat -c");
				mLoggerProcess.waitFor();

			} catch (IOException e) {
				setErrorCount();
				if (isSaveToFileEnable()) {
					logger.e("Error during save log to file", e);
				}
			} catch (InterruptedException e) {
				setErrorCount();
				if (isSaveToFileEnable()) {
					logger.e("Error during waitng for \"logcat -c\" process", e);
				}
			}
			rotateLogFile();
		}
	}

	private static String getMessage(String msg) {
		final String fullClassName = Thread.currentThread().getStackTrace()[4].getClassName();
		final String className = fullClassName.substring(fullClassName.lastIndexOf('.') + 1);
		final String methodName = Thread.currentThread().getStackTrace()[4].getMethodName();
		final int lineNumber = Thread.currentThread().getStackTrace()[4].getLineNumber();

		return "#" + lineNumber + " " + className + "." + methodName + "() : " + msg;
	}

	private static void rotateLogFile() {
		if (mLogFile.length() > MAX_SIZE && !mIsRotating) {
			mIsRotating = true;
			logger.i("Log file size is too big. Start rotating log file");
			new Thread() {
				@Override
				public void run() {
					try {
						LineNumberReader r = new LineNumberReader(new FileReader(mLogFile));

						while (r.readLine() != null) {
							continue;
						}
						r.close();

						int remove = Math.round(r.getLineNumber() * 0.3f);
						if (remove > 0) {
							r = new LineNumberReader(new FileReader(mLogFile));

							while (r.readLine() != null && r.getLineNumber() < remove) {
								continue;
							}

							File newFile = new File(mLogFile.getAbsolutePath() + ".new");
							PrintWriter pw = new PrintWriter(new FileWriter(newFile));
							String line;
							while ((line = r.readLine()) != null) {
								pw.println(line);
							}

							pw.close();
							r.close();

							if (newFile.renameTo(mLogFile)) {
								logger.i("Log file rotated");
							}
							mIsRotating = false;
						}
					} catch (IOException e) {
						logger.e("Error rotating log file. Rotating disable. ", e);
					}
				}
			}.start();
		}
	}

	@Override
	public void v(final String msg) {
		v(mTAG, msg);
	}

	@Override
	public void v(String tag, String msg) {
		Log.v(tag, getMessage(msg));
		saveToFile();
	}

	@Override
	public void v(final String msg, Throwable throwable) {
		v(mTAG, msg, throwable);
	}

	@Override
	public void v(String tag, String msg, Throwable throwable) {
		Log.v(tag, getMessage(msg), throwable);
		saveToFile();
	}

	@Override
	public void d(final String msg) {
		d(mTAG, msg);
	}

	@Override
	public void d(String tag, String msg) {
		if (IS_DEBUGGING_ON) {
			Log.d(tag, getMessage(msg));
			saveToFile();
		}
	}

	@Override
	public void d(final String msg, Throwable throwable) {
		d(mTAG, msg, throwable);
	}

	@Override
	public void d(String tag, String msg, Throwable throwable) {
		if (IS_DEBUGGING_ON) {
			Log.d(tag, getMessage(msg), throwable);
			saveToFile();
		}
	}

	@Override
	public void i(final String msg) {
		i(mTAG, msg);
	}

	@Override
	public void i(String tag, String msg) {
		Log.i(tag, getMessage(msg));
		saveToFile();
	}

	@Override
	public void i(final String msg, Throwable throwable) {
		i(mTAG, msg, throwable);
	}

	@Override
	public void i(String tag, String msg, Throwable throwable) {
		Log.i(tag, getMessage(msg), throwable);
		saveToFile();
	}

	@Override
	public void w(final String msg) {
		w(mTAG, msg);
	}

	@Override
	public void w(String tag, String msg) {
		Log.w(tag, getMessage(msg));
		saveToFile();
	}

	@Override
	public void w(final String msg, Throwable throwable) {
		w(mTAG, msg, throwable);
	}

	@Override
	public void w(String tag, String msg, Throwable throwable) {
		Log.w(tag, getMessage(msg), throwable);
		saveToFile();
	}

	@Override
	public void e(final String msg) {
		e(mTAG, msg);
	}

	@Override
	public void e(String tag, String msg) {
		Log.e(tag, getMessage(msg));
		saveToFile();
	}

	@Override
	public void e(final String msg, Throwable throwable) {
		e(mTAG, msg, throwable);
	}

	@Override
	public void e(String tag, String msg, Throwable throwable) {
		Log.e(tag, getMessage(msg), throwable);
		saveToFile();
	}

	@Override
	public void e(Throwable throwable) {
		e("no message", throwable);
	}

	@Override
	public void setUser(String user) {
		// Intentionally left blank
	}

	public String getLogFilename() {
		return LOG_FILENAME;
	}
}
