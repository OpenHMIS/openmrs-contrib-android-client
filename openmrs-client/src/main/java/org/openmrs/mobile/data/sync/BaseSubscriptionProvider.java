package org.openmrs.mobile.data.sync;

import org.openmrs.mobile.models.PullSubscription;
import org.openmrs.mobile.models.Results;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Base class for all pull subscription providers.
 */
public abstract class BaseSubscriptionProvider implements SubscriptionProvider {
	/**
	 * Initialize the provider.
	 * @param subscription The {@link PullSubscription} that will be processed by the provider
	 */
	@Override
	public void initialize(PullSubscription subscription) { }

	/**
	 * Executes the pull synchronization process.
	 * @param subscription The {@link PullSubscription} that will be processed
	 */
	@Override
	public abstract void pull(PullSubscription subscription);

	/**
	 * Executes a rest call that returns a list result and blocks the current thread until the call is complete, returning
	 * the results.
	 * @param call The rest call to execute
	 * @param <T> The expected result class
	 * @return A list containing the results of the rest call
	 */
	protected <T> List<T> getCallListValue(Call<Results<T>> call) {
		if (call == null) {
			return null;
		}

		Response<Results<T>> response;
		try {
			response = call.execute();
		} catch (Exception ex) {
			response = null;
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
	protected <T> T getCallValue(Call<T> call) {
		if (call == null) {
			return null;
		}

		Response<T> response;
		try {
			response = call.execute();
		} catch (Exception ex) {
			response = null;
		}

		T result = null;
		if (response != null) {
			result = response.body();
		}

		return result;
	}
}
