package org.openmrs.mobile.data;

import android.support.annotation.Nullable;

import org.openmrs.mobile.data.rest.RestConstants;
import org.openmrs.mobile.utilities.StringUtils;

public class QueryOptions {
	public static final QueryOptions INCLUDE_ALL_FULL_REP =
			new Builder().includeInactive(true).customRepresentation(RestConstants.Representations.FULL).build();
	public static final QueryOptions FULL_REP =
			new Builder().customRepresentation(RestConstants.Representations.FULL).build();
	public static final QueryOptions REMOTE_FULL_REP =
			new Builder().customRepresentation(RestConstants.Representations.FULL)
					.requestStrategy(RequestStrategy.REMOTE_THEN_LOCAL).build();
	public static final QueryOptions REMOTE = new Builder().requestStrategy(RequestStrategy.REMOTE_THEN_LOCAL).build();
	private static final boolean DEFAULT_INCLUDE_INACTIVE = false;
	private String cacheKey;
	private boolean includeInactive = DEFAULT_INCLUDE_INACTIVE;
	private String customRepresentation;
	private static final RequestStrategy DEFAULT_REQUEST_STRATEGY = RequestStrategy.LOCAL_THEN_REMOTE;
	private RequestStrategy requestStrategy = DEFAULT_REQUEST_STRATEGY;

	public QueryOptions() {
	}

	public static String getCacheKey(@Nullable QueryOptions options) {
		return options == null ? null : options.getCacheKey();
	}

	public static boolean getIncludeInactive(@Nullable QueryOptions options) {
		return options == null ? DEFAULT_INCLUDE_INACTIVE : options.includeInactive();
	}

	public static String getRepresentation(@Nullable QueryOptions options) {
		String result = RestConstants.Representations.DEFAULT;

		if (options != null && StringUtils.notEmpty(options.getCustomRepresentation())) {
			result = options.getCustomRepresentation();
		}

		return result;
	}

	public static RequestStrategy getRequestStrategy(@Nullable QueryOptions options) {
		if (options != null && options.getRequestStrategy() != null) {
			return options.getRequestStrategy();
		}

		return DEFAULT_REQUEST_STRATEGY;
	}

	public String getCacheKey() {
		return cacheKey;
	}

	public void setCacheKey(String cacheKey) {
		this.cacheKey = cacheKey;
	}

	public boolean includeInactive() {
		return includeInactive;
	}

	public void setIncludeInactive(boolean includeInactive) {
		this.includeInactive = includeInactive;
	}

	public String getCustomRepresentation() {
		return customRepresentation;
	}

	public void setCustomRepresentation(String customRepresentation) {
		this.customRepresentation = customRepresentation;
	}

	public RequestStrategy getRequestStrategy() {
		return requestStrategy;
	}

	public void setRequestStrategy(RequestStrategy requestStrategy) {
		this.requestStrategy = requestStrategy;
	}

	public static class Builder {
		private boolean includeInactive = DEFAULT_INCLUDE_INACTIVE;
		private String cacheKey;
		private String customRepresentation;
		private RequestStrategy requestStrategy = DEFAULT_REQUEST_STRATEGY;

		public Builder() {
		}

		public Builder includeInactive(boolean includeInactive) {
			this.includeInactive = includeInactive;

			return this;
		}

		public Builder cacheKey(String cacheKey) {
			this.cacheKey = cacheKey;

			return this;
		}

		public Builder customRepresentation(String customRepresentation) {
			this.customRepresentation = customRepresentation;

			return this;
		}

		public Builder requestStrategy(RequestStrategy requestStrategy) {
			this.requestStrategy = requestStrategy;

			return this;
		}

		public QueryOptions build() {
			QueryOptions instance = new QueryOptions();
			instance.setIncludeInactive(includeInactive);
			instance.setCacheKey(cacheKey);
			instance.setCustomRepresentation(customRepresentation);
			instance.setRequestStrategy(requestStrategy);

			return instance;
		}
	}
}
