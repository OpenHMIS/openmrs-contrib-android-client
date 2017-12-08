/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and
 * limitations under the License.
 *
 * Copyright (C) OpenHMIS.  All Rights Reserved.
 */
package org.openmrs.mobile.data.impl;

import org.openmrs.mobile.data.BaseDataService;
import org.openmrs.mobile.data.DataService;
import org.openmrs.mobile.data.DatabaseHelper;
import org.openmrs.mobile.data.PagingInfo;
import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.data.db.impl.PatientListContextDbService;
import org.openmrs.mobile.data.rest.impl.PatientListContextRestServiceImpl;
import org.openmrs.mobile.models.PatientListContext;
import org.openmrs.mobile.models.PatientListContext_Table;

import java.util.List;

import javax.inject.Inject;

public class PatientListContextDataService
		extends BaseDataService<PatientListContext, PatientListContextDbService, PatientListContextRestServiceImpl>
		implements DataService<PatientListContext> {

	private DatabaseHelper databaseHelper;

	@Inject
	public PatientListContextDataService(DatabaseHelper databaseHelper) {
		this.databaseHelper = databaseHelper;
	}

	public void getListPatients(String patientListUuid, QueryOptions options, PagingInfo pagingInfo,
			GetCallback<List<PatientListContext>> callback) {
		executeMultipleCallback(callback, options, pagingInfo,
				() -> dbService.getListPatients(patientListUuid, options, pagingInfo),
				() -> restService.getListPatients(patientListUuid, options, pagingInfo),
				(patientListContextsFromServer) -> {
					dbService.saveAll(patientListContextsFromServer);

					databaseHelper.diffDelete(PatientListContext.class,
							PatientListContext_Table.patientList_uuid.eq(patientListUuid), patientListContextsFromServer);
				});
	}
}
