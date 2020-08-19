package org.openmrs.mobile.test.validationTestClasses;

import org.openmrs.mobile.annotation.Validate;
import org.openmrs.mobile.utilities.StringUtils;

public class DataSix {
	@Validate(clazz = StringUtils.class, method = "isNullOrEmpty", negate = true)
	protected String prop;

	public String getProp() {
		return prop;
	}

	public void setProp(String prop) { this.prop = prop; }
}
