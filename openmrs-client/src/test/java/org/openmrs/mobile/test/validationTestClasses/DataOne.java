package org.openmrs.mobile.test.validationTestClasses;

import org.openmrs.mobile.annotation.Validate;
import org.openmrs.mobile.utilities.StringUtils;

public class DataOne {
	@Validate(clazz = StringUtils.class, method = "isNullOrEmpty", negate = true)
	public String prop;

	public DataOne() {}

	public DataOne(String prop) {
		this.prop = prop;
	}
}
