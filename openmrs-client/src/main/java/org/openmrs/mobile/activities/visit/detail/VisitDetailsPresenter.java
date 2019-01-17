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

import android.widget.TextView;

import org.openmrs.mobile.activities.visit.VisitContract;
import org.openmrs.mobile.activities.visit.BaseVisitPresenter;
import org.openmrs.mobile.data.DataService;
import org.openmrs.mobile.data.PagingInfo;
import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.data.impl.ConceptAnswerDataService;
import org.openmrs.mobile.data.impl.ConceptDataService;
import org.openmrs.mobile.data.impl.VisitAttributeTypeDataService;
import org.openmrs.mobile.data.impl.VisitDataService;
import org.openmrs.mobile.data.rest.RestConstants;
import org.openmrs.mobile.models.ConceptAnswer;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.models.VisitAttributeType;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.ToastUtil.ToastType;

import java.util.List;

public class VisitDetailsPresenter extends BaseVisitPresenter implements VisitContract.VisitDetails.Presenter {

	VisitContract.VisitDetails.View visitDetailsView;
	private VisitAttributeTypeDataService visitAttributeTypeDataService;
	private VisitDataService visitDataService;
	private ConceptDataService conceptDataService;
	private String patientUuid;

	private ConceptAnswerDataService conceptAnswerDataService;

	public VisitDetailsPresenter(String patientUuid, String visitUuid, VisitContract.VisitDetails.View visitDetailsView) {
		super(visitUuid, visitDetailsView);

		visitDashboardPageView = visitDetailsView;

		this.patientUuid = patientUuid;

		this.visitDataService = dataAccess().visit();
		this.conceptDataService = dataAccess().concept();
		this.conceptAnswerDataService = dataAccess().conceptAnswer();
		this.visitAttributeTypeDataService = dataAccess().visitAttributeType();

		this.visitDetailsView = (VisitContract.VisitDetails.View) visitDashboardPageView;
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
										.fetchErrorMessage, ToastType.ERROR);
					}
				};
		visitDataService.getByUuid(visitUuid, QueryOptions.FULL_REP, getSingleCallback);
	}

	@Override
	public void getPatientUUID() {
		visitDetailsView.setPatientUUID(patientUuid);
	}

	@Override
	public void getVisitUUID() {
		visitDetailsView.setVisitUUID(visitUuid);
	}

	private void loadVisitAttributeTypes() {

		QueryOptions options = new QueryOptions.Builder()
				.cacheKey(ApplicationConstants.CacheKays.VISIT_ATTRIBUTE_TYPE)
				.customRepresentation(RestConstants.Representations.FULL)
				.build();

		visitAttributeTypeDataService
				.getAll(options, PagingInfo.ALL.getInstance(),
						new DataService.GetCallback<List<VisitAttributeType>>() {
							@Override
							public void onCompleted(List<VisitAttributeType> entities) {
								visitDetailsView.showTabSpinner(false);
								visitDetailsView.setAttributeTypes(entities);
							}

							@Override
							public void onError(Throwable t) {
								visitDetailsView.showTabSpinner(false);
								visitDetailsView.showToast(t.getMessage(), ToastType.ERROR);
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
				visitDetailsView.showToast(t.getMessage(), ToastType.ERROR);
			}
		});
	}

	@Override
	protected void refreshDependentData() {
		getVisit();
		visitDetailsView.displayRefreshingData(false);
	}
}
