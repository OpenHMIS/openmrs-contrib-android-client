package org.openmrs.mobile.data;

import android.support.annotation.Nullable;

import org.openmrs.mobile.data.rest.RestConstants;
import org.openmrs.mobile.utilities.StringUtils;

public class QueryOptions {
	private static final boolean DEFAULT_INCLUDE_INACTIVE = false;

	public static final QueryOptions INCLUDE_ALL_FULL_REP =
			new Builder().includeInactive(true).customRepresentation(RestConstants.Representations.FULL).build();
	public static final QueryOptions FULL_REP =
			new Builder().customRepresentation(RestConstants.Representations.FULL).build();

	private String cacheKey;
	private boolean includeInactive = DEFAULT_INCLUDE_INACTIVE;
	private String customRepresentation;

	public QueryOptions() { }

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

	public static class Builder {
		private boolean includeInactive = DEFAULT_INCLUDE_INACTIVE;
		private String cacheKey;
		private String customRepresentation;

		public Builder() { }

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

		public QueryOptions build() {
			QueryOptions instance = new QueryOptions();
			instance.setIncludeInactive(includeInactive);
			instance.setCacheKey(cacheKey);
			instance.setCustomRepresentation(customRepresentation);

			return instance;
		}
	}
}
