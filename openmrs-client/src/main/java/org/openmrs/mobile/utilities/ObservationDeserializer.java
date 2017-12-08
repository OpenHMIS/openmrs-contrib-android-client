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

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import org.openmrs.mobile.models.Concept;
import org.openmrs.mobile.models.Encounter;
import org.openmrs.mobile.models.Observation;
import org.openmrs.mobile.models.Person;
import org.openmrs.mobile.models.User;
import org.openmrs.mobile.models.Visit;

import java.lang.reflect.Type;

public class ObservationDeserializer implements JsonDeserializer<Observation> {

	private static final String UUID_KEY = "uuid";
	private static final String DISPLAY_KEY = "display";
	private static final String VALUE_KEY = "value";
	private static final String COMMENT_KEY = "comment";
	private static final String DATE_KEY = "obsDatetime";
	private static final String NAME_KEY = "name";

	@Override
	public Observation deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {

		JsonObject jsonObject = json.getAsJsonObject();

		Observation observation = new Observation();
		observation.setUuid(jsonObject.get(UUID_KEY).getAsString());
		if (jsonObject.get(DISPLAY_KEY) != JsonNull.INSTANCE && jsonObject.get(DISPLAY_KEY) != null) {
			observation.setDisplay(jsonObject.get(DISPLAY_KEY).getAsString());
		}

		if (jsonObject.get(COMMENT_KEY) != JsonNull.INSTANCE && jsonObject.get(COMMENT_KEY) != null) {
			observation.setComment(jsonObject.get(COMMENT_KEY).getAsString());
		}

		if (jsonObject.get(DATE_KEY) != JsonNull.INSTANCE && jsonObject.get(DATE_KEY) != null) {
			observation.setObsDatetime(jsonObject.get(DATE_KEY).getAsString());
		}

		JsonElement encounterJson = jsonObject.get("encounter");
		if (null != encounterJson && !encounterJson.isJsonNull()) {
			Encounter encounter = new Encounter();
			encounter.setUuid(encounterJson.getAsJsonObject().get(UUID_KEY).getAsString());

			Visit visit = new Visit();
			JsonElement visitElement = encounterJson.getAsJsonObject().get("visit");
			if (null != visitElement && !visitElement.isJsonNull()) {
				visit.setUuid(visitElement.getAsJsonObject().get(UUID_KEY).getAsString());
			}

			encounter.setVisit(visit);
			observation.setEncounter(encounter);
		}

		JsonElement conceptJson = jsonObject.get("concept");

		if (conceptJson != null && "Visit Diagnoses".equals(conceptJson.getAsJsonObject().get(DISPLAY_KEY).getAsString())) {
			JsonArray diagnosisDetailJSONArray = jsonObject.get("groupMembers").getAsJsonArray();
			for (int i = 0; i < diagnosisDetailJSONArray.size(); i++) {

				JsonObject diagnosisDetails = diagnosisDetailJSONArray.get(i).getAsJsonObject();
				String diagnosisDetail = diagnosisDetails.get("concept").getAsJsonObject().get(DISPLAY_KEY).getAsString();

				if ("Diagnosis order".equals(diagnosisDetail)) {
					observation.setDiagnosisOrder(
							diagnosisDetails.getAsJsonObject().get(VALUE_KEY).getAsJsonObject().get(DISPLAY_KEY)
									.getAsString());
				} else if ("Diagnosis certainty".equals(diagnosisDetail)) {
					observation.setDiagnosisCertanity(
							diagnosisDetails.getAsJsonObject().get(VALUE_KEY).getAsJsonObject().get(DISPLAY_KEY)
									.getAsString());
				} else {
					try {
						observation.setDiagnosisList(
								diagnosisDetails.getAsJsonObject().get(VALUE_KEY).getAsJsonObject().get(DISPLAY_KEY)
										.getAsString());
						observation.setValueCodedName(
								diagnosisDetails.getAsJsonObject().get(VALUE_KEY).getAsJsonObject().get(UUID_KEY)
										.getAsString());
					} catch (IllegalStateException e) {
						observation.setDiagnosisList(diagnosisDetails.getAsJsonObject().get(VALUE_KEY).getAsString());
					}
				}
			}
		} else if (conceptJson != null &&
				"Text of encounter note".equals(conceptJson.getAsJsonObject().get(DISPLAY_KEY).getAsString())) {
			JsonElement encounterNote = jsonObject.getAsJsonObject().get(VALUE_KEY);
			observation.setDiagnosisNote(!encounterNote.isJsonNull() ? encounterNote.getAsString() : "");
		}
		if (conceptJson != null) {
			Concept concept = new Concept();
			concept.setUuid(conceptJson.getAsJsonObject().get(UUID_KEY).getAsString());
			observation.setConcept(concept);
		}

		JsonElement personJson = jsonObject.get("person");
		if (personJson != null) {
			Person person = new Person();
			person.setUuid(personJson.getAsJsonObject().get(UUID_KEY).getAsString());
			person.setDisplay(personJson.getAsJsonObject().get(DISPLAY_KEY).getAsString());
			observation.setPerson(person);
		}

		JsonElement creatorJson = jsonObject.get("creator");
		if (creatorJson != null) {
			User user = new User();
			user.setUuid(creatorJson.getAsJsonObject().get(UUID_KEY).getAsString());
			user.setDisplay(creatorJson.getAsJsonObject().get(DISPLAY_KEY).getAsString());

			JsonElement creatorPersonJson = creatorJson.getAsJsonObject().get("person");
			if (creatorPersonJson != null) {
				Person person = new Person();
				person.setUuid(creatorPersonJson.getAsJsonObject().get(UUID_KEY).getAsString());
				person.setDisplay(creatorPersonJson.getAsJsonObject().get(DISPLAY_KEY).getAsString());
				user.setPerson(person);
			}

			observation.setCreator(user);
		}

		JsonElement dateCreatedJson = jsonObject.get("dateCreated");
		if (dateCreatedJson != null) {
			observation.setDateCreated(DateUtils.convertTimeString(dateCreatedJson.getAsString()).toDate());
		}

		JsonElement voidedJson = jsonObject.get("voided");
		if (voidedJson != null) {
			observation.setVoided(Boolean.getBoolean(voidedJson.getAsString()));
		}

		return observation;
	}

}
