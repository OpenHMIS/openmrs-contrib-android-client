package org.openmrs.mobile.data;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openmrs.mobile.BuildConfig;
import org.openmrs.mobile.data.rest.RestConstants;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class QueryOptionsTest {
	@Test
	public void queryOptions_canGetFullRepresentation() {
		QueryOptions fullQueryOptions = QueryOptions.FULL_REP;
		
		String result = QueryOptions.getRepresentation(fullQueryOptions);

		Assert.assertEquals(RestConstants.Representations.FULL, result);
	}
}
