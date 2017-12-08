package org.openmrs.mobile.data;

/**
 * Define the request critieria that will be used when fetching data.
 */
public enum RequestStrategy {
	/**
	 * Get data from the local database
	 */
	LOCAL_ONLY,

	/**
	 * Get data from the local database first and if not found use remote
	 */
	LOCAL_THEN_REMOTE,

	/**
	 * Data should be retrieved remotely and if not found, locally.
	 */
	REMOTE_THEN_LOCAL
}
