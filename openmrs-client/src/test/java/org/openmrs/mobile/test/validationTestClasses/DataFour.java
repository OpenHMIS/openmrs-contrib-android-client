package org.openmrs.mobile.test.validationTestClasses;

import org.openmrs.mobile.annotation.Validate;
import org.openmrs.mobile.utilities.StringUtils;

public class DataFour {
	@Validate(clazz = StringUtils.class, method = "isNullOrEmpty", negate = true)
	private String prop;

	public String getProp() {
		return prop;
	}

	public void setProp(String prop) { this.prop = prop; }
}
