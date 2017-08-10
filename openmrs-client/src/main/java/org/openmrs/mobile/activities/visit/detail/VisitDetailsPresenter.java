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
package org.openmrs.mobile.activities.visit.detail;

import android.util.Log;
import android.widget.TextView;

import org.openmrs.mobile.activities.visit.VisitContract;
import org.openmrs.mobile.activities.visit.VisitPresenterImpl;
import org.openmrs.mobile.data.DataService;
import org.openmrs.mobile.data.PagingInfo;
import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.data.impl.ConceptAnswerDataService;
import org.openmrs.mobile.data.impl.ConceptDataService;
import org.openmrs.mobile.data.impl.VisitAttributeTypeDataService;
import org.openmrs.mobile.data.impl.VisitDataService;
import org.openmrs.mobile.data.rest.RestConstants;
import org.openmrs.mobile.models.Concept;
import org.openmrs.mobile.models.ConceptAnswer;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.models.VisitAttributeType;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.ToastUtil;

import java.util.List;

public class VisitDetailsPresenter extends VisitPresenterImpl implements VisitContract.VisitDetailsPresenter {

	VisitContract.VisitDetailsView visitDetailsView;
	private VisitAttributeTypeDataService visitAttributeTypeDataService;
	private VisitDataService visitDataService;
	private ConceptDataService conceptDataService;
	private String patientUUID, visitUUID, providerUuid, visitStopDate;

	private ConceptAnswerDataService conceptAnswerDataService;

	public VisitDetailsPresenter(String patientUuid, String visitUuid, String providerUuid,
			String visitStopDate, VisitContract.VisitDetailsView visitDetailsView) {
		super();

		this.visitDetailsView = visitDetailsView;
		this.visitDetailsView.setPresenter(this);
		this.visitUUID = visitUuid;
		this.providerUuid = providerUuid;
		this.patientUUID = patientUuid;
		this.visitStopDate = visitStopDate;

		this.visitDataService = dataAccess().visit();
		this.conceptDataService = dataAccess().concept();
		this.conceptAnswerDataService = dataAccess().conceptAnswer();
		this.visitAttributeTypeDataService = dataAccess().visitAttributeType();
	}

	@Override
	public void subscribe() {
	}

	@Override
	public void unsubscribe() {
	}

	@Override
	public void getVisit() {
		visitDetailsView.showTabSpinner(true);
		DataService.GetCallback<Visit> getSingleCallback =
				new DataService.GetCallback<Visit>() {
					@Override
					public void onCompleted(Visit entity) {
						if (entity != null) {
							visitDetailsView.setVisit(entity);
							loadVisitAttributeTypes();
						} else {
							visitDetailsView.showTabSpinner(false);
						}
					}

					@Override
					public void onError(Throwable t) {
						visitDetailsView.showTabSpinner(false);
						visitDetailsView
								.showToast(ApplicationConstants.entityName.VISITS + ApplicationConstants.toastMessages
										.fetchErrorMessage, ToastUtil.ToastType.ERROR);
					}
				};
		visitDataService.getByUuid(visitUUID, QueryOptions.FULL_REP, getSingleCallback);
	}

	@Override
	public void getConcept(String name) {
		DataService.GetCallback<List<Concept>> getCallback = new DataService.GetCallback<List<Concept>>() {

			@Override
			public void onCompleted(List<Concept> concepts) {
				if (!concepts.isEmpty()) {
					visitDetailsView.setConcept(concepts.get(0));
				}
			}

			@Override
			public void onError(Throwable t) {
				Log.e("error", t.getLocalizedMessage());
			}
		};
		conceptDataService.getByConceptName(name, QueryOptions.FULL_REP, getCallback);

	}

	@Override
	public void getPatientUUID() {
		visitDetailsView.setPatientUUID(patientUUID);
	}

	@Override
	public void getVisitUUID() {
		visitDetailsView.setVisitUUID(visitUUID);
	}

	@Override
	public void getProviderUUID() {
		visitDetailsView.setProviderUUID(providerUuid);
	}

	@Override
	public void getVisitStopDate() {
		visitDetailsView.setVisitStopDate(visitStopDate);
	}

	private void loadVisitAttributeTypes() {
		visitDetailsView.showTabSpinner(true);

		QueryOptions options = new QueryOptions.Builder()
				.cacheKey(ApplicationConstants.CacheKays.VISIT_ATTRIBUTE_TYPE)
				.customRepresentation(RestConstants.Representations.FULL)
				.build();

		visitAttributeTypeDataService
				.getAll(options, new PagingInfo(0, 100),
						new DataService.GetCallback<List<VisitAttributeType>>() {
							@Override
							public void onCompleted(List<VisitAttributeType> entities) {
								visitDetailsView.showTabSpinner(false);
								visitDetailsView.setAttributeTypes(entities);
							}

							@Override
							public void onError(Throwable t) {
								visitDetailsView.showTabSpinner(false);
								ToastUtil.error(t.getMessage());
							}
						});
	}

	@Override
	public void getConceptAnswer(String uuid, String searchValue, TextView textView) {
		conceptAnswerDataService.getByConceptUuid(uuid, null, new DataService.GetCallback<List<ConceptAnswer>>() {
			@Override
			public void onCompleted(List<ConceptAnswer> entities) {
				for (ConceptAnswer conceptAnswer : entities) {
					if (conceptAnswer.getUuid().equalsIgnoreCase(searchValue)) {
						textView.setText(conceptAnswer.getDisplay());
					}
				}
			}

			@Override
			public void onError(Throwable t) {
				ToastUtil.error(t.getMessage());
			}
		});
	}
}
