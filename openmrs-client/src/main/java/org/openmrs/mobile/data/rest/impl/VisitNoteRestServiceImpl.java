package org.openmrs.mobile.data.rest.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.openmrs.mobile.data.rest.BaseRestService;
import org.openmrs.mobile.data.rest.retrofit.VisitNoteRestService;
import org.openmrs.mobile.models.VisitNote;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.strategy.CustomExclusionStrategy;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import retrofit2.Call;

public class VisitNoteRestServiceImpl extends BaseRestService<VisitNote, VisitNoteRestService> {
	@Inject
	public VisitNoteRestServiceImpl() { }

	@Override
	protected String getRestPath() {
		return ApplicationConstants.API.REST_ENDPOINT_V2 + "custom";
	}

	@Override
	protected String getEntityName() {
		return "visitnote";
	}

	public Call<VisitNote> save(VisitNote visitNote) {
		Gson gson = new GsonBuilder().setExclusionStrategies(new CustomExclusionStrategy()).create();
		Map<String, String> params = new HashMap<>();
		params.put("personId", visitNote.getPersonId());
		params.put("htmlFormId", visitNote.getHtmlFormId());
		params.put("createVisit", visitNote.getCreateVisit());
		params.put("formModifiedTimestamp", visitNote.getFormModifiedTimestamp());
		params.put("encounterModifiedTimestamp", visitNote.getEncounterModifiedTimestamp());
		params.put("visitId", visitNote.getVisitId());
		params.put("returnUrl", visitNote.getReturnUrl());
		params.put("closeAfterSubmission", visitNote.getCloseAfterSubmission());
		params.put("encounterDiagnoses", gson.toJson(visitNote.getEncounterDiagnoses()));
		params.put("encounterId", visitNote.getEncounterId());
		params.put("w1", visitNote.getW1());
		params.put("w3", visitNote.getW3());
		params.put("w5", visitNote.getW5());
		params.put("w10", visitNote.getW10());
		params.put("w12", visitNote.getW12());
		params.put("obs", visitNote.getObservationUuid());

		return restService.save(buildRestRequestPath(), params);
	}
}
