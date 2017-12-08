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

package org.openmrs.mobile.data.impl;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.openmrs.mobile.data.BaseDataService;
import org.openmrs.mobile.data.PagingInfo;
import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.data.db.impl.ConceptDbService;
import org.openmrs.mobile.data.rest.impl.ConceptRestServiceImpl;
import org.openmrs.mobile.models.Concept;

import java.util.List;

import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

public class ConceptDataService extends BaseDataService<Concept, ConceptDbService, ConceptRestServiceImpl> {
	@Inject
	public ConceptDataService() { }

	public void getByConceptName(@NonNull String conceptName, @Nullable QueryOptions options,
			@NonNull GetCallback<List<Concept>> callback) {
		checkNotNull(conceptName);
		checkNotNull(callback);

		executeMultipleCallback(callback, options, null,
				() -> dbService.getByName(conceptName, options),
				() -> restService.getByConceptName(conceptName, options));
	}

	public void findConcept(@NonNull String searchQuery, @Nullable QueryOptions options, @NonNull PagingInfo pagingInfo,
			@NonNull GetCallback<List<Concept>> callback) {
		checkNotNull(searchQuery);
		checkNotNull(pagingInfo);
		checkNotNull(callback);

		executeMultipleCallback(callback, options, pagingInfo,
				() -> dbService.getByName(searchQuery, options),
				() -> restService.findConcept(searchQuery, options, pagingInfo));
	}
}
