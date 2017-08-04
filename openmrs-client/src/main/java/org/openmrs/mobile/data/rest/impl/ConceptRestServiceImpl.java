package org.openmrs.mobile.data.rest.impl;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.openmrs.mobile.data.PagingInfo;
import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.data.rest.BaseRestService;
import org.openmrs.mobile.data.rest.retrofit.ConceptRestService;
import org.openmrs.mobile.models.Concept;
import org.openmrs.mobile.models.Results;
import org.openmrs.mobile.utilities.ApplicationConstants;

import javax.inject.Inject;

import retrofit2.Call;

public class ConceptRestServiceImpl extends BaseRestService<Concept, ConceptRestService> {
	@Inject
	public ConceptRestServiceImpl() { }

	@Override
	protected String getRestPath() {
		return ApplicationConstants.API.REST_ENDPOINT_V1;
	}

	@Override
	protected String getEntityName() {
		return "concept";
	}

	public Call<Results<Concept>> getByConceptName(@NonNull String conceptName, @Nullable QueryOptions options) {
		return restService.getByConceptName(buildRestRequestPath(), conceptName, QueryOptions.getRepresentation(options));
	}

	public Call<Results<Concept>> findConcept(@NonNull String searchQuery, @Nullable QueryOptions options,
			@NonNull PagingInfo pagingInfo) {
		return restService.findConcept(buildRestRequestPath(), searchQuery,
				QueryOptions.getRepresentation(options), PagingInfo.getLimit(pagingInfo),
				PagingInfo.getStartIndex(pagingInfo));
	}
}
