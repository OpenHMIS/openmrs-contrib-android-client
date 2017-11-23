package org.openmrs.mobile.event;

public abstract class OpenMRSEvent {

	protected String message;

	public OpenMRSEvent(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}
