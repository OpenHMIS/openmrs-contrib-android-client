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

import android.support.annotation.NonNull;
import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Months;
import org.joda.time.Years;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.openmrs.mobile.application.OpenMRS;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkNotNull;

public final class DateUtils {
	private static final String TAG = DateUtils.class.getSimpleName();
	public static final String DEFAULT_DATE_FORMAT = "dd/MM/yyyy";
	public static final String DATE_WITH_TIME_FORMAT = "dd/MM/yyyy HH:mm";
	public static final String OPEN_MRS_REQUEST_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
	public static final String OPEN_MRS_REQUEST_PATIENT_FORMAT = "yyyy-MM-dd";
	public static final String PATIENT_DASHBOARD_VISIT_DATE_FORMAT = "dd-MMM-yyyy HH:mm";
	public static final String TIME_FORMAT = "HH:mm";
	public static final String DATE_FORMAT = "dd-MMM-yyyy";
	public static final Long ZERO = 0L;
	public static final String OPEN_MRS_RESPONSE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
	public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat(OPEN_MRS_REQUEST_PATIENT_FORMAT, Locale.getDefault());

	private DateUtils() {

	}

	public static String convertTime(long time, String dateFormat, TimeZone timeZone) {
		Date date = new Date(time);
		SimpleDateFormat format = new SimpleDateFormat(dateFormat);
		format.setTimeZone(timeZone);
		return format.format(date);
	}

	public static String convertTime(long time, String dateFormat) {
		return convertTime(time, dateFormat, TimeZone.getDefault());
	}

	public static String convertTime(long timestamp, TimeZone timeZone) {
		return convertTime(timestamp, DEFAULT_DATE_FORMAT, timeZone);
	}

	public static String convertTime(long timestamp) {
		return convertTime(timestamp, DEFAULT_DATE_FORMAT, TimeZone.getDefault());
	}

	public static Long convertTime(String dateAsString) {
		return convertTime(dateAsString, OPEN_MRS_RESPONSE_FORMAT);
	}

	public static Long convertTime(String dateAsString, String dateFormat) {
		Long time = null;
		if (StringUtils.notNull(dateAsString)) {
			DateFormat format = new SimpleDateFormat(dateFormat);
			Date formattedDate;
			try {
				formattedDate = parseString(dateAsString, format);
				time = formattedDate.getTime();
			} catch (ParseException e) {
				try {
					formattedDate = parseString(dateAsString, new SimpleDateFormat(OPEN_MRS_REQUEST_PATIENT_FORMAT));
					time = formattedDate.getTime();
				} catch (ParseException e1) {
					OpenMRS.getInstance().getOpenMRSLogger()
							.w("Failed to parse date :" + dateAsString + " caused by " + e.toString());
				}
			}
		}
		return time;
	}

	public static Date parseString(String dateAsString) {
		try {
			return parseString(dateAsString, new SimpleDateFormat(OPEN_MRS_REQUEST_FORMAT));
		} catch(ParseException ex) {
			OpenMRS.getInstance().getOpenMRSLogger()
					.w("Failed to parse date :" + dateAsString + " caused by " + ex.toString());
		}

		return null;
	}

	private static Date parseString(String dateAsString, DateFormat format) throws ParseException {
		Date formattedDate = null;
		try {
			formattedDate = format.parse(dateAsString);
		} catch (NullPointerException e) {
			OpenMRS.getInstance().getOpenMRSLogger()
					.w("Failed to parse date :" + dateAsString + " caused by " + e.toString());
		}
		return formattedDate;
	}

	public static DateTime convertTimeString(String dateAsString) {
		DateTime date = null;
		if (StringUtils.notNull(dateAsString)) {
			try {

				DateTimeFormatter originalFormat = DateTimeFormat.forPattern(OPEN_MRS_REQUEST_FORMAT);
				date = originalFormat.parseDateTime(dateAsString);

			} catch (Exception ex) {
				DateTimeFormatter originalFormat = DateTimeFormat.forPattern(OPEN_MRS_REQUEST_PATIENT_FORMAT);
				date = originalFormat.parseDateTime(dateAsString);
			}
		}

		return date;
	}

	public static String convertTime1(String dateAsString, String dateFormat) {
		if (StringUtils.notNull(dateAsString) && StringUtils.notEmpty(dateAsString)) {
			return convertTime(convertTime(dateAsString), dateFormat, TimeZone.getDefault());
		}
		return dateAsString;
	}

	public static String getDateToday(String format) {
		return new SimpleDateFormat(format).format(new Date());
	}

	private static Date yesterday() {
		final Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -1);
		return cal.getTime();
	}

	public static String getDateYesterday(String format) {
		DateFormat dateFormat = new SimpleDateFormat(format);
		return dateFormat.format(yesterday());
	}

	public static String calculateAge(String dateOfBirth) {
		int ageInYears;
		int ageInMonths;
		int ageInDays;
		DateTime birthDate;
		DateTime currentTime;

		try {
			birthDate = new DateTime(SIMPLE_DATE_FORMAT.parse(dateOfBirth));
			currentTime = new DateTime();
			if (birthDate.isAfter(currentTime)) {
				Log.w(TAG, "Can't be born in the future");
				return ApplicationConstants.EMPTY_STRING;
			}

			ageInYears = Years.yearsBetween(birthDate.toLocalDate(),currentTime.toLocalDate()).getYears();
			ageInMonths = Months.monthsBetween(birthDate.toLocalDate(),currentTime.toLocalDate()).getMonths();
			ageInDays = Days.daysBetween(birthDate.toLocalDate(),currentTime.toLocalDate()).getDays();
		} catch (ParseException e) {
			Log.e(TAG, "Error parsing date: " + dateOfBirth, e);
			return ApplicationConstants.EMPTY_STRING;
		}

		if (ageInYears > 0) {
			return String.valueOf(ageInYears);
		} else if (ageInMonths == 1) {
			return ageInMonths + " month";
		} else if (ageInMonths > 1) {
			return ageInMonths + " months";
		} else if (ageInDays == 1) {
			return ageInDays + " day";
		} else
			return ageInDays + " days";
	}

	public static String calculateRelativeDate(Date pastDate) {
		String relative = "";
		if (pastDate == null) {
			return relative;
		}

		Date today = new Date();
		long days = TimeUnit.DAYS.convert(today.getTime() - pastDate.getTime(), TimeUnit.MILLISECONDS);
		if (days == 0) {
			relative = "within last 24hrs";
		} else if (days < 7) {
			relative = (days == 1 ? "yesterday" : days + " days ago");
		} else if (days < 30) {
			relative = Math.round(days / 7) + " week" + (days / 7 > 1 ? "s " : " ") + "ago";
		} else {
			relative = Math.round(days / 30) + " month" + (days / 30 > 1 ? "s " : " ") + "ago";
		}

		return relative;
	}

	public static String calculateTimeDifference(String dateStart, String dateStop, boolean minimum) {

		Date startDate = null;
		Date endDate = null;
		SimpleDateFormat format = new SimpleDateFormat(OPEN_MRS_RESPONSE_FORMAT);

		try {
			startDate = format.parse(dateStart);
			endDate = format.parse(dateStop);
		} catch (ParseException ex) {
			ex.printStackTrace();
		}

		return calculateTimeDifference(startDate, endDate, minimum);
	}

	public static String calculateTimeDifference(String startDate, String stopDate) {
		return calculateTimeDifference(startDate, stopDate, true);
	}

	public static String calculateTimeDifference(String startDate, boolean minimum) {
		return calculateTimeDifference(startDate, new SimpleDateFormat(OPEN_MRS_RESPONSE_FORMAT).format(new Date()),
				minimum) + " ago";
	}

	public static String calculateTimeDifference(String startDate) {
		return calculateTimeDifference(startDate, new SimpleDateFormat(OPEN_MRS_RESPONSE_FORMAT).format(new Date()),
				true) + " ago";
	}

	public static String calculateTimeDifference(Date startDate) {
		return calculateTimeDifference(startDate, new Date(), true) + " ago";
	}

	public static String calculateTimeDifference(Date startDate, boolean minimum) {
		return calculateTimeDifference(startDate, new Date(), minimum) + " ago";
	}

	public static String calculateTimeDifference(Date startDate, Date endDate) {
		return calculateTimeDifference(startDate, endDate, true);
	}

	public static String calculateTimeDifference(@NonNull Date startDate, @NonNull Date endDate, boolean minimum) {
		checkNotNull(startDate);
		checkNotNull(endDate);

		long durationInSeconds = TimeUnit.MILLISECONDS.toSeconds(endDate.getTime() - startDate.getTime());

		String relative = "";

		long SECONDS_IN_A_MINUTE = 60;
		long SECONDS_IN_AN_HOUR = SECONDS_IN_A_MINUTE * 60;
		long SECONDS_IN_A_DAY = SECONDS_IN_AN_HOUR * 24;
		long SECONDS_IN_A_WEEK = SECONDS_IN_A_DAY * 7;
		long SECONDS_IN_A_MONTH = SECONDS_IN_A_WEEK * 4;
		long SECONDS_IN_A_YEAR = SECONDS_IN_A_MONTH * 12;

		if (minimum) {
			if (Math.round(durationInSeconds / 60) < 1) {
				relative = durationInSeconds <= 1 ? "1 sec" : durationInSeconds + " secs";
			} else if (Math.round(durationInSeconds / SECONDS_IN_A_MINUTE) < 60) {
				int minutes = Math.round(durationInSeconds / SECONDS_IN_A_MINUTE);
				relative = (minutes == 1 ? "1 min" : minutes + " mins");
			} else if (Math.round(durationInSeconds / SECONDS_IN_AN_HOUR) < 24) {
				int minutes = Math.round(durationInSeconds / SECONDS_IN_AN_HOUR);
				relative = (minutes == 1 ? "1 hr" : minutes + " hrs");
			} else if (Math.round(durationInSeconds / SECONDS_IN_A_DAY) < 7) {
				int days = Math.round(durationInSeconds / SECONDS_IN_A_DAY);
				relative = (days == 1 ? "1 day" : days + " days");
			} else if (Math.round(durationInSeconds / SECONDS_IN_A_WEEK) < 4) {
				int weeks = Math.round(durationInSeconds / SECONDS_IN_A_WEEK);
				relative = (weeks == 1 ? "1 wk" : weeks + " wks");
			} else if (Math.round(durationInSeconds / SECONDS_IN_A_MONTH) < 12) {
				int months = Math.round(durationInSeconds / SECONDS_IN_A_MONTH);
				relative = (months == 1 ? "1 month" : months + " months");
			} else {
				int years = Math.round(durationInSeconds / SECONDS_IN_A_YEAR);
				relative = (years == 1 ? "1 yr" : years + " yrs");
			}
		} else {

			if (Math.round(durationInSeconds / 60) < 1) {
				return "Less than a minute";
			} else if (Math.round(durationInSeconds / SECONDS_IN_A_MINUTE) < 60) {
				int minutes = Math.round(durationInSeconds / SECONDS_IN_A_MINUTE);
				relative = (minutes == 1 ? "a minute" : minutes + " minutes");
			} else if (Math.round(durationInSeconds / SECONDS_IN_AN_HOUR) < 24) {
				int minutes = Math.round(durationInSeconds / SECONDS_IN_AN_HOUR);
				relative = (minutes == 1 ? "an hour" : minutes + " hours");
			} else if (Math.round(durationInSeconds / SECONDS_IN_A_DAY) < 7) {
				int days = Math.round(durationInSeconds / SECONDS_IN_A_DAY);
				relative = (days == 1 ? "a day" : days + " days");
			} else if (Math.round(durationInSeconds / SECONDS_IN_A_WEEK) < 4) {
				int weeks = Math.round(durationInSeconds / SECONDS_IN_A_WEEK);
				relative = (weeks == 1 ? "a week" : weeks + " weeks");
			} else if (Math.round(durationInSeconds / SECONDS_IN_A_MONTH) < 4) {
				int months = Math.round(durationInSeconds / SECONDS_IN_A_MONTH);
				relative = (months == 1 ? "an hour" : months + " hours");
			} else {
				int years = Math.round(durationInSeconds / SECONDS_IN_A_YEAR);
				relative = (years == 1 ? "about a year" : years + " years");
			}
		}

		return relative;
	}

	private static String getDuration(long secs, long mins, long hrs, long days, long weeks, long months, long years,
			boolean minimum) {
		StringBuffer sb = new StringBuffer();
		String EMPTY_STRING = "";
		if (minimum) {
			if (years > 0)
				sb.append(years > 0 ? years + (years > 1 ? " yrs " : " y ") : EMPTY_STRING);
			else if (months > 0)
				sb.append(months > 0 ? months + (months > 1 ? " ms " : " m ") : EMPTY_STRING);
			else if (months > 0)
				sb.append(weeks > 0 ? weeks + (weeks > 1 ? " wks " : " wk ") : EMPTY_STRING);
			else if (days > 0)
				sb.append(days > 0 ? days + (days > 1 ? " ds " : " d ") : EMPTY_STRING);
			else if (hrs > 0)
				sb.append(hrs > 0 ? hrs + (hrs > 1 ? " hs " : " h ") : EMPTY_STRING);
			else if (mins > 0)
				sb.append(mins > 0 ? mins + (mins > 1 ? " min " : " min ") : EMPTY_STRING);
			else if (secs > 0)
				sb.append(secs > 0 ? secs + (secs > 1 ? " secs " : " secs ") : EMPTY_STRING);
		} else {
			sb.append(years > 0 ? years + (years > 1 ? " years " : " year ") : EMPTY_STRING);
			sb.append(months > 0 ? months + (months > 1 ? " months " : " month ") : EMPTY_STRING);
			sb.append(days > 0 ? days + (days > 1 ? " weeks " : " week ") : EMPTY_STRING);
			sb.append(days > 0 ? days + (days > 1 ? " days " : " day ") : EMPTY_STRING);
			sb.append(hrs > 0 ? hrs + (hrs > 1 ? " hours " : " hour ") : EMPTY_STRING);
			sb.append(mins > 0 ? mins + (mins > 1 ? " mins " : " min ") : EMPTY_STRING);
			//sb.append(secs > 0 ? secs + (secs > 1 ? " secs " : " secs ") : EMPTY_STRING);
		}

		//sb.append("ago");
		return sb.toString();
	}

	public static Date constructDate(int year, int month, int day){
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.DAY_OF_MONTH, day);
		return cal.getTime();
	}

}
