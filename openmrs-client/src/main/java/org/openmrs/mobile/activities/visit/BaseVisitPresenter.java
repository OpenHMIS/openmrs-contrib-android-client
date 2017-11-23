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

package org.openmrs.mobile.activities.visit;

import org.greenrobot.eventbus.EventBus;
import org.openmrs.mobile.activities.BasePresenter;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.data.DataService;
import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.data.impl.VisitDataService;
import org.openmrs.mobile.event.DataRefreshEvent;
import org.openmrs.mobile.event.VisitDashboardDataRefreshEvent;
import org.openmrs.mobile.listeners.retrofit.DefaultResponseCallbackListener;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.ToastUtil;

public abstract class BaseVisitPresenter extends BasePresenter implements VisitContract.VisitDashboardPagePresenter {

	private VisitDataService visitDataService;
	private EventBus eventBus;

	protected VisitContract.VisitDashboardPageView visitDashboardPageView;
	protected String visitUuid;

	public BaseVisitPresenter(String visitUuid, VisitContract.VisitDashboardPageView visitDashboardPageView) {
		this.visitUuid = visitUuid;
		this.eventBus = OpenMRS.getInstance().getEventBus();

		this.visitDashboardPageView = visitDashboardPageView;
		this.visitDashboardPageView.setPresenter(this);

		visitDataService = dataAccess().visit();
	}

	@Override
	public void subscribe() {
		// Intentionally left blank
	}

	protected void refreshBaseData(DefaultResponseCallbackListener responseCallbackListener) {
		getVisit(true, responseCallbackListener);
	}

	private void getVisit(boolean forceRefresh, DefaultResponseCallbackListener responseCallbackListener) {
		QueryOptions options = QueryOptions.FULL_REP;
		if (forceRefresh) {
			options = QueryOptions.REMOTE_FULL_REP;
		}

		visitDataService.getByUuid(visitUuid, options, new DataService.GetCallback<Visit>() {

			@Override
			public void onCompleted(Visit visit) {
				responseCallbackListener.onResponse();
			}

			@Override
			public void onError(Throwable t) {
				responseCallbackListener.onErrorResponse(t.getMessage());
			}
		});
	}

	protected void refreshAllTabData() {
		eventBus.post(new VisitDashboardDataRefreshEvent(ApplicationConstants.EventMessages.DataRefresh
				.VisitDashboard.REFRESHING_BASE_DATA));
		eventBus.post(new DataRefreshEvent(ApplicationConstants.EventMessages.DataRefresh.REFRESH));
		refreshBaseData(new DefaultResponseCallbackListener() {

			@Override
			public void onResponse() {
				eventBus.post(new VisitDashboardDataRefreshEvent(ApplicationConstants.EventMessages.DataRefresh
						.VisitDashboard.REFRESH_DEPENDENT_DATA));
			}

			@Override
			public void onErrorResponse(String errorMessage) {
				visitDashboardPageView
						.showToast(ApplicationConstants.entityName.VISITS + ApplicationConstants.toastMessages
								.fetchErrorMessage, ToastUtil.ToastType.ERROR);
				eventBus.post(new VisitDashboardDataRefreshEvent(ApplicationConstants.EventMessages.DataRefresh
						.VisitDashboard.REFRESH_ERROR));
			}
		});
	}

	@Override
	public void dataRefreshWasRequested() {
		refreshAllTabData();
	}

	@Override
	public void dataRefreshEventOccurred(VisitDashboardDataRefreshEvent event) {
		switch (event.getMessage()) {
			case ApplicationConstants.EventMessages.DataRefresh.VisitDashboard.REFRESHING_BASE_DATA:
				visitDashboardPageView.displayRefreshingData(true);
				break;
			case ApplicationConstants.EventMessages.DataRefresh.VisitDashboard.REFRESH_DEPENDENT_DATA:
				refreshDependentData();
				break;
			case ApplicationConstants.EventMessages.DataRefresh.VisitDashboard.REFRESH_ERROR:
				visitDashboardPageView.displayRefreshingData(false);
				break;
			default:
				break;
		}
	}

	protected abstract void refreshDependentData();
}
