package org.openmrs.mobile.utilities;

public abstract class TimeConstants {
	public static final long MILLIS_PER_SECOND = 1000L;
	public static final long SECONDS_PER_MINUTE = 60L;
	public static final long MINUTES_PER_HOUR = 60L;
	public static final long HOURS_PER_DAY = 24L;

	public static final long SECONDS_PER_HOUR = SECONDS_PER_MINUTE * MINUTES_PER_HOUR;
	public static final long SECONDS_PER_DAY = SECONDS_PER_HOUR * HOURS_PER_DAY;
	public static final long MILLIS_PER_MINUTE = MILLIS_PER_SECOND * SECONDS_PER_MINUTE;
}
