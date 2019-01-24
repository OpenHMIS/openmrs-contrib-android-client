/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.mobile.utilities;

import java.util.ArrayList;
import java.util.Collections;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.OneToMany;

public final class StringUtils {
	private static final String NULL_AS_STRING = "null";
	private static final String SPACE_CHAR = " ";
	private static final Gson gson = new GsonBuilder()
			.setExclusionStrategies(new ExclusionStrategy() {

				@Override
				public boolean shouldSkipField(FieldAttributes f) {
					ForeignKey foreignKeyAnnotation = f.getAnnotation(ForeignKey.class);
					OneToMany oneToManyAnnotation = f.getAnnotation(OneToMany.class);
					return foreignKeyAnnotation != null || oneToManyAnnotation != null;
				}

				@Override
				public boolean shouldSkipClass(Class<?> clazz) {
					return false;
				}
			})
			.serializeNulls()
			.create();

	public static boolean notNull(String string) {
		return null != string && !NULL_AS_STRING.equals(string.trim());
	}

	public static boolean isBlank(String string) {
		return null == string || SPACE_CHAR.equals(string);
	}

	public static boolean notEmpty(String string) {
		return !isNullOrEmpty(string);
	}

	public static boolean isNullOrEmpty(String string) {
		return string == null || string.isEmpty();
	}

	public static String unescapeJavaString(String st) {

		StringBuilder sb = new StringBuilder(st.length());

		for (int i = 0; i < st.length(); i++) {
			char ch = st.charAt(i);
			if (ch == '\\') {
				char nextChar = (i == st.length() - 1) ? '\\' : st
						.charAt(i + 1);
				// Octal escape?
				if (nextChar >= '0' && nextChar <= '7') {
					String code = "" + nextChar;
					i++;
					if ((i < st.length() - 1) && st.charAt(i + 1) >= '0'
							&& st.charAt(i + 1) <= '7') {
						code += st.charAt(i + 1);
						i++;
						if ((i < st.length() - 1) && st.charAt(i + 1) >= '0'
								&& st.charAt(i + 1) <= '7') {
							code += st.charAt(i + 1);
							i++;
						}
					}
					sb.append((char)Integer.parseInt(code, 8));
					continue;
				}
				switch (nextChar) {
					case '\\':
						ch = '\\';
						break;
					case 'b':
						ch = '\b';
						break;
					case 'f':
						ch = '\f';
						break;
					case 'n':
						ch = '\n';
						break;
					case 'r':
						ch = '\r';
						break;
					case 't':
						ch = '\t';
						break;
					case '\"':
						ch = '\"';
						break;
					case '\'':
						ch = '\'';
						break;
					// Hex Unicode: u????
					case 'u':
						if (i >= st.length() - 5) {
							ch = 'u';
							break;
						}
						int code = Integer.parseInt(
								"" + st.charAt(i + 2) + st.charAt(i + 3)
										+ st.charAt(i + 4) + st.charAt(i + 5), 16);
						sb.append(Character.toChars(code));
						i += 5;
						continue;
					default:
						// Do nothing
						break;
				}
				i++;
			}
			sb.append(ch);
		}
		return sb.toString();
	}

	public static String stripHtmlTags(String htmlString) {
		return htmlString.replaceAll("\\<[^>]*>", "").replaceAll("\\s", " ");
	}

	public static ArrayList splitStrings(String display, String splitter) {
		ArrayList<String> displayArray = new ArrayList<>();
		Collections.addAll(displayArray, display.split(splitter));
		return displayArray;
	}

	public static String getConceptName(String obsDisplay) {
		String diagnosisStringOne = "", diagnosisStringTwo = "", diagnosisStringThree = "", diagnosisStringFour = "",
				diagnosisStringFive = "", diagnosisStringSix = "";
		String diagnosisString = (obsDisplay.replaceAll(ApplicationConstants.ObservationLocators.DIAGNOSES, ""));
		diagnosisStringOne += (diagnosisString.replaceAll(ApplicationConstants.ObservationLocators.PRIMARY_DIAGNOSIS, ""));
		diagnosisStringTwo +=
				(diagnosisStringOne.replaceAll(ApplicationConstants.ObservationLocators.SECONDARY_DIAGNOSIS, ""));
		diagnosisStringThree +=
				(diagnosisStringTwo.replaceAll(ApplicationConstants.ObservationLocators.PRESUMED_DIAGNOSIS, ""));
		diagnosisStringFour +=
				(diagnosisStringThree.replaceAll(ApplicationConstants.ObservationLocators.CONFIRMED_DIAGNOSIS, ""));
		diagnosisStringFive += (diagnosisStringFour.replaceAll(",", ""));
		diagnosisStringSix += (diagnosisStringFive.replaceAll(":", ""));

		return diagnosisStringSix;
	}

	public static String extractDisplayValue(String display, int index) {
		if (isNullOrEmpty(display)) {
			return ApplicationConstants.EMPTY_STRING;
		}

		return display.split("=")[index].trim();
	}

	public static <T> String toJson(T object) {
		if (object == null) {
			return "";
		}
		return gson.toJson(object, new TypeToken<T>() {}.getType());
	}
}
