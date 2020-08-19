package org.openmrs.mobile.test.validationTestClasses;

import org.openmrs.mobile.annotation.Validate;
import org.openmrs.mobile.utilities.StringUtils;

public class DataFive {
	@Validate(clazz = StringUtils.class, method = "isTheBombDotCom")
	public String prop;
}
