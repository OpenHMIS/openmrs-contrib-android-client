package org.openmrs.mobile.utilities;

import android.widget.EditText;

import java.util.regex.Pattern;

public class ViewUtils {

	public static String getInput(EditText e) {
		if (e.getText() == null) {
			return null;
		} else if (isEmpty(e)) {
			return null;
		} else {
			return e.getText().toString();
		}
	}

	public static boolean isEmpty(EditText etText) {
		return etText.getText().toString().trim().length() == 0;
	}

	/**
	 * Validate input fields by checking for non-alphanumeric characters,
	 * @param value the string to be validated
	 * @return validity status of the input value
	 */
	public static boolean isValidInput(String value){
		String validCharacters = "[a-zA-Z0-9]";
		return Pattern.matches(validCharacters,value.trim());
	}

}
