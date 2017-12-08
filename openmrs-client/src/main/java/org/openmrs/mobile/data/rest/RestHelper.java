package org.openmrs.mobile.data.rest;

import android.support.annotation.Nullable;

import org.openmrs.mobile.data.DataOperationException;
import org.openmrs.mobile.data.DataService;
import org.openmrs.mobile.models.Results;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class RestHelper {
	/**
	 * Executes a rest call that returns a list result and blocks the current thread until the call is complete, returning
	 * the results.
	 * @param call The rest call to execute
	 * @param <T> The expected result class
	 * @return A list containing the results of the rest call
	 */
	public static @Nullable <T> List<T> getCallListValue(Call<Results<T>> call) {
		if (call == null) {
			return null;
		}

		Response<Results<T>> response;
		try {
			response = call.execute();
		} catch (Exception ex) {
			throw new DataOperationException(ex);
		}

		List<T> result = null;
		if (response != null) {
			Results<T> temp = response.body();

			if (temp != null) {
				result = temp.getResults();
			}
		}

		return result;
	}

	/**
	 * Executes a rest call that returns a single result and blocks the current thread until the call is complete,
	 * returning the result.
	 * @param call The rest call to execute
	 * @param <T> The expected result class
	 * @return The results of the rest call
	 */
	public static @Nullable  <T> T getCallValue(Call<T> call) {
		if (call == null) {
			return null;
		}

		Response<T> response;
		try {
			response = call.execute();
		} catch (Exception ex) {
			throw new DataOperationException(ex);
		}

		T result = null;
		if (response != null) {
			result = response.body();
		}

		return result;
	}
}
