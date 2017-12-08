package org.openmrs.mobile.utilities;

import static org.openmrs.mobile.utilities.TimeConstants.MINUTES_PER_HOUR;
import static org.openmrs.mobile.utilities.TimeConstants.SECONDS_PER_MINUTE;

public abstract class SyncConstants {

	public static final long SYNC_INTERVAL_FOR_FREQUENT_DATA = MINUTES_PER_HOUR / 12 * SECONDS_PER_MINUTE;
}
