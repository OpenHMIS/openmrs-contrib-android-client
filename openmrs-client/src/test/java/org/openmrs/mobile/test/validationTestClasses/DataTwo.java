package org.openmrs.mobile.test.validationTestClasses;

import org.openmrs.mobile.annotation.Validate;
import org.openmrs.mobile.utilities.StringUtils;

public class DataTwo {
	@Validate(clazz = StringUtils.class, method = "isNullOrEmpty")
	public String prop;
}
