package org.openmrs.mobile.utilities.strategy;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.annotations.SerializedName;

public class CustomExclusionStrategy implements ExclusionStrategy {

	@Override
	public boolean shouldSkipField(FieldAttributes f) {
		SerializedName clazz = f.getAnnotation(SerializedName.class);
		if(clazz != null)
			if(clazz.value() == "display" || clazz.value() == "links" || clazz.value() == "uuid")
				return true;

		return false;
	}

	@Override
	public boolean shouldSkipClass(Class<?> clazz) {
		return false;
	}
}
