package org.openmrs.mobile.models;

public enum PersonGenderEnum {
	MALE("M"),
	FEMALE("F");

	private String gender;

	PersonGenderEnum(String gender) {
		this.gender = gender;
	}

	@Override
	public String toString() {
		return gender;
	}
}
