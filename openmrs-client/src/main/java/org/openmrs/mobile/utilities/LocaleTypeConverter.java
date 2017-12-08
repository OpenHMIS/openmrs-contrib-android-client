package org.openmrs.mobile.utilities;

import com.raizlabs.android.dbflow.converter.TypeConverter;

import java.util.Locale;

public class LocaleTypeConverter extends TypeConverter<String, Locale> {
	@Override
	public String getDBValue(Locale model) {
		return model.toString();
	}

	@Override
	public Locale getModelValue(String data) {
		if (StringUtils.isNullOrEmpty(data)) {
			return Locale.getDefault();
		}

		String parts[] = data.split("_", -1);
		if (parts.length == 1) return new Locale(parts[0]);
		else if (parts.length == 2
				|| (parts.length == 3 && parts[2].startsWith("#")))
			return new Locale(parts[0], parts[1]);
		else return new Locale(parts[0], parts[1], parts[2]);
	}
}
