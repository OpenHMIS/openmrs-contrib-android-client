package org.openmrs.mobile.utilities;

import org.openmrs.mobile.annotation.Validate;
import org.openmrs.mobile.application.OpenMRS;

import java.lang.reflect.Field;
import java.util.List;

/**
 * This class hold methods to work with data in objects, typically via reflection
 */
public class DataUtil {

	private final static OpenMRS openMRS = OpenMRS.getInstance();

	/**
	 * Check if all fields on an entity or a list of entities marked with the @Validate annotation are valid
	 *
	 * @param data The data to validate
	 * @return Whether the data is valid or not.
	 */
	public static boolean isValid(Object data) {
		try {
			// Get all fields (even private) on the object
			Field[] fields = data.getClass().getDeclaredFields();
			// If this is a list, we need to check each entity inside it
			if (data instanceof List<?> && ((List<?>) data).size() > 0) {
				for (Object listItemData : (List<?>) data) {
					fields = listItemData.getClass().getDeclaredFields();
					for (Field field : fields) {
						if (isFieldInvalid(field, listItemData)) {
							return false;
						}
					}
				}
			} else {
				for (Field field : fields) {
					if (isFieldInvalid(field, data)) {
						return false;
					}
				}
			}
		} catch (Exception ex) {
			// There was an error somewhere, but don't hold up the code
			if (openMRS != null && openMRS.getLogger() != null) {
				openMRS.getLogger().e(ex);
			} else {
				System.out.println(ex.getMessage());
			}
		}
		return true;
	}

	/**
	 * Perform the field validation
	 *
	 * @param field The field to validate
	 * @param data  The object holding the field to validate
	 * @return Whether the field is invalid or not
	 */
	private static boolean isFieldInvalid(Field field, Object data) {
		try {
			Validate validate = field.getAnnotation(Validate.class);
			if (validate != null) {
				// If it's a private field, we need to set accessibility to we can get the value
				field.setAccessible(true);
				Object fieldData = field.get(data);
				boolean invalidationResult =
						!((boolean) validate.clazz().getMethod(validate.method(), field.getType()).invoke(null, fieldData));
				// Negate the validation result if we're supposed to, while also flipping the result because we want whether
				// it's invalid
				return validate.negate() != invalidationResult; // validate.negate() ? !invalidationResult : invalidationResult
			}
		} catch (Exception ex) {
			// There was an error somewhere, so assume entity isn't valid
			if (openMRS != null && openMRS.getLogger() != null) {
				openMRS.getLogger().e(ex);
			}
			return true;
		}
		return false;
	}
}
