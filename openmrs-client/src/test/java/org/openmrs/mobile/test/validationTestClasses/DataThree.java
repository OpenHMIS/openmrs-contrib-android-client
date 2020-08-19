package org.openmrs.mobile.test.validationTestClasses;

import org.openmrs.mobile.annotation.Validate;
import org.openmrs.mobile.utilities.StringUtils;

public class DataThree {
	@Validate(clazz = StringUtils.class, method = "isNullOrEmpty")
	public boolean prop;
}
